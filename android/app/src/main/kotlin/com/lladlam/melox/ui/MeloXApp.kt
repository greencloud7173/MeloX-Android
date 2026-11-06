package com.lladlam.melox.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.tween

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Podcasts
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
// ── 底部 chrome 明暗切换（2026-09-25）─────────────────────────────────
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onGloballyPositioned
import com.lladlam.melox.ui.controls.BottomBarToneTheme
import com.lladlam.melox.ui.controls.LocalBottomBarTone
import com.lladlam.melox.ui.controls.bottomBarTone
import com.lladlam.melox.ui.controls.darkness
import com.lladlam.melox.ui.controls.rememberBottomBarToneState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.ui.zIndex
import com.lladlam.melox.core.account.rememberNeteaseSessionStore
import com.lladlam.melox.core.account.NeteaseSessionStore
import com.lladlam.melox.BuildConfig
import com.lladlam.melox.R
import com.lladlam.melox.core.music.model.MusicSource
import com.lladlam.melox.core.music.provider.MusicProviderSelectionStore
import com.lladlam.melox.ui.account.MeloXAccountActivity
import com.lladlam.melox.ui.feedback.MeloXTransientMessage
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.shapes.Capsule
import com.lladlam.melox.ui.library.LibraryScreen
import com.lladlam.melox.core.provider.kugou.KugouLiteDailyCheckIn
import com.lladlam.melox.ui.legal.MELOX_LEGAL_VERSION
import com.lladlam.melox.core.remoteconfig.MeloXRemoteConfigConsent
import com.lladlam.melox.core.remoteconfig.MeloXRemoteConfigSource
import com.lladlam.melox.core.remoteconfig.MeloXRemoteConfigRuntime
import com.lladlam.melox.core.remoteconfig.MeloXRemoteNotice
import com.lladlam.melox.core.remoteconfig.MeloXRemoteNoticeStore
import com.lladlam.melox.ui.legal.MeloXRemoteNoticeDialog
import com.lladlam.melox.ui.messages.MessagesScreen
import com.lladlam.melox.ui.podcast.MeloXPodcastScreen
import com.lladlam.melox.ui.cloud.MeloXCloudMusicScreen
import com.lladlam.melox.ui.discovery.MeloXExploreScreen
import com.lladlam.melox.ui.discovery.MeloXHomeScreen
import com.lladlam.melox.ui.controls.LocalMeloXBackdrop
import com.lladlam.melox.ui.controls.MeloXSymbol
import com.lladlam.melox.ui.controls.MeloXSymbolIcon
import com.lladlam.melox.ui.controls.meloXIconDrawingScale
import com.lladlam.melox.ui.controls.MeloXSymbolVariant
import com.lladlam.melox.ui.controls.MeloXSystemColors
import com.lladlam.melox.ui.controls.MeloXDialog
import com.lladlam.melox.ui.controls.MeloXButton
import com.lladlam.melox.ui.controls.MeloXButtonStyle
import com.lladlam.melox.ui.theme.isMeloXDarkTheme
import com.lladlam.melox.ui.animation.BottomBarCollapseSpec
import com.lladlam.melox.ui.animation.BottomBarExpandSpec
import com.lladlam.melox.ui.animation.MeloXSprings
import com.lladlam.melox.ui.animation.NavExpandLeftShare
import com.lladlam.melox.ui.animation.meloXContentEnter
import com.lladlam.melox.ui.animation.meloXContentExit
import com.lladlam.melox.ui.animation.MeloXPlayerRecede
import com.lladlam.melox.ui.animation.meloXPageEnter
import com.lladlam.melox.ui.animation.meloXPageExit
import com.lladlam.melox.ui.animation.sprungFrac
import com.lladlam.melox.ui.controls.BOTTOM_GLASS_SURFACE_ALPHA
import com.lladlam.melox.ui.controls.bottomGlassFallbackColor
import com.lladlam.melox.ui.controls.finiteOrZero
import com.lladlam.melox.ui.controls.meloXBackdrop
import com.lladlam.melox.ui.controls.publicdemo.PublicDampedDragAnimation
import com.lladlam.melox.ui.controls.publicdemo.PublicInteractiveHighlight
import com.lladlam.melox.ui.layout.rememberMeloXWindowInfo
import com.lladlam.melox.ui.layout.MeloXDetailPageOwner
import com.lladlam.melox.ui.layout.MeloXDetailPaneState
import com.lladlam.melox.ui.layout.MeloXListDetailLayout
import com.lladlam.melox.ui.layout.MeloXPositionedChrome
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.animateContentSize
import com.lladlam.melox.ui.layout.meloXUsesListDetail
import androidx.compose.ui.platform.LocalWindowInfo
import com.lladlam.melox.ui.player.MeloXImmersivePlaybackEffect
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lladlam.melox.ui.player.MeloXIOSMiniPlayer
import com.lladlam.melox.ui.player.MeloXProviderLyricsLoader
import com.lladlam.melox.ui.player.MeloXIOSNowPlayingSharedHost
import com.lladlam.melox.ui.player.MeloXPlayerSinkDiag
import com.lladlam.melox.ui.player.meloXPlayerRecedeSpec
import com.lladlam.melox.ui.player.meloXPlayerTweenDurationMillis
import com.lladlam.melox.ui.player.rememberMeloXDisplayCornerPx
import com.lladlam.melox.ui.player.rememberMeloXDisplayCornerInfo
import com.lladlam.melox.ui.player.MeloXSmoothCorner
import com.lladlam.melox.ui.player.MeloXPlayerRecedeCompatFallbackDp
import com.lladlam.melox.ui.player.meloXPlayerTransitionDurationMillis
import com.lladlam.melox.ui.player.rememberMeloXPlaybackUiState
import com.lladlam.melox.ui.player.MeloXAudioOutputDialog
import com.lladlam.melox.ui.provider.ProviderExploreScreen
import com.lladlam.melox.ui.provider.ProviderHomeScreen
import com.lladlam.melox.ui.provider.ProviderLibraryScreen
import com.lladlam.melox.ui.provider.ProviderSearchScreen
import com.lladlam.melox.ui.provider.ProviderSettingsHub
import com.lladlam.melox.ui.recognition.MeloXRecognitionOverlay
import com.lladlam.melox.ui.provider.MeloXProviderServicesActivity
import com.lladlam.melox.ui.search.SearchScreen
import com.lladlam.melox.ui.search.MeloXSearchLaunchBus
import com.lladlam.melox.ui.settings.MeloXSettingsDetailActivity
import com.lladlam.melox.ui.settings.MeloXSettingsPreferences
import com.lladlam.melox.ui.settings.MeloXSettingsRuntime
import com.lladlam.melox.core.network.MeloXSearchKind
import com.lladlam.melox.core.network.NeteaseClipboardLink
import com.lladlam.melox.core.network.NeteaseClipboardTarget
import com.lladlam.melox.core.library.NeteaseLibraryClient
import com.lladlam.melox.core.update.MeloXRelease
import com.lladlam.melox.core.update.MeloXUpdateClient
import com.lladlam.melox.playback.PlaybackCommands
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.roundToInt

