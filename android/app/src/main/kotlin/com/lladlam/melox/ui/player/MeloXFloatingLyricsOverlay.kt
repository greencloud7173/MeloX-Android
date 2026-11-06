package com.lladlam.melox.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compose content of the floating-lyrics system overlay window.
 *
 * Geometry, colors and text sizes mirror the previous `LinearLayout`/`TextView`
 * renderer 1:1 so switching the window root to Compose is visually invisible.
 * Dragging is reported through [onDrag] so the service can keep the window's
 * `WindowManager.LayoutParams` authoritative.
 */
@Composable
internal fun MeloXFloatingLyricsOverlay(
    primary: String,
    secondary: String,
    highContrast: Boolean,
    fontSizeSp: Int,
    onDrag: (Offset) -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (highContrast) Color(0xCD0C0C10) else Color(0x7D121216))
            .border(1.dp, Color(0x4BFFFFFF), shape)
            // Raw pointer tracking instead of `detectDragGestures`: the previous
            // View implementation followed the finger from the first pixel, with
            // no touch-slop dead zone. Kept *before* `padding` so the whole
            // panel stays draggable, padding included.
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type != PointerEventType.Move) continue
                        val change = event.changes.firstOrNull() ?: continue
                        if (!change.pressed) continue
                        val delta = change.position - change.previousPosition
                        if (delta == Offset.Zero) continue
                        change.consume()
                        onDrag(delta)
                    }
                }
            }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = primary,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                color = Color.White,
                fontSize = fontSizeSp.sp,
                textAlign = TextAlign.Center,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(3.dp))
        BasicText(
            text = secondary,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                color = Color(0x96FFFFFF),
                fontSize = (fontSizeSp * SECONDARY_SCALE).coerceAtLeast(10f).sp,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private const val SECONDARY_SCALE = 0.68f
