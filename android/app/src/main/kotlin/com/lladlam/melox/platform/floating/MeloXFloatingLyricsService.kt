package com.lladlam.melox.platform.floating

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.lladlam.melox.MainActivity
import com.lladlam.melox.R
import com.lladlam.melox.core.lyrics.LyricTimelineProcessor
import com.lladlam.melox.core.lyrics.LyricsDocument
import com.lladlam.melox.playback.MeloXPlaybackService
import com.lladlam.melox.playback.PlaybackTrackIdentity
import com.lladlam.melox.playback.ProviderPlaybackRuntime
import com.lladlam.melox.ui.player.MeloXFloatingLyricsOverlay
import com.lladlam.melox.ui.player.MeloXProviderLyricsLoader
import com.lladlam.melox.ui.settings.MeloXSettingsPreferences
import com.lladlam.melox.ui.settings.MeloXSecondaryLyricMode
import java.util.concurrent.Executor
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** System overlay renderer used while another app is in the foreground. */
class MeloXFloatingLyricsService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val mainExecutor = Executor { handler.post(it) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var controller: MediaController? = null
    private var windowManager: WindowManager? = null
    private var overlay: View? = null
    private var overlayOwner: OverlayViewOwner? = null
    private val primaryLine = mutableStateOf("")
    private val secondaryLine = mutableStateOf("")
    private var lyrics: LyricsDocument? = null
    private var lyricsMediaId: String? = null
    private var lyricsJob: Job? = null
    private var lastIndex = Int.MIN_VALUE
    private var anchorPositionMs = 0L
    private var anchorUptimeMs = 0L

    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
            loadLyrics(mediaItem)
        }

        override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
            anchorPositionMs = newPosition.positionMs
            anchorUptimeMs = android.os.SystemClock.uptimeMillis()
            handler.removeCallbacks(ticker)
            handler.post(ticker)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            controller?.currentPosition?.let { anchorPositionMs = it }
            anchorUptimeMs = android.os.SystemClock.uptimeMillis()
            handler.removeCallbacks(ticker)
            handler.post(ticker)
        }
    }

    private val ticker = object : Runnable {
        override fun run() {
            val wait = updateText()
            handler.postDelayed(this, wait)
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            stopSelf()
            return
        }
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        createOverlay()
        connectPlayer()
        handler.post(ticker)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            MeloXSettingsPreferences.setBoolean(this, "floating_lyrics_enabled", false)
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun connectPlayer() {
        val future = MediaController.Builder(
            this,
            SessionToken(this, ComponentName(this, MeloXPlaybackService::class.java)),
        ).buildAsync()
        future.addListener({
            runCatching { future.get() }.onSuccess { player ->
                controller = player
                player.addListener(listener)
                loadLyrics(player.currentMediaItem)
            }
        }, mainExecutor)
    }

    private fun loadLyrics(item: androidx.media3.common.MediaItem?) {
        val mediaId = item?.mediaId?.takeIf(String::isNotBlank)
        if (shouldClearLegacyNeteaseLyrics(mediaId)) {
            clearLyrics()
            return
        }
        if (mediaId == null || lyricsMediaId == mediaId) return
        lyricsMediaId = mediaId
        lyrics = null
        lastIndex = Int.MIN_VALUE
        primaryLine.value = getString(R.string.floating_lyrics_loading)
        secondaryLine.value = ""
        lyricsJob?.cancel()
        val metadata = item.mediaMetadata
        val durationMs = metadata.durationMs?.takeIf { it > 0L }
            ?: controller?.duration?.takeIf { it != androidx.media3.common.C.TIME_UNSET && it > 0L }
            ?: 0L
        lyricsJob = scope.launch {
            // Shares the in-app provider matching/cache, so every music source
            // gets the same lyrics instead of only NetEase IDs.
            ProviderPlaybackRuntime.initialize(this@MeloXFloatingLyricsService)
            val document = withContext(Dispatchers.IO) {
                runCatching {
                    MeloXProviderLyricsLoader.load(
                        context = this@MeloXFloatingLyricsService,
                        mediaId = mediaId,
                        title = metadata.title?.toString().orEmpty(),
                        artist = metadata.artist?.toString().orEmpty(),
                        album = metadata.albumTitle?.toString().orEmpty(),
                        artworkUrl = metadata.artworkUri?.toString(),
                        durationMs = durationMs,
                    )
                }.getOrNull()
            }
            if (lyricsMediaId == mediaId) lyrics = document
        }
    }

    private fun clearLyrics() {
        lyricsJob?.cancel()
        lyricsJob = null
        lyricsMediaId = null
        lyrics = null
        lastIndex = Int.MIN_VALUE
        primaryLine.value = ""
        secondaryLine.value = ""
    }

    private fun updateText(): Long {
        val player = controller ?: return 250L
        val document = lyrics ?: return 250L
        val advance = MeloXSettingsPreferences.int(this, "lyrics_advance_ms", 0).toLong()
        if (player.isPlaying) {
            anchorPositionMs = player.currentPosition
            anchorUptimeMs = android.os.SystemClock.uptimeMillis()
        }
        val position = if (player.isPlaying) {
            anchorPositionMs + (android.os.SystemClock.uptimeMillis() - anchorUptimeMs)
        } else {
            player.currentPosition
        } + advance
        val safePosition = position.coerceAtLeast(0L)
        val index = document.highlightedIndex(safePosition) ?: return 250L
        if (index != lastIndex) {
            lastIndex = index
            val line = document.lines.getOrNull(index)
            primaryLine.value = line?.text.orEmpty()
            val mode = runCatching {
                MeloXSecondaryLyricMode.valueOf(
                    MeloXSettingsPreferences.string(this, "floating_lyrics_secondary_mode", MeloXSecondaryLyricMode.Auto.name),
                )
            }.getOrDefault(MeloXSecondaryLyricMode.Auto)
            val translation = line?.translation.orEmpty()
            val romanization = line?.romanization.orEmpty()
            val nextLineText = document.lines.getOrNull(index + 1)?.text.orEmpty()
            secondaryLine.value = when (mode) {
                MeloXSecondaryLyricMode.Translation -> translation
                MeloXSecondaryLyricMode.Romanization -> romanization
                MeloXSecondaryLyricMode.NextLine -> nextLineText
                MeloXSecondaryLyricMode.Hidden -> ""
                MeloXSecondaryLyricMode.Auto -> when {
                    MeloXSettingsPreferences.boolean(this, "lyrics_translation", true) && translation.isNotBlank() -> translation
                    MeloXSettingsPreferences.boolean(this, "lyrics_romanization", false) && romanization.isNotBlank() -> romanization
                    else -> nextLineText
                }
            }
        }
        val nextLine = document.lines.getOrNull(index + 1)
        val nextEvent = nextLine?.timeMs
            ?: LyricTimelineProcessor.nextEventTimeMs(document, safePosition)
        return nextEvent?.minus(safePosition)?.coerceIn(16L, 500L) ?: 250L
    }

    private fun createOverlay() {
        val manager = getSystemService(WindowManager::class.java)
        windowManager = manager
        val density = resources.displayMetrics.density
        val highContrast = MeloXSettingsPreferences.boolean(this, "floating_lyrics_high_contrast", true)
        val fontSize = MeloXSettingsPreferences.int(this, "floating_lyrics_font_size", 18).coerceIn(14, 28)
        primaryLine.value = getString(R.string.floating_lyrics_title)
        val params = WindowManager.LayoutParams(
            (330 * density).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (18 * density).toInt()
            y = (110 * density).toInt()
        }
        val owner = OverlayViewOwner().apply { onCreate() }
        overlayOwner = owner
        val composeView = ComposeView(this).apply {
            // The window can be torn down from the service at any time, so the
            // composition must follow the view and never outlive it.
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setViewTreeLifecycleOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)
            setViewTreeViewModelStoreOwner(owner)
        }
        composeView.setContent {
            MeloXFloatingLyricsOverlay(
                primary = primaryLine.value,
                secondary = secondaryLine.value,
                highContrast = highContrast,
                fontSizeSp = fontSize,
                onDrag = { delta ->
                    params.x += delta.x.roundToInt()
                    params.y += delta.y.roundToInt()
                    runCatching { windowManager?.updateViewLayout(composeView, params) }
                },
            )
        }
        manager.addView(composeView, params)
        overlay = composeView
    }

    private fun createNotificationChannel() {
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CHANNEL_ID, getString(R.string.floating_lyrics_channel), NotificationManager.IMPORTANCE_LOW).apply {
                setSound(null, null)
                enableVibration(false)
            },
        )
    }

    private fun buildNotification(): android.app.Notification {
        val open = PendingIntent.getActivity(
            this,
            1801,
            Intent(this, MainActivity::class.java).apply {
                action = MainActivity.ACTION_OPEN_NOW_PLAYING
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val stop = PendingIntent.getService(
            this,
            1802,
            Intent(this, MeloXFloatingLyricsService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(getString(R.string.floating_lyrics_title))
            .setContentText(getString(R.string.floating_lyrics_showing))
            .setContentIntent(open)
            .addAction(0, getString(R.string.floating_lyrics_stop), stop)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        lyricsJob?.cancel()
        controller?.removeListener(listener)
        controller?.release()
        controller = null
        overlay?.let { runCatching { windowManager?.removeView(it) } }
        overlay = null
        overlayOwner?.onDestroy()
        overlayOwner = null
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "melox_floating_lyrics"
        const val NOTIFICATION_ID = 1800
        const val ACTION_STOP = "com.lladlam.melox.action.STOP_FLOATING_LYRICS"
    }
}

/**
 * Minimal owners required to host a [ComposeView] outside an Activity.
 *
 * The overlay lives in a `WindowManager` window owned by a service, so no
 * Activity lifecycle is available; Compose only needs the three view-tree
 * owners to be present and their state to follow the window.
 */
private class OverlayViewOwner : LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateController = SavedStateRegistryController.create(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateController.savedStateRegistry
    override val viewModelStore = ViewModelStore()

    fun onCreate() {
        savedStateController.performAttach()
        savedStateController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        viewModelStore.clear()
    }
}

internal fun shouldClearLegacyNeteaseLyrics(mediaId: String?): Boolean =
    mediaId.isNullOrBlank() || PlaybackTrackIdentity.decode(mediaId) == null