enum class AppTab(@StringRes val titleRes: Int) {
    Home(R.string.tab_home),
    Explore(R.string.tab_explore),
    Library(R.string.tab_library),
    Podcasts(R.string.tab_podcasts),
    Downloads(R.string.tab_downloads),
    Cloud(R.string.tab_cloud),
    Settings(R.string.tab_settings),
    Search(R.string.tab_search),
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MeloXApp(
    openNowPlayingRequest: Int = 0,
    clipboardLinkRequest: String? = null,
    onClipboardLinkConsumed: () -> Unit = {},
    playbackConnectionEnabled: Boolean = true,
) {
    val hostContext = LocalContext.current
    DisposableEffect(hostContext) {
        val hostId = System.identityHashCode(hostContext)
        if (com.lladlam.melox.BuildConfig.DEBUG) {
            android.util.Log.i("MeloXLifecycle", "MeloXApp mounted host=$hostId")
        }
        onDispose {
            if (com.lladlam.melox.BuildConfig.DEBUG) {
                android.util.Log.i("MeloXLifecycle", "MeloXApp disposed host=$hostId")
            }
        }
    }
    val context = hostContext.applicationContext
    val detailPaneState = (hostContext as? MeloXDetailPageOwner)?.detailPaneState
        ?: remember { MeloXDetailPaneState() }
    val listDetailMode = meloXUsesListDetail(LocalWindowInfo.current.containerSize, MeloXSettingsRuntime.splitScreenEnabled)
    var selectedSource by remember {
        mutableStateOf(MusicProviderSelectionStore.selectedSource(context))
    }
    val sourceSelectionRevision by MusicProviderSelectionStore.changes.collectAsState()
    var selectedSourceId by remember { mutableStateOf(MusicProviderSelectionStore.selectedSourceId(context)) }
    LaunchedEffect(sourceSelectionRevision) {
        selectedSourceId = MusicProviderSelectionStore.selectedSourceId(context)
        selectedSourceId.builtIn?.let { selectedSource = it }
    }
    val initialTab = remember(context) {
        runCatching {
            AppTab.valueOf(
                if (MeloXSettingsRuntime.rememberLastTab) {
                    MeloXSettingsPreferences.string(context, "general_last_tab", MeloXSettingsRuntime.defaultTab)
                } else MeloXSettingsRuntime.defaultTab,
            )
        }.getOrDefault(AppTab.Home)
    }
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }
    var messagesVisible by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = messagesVisible) { messagesVisible = false }
    var recognitionVisible by rememberSaveable { mutableStateOf(false) }
    fun launchNeteaseLogin(returnTab: AppTab) {
        detailPaneState.openLogin(hostContext) { selectedTab = returnTab }
    }
    val haptics = LocalHapticFeedback.current
    // 底栏 tab 选中类触感（轻 tick）。
    val tabHaptic = meloXHaptic()
    var libraryModalVisible by remember { mutableStateOf(false) }
    var onboardingLoginRequested by rememberSaveable { mutableStateOf(false) }
    var onboardingPage by rememberSaveable {
        mutableStateOf(if (MeloXSettingsPreferences.boolean(context, "onboarding_completed", false)) -1 else 0)
    }
    var availableUpdate by remember { mutableStateOf<MeloXRelease?>(null) }
    val remoteConfigStatus by MeloXRemoteConfigRuntime.status.collectAsState()
    var pendingRemoteNotice by remember { mutableStateOf<MeloXRemoteNotice?>(null) }
    var cloudControlChoicePending by remember {
        mutableStateOf(!MeloXRemoteConfigConsent.choiceMade(context))
    }
    LaunchedEffect(remoteConfigStatus, cloudControlChoicePending) {
        pendingRemoteNotice = remoteConfigStatus.config.notice?.takeIf {
            !cloudControlChoicePending &&
                MeloXRemoteConfigConsent.enabled(context) &&
                remoteConfigStatus.source == MeloXRemoteConfigSource.VerifiedRemote &&
                MeloXRemoteNoticeStore.shouldShow(context, it)
        }
    }
    LaunchedEffect(Unit) {
        if (MeloXSettingsPreferences.boolean(context, "onboarding_completed", false)) {
            withContext(Dispatchers.IO) {
                runCatching { KugouLiteDailyCheckIn.run(context) }
            }
        }
    }
    var heartModeLaunchAttempted by remember { mutableStateOf(false) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        selectedSourceId = MusicProviderSelectionStore.selectedSourceId(context)
        val persistedSource = MusicProviderSelectionStore.selectedSource(context)
        if (persistedSource != selectedSource) {
            selectedSource = persistedSource
            libraryModalVisible = false
            heartModeLaunchAttempted = false
        }
    }
    val playbackState = rememberMeloXPlaybackUiState(connectionEnabled = playbackConnectionEnabled)
    val playerTransitionState = rememberSaveable(
        saver = androidx.compose.runtime.saveable.Saver<SeekableTransitionState<Boolean>, Boolean>(
            save = { it.targetState },
            restore = { SeekableTransitionState(it) },
        ),
    ) { SeekableTransitionState(false) }
    val playerTransition = rememberTransition(
        transitionState = playerTransitionState,
        label = "melox-player-transition",
    )
    // ── 后方页面下沉的主时间线（Apple Music 口径，2026-09-28）────────────────
    //   与全屏播放器的外壳 morph / 内容层**同源**（同一个 SeekableTransitionState、
    //   同一条 `tween(D, MeloXPlayerAppleEasing)`）⇒ 三者全为**单层**缓动、
    //   逐帧严格相等；手势 seekTo 拖拽时页面缩放与外壳 morph 也严格同拍。
    //   只喂 graphicsLayer（draw 期），缩放不触发重排。
    //   ⚠⚠ 2026-10-04 两次教训（详见 `MeloXPlayerTransitionSpec.kt` 文件头
    //   「三条时间线的同源铁律」）：本 spec 与 master / 几何 spec **必须同族**。
    //   中间试过把三处一起换成 ζ=1 弹簧（想「统一」）⇒ 反而让**全链双层**、
    //   95% 提前到 101ms、尾段空转 139ms，用户报「效果更差」，已整条回退到线性 tween。
    val playerRootFraction by playerTransition.animateFloat(
        transitionSpec = { meloXPlayerRecedeSpec(initialState, targetState) },
        label = "melox-player-root-recede",
    ) { open -> if (open) 1f else 0f }

    // ★ 播放器下沉弹簧 —— **双拍节奏**（2026-10-06，与底栏 tab 同款表现形式）：
    //   拍 1（按下/松手瞬间，`Confirm` 中等）："啪"——确认已激活下沉；
    //   拍 2（弹簧落定，`LongPress` 最重）："咚"——重重落定。
    //   ⚠ 只改表现形式，不改强度；两条下沉路径（系统返回 / 下拉手势收起）共用。
    //   master spec 是 `playerSettleMasterSpec()`（spring ζ=1，stiffness 由设置时长反解），
    //   ζ=1 = 临界阻尼 ⇒ 无过冲，fraction 单调趋近 target；落定 ≈ 设置时长，故延迟该时长后拍 2。
    val playerSpringThumpEngage = rememberSpringThump(HapticFeedbackType.Confirm)
    val playerSpringThumpLand = rememberSpringThump(HapticFeedbackType.LongPress)
    // ★ 打开播放页的触感 = **连续双拍**（2026-10-15 第二轮定案）。
    //
    // 原版的「双拍」真身：**`MeloXIOSLyricsPanel` 首次挂载**时，列表边沿观察者
    //   `snapshotFlow{ canScrollForward to canScrollBackward }` 对**两个条件各震一记**
    //   `LongPress`；挂载瞬间列表停在 (0,0)、两者皆 `false` ⇒ **同一帧连发两记**
    //   ⇒ 系统顺序播放出两下 = 用户听到的「双拍」。
    //   面板在旧口径下于**转场开始**就组合 ⇒ 这两下恰好落在点击瞬间；
    //   2026-10-15「常驻重层延后组合」把面板首帧组合推到转场结束 ⇒ 整体后移
    //   ⇒ 用户报「点击瞬间有、性能版打开完之后才有」。
    //
    // ⚠⚠ 修法要点（踩过两次）：
    //   原版这两下是**紧挨着**的（同帧 / 相隔一两帧，用户听成「连续的」），
    //   **不是**「点击一拍 + 展开落定一拍」——落定在数百毫秒之后，用户会直接判成「不连续」。
    //   ⇒ 这里显式复刻：两记 `LongPress` 都钉在**点击帧**、只隔约 60ms，
    //     不再依赖歌词面板的挂载时机与它的条件个数。力度沿用 `LongPress`（与原版一致，不改成 Confirm）。
    //   误触发已在 `MeloXIOSLyricsPanel` 侧去掉（初值 `true`→`null`、跳过首次发射），保证不叠成三下。
    val playerOpenThump = rememberSpringThump(HapticFeedbackType.LongPress)

    // 下沉进度（0 静止 / 1 完全沉下）。**只在 draw 期调用**（graphicsLayer /
    // drawBehind / drawWithContent 的 lambda 里读 playerRootFraction）⇒ 每帧只重绘，
    // 不重组 MeloXApp 这棵大树、也不重排页面。
    // ⚠ **线性映射，不再套 smoothStep**（2026-09-28 三次定档）：套窗口会让下沉提前
    //   14% 停住、而外壳还在动 ⇒ 用户报障「没有跟动画同步」。线性 + [ScaleStart]=0 /
    //   [ScaleEnd]=1 时 f ≡ playerRootFraction，与外壳 morph 是**同一条** master 曲线。
    //   ⚠ 关闭方向的过冲回弹现在由同一条 master spec（iOS 回弹曲线）直接给出，f 会略为负。
    val recedeMasterFraction: () -> Float = {
        if (MeloXSettingsRuntime.reduceMotion) {
            0f
        } else {
            val span = (MeloXPlayerRecede.ScaleEnd - MeloXPlayerRecede.ScaleStart)
                .takeIf { it > 0f } ?: 1f
                    // ⚠ 只裁上界（1）：关闭过冲时 f<0（页面缩放 >1、被屏裁掉）必须放行，正是回弹来源。
                    ((playerRootFraction - MeloXPlayerRecede.ScaleStart) / span).coerceAtMost(1f)
        }
    }
    var playerArtworkPageUsesScale by remember { mutableStateOf(false) }
    // ── 系统屏幕圆角（Apple Music 口径，2026-09-28 十次定档）────────────────
    //   页面与播放器外壳**共用同一个值**（`rememberMeloXDisplayCornerInfo()`，内部轮询到
    //   真取到为止）。用户要求「再次确认获取系统圆角」⇒ debug 包在屏幕上打读数
    //   （runtime = WindowInsets 原始读数，used = 实际渲染采用值），release 不打。
    //   真值参考（iQOO 15）：`dumpsys window` 的 `mRoundedCorners radius=184`（四角同值）。
    val density = LocalDensity.current
    val displayCorner = rememberMeloXDisplayCornerInfo()
    val displayCornerPx = displayCorner.usedPx
    val playerScope = rememberCoroutineScope()
    var playerTransitionJob by remember { mutableStateOf<Job?>(null) }
    // ★ 2026-10-15 配套「转场可打断」：**关闭落定那记「咚」的待发任务**。
    //   关闭若被「点按抢回播放页」（或任何新的主状态变更）打断，这条延迟任务**必须撤销** ——
    //   否则它会带着旧计时在**打开途中**炸出一记无主的「咚」（听感上就是打开时莫名多了一下）。
    var playerSettleThumpJob by remember { mutableStateOf<Job?>(null) }
    // ⚠⚠⚠ 2026-10-04 修「下沉回弹偶尔消失」：`BoundsTransform` 生成 spec 时会**实时读取**
    //   方向判据（`targetState == PostExit`），而读的那一刻与动画起跑的那个合成帧之间并没有
    //   顺序保证 ⇒ 偶尔读到未翻转的值 ⇒ 那一轮 keyframes 被退化成普通 tween ⇒ **回弹丢失**。
    //   ⇒ 这里在**动画启动之前**同步写下一个方向 intent（「本次主状态推进是关闭方向吗」），
    //     保证任何一次 bounds spec 求值都能读到确定的方向 —— 不依赖 targetState 的翻转时机。
    //   ⚠ 必须在**每个**主状态变更点都写（openPlayer / closePlayer / intent 打开 /
    //     手势 seek 收起 / 手势释放 settle），漏一处就会留一条漏回弹的路径。
    var playerClosingIntent by remember { mutableStateOf(false) }
    // ★ 2026-10-15 修「打断动画期间点底栏误触抢回」：mini 播放器在 window 中的命中矩形，
    //   由 `MeloXIOSMiniPlayer` 实时上报、供 `MeloXIOSNowPlayingSharedHost` 限定 reopen 触发范围。
    val miniPlayerHitRect = remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    // ⚠⚠⚠ 2026-10-06：`expansionProgress`（内容层进度）的**动画目标值**镜像 ——
    //   「本次推进的**目标方向**是打开吗」。**必须在 `animateTo / seekTo` 之前同步写**。
    //   为什么不能直接用 `playerTransitionState.targetState`：它的更新晚于动画启动
    //   （实测 settle 后内容层仍朝关闭侧跑了约 **77ms**：p 0.804 → 0.661 → 回升，
    //     而同期 bounds 的 f 已从 0 正常爬回 1）⇒ 那记反向就是用户看到的「下沉」。
    //   为什么不能直接用 `closingIntent`：拖拽期间它恒为 false（不变化）⇒ 动画被判
    //   「已完成」⇒ **不参与 `SeekableTransitionState` 的 seek** ⇒ 拖拽完全不跟手
    //   （v1 实测 p 恒 1.000）。所以拖拽时这里必须是 **false**（相对上一稳态的 true
    //   产生一次目标变化，动画才会保持活跃、才会被 seek 驱动）。
    //   取值：
    //   · 拖拽中   → false（朝关闭；同时提供那次"目标变化"以保住 seek 跟手）
    //   · 回弹/打开 → true （目标恒 1f ⇒ 松手后不再朝 0 跑）
    //   · 松手收起 → false（目标 0f）
    var playerTransitionTargetOpen by remember { mutableStateOf(true) }
    val clipboardTarget = remember(clipboardLinkRequest, selectedSource) {
        if (selectedSource == MusicSource.Netease) {
            clipboardLinkRequest?.let(NeteaseClipboardLink::parse)
        } else {
            null
        }
    }
    val openPlayer: () -> Unit = {
        if (playbackState.hasMedia) {
            MeloXPlayerSinkDiag.event("open")
            // ★ 打开触感 = **连续双拍**（复刻原版「歌词面板首次挂载时同帧连发两记 LongPress」）：
            //   两记都钉在**点击瞬间**、只隔约 60ms —— ⚠ 不是「拍 1 在点击、拍 2 在落定」。
            //   说明见上方 `playerOpenThump` 声明处。
            //   60ms = 干净的双击节奏（≲20ms 会被听成一记，间隔过大则成「不连续」）。
            playerOpenThump()                     // 双拍 ①
            playerScope.launch {
                kotlinx.coroutines.delay(60L)
                playerOpenThump()                 // 双拍 ②（紧接，连续）
            }
            playerClosingIntent = false
            playerTransitionTargetOpen = true
            // ★ 转场可打断：本次是「打开」（含关闭途中被点按抢回）⇒ 撤销关闭那条尚未发出的
            //   落定「咚」，避免它在打开途中多震一记。
            playerSettleThumpJob?.cancel()
            playerSettleThumpJob = null
            playerTransitionJob?.cancel()
            playerTransitionJob = playerScope.launch {
                playerTransitionState.animateTo(
                    targetState = true,
                    // ⚠ 2026-10-04 二次回退：与关闭**同一函数**（线性 tween master）⇒
                    //   打开/关闭互为时间镜像，且全链只有**一层** `MeloXPlayerAppleEasing`。
                    //   试过改成 ζ=1 弹簧（当时为修「打开太线性」）⇒ 几何/圆角/内容/背景页
                    //   全部变成双层，95% 提前到 101ms、尾段空转 139ms ⇒ 用户报「效果更差」。
                    animationSpec = playerOpenFractionSpec(),
                )
            }
        }
    }
    val closePlayer: () -> Unit = {
        MeloXPlayerSinkDiag.event("close")
        playerClosingIntent = true
        playerTransitionTargetOpen = false
        playerTransitionJob?.cancel()
        // ★ 双拍下沉触感（与底栏同款）：拍 1 立即「啪」激活，拍 2 落定时「咚」。
        //   单独开一条 coroutine 跑延迟的拍 2，不阻塞动画启动。
        //   spring ζ=1，99% 落定 ≈ 设置时长。延迟设置时长后震落定拍。
        playerSpringThumpEngage()                  // 拍 1：返回/关闭瞬间「啪」——激活下沉
        playerSettleThumpJob?.cancel()
        playerSettleThumpJob = playerScope.launch {
            val settleMs = meloXPlayerTweenDurationMillis().toLong()
            kotlinx.coroutines.delay(settleMs)
            playerSpringThumpLand()                // 拍 2：弹簧落定「咚」——落定
        }
        playerTransitionJob = playerScope.launch {
            playerTransitionState.animateTo(
                targetState = false,
                // ⚠⚠ 2026-10-06 **当前口径**：master = [playerSettleMasterSpec]
                //   （spring ζ=1，stiffness 由**设置时长**反解）—— 四条路径同源，
                //   理由与历史两次回退的辨析见 [playerOpenFractionSpec] 的 KDoc。
                //   ⚠ **别写死 stiffness**：`spring` 不读设置时长 ⇒ 时长设置会**完全失效**
                //     （用户报障「播放页打开动画设置…调节没反应」）。
                //   ⚠ 关闭尾段的下沉窗口长度仍由 `MeloXPlayerSinkWindowStart` 决定
                //     （纯常量），**不要**再拿 master 曲线去补偿。
                animationSpec = playerCloseFractionSpec(),
            )
        }
    }
    // ★ 帧率：播放页转场（morph）**全程锁定高刷**（2026-10-16 修「120-60-120」）。
    //   症状：点开 / 收起播放页，帧率 120→60→120，中段掉到 60Hz。
    //   根因（已定位）：morph 在 `SharedTransitionLayout` 的 overlay 里飞行，主 decor
    //   那段不合成，面板会把 `setFrameRate(120)` 请求丢回 60Hz；而 `applyIntent` 有
    //   「意图不变则不下发」早退 —— 意图一直 High ⇒ 早退把重钉请求吞了，轮询再也钉不回去，
    //   只能等意图先掉 Low 再回 High 才重新生效 ⇒ 120-60-120。
    //   修法：转场进行中（`currentState != targetState`）置控制器级
    //   `playerTransitionActive=true` —— 强制 `desiredIntent=High` 且**绕过早退**每 tick
    //   重发 `setFrameRate(120)`；落定即关，交回常规判据。循环内每拍重断言 true，
    //   抗「快速重开导致旧 effect finally 把标志误关」的竞态。
    //   ⚠ 只读布尔 `currentState/targetState`，**绝不读 `.fraction`**（根 composable
    //   读 fraction 会触发全树重组抖动 —— 见文件头铁律）。
    val neteaseSession = rememberNeteaseSessionStore()
    val rootPageState = rememberSaveableStateHolder()
    // Page glass samples a stable background layer. The page itself is
    // recorded separately for the bottom chrome, so neither layer samples
    // its own controls and HWUI never enters a recursive RenderNode graph.
    val pageBackdrop = rememberLayerBackdrop()
    val bottomChromeBackdrop = rememberLayerBackdrop()
    // ⚠ 全屏播放器「封面背景」背板（2026-09-30）：声明在 MeloXApp 层，由
    //   MeloXIOSNowPlayingSharedHost 录制（其内 .layerBackdrop 节点）、供迷你栏采样。
    //   关闭播放页时迷你栏玻璃按 expansionProgress 在「封面背板 ↔ 主页背板」间交叉淡入，
    //   消除关闭末帧「封面色 → 液态玻璃」硬切。详见 MeloXIOSMiniPlayer。
    val playerControlBackdrop = rememberLayerBackdrop()

    LaunchedEffect(playbackConnectionEnabled) {
        if (!playbackConnectionEnabled) return@LaunchedEffect
        if (MeloXSettingsPreferences.boolean(context, "update_auto_check", true)) {
            val now = System.currentTimeMillis()
            val last = MeloXSettingsPreferences.string(context, "update_last_check_ms", "0").toLongOrNull() ?: 0L
            if (now - last >= 24L * 60L * 60L * 1000L) {
                val client = MeloXUpdateClient(context)
                runCatching { client.latestStableRelease() }.getOrNull()?.let { release ->
                    MeloXSettingsPreferences.setString(context, "update_last_check_ms", now.toString())
                    if (client.isNewer(release.version, BuildConfig.VERSION_NAME)) availableUpdate = release
                }
            }
        }
    }

    val currentListDetailMode by rememberUpdatedState(listDetailMode)

    LaunchedEffect(openNowPlayingRequest, playbackState.hasMedia) {
        if (openNowPlayingRequest > 0 && playbackState.hasMedia) {
            playerClosingIntent = false
            playerTransitionTargetOpen = true
            // ★ 转场可打断：同 `openPlayer` —— 撤销关闭那条待发的落定「咚」。
            playerSettleThumpJob?.cancel()
            playerSettleThumpJob = null
            playerTransitionState.animateTo(
                targetState = true,
                animationSpec = playerOpenFractionSpec(),
            )
        }
    }

    LaunchedEffect(clipboardLinkRequest, clipboardTarget, selectedSource) {
        if (clipboardLinkRequest != null && clipboardTarget == null) onClipboardLinkConsumed()
    }

    var hasConnectedMedia by remember { mutableStateOf(false) }
    LaunchedEffect(playbackState.hasMedia) {
        if (playbackState.hasMedia) hasConnectedMedia = true
        // Initial service reconnection has an empty UI snapshot. It must not
        // close a restored player before the real queue/metadata arrives.
        if (!playbackState.hasMedia && hasConnectedMedia) {
            playerTransitionState.snapTo(false)
        }
    }

    LaunchedEffect(
        playbackState.currentIndex,
        playbackState.queue.map { it.mediaId },
        MeloXSettingsRuntime.automaticLyricSelectionEnabled,
    ) {
        if (playbackState.hasMedia) {
            MeloXProviderLyricsLoader.preloadQueue(context, playbackState, count = 2)
        }
    }

    LaunchedEffect(neteaseSession.cookie, playbackConnectionEnabled) {
        if (playbackConnectionEnabled && neteaseSession.isLoggedIn) {
            neteaseSession.refreshProfile()
        }
    }

    LaunchedEffect(
        selectedSource,
        neteaseSession.cookie,
        onboardingPage,
        MeloXSettingsRuntime.startsHeartModeOnLaunch,
        playbackState.hasMedia,
        playbackConnectionEnabled,
    ) {
        if (!playbackConnectionEnabled || selectedSource != MusicSource.Netease || heartModeLaunchAttempted ||
            onboardingPage >= 0 || playbackState.hasMedia ||
            !MeloXSettingsRuntime.startsHeartModeOnLaunch || !neteaseSession.isLoggedIn
        ) return@LaunchedEffect
        heartModeLaunchAttempted = true
        if (neteaseSession.profile == null) neteaseSession.refreshProfile(force = true)
        val userId = neteaseSession.profile?.userId ?: return@LaunchedEffect
        val client = NeteaseLibraryClient(cookieProvider = { NeteaseSessionStore.readCookie(context) })
        val songs = runCatching {
            val snapshot = client.snapshot(userId)
            val seed = snapshot.likedSongs.randomOrNull() ?: error("收藏歌曲为空")
            val playlist = snapshot.playlists.firstOrNull() ?: error("没有可用歌单")
            client.intelligenceModeSongs(seed.id, playlist.id)
        }.getOrNull().orEmpty()
        PlaybackCommands.playAll(context, songs, heartMode = true)
    }

    var previousSelectedTab by rememberSaveable { mutableStateOf(selectedTab) }
    LaunchedEffect(selectedTab) {
        previousSelectedTab = selectedTab
        if (selectedTab != AppTab.Library) libraryModalVisible = false
        if (MeloXSettingsRuntime.rememberLastTab) {
            MeloXSettingsPreferences.setString(context, "general_last_tab", selectedTab.name)
        }
    }

    val visibleRootTabs = (if (selectedSource == MusicSource.Bilibili) {
        listOf(AppTab.Library, AppTab.Settings)
    } else MeloXSettingsRuntime.tabOrder.mapNotNull { runCatching { AppTab.valueOf(it) }.getOrNull() })
        .filter {
            when (it) {
                AppTab.Home -> MeloXSettingsRuntime.homeTabEnabled
                AppTab.Explore -> MeloXSettingsRuntime.exploreTabEnabled
                AppTab.Library -> MeloXSettingsRuntime.libraryTabEnabled
                AppTab.Podcasts -> MeloXSettingsRuntime.podcastsEnabled && MeloXSettingsRuntime.podcastsTabPlacement
                AppTab.Downloads -> MeloXSettingsRuntime.downloadsEnabled && MeloXSettingsRuntime.downloadsTabPlacement
                AppTab.Cloud -> MeloXSettingsRuntime.cloudMusicEnabled && MeloXSettingsRuntime.cloudTabPlacement
                AppTab.Settings -> true
                AppTab.Search -> false
            }
        }.let { if (AppTab.Settings in it) it else it + AppTab.Settings }
    LaunchedEffect(visibleRootTabs, selectedTab) {
        if (selectedTab !in visibleRootTabs && selectedTab != AppTab.Search) {
            selectedTab = visibleRootTabs.first()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
      // This is intentionally a small, stable source layer. It gives every
      // page control the same optical backdrop without recording the control
      // into the layer it samples.
      Box(
          modifier = Modifier
              .fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .layerBackdrop(pageBackdrop),
      )
      CompositionLocalProvider(LocalMeloXBackdrop provides pageBackdrop) {
        CompositionLocalProvider(LocalMeloXBackdrop provides playerControlBackdrop) {
            MeloXAudioOutputDialog(
                visible = playbackState.showOutputPicker,
                onDismiss = { playbackState.showOutputPicker = false },
                state = playbackState,
            )
        }
        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
            val sharedScope = this
            val fullPlayerVisible = playbackState.hasMedia &&
                (playerTransitionState.currentState || playerTransitionState.targetState)

            // ── 下沉进度（2026-09-29 改用 iOS 曲线，外壳与背景同拍）──────────────
            //   打开 / 拖拽跟手 / 关闭：**全部吃同一条 master 时间线**
            //   （`meloXPlayerRecedeSpec`：打开 = iOS present 曲线，关闭 = iOS 回弹曲线
            //   easeOutBack）。⇒ 播放器外壳 morph 与后方页面下沉**严格同拍**，关闭过冲
            //   （进度短暂为负、页面缩放 >1 被屏裁掉）也由同一条 spec 自然给出，不再用
            //   单独弹簧（那会让外壳与背景在关闭方向不同步）。
            //   ⚠ 这里**不能 coerce 到 [0,1]**：关闭过冲时 f 略小于 0（见
            //     `recedeMasterFraction`），裁掉即可，正是「页面往边缘扩的过冲回弹」。
            val recedeFraction: () -> Float = {
                if (MeloXSettingsRuntime.reduceMotion) 0f else recedeMasterFraction()
            }

            // ⚠ 退让层的圆角**必须走实例缓存**，不能每帧 `MeloSmoothCornerShape(...)`：
            //   `graphicsLayer` 的 lambda **每帧执行**，而该 Shape 没实现 `equals`
            //   ⇒ 每帧喂进去的都是"新"形状 ⇒ `RenderNode.setOutline` 按引用判等必然标脏
            //   ⇒ 这一层全屏的路径裁剪每帧重建重栅格化（`MeloXPageTransitions` 里
            //   `MeloXSlideShapeCache` 的注释给的是同一套论证，这里只是把它补到
            //   当时没覆盖到的调用点上）。本处是 **sx ≠ sy** 的分轴缩放 ⇒ 用 `ofXY`。
            val recedeShapeCache = remember { MeloXSlideShapeCache() }

            // 横屏播放器默认沉浸：隐藏状态栏 + 底部导航小白条（systemBars）；
            // 竖屏只藏顶栏（statusBars），且仍走「沉浸式播放」设置开关，底栏保持可见。
            val playerWindow = rememberMeloXWindowInfo()
            MeloXImmersivePlaybackEffect(
                enabled = fullPlayerVisible &&
                    (MeloXSettingsRuntime.immersivePlaybackEnabled || playerWindow.isLandscape),
                type = if (playerWindow.isLandscape) {
                    WindowInsetsCompat.Type.systemBars()
                } else {
                    WindowInsetsCompat.Type.statusBars()
                },
            )

            // ── 后页面下沉（Apple Music 口径，2026-09-28 三次定档）──────────────
            //   ① 下沉舞台：页面缩小后露出的外圈**必须是黑色**（用户报障：「我要的是下沉
            //      的背景页外面是黑色而不是白色」）。浅色主题下铺 `colorScheme.background`
            //      （白）会让整块下沉根本读不出来 —— 白页缩在白底上等于没动。
            //      做法：先铺主题背景色（静止时零观感变化），再按下沉进度把黑压上去，
            //      且**快速到位**（smoothStep t:0→0.35 就全黑）—— 空隙刚开始露出来时
            //      它就已经是黑的了，不会看到「灰→黑」的过渡灰边。
            //   ② 页面向中心缩小 + 下沉，四角浮出与播放器**同一条曲线**的系统圆角，
            //      再压暗一档。全部只走 draw 期（drawBehind / graphicsLayer），
            //      每帧只重绘、不重组、不重排。
            //   ⚠ 只缩页面层，不缩底部 chrome —— chrome 里挂着 MiniPlayer 的
            //     sharedBounds 源，祖先缩放会把 morph 几何带歪。
            val recedeBackdropColor = MaterialTheme.colorScheme.background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val t = recedeFraction()
                        // ⚠ **页面外侧全程纯黑**（用户报障：「背景页最靠近屏幕时，边缘会变白，
                        //   正确应该是背景页外面全程是黑的」）。
                        //   之前是 `lerp(背景色, 黑, smoothStep(t, 0, 0.35))` —— 前 35% 其实是
                        //   「半黑的浅色」，而页面刚离开满屏那一刻露出的缝正是这一段 ⇒ 发白。
                        //   现在只在**完全没有下沉**时铺主题背景色（此刻页面满屏、舞台不可见，
                        //   免得切换 tab 时 AnimatedContent 的淡入淡出透出黑底），
                        //   一旦 t>0 立刻切纯黑。
                        drawRect(if (t <= 0.0005f) recedeBackdropColor else Color.Black)
                    },
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(if (libraryModalVisible && selectedTab == AppTab.Library && !fullPlayerVisible) 15f else 0f)
                    // ══ 底栏玻璃背板的**录制点**（2026-09-30 六次修，本轮定档）══════════════
                    //   `bottomChromeBackdrop` 只给底栏胶囊 / 迷你栏 / 搜索键 / 水珠采样。
                    //   录制节点（`Modifier.layerBackdrop`）就是**这个 Box**，
                    //   且**必须**挂在 recede `graphicsLayer` **之前**（= 外层）。
                    //
                    //   ── 唯一不变式（记住这一条就够）─────────────────────────────────
                    //   录制内容所在的**空间** 必须 = 采样映射所在的**空间**。
                    //   采样映射由 `LayerBackdrop.drawBackdrop` 给出（反编译 2.0.0）：
                    //     `p = layerCoordinates.localPositionOf(drawingCoords, Zero)`
                    //     → `translate(-p.x, -p.y)` → `drawLayer(graphicsLayer)`
                    //   也就是「把录制节点的本地原点摆回它在屏幕上的物理位置」。
                    //   而 `recordLayer(...)` 录进去的，是 `drawContent()` 在该节点
                    //   **本地空间**里的绘制结果。
                    //   ⇒ 只有让录制点坐在所有变换的**外面**，本地空间才等于屏幕空间，
                    //     「录到的内容」与「采样查表的位置」才指向同一张图。
                    //
                    //   ── 挂在里面（旧做法：挂在 Scaffold 上）为什么会**渲染内容跳变** ──
                    //   `graphicsLayer` 的缩放/圆角/裁切只作用在**它自己的 RenderNode** 上，
                    //   节点本地空间的绘制结果**不含**这个变换：
                    //     · 录制内容 = **未缩放**的页面；
                    //     · 而 `p` 一旦沿路穿过 recede 层，描述的就是**屏幕上**的位置。
                    //   ⇒ 玻璃显示的永远是「未缩放页面」的一个切片，与它背后真实（缩放过的）
                    //     画面错开，错位量 ∝ 当前 recede 进度（底栏离屏幕中心 ~1400px，
                    //     f=1 时纵向差 50~70px、横向在屏幕左右缘可达 80px）。
                    //   ⇒ 而**录制何时刷新**只取决于该 draw 节点是否失效 ⇒ 与进度无关。
                    //     每次刷新都会把玻璃内容从「上一次刷新的错位切片」切成「这一次的」，
                    //     于是表现为**内容整体跳变**（用户报障线：「不是颜色问题…是渲染的内容跳变」）。
                    //   真机 60fps 逐帧取证（`fix1.mp4`，3 次开合循环）× 观感一致：
                    //     · 跳变只发生在**有结构的区域** —— 底栏下缘 y≈2940~3066、
                    //       横向 x89~518 与 x1168~1327（首页胶囊 / 搜索键，背后压着页面大字），
                    //       而 x519~1167（平滑区）与胶囊上半**逐像素为 0**；
                    //     · 同一帧内**几何完全没动**（平移搜索 best dx=dy=0）、
                    //       红/白前景峰值不变、平滑区亮度不变
                    //     ⇒ 铁证：不是底栏自身的材质/高光/几何在动（那样四个 tab 会一起动），
                    //       而是**玻璃采样到的背板内容**在动。
                    //     · 时刻：关闭动画结束（f481）后 ~5 帧（f486，t≈8.11s）——
                    //       即 `fullPlayerVisible` 翻转引发全量重绘、录制终于刷新那一帧。
                    //   ⇒ 与用户现场口径一致：「信息在底栏的上下边缘会出现跳变渲染，
                    //     在底栏的中间就没事」—— 中间背后是平滑区，错位看不出来。
                    //
                    //   ── 修法 ──────────────────────────────────────────────────────────
                    //   把录制点搬到 recede `graphicsLayer` **外面**（本 Box）。
                    //   此时 `p` 的计算路径上**不再有任何变换层**（recede 层在录制点内侧，
                    //   与玻璃是兄弟子树）⇒ 采样空间 ≡ 屏幕空间 ≡ 录制空间。
                    //   ⇒ 录制内容 = 屏幕上**真实看到的合成画面**，玻璃在任何进度、任何刷新
                    //     时机采到的都与背后画面逐帧一致 —— **内容跳变在结构上不可能发生**
                    //     （刷新从"事件"退化成"无观感差异的幂等操作"）。
                    //   附带修好三个老毛病：
                    //     ① 底栏 chrome 在**这个 Box 之外**（depth 与 recede Box 平级）
                    //        ⇒ 录制内容**不再包含玻璃自己**，去掉「玻璃采样含自身」的自反馈；
                    //     ② 页面压暗 scrim 在**这个 Box 之内** ⇒ 玻璃能正确显示转场中
                    //        压暗过的页面（以前录不到，玻璃偏亮）；
                    //     ③ 同一个 backdrop **只此一处**录制点（Scaffold 上那份已删）——
                    //        `LayerBackdropNode.onGloballyPositioned` 是**无条件覆盖**
                    //        `layerCoordinates`，挂两处 = 竞态。
                    //   ⚠ `drawBehind { recedeFraction() }` 在最外层：转场期间进度每帧变
                    //     ⇒ 这条 draw 链每帧失效 ⇒ 外层重跑 ⇒ 每帧重新 `recordLayer`
                    //     （静止时进度不变 ⇒ 不失效 ⇒ 零额外开销）。
                    //   ⚠ 透明边：f 较大时缩放后的页面盖不满屏（左右各 ~62px 空隙），
                    //     那一段录成透明 —— 但此时底栏已被不透明的全屏播放器完全盖住，
                    //     且 f 越小空隙越小，故无可见回归。
                    .drawBehind { recedeFraction() }
                    .layerBackdrop(bottomChromeBackdrop)
                    .graphicsLayer {
                        val f = recedeFraction()
                        val sx = lerp(1f, MeloXPlayerRecede.PageScaleX, f)
                        // 纵向比例由横向内缩反推：只有**可见内缩在 x/y 上相等**，
                        // 与屏幕同心/相切的圆弧才能同时贴住上下左右四条边、黑边才等宽。
                        // ⚠ 这里的「等宽」只管**缩放**：上下等宽随后被 PageSinkDp 的**平移**
                        //    刻意打破（AM 下沉口径，见 MeloXMotion 文档）——平移不改圆弧贴合，
                        //    两者不矛盾，别看到上下不等宽就回来"修"这里。
                        val syFull = 1f - (1f - MeloXPlayerRecede.PageScaleX) * size.width / size.height
                        val sy = lerp(1f, syFull, f)
                        scaleX = sx
                        scaleY = sy
                        // 向下沉降（AM 口径）：与缩放读**同一个** f ⇒ 与卡片推进严格同拍。
                        // 数值/推导见 `MeloXPlayerRecede.PageSinkDp`。
                        translationY = MeloXPlayerRecede.PageSinkDp.dp.toPx() * f
                        // 圆角 = **系统屏幕圆角**（全程恒定、不收），且用**平滑圆角**
                        // （Lamé 超椭圆、连续曲率）而不是正圆弧 —— 后者曲率在直边处跳变，
                        // 肉眼就是「圆弧生硬」。分轴补偿保证可见半径在 x/y 上都是 displayCornerPx。
                        // ⚠ 缩放先吸附到 1/64 步长（= ±1.6px @184px，肉眼不可辨）再进缓存：
                        //   否则 sx/sy 每帧都是新值，缓存永远 miss，等于没加。
                        shape = recedeShapeCache.ofXY(
                            displayCornerPx,
                            meloXQuantizedScale(sx),
                            meloXQuantizedScale(sy),
                        )
                        clip = f > 0.0005f
                    },
            ) {
            // ⚠⚠ 2026-09-29：**不再消费 status bar inset**，让每屏内容真正铺到 y=0
            //   （状态栏下直接透出页面内容，而不是那 46dp 的纯背景「白条」）。
            //
            // 改法说明：
            //   旧做法 `contentWindowInsets = recedeStableStatusInsets` 让 Scaffold 给**整个**
            //   子树顶推 46dp，各屏自己画的背景/渐变/hero 图起于 y=46dp，上方那 46dp 只剩
            //   Scaffold 的 containerColor（纯页背景色）—— 视觉上就是「状态栏处一条白/黑遮罩带」。
            //   现在：
            //     1. `contentWindowInsets = WindowInsets()`  ⇒ 壳**不加**顶部 padding；
            //     2. 不再 `consumeWindowInsets` 状态栏 ⇒ 各屏自己的 `statusBarsPadding()`
            //        重新拿到真实值，只垫它自己的文字/控件，而**背景（fillMaxSize + background）
            //        顶到 y=0**，内容从状态栏底下穿出来。
            //   净位移不变：原来「壳垫 46dp + 子级 padding 被吃成 0」= 46dp；
            //   现在「壳垫 0 + 子级 padding 46dp」= 46dp ⇒ 文字控件位置不回归，只多出背景露出。
            //
            // ⚠ 回归风险：任何**根节点没有** `statusBarsPadding()` 的屏幕，其内容会直接顶到
            //   y=0、撞进状态栏图标。改完需逐屏验收（tab 页 + overlay）。
            // ⚠ 底栏背板录制节点**不在这里**（2026-09-30 六次修定档）：
            //   `bottomChromeBackdrop` 的录制点已搬到**外层 recede Box** 上
            //   （见上方 `Box(...)` 处的长注释）。这里只剩"页面本体"，
            //   所以 Scaffold 上**不再挂** `layerBackdrop` / `drawBehind{recedeFraction()}`。
            //   ⚠ 千万**不要**同一个 backdrop 挂两处 `Modifier.layerBackdrop`：
            //     `LayerBackdropNode.onGloballyPositioned` 会 `setLayerCoordinates$backdrop(coordinates)`
            //     无条件覆盖 —— 谁最后被定位谁生效（Compose 的定位回调顺序不保证），
            //     等于把「录制点在哪一层」变成了竞态 ⇒ 采样原点会在两处之间跳。
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                contentWindowInsets = WindowInsets(),
                containerColor = MaterialTheme.colorScheme.background,
                ) { innerPadding ->
                    Box(Modifier.fillMaxSize().padding(innerPadding)) {
                    MeloXListDetailLayout(
                        state = detailPaneState,
                        session = neteaseSession,
                        hasMiniPlayer = playbackState.hasMedia,
                    primaryModifier = Modifier,
                        onSourceSelected = { source ->
                            selectedSource = source
                            MusicProviderSelectionStore.setSelectedSource(context, source)
                            libraryModalVisible = false
                            heartModeLaunchAttempted = false
                        },
                    ) {
                    val searchTabBackClear = remember { mutableStateOf(com.lladlam.melox.ui.search.SearchBackAction.SwitchToHome) }
                    var searchTabAction by remember { mutableStateOf(com.lladlam.melox.ui.search.SearchBackAction.SwitchToHome) }
                    // ⚠ 覆盖层优先于切 tab：覆盖层（听歌识曲 / 私信 / 云控同意 / 更新 / 剪贴板）
                    // 可在任意 tab 上打开。原写法先判 `selectedTab != AppTab.Home`，于是浮在非主页
                    // 上的覆盖层第一次返回被「切回主页」吞掉、第二次才关 —— 用户感知即「退两次、
                    // 且直接跳了两级（tab + 覆盖层）」。改为先关覆盖层，再处理切 tab / 退出确认。
                    val anyOverlayVisible = recognitionVisible || messagesVisible ||
                        cloudControlChoicePending || availableUpdate != null || clipboardTarget != null
                    val exitConfirmThresholdMs = 2_000L
                    var pendingExitAtMs by remember { mutableStateOf(0L) }
                    BackHandler {
                        if (anyOverlayVisible) {
                            // 覆盖层优先关闭，不被切 tab 抢走返回事件。
                            when {
                                recognitionVisible -> recognitionVisible = false
                                messagesVisible -> messagesVisible = false
                                cloudControlChoicePending -> cloudControlChoicePending = false
                                availableUpdate != null -> availableUpdate = null
                                clipboardTarget != null -> onClipboardLinkConsumed()
                            }
                            return@BackHandler
                        }
                        if (selectedTab != AppTab.Home) {
                            if (selectedTab == AppTab.Search) {
                                if (searchTabAction != com.lladlam.melox.ui.search.SearchBackAction.SwitchToHome) {
                                    searchTabBackClear.value = searchTabAction
                                    return@BackHandler
                                }
                            }
                            // Any non-home root page returns to the default launch
                            // page first. System back from the search page or any
                            // other tab no longer exits the app directly.
                            selectedTab = AppTab.Home
                            return@BackHandler
                        }
                        val now = System.currentTimeMillis()
                        if (now - pendingExitAtMs < exitConfirmThresholdMs) {
                            (hostContext as? Activity)?.finish()
                        } else {
                            pendingExitAtMs = now
                            MeloXTransientMessage.show(hostContext.getString(R.string.app_exit_confirm))
                        }
                    }
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = { meloXContentEnter() togetherWith meloXContentExit() },
                        modifier = Modifier.fillMaxSize(),
                        label = "melox-page-transition",
                    ) { tab ->
                    rootPageState.SaveableStateProvider(tab.name) { when (tab) {
                         AppTab.Search -> if (selectedSourceId.builtIn == null) SearchScreen(pluginSourceId = selectedSourceId.value)
                         else if (selectedSource == MusicSource.Netease) SearchScreen(
                             backClearSignal = searchTabBackClear,
                             onSearchBackState = { searchTabAction = it },
                              onSearchExit = {
                                  detailPaneState.clearPages()
                                 selectedTab = AppTab.Home
                             },
                             onOpenRecognition = { recognitionVisible = true },
                         ) else ProviderSearchScreen(selectedSource)
                         AppTab.Home -> if (selectedSourceId.builtIn == null) com.lladlam.melox.ui.provider.PluginSourcePage(selectedSourceId, "home") else MeloXHomeScreen(
                            source = selectedSource,
                            onOpenTool = { route ->
                                when (route) {
                                     "Podcasts", "Downloads", "Cloud" -> detailPaneState.openTool(route)
                                     else -> {
                                         when (route) {
                                             "Recognition" -> recognitionVisible = true
                                             "Messages" -> messagesVisible = true
                                             else -> MeloXSettingsDetailActivity.launch(hostContext, route, selectedSource)
                                         }
                                     }
                                 }
                            },
                            onOpenSearch = { selectedTab = AppTab.Search },
                        )
                         AppTab.Explore -> if (selectedSourceId.builtIn == null) com.lladlam.melox.ui.provider.PluginSourcePage(selectedSourceId, "rankings") else if (selectedSource == MusicSource.Netease) MeloXExploreScreen() else ProviderExploreScreen(selectedSource)
                         AppTab.Library -> if (selectedSourceId.builtIn == null) com.lladlam.melox.ui.provider.PluginSourcePage(selectedSourceId, "library") else if (selectedSource == MusicSource.Netease) {
                            LibraryScreen(
                                session = neteaseSession,
                                playlistBackEnabled = !fullPlayerVisible && !libraryModalVisible,
                                onModalVisibilityChanged = { libraryModalVisible = it },
                            onLogin = { launchNeteaseLogin(AppTab.Library) },
                            )
                        } else {
                            ProviderLibraryScreen(selectedSource)
                        }
                        AppTab.Podcasts -> MeloXPodcastScreen(applyStatusBarPadding = true)
                        AppTab.Downloads -> LibraryScreen(
                            session = neteaseSession,
                            onLogin = { launchNeteaseLogin(AppTab.Downloads) },
                            forcedPageName = "Downloads",
                        )
                        AppTab.Cloud -> MeloXCloudMusicScreen()
                         AppTab.Settings -> ProviderSettingsHub(
                             currentSource = selectedSource,
                             currentSourceId = selectedSourceId,
                             onSourceSelected = { source ->
                                 detailPaneState.clearPages()
                                 selectedSource = source
                                MusicProviderSelectionStore.setSelectedSource(context, source)
                                libraryModalVisible = false
                                heartModeLaunchAttempted = false
                                // Provider switching changes only the backing data source.
                                // Stay on the current Settings route and preserve all MeloX settings UI/state.
                            },
                            neteaseSession = neteaseSession,
                            onNeteaseLogin = { launchNeteaseLogin(AppTab.Settings) },
                            onOpenAccount = {
                                neteaseSession.profile?.userId?.let { userId ->
                                    MeloXAccountActivity.launch(hostContext, userId)
                                } ?: run {
                                    launchNeteaseLogin(AppTab.Settings)
                                }
                            },
                            onOpenServices = { MeloXProviderServicesActivity.launch(hostContext) },
                             onOpenMessages = { messagesVisible = true },
                         )
                     } }
                     }
                    }
                 }
            }
                // 下沉时的压暗层：只罩住被缩小的页面本身，底部 chrome 与播放器不受影响。
                // 与缩放窗口同源（同一条 recedeFraction），保证「缩多少 = 暗多少」。
                Box(
                    Modifier
                        .matchParentSize()
                        .drawBehind {
                            val t = recedeFraction()
                            if (t <= 0.001f) return@drawBehind
                            drawRect(Color.Black.copy(alpha = MeloXPlayerRecede.PageScrimAlpha * t))
                        },
                )
            }

            // ══ 底部 chrome 明暗切换状态（2026-09-25）════════════════════════════
            //   `isMeloXDarkTheme()` **只能在这里读一次**，作为打底值；之后全部走 `bottomBarTone`。
            val systemDark = isMeloXDarkTheme()
            val toneState = rememberBottomBarToneState(
                systemDark,
                paused = fullPlayerVisible,
                lockDarkInDarkMode = MeloXSettingsRuntime.bottomBarLockDarkInDarkMode,
            )
            DisposableEffect(toneState) {
                onDispose { toneState.release() }
            }
            val bottomBarTone = toneState.darkness(systemDark)

            if ((!messagesVisible && detailPaneState.pages.isEmpty()) || listDetailMode) CompositionLocalProvider(
                // ⚠ 底栏玻璃**恒**采样 bottomChromeBackdrop（底栏 / 迷你栏 / 搜索键 / 水珠统一）。
                //   它的录制节点在**外层 recede Box** 上（`graphicsLayer` 之外，唯一一处平行层）：
                //   **恒定挂载 + 每帧跟着转场进度重录**
                //   （`drawBehind { recedeFraction() }` 在 `layerBackdrop` 外层强制失效）。
                //   ★ 两个都不能省：① 绝不按条件卸载 —— 录制节点 detach 后 kyant0
                //     `drawBackdrop` 会**直接 return 什么都不画**（反编译确认），玻璃只剩底色
                //     = 半透明；② 光挂着也不行 —— 祖先 graphicsLayer 只改变换时子树不重绘，
                //     录制内容会冻结成旧快照。完整推导见 recede Box 处注释。
                LocalMeloXBackdrop provides bottomChromeBackdrop,
                // ── 底部 chrome 明暗切换（2026-09-25）─────────────────────────────
                //   Apple 口径：navbars / tabbars 属于「会按背后内容在明暗之间整体翻转」
                //   那一档（WWDC25 Session 219 @15:16）。用户拍板：**系统主题打底 +
                //   同时真实采样底栏背后像素** 覆盖。
                //   ⚠ 必须**整个底部 chrome 共用同一个 d**（nav / 搜索键 / 播放栏 /
                //   水珠 / 前景字形全部在内），否则会出现「底栏亮、搜索键暗」的碎裂感。
                LocalBottomBarTone provides bottomBarTone,
            ) {
                // ══ 明暗两种模式的底栏互相切换（方案 A，2026-09-25 用户选定）══════════
                //   整棵子树改用「LightColors ↔ DarkColors 插值出来的配色」重builder一个
                //   MaterialTheme。 ⇒ 所有读 `MaterialTheme.colorScheme.*` 的组件自动跟着过渡，
                //   **不需要**在底栏里手写任何颜色端点。
                //   ⚠ `isMeloXDarkTheme()` 也是读 `colorScheme.background.luminance()`，
                //     所以 subtree 里的布尔判定同样会平滑地跟着翻。
                BottomBarToneTheme(darkness = bottomBarTone) {
                MeloXBottomChrome(
                    selectedTab = selectedTab,
                    source = selectedSource,
                    onSelect = { tab ->
                        // ★ tab 点击触感：只在 tab 真正切换时响一记。
                        if (tab != selectedTab) tabHaptic(HapticFeedbackType.LongPress)
                        if (tab != selectedTab) {
                            detailPaneState.clearPages()
                            messagesVisible = false
                            recognitionVisible = false
                        }
                        selectedTab = tab
                    },
                    hasMedia = playbackState.hasMedia,
                    visibleRootTabs = visibleRootTabs,
                    navigationModifier = Modifier,
                    modifier = Modifier
                        .align(if (listDetailMode) Alignment.BottomStart else Alignment.BottomCenter)
                        .then(if (listDetailMode) Modifier.fillMaxSize() else Modifier.fillMaxWidth()),
                    miniPlayer = { compactProgress ->
                        playerTransition.AnimatedVisibility(
                            // ⚠⚠ 2026-10-06 取证埋点：记录本 lambda 每帧收到的 `value`
                            //   （= `SeekableTransitionState` 给 `Transition` 的哪一个状态）。
                            //   「轻微下拉下沉」的候选机制之一就是它 —— 若回弹（target=打开）
                            //   期间该值仍是"收起侧"，迷你栏就会被判为可见 ⇒ 它的外壳
                            //   （共享元素 overlay，zIndex 100f，浮在播放页之上）会从
                            //   full 一路收缩到 mini，而 `exit = None` 不淡出 ⇒ 看得见的"下沉"。
                            //   `sample` 只在值变化时落一行，故不会刷爆文件。
                            // ⚠⚠⚠ 2026-10-06 结论：这里的判定**必须继续用 lambda 的 `value`**，
                            //   不能改成只读 `playerTransitionState.targetState`（试过 ⇒ 撤回）。
                            //   原因：`AnimatedVisibility` 的 `currentlyVisible = visible(currentState)`。
                            //   `SeekableTransitionState` 在拖拽/收起期间 `currentState` 仍是"打开侧"
                            //   ⇒ 原写法让 `currentlyVisible = true` ⇒ **内容保持挂载**。
                            //   若改成读 `targetState`，收起时两次求值都是 false ⇒ 被判定"当前不可见"
                            //   ⇒ 收起过程中内容就被卸载（播放页提前消失）。
                            //   `value` 仍照旧参与判定，并额外采样用于取证。
                            visible = { value ->
                                MeloXPlayerSinkDiag.sample("miniAV", value)
                                !value
                            },
                            enter = EnterTransition.None,
                            exit = ExitTransition.None,
                        ) {
                            MeloXIOSMiniPlayer(
                                state = playbackState,
                                onExpand = openPlayer,
                                compactProgress = compactProgress,
                                vertical = listDetailMode && MeloXSettingsRuntime.splitMiniPlayerPosition.isSideRail,
                                 dynamicGlassEnabled = true,
                                 sharedTransitionScope = sharedScope,
                                 animatedVisibilityScope = this,
                                 applyPlayerArtworkScale = playerArtworkPageUsesScale,
                                 // ⚠⚠ 2026-10-06：下沉回弹的方向判据，**必须**与 host 侧同源。
                                 //   mini 侧原来硬编码 `{ true }` ⇒ 回弹/打开时也沉 87px
                                 //   （「下沉抽搐一下再往上」「点击很难看」）。详见
                                 //   `MeloXIOSMiniPlayer` 里 `sharedSinkBoundsTransform` 处的说明。
                                 closingIntent = playerClosingIntent,
                                miniPlayerHitRect = miniPlayerHitRect,
                            )
                        }
                    },
                )
                }
            }

            AnimatedVisibility(
                visible = messagesVisible,
                enter = meloXPageEnter(veilColor = MaterialTheme.colorScheme.background),
                exit = meloXPageExit(),
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(if (listDetailMode) 0.5f else 1f)
                    .align(Alignment.TopEnd)
                    .zIndex(15f),
            ) {
                // 包一层只为把页面几何（含**系统屏幕圆角**）挂进 slide 变换内侧
                // —— 挂在 `AnimatedVisibility` 的 modifier 上会落在滑动之外、不跟着走。
                // ⚠ `settled` 见 `meloXPageSlideGeometry`：静止（没有动画进程）时不裁圆角，
                //   否则亮色页四角会露出一块看得见的屏幕圆角缺口。
                Box(
                    Modifier.fillMaxSize().meloXPageSlideGeometry(
                        settled = transition.currentState == transition.targetState,
                    ),
                ) {
                    BackHandler { messagesVisible = false }
                    MessagesScreen(onBack = { messagesVisible = false })
                }
            }

            // 「听歌识曲」就地弹层：与私信同一层，但不切 tab —— 关闭即退回打开它的页面。
            // 它是一整页（自绘不透明背板），所以走满宽推入/弹出；纯淡入淡出在这层读起来
            // 就是「啪一下换掉了」，等于没有动画。
            AnimatedVisibility(
                visible = recognitionVisible,
                enter = meloXPageEnter(veilColor = MaterialTheme.colorScheme.background),
                exit = meloXPageExit(),
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(if (listDetailMode) 0.5f else 1f)
                    .align(Alignment.TopEnd)
                    .zIndex(15f),
            ) {
                Box(
                    Modifier.fillMaxSize().meloXPageSlideGeometry(
                        settled = transition.currentState == transition.targetState,
                    ),
                ) {
                    BackHandler { recognitionVisible = false }
                    MeloXRecognitionOverlay(onBack = { recognitionVisible = false })
                }
            }

            playerTransition.AnimatedVisibility(
                // ⚠⚠ 2026-10-06 取证埋点：与 mini 侧同一目的的对照采样（见那里说明）。
                //   ⚠ 判定仍用 `value`（理由见 mini 侧：改读 targetState 会让内容在收起中提前卸载）。
                visible = { value ->
                    MeloXPlayerSinkDiag.sample("hostAV", value)
                    value
                },
                enter = EnterTransition.None,
                exit = ExitTransition.None,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(20f),
            ) {
                val fullPlayerAnimatedVisibilityScope = this
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                ) {
                    MeloXIOSNowPlayingSharedHost(
                        state = playbackState,
                        onDismiss = closePlayer,
                        // ★ 转场可打断（2026-10-15）：关闭动画进行中，在播放面上点按一次
                        //   = 抢回播放页。与点迷你栏**同一条链路**（`openPlayer`）
                        //   ⇒ 动画从当前 fraction 连续回 1f，不跳变。
                        onReopen = openPlayer,
                        miniPlayerHitRect = miniPlayerHitRect,
                         onNavigateSearch = { query, kind ->
                             detailPaneState.clearPages()
                            if (selectedSource == MusicSource.Netease) {
                                MeloXSearchLaunchBus.post(query, kind)
                            }
                            selectedTab = AppTab.Search
                            closePlayer()
                        },
                        onOpenPlaybackSettings = {
                            selectedTab = AppTab.Settings
                            closePlayer()
                            MeloXSettingsDetailActivity.launch(hostContext, "Playback", selectedSource)
                        },
                        onLocalMetadataChanged = { playbackState.refreshCurrentLocalMetadata() },
                        onSeekCollapse = { fraction ->
                            // ⚠⚠⚠ 2026-10-06（用户诊断 + 真机探针实证）：这里**不能**写 `true`。
                            //   下沉（`meloXPlayerSinkFraction`）的语义是「**落定到迷你栏**的那一记
                            //   落地回弹」——它属于「完全收起」这个动作的收尾。而拖拽 / seek 阶段
                            //   **尚不知道**本次会不会走到完全收起：要等松手时
                            //   `settleFromCurrent` 的 `shouldCollapse`（距离 ≥42% 或速度 ≥1200）
                            //   才定得下来。
                            //   原实现同步写 `true` ⇒ 手指一旦拖过 f=0.62 就提前把下沉施加出来，
                            //   而松手**回弹**时 `onSettleCollapse(false)` 立刻把它撤掉 ⇒
                            //   播放器先被压下去 22dp 又被瞬间提回 ⇒ 用户报障
                            //   「下拉一点松手后，播放器会下沉抽搐一下再往上」、
                            //   「半程取消还会下沉（因为完全收起就有下沉）」。
                            //   ⇒ 拖拽阶段一律**不沉**（`false`）。只有两条**确定收起**的路径置 `true`：
                            //     · `closePlayer()` —— 点击关闭 / 系统返回（f 从 0 完整跑到 1，下沉相位完整）
                            //     · `onSettleCollapse(true)` —— 手势松手判定为收起
                            //   ⚠ 副作用（已知并接受）：若手指已拖过 f=0.62 才松手收起，那一趟的下沉
                            //     会从 `f_start` 处接上（不是从 0 渐入），幅度偏小但**方向与手指一致**
                            //     （本来就在往下落），实测不违和。
                            playerClosingIntent = false
                            // ⚠ 拖拽中必须写 false：既表达"此刻朝关闭"（内容层目标 0f），
                            //   又借这次目标变化让该动画保持活跃 ⇒ 才会被 seek 驱动 ⇒ 跟手。
                            playerTransitionTargetOpen = false
                            playerTransitionJob?.cancelAndJoin()
                            playerTransitionJob = null
                            // ★ 转场可打断：拖拽开始 ⇒ 撤销上一条待发的落定「咚」。
                            playerSettleThumpJob?.cancel()
                            playerSettleThumpJob = null
                            playerTransitionState.seekTo(
                                fraction = fraction.coerceIn(0f, 0.999f),
                                targetState = false,
                            )
                            // ⚠⚠ 2026-10-06 取证埋点（去重，值不变不写）：拖拽 seek 之后
                            //   transition 的 currentState / targetState。
                            MeloXPlayerSinkDiag.sample(
                                "seekState",
                                "cs=${playerTransitionState.currentState} " +
                                    "ts=${playerTransitionState.targetState}",
                            )
                        },
                        onSettleCollapse = { collapse ->
                            MeloXPlayerSinkDiag.event("settle collapse=$collapse")
                            // ⚠⚠ 2026-10-06 取证埋点：settle 之后**头 6 帧**逐帧记录
                            //   transition 的 currentState / targetState。
                            //   用途：「轻微下拉下沉」是否来自「回弹期间迷你栏被判为可见
                            //   ⇒ 它的外壳（共享元素 overlay，zIndex 100f，浮在播放页 20f
                            //   之上）从 full 一路收缩到 mini，而 `exit = None` 不淡出」。
                            //   若这 6 帧里 `ts` 已经是 `true`（打开）而 `cs` 仍是 `false`
                            //   （收起），则 `visible = { !value }` 的取值取决于该 lambda
                            //   拿的是哪一个 —— 配合 `S miniAV=…` 的采样即可定案。
                            playerScope.launch {
                                repeat(6) { i ->
                                    withFrameNanos { }
                                    MeloXPlayerSinkDiag.event(
                                        "settle+f$i cs=${playerTransitionState.currentState} " +
                                            "ts=${playerTransitionState.targetState}",
                                    )
                                }
                            }
                            // `collapse == true` ⇒ 目标是 false（收起）⇒ 该趟仍属关闭方向。
                            playerClosingIntent = collapse
                            // ⚠⚠ 关键：**在 `animateTo` 之前同步写**，让内容层目标立刻变成
                            //   正确方向 —— 回弹（collapse=false）⇒ true ⇒ 目标 1f ⇒ 不再反向；
                            //   收起（collapse=true）⇒ false ⇒ 目标 0f。
                            //   用 `playerTransitionState.targetState` 会晚约 77ms（实测反向来源）。
                            playerTransitionTargetOpen = !collapse
                            playerTransitionJob?.cancelAndJoin()
                            playerTransitionJob = null
                            // ★ 转场可打断：上一条待发的落定「咚」先撤销（回弹 collapse=false
                            //   时更不能让它残留着、在回弹途中炸出来）。
                            playerSettleThumpJob?.cancel()
                            playerSettleThumpJob = null
                            // ★ 双拍下沉触感（与底栏同款）：只在 collapse=true（确定收起）时震。
                            //   拍 1 松手瞬间「啪」激活，拍 2 弹簧落定「咚」落定。
                            if (collapse) {
                                playerSpringThumpEngage()       // 拍 1：松手瞬间「啪」——激活下沉
                                playerSettleThumpJob = playerScope.launch {
                                    val settleMs = meloXPlayerTweenDurationMillis().toLong()
                                    kotlinx.coroutines.delay(settleMs)
                                    playerSpringThumpLand()     // 拍 2：弹簧落定「咚」——落定
                                }
                            }
                            playerTransitionState.animateTo(
                                targetState = !collapse,
                                animationSpec = playerSettleMasterSpec(),
                            )
                        },
                        sharedTransitionScope = sharedScope,
                        animatedVisibilityScope = fullPlayerAnimatedVisibilityScope,
                        playerControlBackdrop = playerControlBackdrop,
                        closingIntent = playerClosingIntent,
                        // ⚠⚠ 2026-10-06：内容层 `expansionProgress` 的动画目标值镜像 ——
                        //   由 `MeloXApp` 在上述**每个主状态变更点**同步写入（早于 `animateTo`）。
                        //   详见 `playerTransitionTargetOpen` 声明处的说明（含两次踩坑记录）。
                        //   ⚠ 不要改成 `playerTransitionState.targetState`（晚 ~77ms ⇒ 会反向），
                        //     也不要改成 `closingIntent`（拖拽期不变 ⇒ 动画被判完成 ⇒ 不跟手）。
                        transitionTargetOpen = playerTransitionTargetOpen,
                        // ⛔⛔ 2026-10-06 已回滚：这里曾传 `playerTransitionState.fraction` 给 host
                        //   让 `expansionProgress` 直算 —— **在根组合体读 `fraction` = 整个 App 订阅
                        //   转场动画的每一帧** ⇒ 一次点击即"全树每帧重组" ⇒ **App 卡死**（用户报
                        //   「一点击播放键就卡住」）。**严禁在此处（或任何外层 composable）读
                        //   `SeekableTransitionState.fraction`**；如确需直算，用 `() -> Float`
                        //   lambda 把读取限制在 host 内部。
                        onArtworkPageChanged = { playerArtworkPageUsesScale = it },
                    )
                 }
             }

         }

        // 全局弹窗独立于左右栏导航，保持覆盖整个窗口。
        clipboardTarget?.let { target ->
            MeloXAppDialog(
                title = stringResource(R.string.app_clipboard_title),
                message = if (target is NeteaseClipboardTarget.Song) {
                    stringResource(R.string.app_clipboard_song)
                } else {
                    stringResource(R.string.app_clipboard_playlist)
                },
                onDismiss = onClipboardLinkConsumed,
                onConfirm = {
                    onClipboardLinkConsumed()
                    playerScope.launch {
                        val client = NeteaseLibraryClient(cookieProvider = { NeteaseSessionStore.readCookie(context) })
                        val songs = withContext(Dispatchers.IO) {
                            runCatching {
                                when (target) {
                                    is NeteaseClipboardTarget.Song -> client.songDetailsBlocking(listOf(target.id))
                                    is NeteaseClipboardTarget.Playlist -> client.playlistDetailBlocking(target.id).songs
                                }
                            }.getOrDefault(emptyList())
                        }
                        PlaybackCommands.playAll(context, songs)
                    }
                },
            )
        }
        if (onboardingPage >= 0) {
            fun finishOnboarding() {
                MeloXSettingsPreferences.setBoolean(context, "onboarding_completed", true)
                onboardingPage = -1
                if (onboardingLoginRequested) launchNeteaseLogin(AppTab.Settings)
            }
            when (onboardingPage) {
                2 -> MeloXOnboardingLegalPage(
                    onAgree = {
                        MeloXSettingsPreferences.setString(context, "legal_consent_version", MELOX_LEGAL_VERSION)
                        MeloXSettingsPreferences.setLong(context, "legal_consent_at", System.currentTimeMillis())
                        if (cloudControlChoicePending) onboardingPage = 3 else finishOnboarding()
                    },
                    onDecline = { (hostContext as? Activity)?.finish() },
                    onOpenProject = {
                        runCatching {
                            hostContext.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lladlam/MeloX-Android")),
                            )
                        }
                    },
                )
                0 -> MeloXOnboardingWelcomePage(
                    onContinue = { onboardingPage = 1 },
                    onLicenses = {
                        runCatching {
                            hostContext.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/lladlam/MeloX-Android/blob/main/README.md#credits"),
                                ),
                            )
                        }
                    },
                )
                1 -> MeloXOnboardingAccountPage(
                    isLoggedIn = neteaseSession.isLoggedIn,
                    nickname = neteaseSession.profile?.nickname,
                    avatarUrl = neteaseSession.profile?.avatarUrl,
                    onLogin = {
                        onboardingLoginRequested = true
                        onboardingPage = 2
                    },
                    onFinish = {
                        onboardingLoginRequested = false
                        onboardingPage = 2
                    },
                )
                else -> MeloXOnboardingCloudPage(
                    onReject = {
                        MeloXRemoteConfigConsent.reject(context)
                        playerScope.launch { MeloXRemoteConfigRuntime.clearCache(context) }
                        cloudControlChoicePending = false
                        finishOnboarding()
                    },
                    onAccept = {
                        MeloXRemoteConfigConsent.accept(context)
                        MeloXRemoteConfigRuntime.initializeAndRefresh(context, BuildConfig.VERSION_CODE, force = true)
                        cloudControlChoicePending = false
                        finishOnboarding()
                    },
                )
            }
        }
        if (onboardingPage < 0 && cloudControlChoicePending) {
            MeloXOnboardingCloudPage(
                onReject = {
                    MeloXRemoteConfigConsent.reject(context)
                    playerScope.launch { MeloXRemoteConfigRuntime.clearCache(context) }
                    cloudControlChoicePending = false
                },
                onAccept = {
                    MeloXRemoteConfigConsent.accept(context)
                    MeloXRemoteConfigRuntime.initializeAndRefresh(context, BuildConfig.VERSION_CODE, force = true)
                    cloudControlChoicePending = false
                },
            )
        }
        availableUpdate?.takeIf {
            onboardingPage < 0 && !cloudControlChoicePending && pendingRemoteNotice == null
        }?.let { release ->
            MeloXUpdateDialog(
                release = release,
                downloadAvailable = release.apkUrl != null,
                onLater = { availableUpdate = null },
                onConfirm = {
                    availableUpdate = null
                    playerScope.launch {
                        val target = runCatching { MeloXUpdateClient(context).downloadUrl(release) }.getOrNull()
                            ?: release.pageUrl
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(target))) }
                    }
                },
            )
        }
        pendingRemoteNotice?.takeIf {
            onboardingPage < 0 && !cloudControlChoicePending
        }?.let { notice ->
            LaunchedEffect(notice.id, notice.frequency) {
                MeloXRemoteNoticeStore.markShown(context, notice)
            }
            MeloXRemoteNoticeDialog(
                notice = notice,
                onAcknowledge = { pendingRemoteNotice = null },
            )
        }
        if (BuildConfig.DEBUG && MeloXSettingsRuntime.performanceOverlayEnabled) {
            MeloXPerformanceOverlay()
        }
      }
    }
}

