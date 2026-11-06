package com.lladlam.melox.ui.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import kotlin.math.PI
import kotlin.math.sin

/**
 * Now-playing screen following the AOSP / Material 3 player reference.
 *
 * Artwork on top, metadata, a wavy Material 3 progress bar, then the transport
 * row with a filled play/pause button. No iOS glass, no shared-element morph:
 * the whole page is one Material surface.
 */
@Composable
internal fun MeloXMaterialNowPlaying(
    state: MeloXPlaybackUiState,
    onDismiss: () -> Unit,
    onOpenActions: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "收起播放页")
                }
                Text(
                    text = "正在播放",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                IconButton(onClick = onOpenActions) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "更多")
                }
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(28.dp)),
            ) {
                AsyncImage(
                    model = state.artworkUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = state.artist,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )

            Spacer(Modifier.height(20.dp))

            WavyProgressBar(
                positionMs = state.positionMs,
                durationMs = state.durationMs,
                animated = state.isPlaying,
                onSeek = { state.seekTo(it) },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                Text(
                    text = formatPlaybackTime(state.positionMs),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatPlaybackTime(state.durationMs),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { state.toggleShuffle() }) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = "随机播放",
                        tint = if (state.shuffleEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
                IconButton(onClick = { state.previous() }) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "上一首", modifier = Modifier.size(36.dp))
                }
                FilledIconButton(
                    onClick = { state.togglePlayPause() },
                    modifier = Modifier.size(72.dp),
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (state.isPlaying) "暂停" else "播放",
                        modifier = Modifier.size(40.dp),
                    )
                }
                IconButton(onClick = { state.next() }) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "下一首", modifier = Modifier.size(36.dp))
                }
                IconButton(onClick = { state.cycleRepeatMode() }) {
                    Icon(
                        imageVector = if (state.repeatMode == Player.REPEAT_MODE_ONE) {
                            Icons.Filled.RepeatOne
                        } else {
                            Icons.Filled.Repeat
                        },
                        contentDescription = "循环模式",
                        tint = if (state.repeatMode != Player.REPEAT_MODE_OFF) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

/**
 * Material 3 expressive wavy progress: the active portion is drawn as a sine
 * wave that keeps travelling while playback is running, with a round knob at
 * the current position. Tap anywhere on the bar to seek.
 */
@Composable
internal fun WavyProgressBar(
    positionMs: Long,
    durationMs: Long,
    animated: Boolean,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = if (durationMs > 0L) {
        (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val primary = MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.surfaceContainerHighest

    val transition = rememberInfiniteTransition(label = "wavy-progress")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 2800, easing = LinearEasing)),
        label = "wavy-phase",
    )
    val wavePhase = if (animated) phase else 0f

    Canvas(
        modifier = modifier
            .height(40.dp)
            .pointerInput(durationMs) {
                detectTapGestures { offset ->
                    if (durationMs > 0L && size.width > 0) {
                        val fraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                        onSeek((fraction * durationMs).toLong())
                    }
                }
            },
    ) {
        val centerY = size.height / 2f
        val strokeWidth = 3.dp.toPx()
        val amplitude = 6.dp.toPx()
        val wavelength = 46.dp.toPx()
        val playheadHalfHeight = 12.dp.toPx()
        val playheadWidth = 4.dp.toPx()

        val activeWidth = size.width * progress

        // Unplayed part: a plain straight line.
        drawLine(
            color = track,
            start = Offset(activeWidth, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )

        if (activeWidth <= 0f) {
            drawLine(
                color = primary,
                start = Offset(0f, centerY - playheadHalfHeight),
                end = Offset(0f, centerY + playheadHalfHeight),
                strokeWidth = playheadWidth,
                cap = StrokeCap.Round,
            )
            return@Canvas
        }

        // Played part: a full wave, constant amplitude from the first pixel.
        val path = Path()
        var x = 0f
        var first = true
        while (x <= activeWidth) {
            val y = centerY + sin((x / wavelength + wavePhase) * 2f * PI.toFloat()) * amplitude
            if (first) {
                path.moveTo(x, y)
                first = false
            } else {
                path.lineTo(x, y)
            }
            x += 2f
        }
        val tailY = centerY + sin((activeWidth / wavelength + wavePhase) * 2f * PI.toFloat()) * amplitude
        path.lineTo(activeWidth, tailY)
        drawPath(
            path = path,
            color = primary,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )

        // Boundary between played and unplayed: a slightly taller vertical bar.
        drawLine(
            color = primary,
            start = Offset(activeWidth, centerY - playheadHalfHeight),
            end = Offset(activeWidth, centerY + playheadHalfHeight),
            strokeWidth = playheadWidth,
            cap = StrokeCap.Round,
        )
    }
}

private fun formatPlaybackTime(ms: Long): String {
    val safe = ms.coerceAtLeast(0L)
    val totalSeconds = safe / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}
