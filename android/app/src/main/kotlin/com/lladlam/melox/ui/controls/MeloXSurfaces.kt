package com.lladlam.melox.ui.controls

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.lladlam.melox.ui.theme.isMeloXDarkTheme

/**
 * Plain-Compose replacements for the removed liquid-glass surface layer.
 *
 * The refraction/blur/lens shaders are gone; every modifier below paints a flat
 * Material surface instead. Names and signatures are kept so the rest of the
 * app keeps compiling while the visual treatment collapses onto MaterialTheme
 * tokens.
 */

/** Kept for compatibility: nothing consumes the screen backdrop any more. */
val LocalMeloXBackdrop = staticCompositionLocalOf<Backdrop?> { null }

/** Dark-tone scalar for bottom chrome: 0 = light, 1 = dark. */
val LocalBottomBarTone = staticCompositionLocalOf<Float> { Float.NaN }

/** Reads the current tone; falls back to the theme's binary answer. */
@Composable
internal fun bottomBarTone(): Float {
    val provided = LocalBottomBarTone.current
    return if (provided.isNaN()) (if (isMeloXDarkTheme()) 1f else 0f) else provided
}

internal val BottomGlassTintLight = Color.White.copy(alpha = 0.12f)
internal val BottomGlassTintDark = Color.Black.copy(alpha = 0.10f)

@Composable
internal fun bottomLiquidGlassTint(): Color =
    if (bottomBarTone() >= 0.5f) BottomGlassTintDark else BottomGlassTintLight

@Composable
internal fun bottomGlassFallbackColor(): Color =
    if (bottomBarTone() >= 0.5f) MaterialTheme.colorScheme.surface else Color.White

@Composable
internal fun bottomGlassSurfaceColor(): Color =
    bottomGlassFallbackColor().copy(alpha = BOTTOM_GLASS_SURFACE_ALPHA)

internal const val BOTTOM_GLASS_SURFACE_ALPHA = 0.52f

internal const val MeloXDropletLensScale = 49f / 56f

internal fun safeProgress(value: Float): Float =
    if (value.isNaN() || value.isInfinite()) 0f else value.coerceIn(0f, 1f)

internal fun Float.finiteOrZero(): Float = if (isNaN() || isInfinite()) 0f else this

internal fun Double.finiteOrZero(): Double = if (isNaN() || isInfinite()) 0.0 else this

/** Whether the fabricated glass effects are available. Always false now. */
@Composable
internal fun meloXEffectsAvailable(): Boolean = false

/** Kept for call sites that still ask for the sampling backdrop. */
@Composable
internal fun meloXBackdrop(): Backdrop? = null

/** Compat spec value; carries no rendering behaviour any more. */
data class MeloXPanelSpec(
    val blurRadius: Dp = 0.dp,
    val lensRadius: Dp = 0.dp,
    val refractionHeight: Dp = 0.dp,
    val useLens: Boolean = false,
) {
    companion object {
        fun forTone(tone: MeloXSurfaceTone): MeloXPanelSpec = MeloXPanelSpec()
    }
}