/**
 * 弹窗动作按钮的最小宽度。内容自适应宽度时用它保底，让「取消/确定」这类两字标签
 * 仍然看起来是一对等宽按钮，而不是缩成两个小圆点。
 *
 * ⚠ 2026-09-26 由 112dp 缩到 96dp：320dp 屏上「稍后再说 / 登录网易云音乐」这对
 *   组合内容宽度约 250dp（7 字 CJK ≈ 105dp），加 10dp 间距后总宽 250dp；
 *   若按钮保底 112dp，Row 就超出内容区（320−40−36=244dp）⇒ Text 被迫换行。
 *   缩到 96dp 之后总宽 222dp 稳落进 244dp，两个按钮各自再按内容撑开，视觉不变。
 */
private val DialogActionButtonMinWidth = 96.dp

@Composable
private fun MeloXAppDialog(
    title: String,
    message: String,
    dismissLabel: String = stringResource(R.string.action_cancel),
    confirmLabel: String = stringResource(R.string.app_confirm),
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    MeloXDialog(visible = true, onDismiss = onDismiss) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(
            message,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
        )
        // ⚠ 不能平分：平分后每个按钮只有 ~129dp（内容区 97dp），6 个 CJK 字的标签
        //   （「登录网易云音乐」17sp ≈ 102dp）会被挤成两行。改为按各自文字自适应宽度、
        //   整组居中，widthIn 只保底 —— 「取消/确定」「稍后/下载 APK」这类短标签仍是等宽。
        // ⚠ 2026-09-26 追加跨设备防护：Text 强制单行 + Ellipsis。不同 CJK 字体
        //   （Noto Sans CJK vs MiSans vs 厂商字）对同一字符串的 em 宽度可能差 5~8%，
        //   保底宽度再紧就会换行。单行 + 溢出省略号是**跨字体一致**的降级 ——
        //   比「悄悄换两行、把 Row 撑高、破坏面板高度」的默认行为可预测得多。
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 18.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            MeloXButton(
                onClick = onDismiss,
                modifier = Modifier.widthIn(min = DialogActionButtonMinWidth),
                style = MeloXButtonStyle.Plain,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            ) { Text(dismissLabel, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            Spacer(Modifier.width(10.dp))
            MeloXButton(
                onClick = onConfirm,
                modifier = Modifier.widthIn(min = DialogActionButtonMinWidth),
                style = MeloXButtonStyle.BorderedProminent,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            ) { Text(confirmLabel, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        }
    }
}

@Composable
private fun MeloXUpdateDialog(
    release: MeloXRelease,
    downloadAvailable: Boolean,
    onLater: () -> Unit,
    onConfirm: () -> Unit,
) {
    // Capped to the safe drawing area: a long release note scrolls inside the
    // panel instead of pushing the buttons off screen.
    MeloXDialog(visible = true, onDismiss = onLater, maxHeightFraction = 0.92f) {
        Text(
            stringResource(R.string.app_update_found, release.version),
            style = MaterialTheme.typography.titleLarge,
        )
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(top = 10.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (release.name.isNotBlank()) {
                Text(
                    release.name,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            if (release.notes.isNotBlank()) {
                Text(
                    release.notes,
                    modifier = Modifier.padding(top = if (release.name.isNotBlank()) 8.dp else 0.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .64f),
                    lineHeight = 21.sp,
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            MeloXButton(
                onClick = onLater,
                modifier = Modifier.widthIn(min = DialogActionButtonMinWidth),
                style = MeloXButtonStyle.Plain,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            ) { Text(stringResource(R.string.app_update_later), maxLines = 1, overflow = TextOverflow.Ellipsis) }
            Spacer(Modifier.width(10.dp))
            MeloXButton(
                onClick = onConfirm,
                modifier = Modifier.widthIn(min = DialogActionButtonMinWidth),
                style = MeloXButtonStyle.BorderedProminent,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    if (downloadAvailable) stringResource(R.string.app_update_download) else stringResource(R.string.app_update_view),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun MeloXSectionShell(
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 48.dp),
    ) {
        Text(
            text = title,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.48f),
        )
    }
}

/**
 * 展开态下底栏 nav 胶囊的高度与搜索键边长 —— 同一个设计基准（360dp 屏）。
 * 收缩态对应 [ChromeCompactSize]。两处必须同源，否则展开态会出现
 * 「胶囊比邻居高一截」的错位。
 */
private val ChromeExpandedSize = 57.dp

/** 收缩态下底栏 nav 胶囊的高度与搜索键边长。 */
private val ChromeCompactSize = 46.dp

/**
 * ⚠ **2026-09-25 一笔勾销**：本常量（及其自研「实心着色胶囊」Layer 2 方案）已随
 *   一比一复刻 BiliNext `LiquidGlassTabsBar.kt` 全部删除 —— Layer 2 现在是一块
 *   **真玻璃**（`meloXChromeSurface`），不再是本项目自创的着色胶囊。
 *   保留此墓碑仅为阻止后来者重新发明它。
 */

/**
 * Material3 bottom navigation for the root tabs.
 *
 * Opaque by design: the container uses the dynamic (Monet) `surfaceContainer`
 * role, so the bar follows the wallpaper palette instead of sampling whatever
 * scrolls behind it. The previous liquid dock -- horizontal swipe collapse,
 * scroll-coupled shrink, droplet indicator and the detached search key -- is
 * gone, so the bar always stays expanded. Search moved to the home top bar.
 */
@Composable
private fun MeloXBottomChrome(
    selectedTab: AppTab,
    source: MusicSource,
    onSelect: (AppTab) -> Unit,
    hasMedia: Boolean,
    visibleRootTabs: List<AppTab>,
    navigationModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
    miniPlayer: @Composable (compactProgress: Float) -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (hasMedia) miniPlayer(0f)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 3.dp,
            shadowElevation = 6.dp,
        ) {
            // NavigationBar already applies the navigation-bar window insets, so
            // no extra padding here.
            NavigationBar(
                modifier = navigationModifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp,
            ) {
                visibleRootTabs.forEach { tab ->
                    val selected = tab == selectedTab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onSelect(tab) },
                        icon = { Icon(tab.md3Icon(selected), contentDescription = null) },
                        label = {
                            Text(
                                text = tab.titleFor(source),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppTab.titleFor(source: MusicSource): String = stringResource(titleRes)

/** Material3 tab icon: filled while selected, outlined otherwise. */
private fun AppTab.md3Icon(selected: Boolean): ImageVector = when (this) {
    AppTab.Home -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
    AppTab.Explore -> if (selected) Icons.Filled.Explore else Icons.Outlined.Explore
    AppTab.Library -> if (selected) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic
    AppTab.Podcasts -> if (selected) Icons.Filled.Podcasts else Icons.Outlined.Podcasts
    AppTab.Downloads -> if (selected) Icons.Filled.Download else Icons.Outlined.Download
    AppTab.Cloud -> if (selected) Icons.Filled.Cloud else Icons.Outlined.Cloud
    AppTab.Settings -> if (selected) Icons.Filled.Settings else Icons.Outlined.Settings
    AppTab.Search -> if (selected) Icons.Filled.Search else Icons.Outlined.Search
}

private fun playerAutomaticFractionSpec() = tween<Float>(
    durationMillis = meloXPlayerTweenDurationMillis(),
    easing = LinearEasing,
)

/**
 * 播放器**四条路径共用**的那条 master（点迷你栏打开 / 点击关闭 / 系统返回 / 下拉手势）：
 * ζ=1 临界阻尼弹簧。
 *
 * ⚠⚠⚠ 2026-10-06：`stiffness` **必须由「设置时长」反解，绝不能写死**。
 *   用户报障：「播放页打开动画设置…**调节没反应**」。根因：`spring` **完全不读**
 *   `MotionDurationScale`、也不读设置时长 —— 之前写死 `stiffness = 420f` ⇒
 *   拖动「播放器展开/收回时长」对整条动画**毫无作用**（只有几何 bounds 那一层读设置，
 *   观感上几乎看不出来）。用户口径：「**把动画速度跟设置数值对应**」。
 *   ⇒ 反解：ζ=1 时 99% 落定 ≈ `k / ω`（k = [PLAYER_SETTLE_99_K]）⇒ `ω = k / D`
 *     ⇒ `stiffness = ω²`。这样「设置里选的毫秒数」就是真实落定时间。
 *   ⚠ 用 [meloXPlayerTweenDurationMillis]（**已抵消系统动画缩放**）而不是裸设置值：
 *     与几何 bounds 的 `durationMillis` 逐字同源，系统缩放 ≠ 1 时两者仍严格同步。
 *
 * ⚠ 与 2026-10-04 那次「master 换弹簧 ⇒ 效果更差」的区别（**别再混淆**）：
 *   那次 ω 由 **240ms** 反解 ⇒ `stiffness = (6.2/0.24)² = 667`（很硬），
 *   把前段推到「100ms 就走完 95%」才被否。**公式相同，但现在默认时长是 360ms**
 *   ⇒ ω ≈ 17.2、`stiffness ≈ 297`（比 420 软、比 667 软得多）。
 *   ⚠ 用户此前认可的手势那条 `420` ≈ **302ms 档**的反解值 ⇒ 设置调到 300ms 即与之几乎一致。
 */
private const val PLAYER_SETTLE_99_K = 6.2f

private fun playerSettleMasterSpec(): FiniteAnimationSpec<Float> {
    val durationSec = (meloXPlayerTweenDurationMillis().coerceAtLeast(80)) / 1000f
    val omega = PLAYER_SETTLE_99_K / durationSec
    return spring(
        dampingRatio = 1.0f,
        stiffness = omega * omega,
        visibilityThreshold = 0.001f,
    )
}

/**
 * **打开方向**（点迷你栏 / 冷启 intent / 任何 `openPlayer()`）的 master。
 *
 * ⚠⚠⚠ 2026-10-06：与 [playerCloseFractionSpec] **一起**改成与「下拉手势」同一条
 *   [playerSettleMasterSpec]（spring ζ=1，stiffness 由**设置时长**反解）。
 *   用户口径：「迷你栏点击**和**通过手势退出的动画不对劲，很难看。**只有通过手来下滑的
 *   返回动画符合丝滑的理念**」+「下拉关闭的，那个是对的」。
 *   ⇒ 四条路径里只有「下拉手势」那条是 spring，其余（点击打开 / 点击关闭 /
 *     系统返回）都是 `tween(D, LinearEasing)` —— **差别只有这一条 master**。
 *   ⇒ 统一到同一条 spring：临界阻尼软起软收，取代原来
 *     50%@127ms、95%@338ms、**尾段空转 123ms** 的线性 tween。
 *
 * ⚠ 同一天**第二次**收口（用户报「设置调节**没反应**」）：原先写成 `stiffness = 420f`
 *   硬编码 ⇒ `spring` 不读设置 ⇒ 时长设置对动画完全无效。
 *   现改为 `stiffness = (k/D)²`（D 取设置时长），**「设置里选的毫秒数」= 真实落定时间**。
 *   ⚠ 在默认 360ms 档下 stiffness ≈ 297；420 恰好是 **302ms 档**的反解值
 *     ⇒ 想要回原来那种手感，把设置调到 300ms 即可。
 *
 * ⚠ 与 2026-10-04 那两次「master 换弹簧 ⇒ 效果更差」的区别（**别再混淆**）：
 *   那两次用的是 `stiffness = (6.2/D)²` 且 **D = 240ms** ⇒ **667**（很硬），
 *   把前段推到「100ms 走完 95%、尾段空转 139ms」⇒ 被否。
 *   **公式相同**，现在默认 360ms ⇒ ω ≈ 17.2、stiffness ≈ **297**，软得多。
 *
 * ⚠ 回退只需一行：把本函数与 [playerCloseFractionSpec] 都改回
 *   `playerAutomaticFractionSpec()`（保留未删，专为回退）。
 *
 * ⚠⚠ 2026-10-15 回退记录：曾按用户口径 "damping 0.75，有轻微回弹"把**打开方向**
 *   单独换成 underdamped 弹簧（ζ=0.75，过冲 ~2.8%；recede / 内容层 / MiniPlayer
 *   三处同换 + recedeMasterFraction 上界钳制放开），用户实测「**效果太差**」⇒
 *   整条回退到本函数（ζ=1）。当时的新增 spec / 常量 / import 已全部删除。
 *   教训：本项目播放器动画的「弹」已由关闭方向 + 迷你栏尾段 bounce 提供，
 *   打开方向再加过冲是**双重回弹**，与 AM 手感相反。
 */
private fun playerOpenFractionSpec(): FiniteAnimationSpec<Float> = playerSettleMasterSpec()

/**
 * **关闭方向**（点击关闭 / 系统返回 / 任何 `closePlayer()`）的 master。
 *
 * ⚠⚠ 与 [playerOpenFractionSpec] **同一函数** ⇒ 打开/关闭严格互为时间镜像，
 *   且**同样跟随「播放器展开/收回时长」设置**（stiffness 由它反解）。
 *   历史两次回退记录见 [playerOpenFractionSpec] 的说明（主因是当时 D=240 ⇒ 667 太硬）。
 */
private fun playerCloseFractionSpec(): FiniteAnimationSpec<Float> = playerSettleMasterSpec()

private fun smoothStep(value: Float, start: Float, end: Float): Float {
    if (end <= start) return if (value >= end) 1f else 0f
    val t = ((value - start) / (end - start)).coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

private fun lerpDp(start: Dp, end: Dp, progress: Float): Dp =
    (start.value + (end.value - start.value) * progress.coerceIn(0f, 1f)).dp

/**
 * 与 [lerpDp] 相同，但**不夹** fraction —— 专供带过冲的几何。
 *
 * ⚠ 必须用这个版本，否则弹簧的过冲会在最后一步被 [lerpDp] 吃掉，Q 弹永远看不见。
 * fraction 由 [sprungFrac] 产出，已限幅，不会把布局撑爆；但**外扩方向**是否安全
 * 取决于该几何周围有没有邻居 —— 现在这件事交给**弹簧参数自己**，不再做事后压缩：
 * nav 展开方向的越界量由 `MeloXSprings.BottomBarExpandBounce` 定死在 6.02dp
 * （右预算 4dp + 左侧让出 2dp），再按 `NavExpandLeftShare` 落到两端；
 * 收缩方向由 0.30 那条管着（10.30dp，四周没有邻居）。
 */
private fun lerpDpBouncy(start: Dp, end: Dp, fraction: Float): Dp =
    (start.value + (end.value - start.value) * fraction).dp

/**
 * BiliNext `LiquidGlassTabsBar.kt` L99 的等价物：让 Layer 2（隐形捕获层）里的
 * tab 内容在按压时整体放大（`lerp(1f, 1.2f, pressProgress)`）。
 *
 * ⚠ 它**只影响捕获层的背板内容**（= 被水珠折射的那张图），不影响可见面板 ——
 * 所以「按压时图标放大」这件事在本项目只表现为折射内容的呼吸感，1:1 复刻 BiliNext。
 */
private val LocalGlassTabScale: ProvidableCompositionLocal<() -> Float> =
    staticCompositionLocalOf { { 1f } }

/**
 * 恒定的 status bar 高度 [WindowInsets]，**不受系统栏 hide/show 影响**。
 *
 * ⚠ 为什么不用 `WindowInsets.statusBars.ignoringVisibility()`：Compose 的
 * `ignoringVisibility()` 扩展在当前 BOM 版本里不可用（unresolved reference）。
 * 这里改用 Android ViewCompat 取 `getInsetsIgnoringVisibility` 的真实高度，
 * 与 [com.lladlam.melox.ui.player.meloXPlayerStatusBarsPadding] 同一取数路径。
 *
 * 用途：给 recede 背景页 Scaffold 当 `contentWindowInsets`。沉浸播放时状态栏
 * 被 `hide()`，可见 inset 归 0，普通 `statusBars` 会塌陷、返回时弹回真实高度
 * ⇒ 背景页顶部跳变（抽搐）。用本函数返回稳定高度让 padding 恒定；调用方再用
 * `consumeWindowInsets(WindowInsets.statusBars)` 让子级拿到的 statusBars 归零，
 * 避免它们的 `statusBarsPadding()` 在这份稳定 padding 之上重复叠加。
 */
@Composable
private fun stableStatusBarsInsets(): WindowInsets {
    val view = LocalView.current
    val density = LocalDensity.current
    var stableTopPx by remember { mutableIntStateOf(0) }

    // ⚠ 缓存**只在拿到 >0 的值时写入**，隐藏期间保留上一次真实高度（真正的稳定）。
    //   部分 ROM 在 `hide(statusBars)` 后连 `getInsetsIgnoringVisibility` 也返 0 ⇒ 照单写入
    //   会让 padding 随显隐 0↔真实高度跳，退出沉浸时页面下移。
    //   ⚠⚠**不要用 `insets.isVisible(statusBars)` 当守卫**：vivo 上它可能恒为 false（或首帧
    //   insets 尚未派发），加了就导致缓存**一次都没被写入**、恒为 0 ⇒ 内容顶到状态栏底下
    //   （真机症状：歌单页按钮顶到最上面）。只看 `v > 0` 即可：隐藏时自然为 0、保持旧值。
    DisposableEffect(view) {
        ViewCompat.getRootWindowInsets(view)?.let { insets ->
            val v = maxOf(
                insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
            )
            if (v > 0) stableTopPx = v
        }
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val v = maxOf(
                insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
            )
            if (v > 0) stableTopPx = v
            insets
        }
        onDispose {
            ViewCompat.setOnApplyWindowInsetsListener(view, null)
        }
    }

    return remember(stableTopPx, density) {
        WindowInsets(0.dp, with(density) { stableTopPx.toDp() }, 0.dp, 0.dp)
    }
}

/**
 * 底部 chrome 的**稳定**底部内边距：走 ViewCompat 直接读 `navigationBars` 的
 * **忽略可见性**真实高度，而不是用 `Modifier.navigationBarsPadding()`。
 *
 * 为什么（2026-09-29 真机回归）：edge-to-edge（`setDecorFitsSystemWindows(false)`）之后
 * content frame 铺满整屏，底栏胶囊的抬高就**全靠** Compose 的 `navigationBars` inset。
 * 真机 iQOO 15 竖屏实测底栏内容底沿 = 屏底 − 8dp，正好少了 72px = 导航栏高度
 * ⇒ 这条传播链读到 0，胶囊直接压到系统手势条（小白条）上。
 *
 * 改用与 [stableStatusBarsInsets] 同源的取数路径（`getRootWindowInsets` +
 * `getInsetsIgnoringVisibility`）：既不依赖 insets 传播链，也不会随系统栏 hide/show 跳变。
 *
 * ⚠ **不注册** `setOnApplyWindowInsetsListener`（它是**覆盖式**的，会和
 *   [stableStatusBarsInsets] / [meloXPlayerStatusBarsPadding] 互踩），改为轮询到拿到为止；
 *   转屏后按 screenWidthDp/screenHeightDp 重新读取。
 */
@Composable
private fun Modifier.stableNavigationBarsPadding(): Modifier = composed {
    val view = LocalView.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    var navBottomPx by remember(view, configuration.screenWidthDp, configuration.screenHeightDp) {
        mutableIntStateOf(
            ViewCompat.getRootWindowInsets(view)
                ?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars())
                ?.bottom ?: 0,
        )
    }
    LaunchedEffect(view, configuration.screenWidthDp, configuration.screenHeightDp) {
        repeat(20) {
            if (navBottomPx > 0) return@LaunchedEffect
            delay(50L)
            ViewCompat.getRootWindowInsets(view)
                ?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars())
                ?.let { insets -> navBottomPx = insets.bottom }
        }
    }
    Modifier.padding(bottom = with(density) { navBottomPx.toDp() })
}
