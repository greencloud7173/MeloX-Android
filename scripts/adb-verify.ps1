# Build, install and smoke-test the debug APK on the connected device.
#
# Run after every UI change:
#   powershell -ExecutionPolicy Bypass -File scripts/adb-verify.ps1
#   powershell -ExecutionPolicy Bypass -File scripts/adb-verify.ps1 -Screenshot shot.png
#
# It fails (exit 1) when no device is attached, the install fails, the process
# dies, or anything lands in the crash buffer.
[CmdletBinding()]
param(
    [string]$Apk,
    [string]$Package = '',
    [string]$Activity = '',
    [string]$Screenshot,
    [int]$LaunchWaitSeconds = 10,
    [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$androidRoot = Join-Path $repoRoot 'android'

function Find-Adb {
    $candidates = @(
        (Join-Path $env:USERPROFILE 'android-sdk\platform-tools\adb.exe'),
        'C:\Program Files (x86)\Android\android-sdk\platform-tools\adb.exe',
        'C:\Program Files\Android\android-sdk\platform-tools\adb.exe'
    )
    if ($env:ANDROID_HOME) { $candidates = @((Join-Path $env:ANDROID_HOME 'platform-tools\adb.exe')) + $candidates }
    if ($env:ANDROID_SDK_ROOT) { $candidates = @((Join-Path $env:ANDROID_SDK_ROOT 'platform-tools\adb.exe')) + $candidates }
    foreach ($candidate in $candidates) { if (Test-Path $candidate) { return $candidate } }
    $onPath = Get-Command adb -ErrorAction SilentlyContinue
    if ($onPath) { return $onPath.Source }
    throw 'adb not found. Set ANDROID_HOME or install platform-tools.'
}

if (-not $Apk) {
    # Default to the arm64 split produced by :app:assembleDebug.
    $Apk = Join-Path $androidRoot 'app\build\outputs\apk\debug\app-arm64-v8a-debug.apk'
}

if (-not $SkipBuild) {
    Write-Host '==> Building :app:assembleDebug'
    Push-Location $androidRoot
    try {
        $gradlew = Join-Path $androidRoot 'gradlew'
        $buildOut = & bash $gradlew ':app:assembleDebug' '--console=plain' 2>&1
        $buildOut | Select-String -Pattern '^e: |FAILED|BUILD ' | ForEach-Object { $_.Line }
        if ($LASTEXITCODE -ne 0) { throw "Gradle build failed (exit $LASTEXITCODE)." }
    } finally { Pop-Location }
}

if (-not (Test-Path $Apk)) { throw "APK not found: $Apk" }

$adb = Find-Adb
& $adb start-server | Out-Null

$devices = @(& $adb devices | Select-Object -Skip 1 | Where-Object { $_ -match '\sdevice$' })
if ($devices.Count -eq 0) { throw 'No device connected (adb devices shows none in "device" state).' }
Write-Host "==> Device: $($devices[0])"

if (-not $Package) {
    # Read the application id straight from the APK so split builds and the
    # ".dev" debug suffix keep working without hard-coding them.
    $aapt = Get-ChildItem (Join-Path (Split-Path -Parent $adb) '..\build-tools') -Directory -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending | Select-Object -First 1
    $aapt2 = if ($aapt) { Join-Path $aapt.FullName 'aapt2.exe' } else { $null }
    if ($aapt2 -and (Test-Path $aapt2)) {
        $badging = & $aapt2 dump badging $Apk 2>&1
        $match = $badging | Select-String -Pattern "package: name='([^']+)'" | Select-Object -First 1
        if ($match) { $Package = $match.Matches[0].Groups[1].Value }
    }
    if (-not $Package) { $Package = 'com.lladlam.melox.android.dev' }
}
if (-not $Activity) { $Activity = "$Package/com.lladlam.melox.MainActivity" }
Write-Host "==> Package: $Package"

Write-Host '==> Installing'
& $adb install -r -t $Apk | Select-Object -Last 2
if ($LASTEXITCODE -ne 0) { throw 'adb install failed.' }

& $adb logcat -c
& $adb shell am force-stop $Package | Out-Null
& $adb shell am start -n $Activity | Out-Null
Start-Sleep -Seconds $LaunchWaitSeconds

$appPid = (& $adb shell pidof $Package) -join ''
if (-not $appPid) { throw "Process $Package is not running after launch." }
Write-Host "==> Running, pid=$appPid"

$crashes = @(& $adb logcat -d -b crash | Select-String -Pattern 'FATAL EXCEPTION|AndroidRuntime: (java|kotlin)\.')
if ($crashes.Count -gt 0) {
    $crashes | ForEach-Object { Write-Host $_.Line }
    throw 'Crash detected in the crash buffer.'
}
Write-Host '==> No crash in the crash buffer'

if ($Screenshot) {
    $target = [System.IO.Path]::GetFullPath($Screenshot)
    cmd /c "`"$adb`" exec-out screencap -p > `"$target`"" | Out-Null
    Write-Host "==> Screenshot: $target"
}

Write-Host 'ADB VERIFY OK'