/** Resolves the flat fill for a control surface. */
@Composable
private fun controlSurfaceColor(tint: Color, surfaceColor: Color, material: MeloXSurfaceTone): Color =
    when {
        surfaceColor != Color.Unspecified -> surfaceColor
        tint != Color.Unspecified -> tint
        material == MeloXSurfaceTone.Clear -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

@Composable
fun Modifier.meloXControlSurface(
    shape: Shape,
    material: MeloXSurfaceTone = MeloXSurfaceTone.Regular,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.Unspecified,
    pressProgress: Float = 0f,
    dragOffset: Offset = Offset.Zero,
    spec: MeloXPanelSpec = MeloXPanelSpec.forTone(material),
): Modifier {
    val fill = controlSurfaceColor(tint, surfaceColor, material)
    return this
        .clip(shape)
        .background(if (enabled) fill else fill.copy(alpha = 0.45f))
        .graphicsLayer {
            if (enabled && pressProgress > 0f) {
                val scale = 1f - 0.02f * safeProgress(pressProgress)
                scaleX = scale
                scaleY = scale
            }
        }
}

@Composable
fun Modifier.meloXButtonSurface(
    shape: Shape,
    material: MeloXSurfaceTone = MeloXSurfaceTone.Regular,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.Unspecified,
    blurRadius: Dp? = null,
    lensRadius: Dp? = null,
    refractionHeight: Dp? = null,
    interaction: MeloXControlInteraction? = null,
): Modifier = meloXControlSurface(
    shape = shape,
    material = material,
    enabled = enabled,
    tint = tint,
    surfaceColor = surfaceColor,
    pressProgress = interaction?.highlight?.pressProgress ?: 0f,
)

@Composable
fun Modifier.meloXPanelSurface(
    shape: Shape,
    blurRadius: Dp = 20.dp,
    surfaceColor: Color = Color.Transparent,
): Modifier = this
    .clip(shape)
    .background(
        if (surfaceColor != Color.Transparent) surfaceColor else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
    )

@Composable
fun Modifier.meloXContentSurface(
    shape: Shape,
    surfaceColor: Color = Color.Unspecified,
): Modifier = this
    .clip(shape)
    .background(if (surfaceColor != Color.Unspecified) surfaceColor else MaterialTheme.colorScheme.surface)

@Composable
fun Modifier.meloXBottomBarSurface(
    shape: Shape,
    tint: Color,
    surfaceColor: Color,
    pressProgress: Float = 0f,
    refractionHeight: Dp = 24.dp,
    exportedBackdrop: LayerBackdrop? = null,
): Modifier = this
    .clip(shape)
    .background(surfaceColor)
    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)

@Composable
fun Modifier.meloXTabIndicator(
    shape: Shape,
    selected: Boolean,
    tint: Color,
    panelBackdrop: Backdrop? = null,
    pressProgress: Float = 0f,
    layerBlock: (androidx.compose.ui.graphics.GraphicsLayerScope.() -> Unit)? = null,
): Modifier = if (selected) this.clip(shape).background(tint) else this

@Composable
fun Modifier.meloXChromeSurface(
    shape: Shape,
    surfaceColor: Color,
    pressProgress: Float,
    refractionHeight: Dp = 10.dp,
    refractionAmount: Dp = 28.dp,
): Modifier = this.clip(shape).background(surfaceColor)

@Composable
fun Modifier.meloXSceneDisc(
    surfaceColor: Color,
    holePath: Path? = null,
): Modifier = this.background(surfaceColor)

/** No-op content transform: content no longer rides on a refracted layer. */
@Composable
fun Modifier.meloXContentTransform(interaction: MeloXControlInteraction): Modifier = this

class MeloXInteractionHighlight internal constructor() {
    val pressProgress: Float get() = 0f
}

/** Shared interaction state for a control surface and its content. */
class MeloXControlInteraction internal constructor() {
    val highlight = MeloXInteractionHighlight()
    val modifier: Modifier = Modifier
    val gestureModifier: Modifier = Modifier
}

@Composable
fun rememberMeloXControlInteraction(): MeloXControlInteraction = MeloXControlInteraction()

/** Placeholder state: tone now follows the theme instead of screenshots. */
class BottomBarToneState internal constructor() {
    internal var target: Float = Float.NaN

    /** No sampling session to release any more. */
    fun release() = Unit

    /** Bounds are no longer sampled. */
    fun updateBounds(coordinates: androidx.compose.ui.layout.LayoutCoordinates) = Unit
}

@Composable
internal fun rememberBottomBarToneState(
    systemDark: Boolean,
    paused: Boolean = false,
    lockDarkInDarkMode: Boolean = false,
): BottomBarToneState = androidx.compose.runtime.remember(systemDark) {
    BottomBarToneState().apply { target = if (systemDark) 1f else 0f }
}

@Composable
internal fun BottomBarToneTheme(
    darkness: Float,
    content: @Composable () -> Unit,
) {
    content()
}

private val ToneTransition = spring<Float>(dampingRatio = 1f, stiffness = 180f)

@Composable
internal fun BottomBarToneState.darkness(systemDark: Boolean): Float {
    val targetValue = if (target.isNaN()) (if (systemDark) 1f else 0f) else target
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = ToneTransition,
        label = "melox-bottom-bar-tone",
    ).value
}
