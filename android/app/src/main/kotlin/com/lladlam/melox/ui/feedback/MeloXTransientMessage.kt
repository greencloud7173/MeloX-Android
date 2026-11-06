package com.lladlam.melox.ui.feedback

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Compose replacement for `android.widget.Toast` on screens that already render
 * Compose content.
 *
 * Messages are published into snapshot state and rendered by
 * [MeloXTransientMessageOverlay], which [com.lladlam.melox.ui.theme.MeloXTheme]
 * installs above every screen. Feedback that must work with no Compose tree at
 * all (playback service, OAuth redirect activity) intentionally stays a platform
 * toast.
 */
object MeloXTransientMessage {
    internal data class Entry(val id: Long, val text: String)

    private val mainHandler = Handler(Looper.getMainLooper())
    private var nextId = 0L

    internal var current by mutableStateOf<Entry?>(null)
        private set

    fun show(text: String) {
        if (text.isBlank()) return
        if (Looper.myLooper() == Looper.getMainLooper()) {
            nextId += 1
            current = Entry(nextId, text)
        } else {
            mainHandler.post {
                nextId += 1
                current = Entry(nextId, text)
            }
        }
    }
}

/**
 * Draws the current [MeloXTransientMessage] as a floating pill above the bottom
 * edge of its parent. It is zero-sized while idle and only paints: no
 * pointer-input modifiers, so touches keep reaching the screen underneath.
 */
@Composable
fun MeloXTransientMessageOverlay(modifier: Modifier = Modifier) {
    val entry = MeloXTransientMessage.current
    var shownText by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(entry?.id) {
        val text = entry?.text ?: return@LaunchedEffect
        shownText = text
        visible = true
        delay(MELO_X_TRANSIENT_MESSAGE_HOLD_MS)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier.padding(start = 32.dp, end = 32.dp, bottom = 112.dp),
        enter = fadeIn(tween(160)) + slideInVertically(tween(220)) { it / 3 },
        exit = fadeOut(tween(160)) + slideOutVertically(tween(180)) { it / 3 },
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .clip(MeloXTransientMessageShape)
                .background(Color(0xE626262C))
                .border(1.dp, Color(0x33FFFFFF), MeloXTransientMessageShape)
                .padding(horizontal = 18.dp, vertical = 12.dp),
        ) {
            Text(
                text = shownText,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private val MeloXTransientMessageShape = RoundedCornerShape(20.dp)
private const val MELO_X_TRANSIENT_MESSAGE_HOLD_MS = 2_000L
