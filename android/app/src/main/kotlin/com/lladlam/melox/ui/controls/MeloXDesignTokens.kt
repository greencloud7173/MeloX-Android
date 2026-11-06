package com.lladlam.melox.ui.controls

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Surface weight of a control. Kept as a semantic hint, no material effect. */
enum class MeloXSurfaceTone {
    Clear,
    Regular,
}

/** Semantic colors shared by the MeloX controls. */
object MeloXSystemColors {
    val Blue = Color(0xFF0A84FF)
    val Red = Color(0xFFFF3B30)
    val SecondaryFill = Color(0x26787880)
    val TertiaryFill = Color(0x1F767680)
    val Separator = Color(0x4A3C3C43)
}

/** Shared corner geometry, aligned with the Material3 shape scale. */
object MeloXShapes {
    val capsule: Shape = RoundedCornerShape(50)
    val compact: Shape = RoundedCornerShape(12.dp)
    val card: Shape = RoundedCornerShape(16.dp)
    val largeCard: Shape = RoundedCornerShape(28.dp)
    val sheet: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val circle: Shape = CircleShape
}

/**
 * Type scale used by the list/heading controls. Family is inherited from
 * MaterialTheme typography, which already follows the system-font toggle.
 */
object MeloXTypography {
    val largeTitle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 41.sp)
    val title2 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp)
    val headline = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp)
    val body = TextStyle(fontWeight = FontWeight.Normal, fontSize = 17.sp, lineHeight = 22.sp)
    val subheadline = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 20.sp)
    val caption = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp)
}

/**
 * The type styles above carry no fontFamily. Passing them straight to
 * `Text(style = …)` REPLACES the ambient style instead of merging, so a null
 * family would fall back to the platform default. Merging into [LocalTextStyle]
 * keeps inheritance from MaterialTheme typography.
 */
@Composable
fun TextStyle.inheritAmbientFamily(): TextStyle = LocalTextStyle.current.merge(this)

/** Emphasis of [MeloXButton]. */
enum class MeloXButtonStyle {
    Bordered,
    BorderedProminent,
    Plain,
    Destructive,
}
