package com.lladlam.melox.ui.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lladlam.melox.ui.settings.MeloXSettingsPreferences

/**
 * Material3 implementations of every control that used to be a hand-drawn
 * iOS-style "glass" widget. The old drawing code is gone: these are ordinary
 * Material components with the same parameter surface, so call sites keep
 * working while the look becomes standard Material3.
 */

@Composable
fun MeloXButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: MeloXButtonStyle = MeloXButtonStyle.Bordered,
    material: MeloXSurfaceTone = MeloXSurfaceTone.Regular,
    shape: Shape = MeloXShapes.capsule,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val container = when {
        surfaceColor != Color.Unspecified -> surfaceColor
        style == MeloXButtonStyle.BorderedProminent -> scheme.error
        style == MeloXButtonStyle.Destructive -> scheme.errorContainer
        style == MeloXButtonStyle.Plain -> Color.Transparent
        else -> scheme.surfaceVariant
    }
    val contentColor = when (style) {
        MeloXButtonStyle.BorderedProminent -> scheme.onError
        MeloXButtonStyle.Destructive -> scheme.onErrorContainer
        else -> scheme.onSurface
    }
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        color = container,
        contentColor = contentColor,
        border = if (style == MeloXButtonStyle.Bordered) BorderStroke(1.dp, scheme.outlineVariant) else null,
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
fun MeloXIconButton(
    symbol: MeloXSymbol,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(44.dp),
    enabled: Boolean = true,
    selected: Boolean = false,
    contentDescription: String? = null,
) {
    IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        MeloXSymbolIcon(
            symbol = symbol,
            modifier = Modifier.size(22.dp),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            variant = if (selected) MeloXSymbolVariant.Fill else MeloXSymbolVariant.Regular,
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun MeloXCard(
    modifier: Modifier = Modifier,
    shape: Shape = MeloXShapes.card,
    material: MeloXSurfaceTone = MeloXSurfaceTone.Regular,
    surfaceColor: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CardDefaults.cardColors(
        containerColor = if (surfaceColor != Color.Unspecified) surfaceColor else MaterialTheme.colorScheme.surfaceVariant,
    )
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors) {
            Column(content = content)
        }
    } else {
        Card(modifier = modifier, shape = shape, colors = colors) {
            Column(content = content)
        }
    }
}

@Composable
fun MeloXTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onFocusChanged: ((Boolean) -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = if (onFocusChanged == null) {
            modifier
        } else {
            modifier.onFocusChanged { onFocusChanged(it.isFocused) }
        },
        enabled = enabled,
        singleLine = singleLine,
        textStyle = textStyle,
        placeholder = placeholder,
        leadingIcon = leadingContent,
        trailingIcon = trailingContent,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}

@Composable
fun MeloXSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled, modifier = modifier)
}

@Composable
fun MeloXSegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, label ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onSelected(index) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = items.size),
            ) {
                Text(text = label, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun MeloXToolbarButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    if (selected) {
        FilledTonalButton(onClick = onClick, modifier = modifier) { Text(title) }
    } else {
        TextButton(onClick = onClick, modifier = modifier) { Text(title) }
    }
}

@Composable
fun MeloXTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    navigation: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    titleStyle: TextStyle = MeloXTypography.largeTitle,
) {
    Column(modifier = modifier.fillMaxWidth().padding(contentPadding)) {
        if (navigation != null) {
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) { navigation() }
                Text(
                    text = title,
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    style = titleStyle.inheritAmbientFamily(),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), content = actions)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().height(44.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), content = actions)
            }
            Text(
                text = title,
                style = titleStyle.inheritAmbientFamily(),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        subtitle?.let {
            Text(
                text = it,
                style = MeloXTypography.subheadline.inheritAmbientFamily(),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.56f),
            )
        }
    }
}

@Composable
fun MeloXGroupedList(
    modifier: Modifier = Modifier,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MeloXShapes.largeCard)
            .background(surfaceColor),
        content = content,
    )
}

@Composable
fun MeloXListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    detail: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    chevronTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: (() -> Unit)? = null,
    showTopSeparator: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showTopSeparator) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (subtitle == null) 56.dp else 68.dp)
                .then(
                    if (onClick != null) {
                        Modifier.combinedClickable(onClick = onClick, onLongClick = null)
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading?.let {
                it()
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    title,
                    style = MeloXTypography.body.inheritAmbientFamily(),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                subtitle?.let {
                    Text(
                        it,
                        style = MeloXTypography.subheadline.inheritAmbientFamily(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            detail?.let {
                Text(
                    it,
                    style = MeloXTypography.subheadline.inheritAmbientFamily(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            trailing?.invoke()
            if (onClick != null && trailing == null) {
                Spacer(Modifier.width(8.dp))
                MeloXSymbolIcon(
                    MeloXSymbol.ChevronRight,
                    Modifier.size(20.dp),
                    chevronTint,
                    iconSize = 19.sp,
                )
            }
        }
    }
}

@Composable
fun MeloXListIcon(
    symbol: MeloXSymbol,
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    MeloXSymbolIcon(symbol, modifier, tint, iconSize = 22.sp)
}

@Composable
fun <T> MeloXDropdown(
    title: String,
    selected: T,
    items: List<Pair<T, String>>,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    grouped: Boolean = false,
    showTopSeparator: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    val rowClick: (() -> Unit)? = if (enabled) ({ expanded = true }) else null
    Box(modifier) {
        MeloXListRow(
            title = title,
            detail = items.firstOrNull { it.first == selected }?.second.orEmpty(),
            onClick = rowClick,
            showTopSeparator = showTopSeparator,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun MeloXDropdown(
    context: android.content.Context,
    title: String,
    prefKey: String,
    default: String,
    items: List<Pair<String, String>>,
    grouped: Boolean = false,
    showTopSeparator: Boolean = false,
) {
    val current = MeloXSettingsPreferences.string(context, prefKey, default)
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = items.firstOrNull { it.first == current }?.second ?: default
    Box {
        MeloXListRow(
            title = title,
            detail = selectedLabel,
            onClick = { expanded = true },
            showTopSeparator = showTopSeparator,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        MeloXSettingsPreferences.setString(context, prefKey, value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun MeloXDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    maxHeightFraction: Float? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.widthIn(max = 480.dp),
            shape = MeloXShapes.card,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(20.dp), content = content)
        }
    }
}

@Composable
fun MeloXSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp), content = content)
    }
}

@Composable
fun MeloXSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onTransientValueChange: (Float) -> Unit = {},
    onValueChangeFinished: () -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float>,
    stepSize: Float = 0f,
    visibilityThreshold: Float = 0.01f,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val steps = if (stepSize > 0f) {
        ((valueRange.endInclusive - valueRange.start) / stepSize).toInt().minus(1).coerceAtLeast(0)
    } else {
        0
    }
    Slider(
        value = value,
        onValueChange = {
            onTransientValueChange(it)
            onValueChange(it)
        },
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
    )
}

@Composable
fun MeloXLabeledSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    valueLabel: (Float) -> String = { "%.0f".format(it) },
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MeloXTypography.body.inheritAmbientFamily(),
            )
            Text(
                text = valueLabel(value),
                style = MeloXTypography.subheadline.inheritAmbientFamily(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
        )
    }
}

@Composable
fun MeloXStepper(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MeloXTypography.body.inheritAmbientFamily(),
        )
        IconButton(onClick = { onValueChange((value - 1).coerceIn(range)) }, enabled = value > range.first) {
            MeloXSymbolIcon(MeloXSymbol.ChevronLeft, Modifier.size(20.dp), MaterialTheme.colorScheme.onSurface)
        }
        Text(text = value.toString(), style = MeloXTypography.body.inheritAmbientFamily())
        IconButton(onClick = { onValueChange((value + 1).coerceIn(range)) }, enabled = value < range.last) {
            MeloXSymbolIcon(MeloXSymbol.ChevronRight, Modifier.size(20.dp), MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun MeloXAlert(
    visible: Boolean,
    title: String,
    message: String,
    confirmTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    fieldValue: String? = null,
    onFieldValueChange: ((String) -> Unit)? = null,
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text(message)
                if (fieldValue != null && onFieldValueChange != null) {
                    MeloXTextField(
                        value = fieldValue,
                        onValueChange = onFieldValueChange,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmTitle) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

data class MeloXContextAction(
    val title: String,
    val symbol: MeloXSymbol,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
fun MeloXContextMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    actions: List<MeloXContextAction>,
    anchor: @Composable () -> Unit,
) {
    Box {
        anchor()
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            actions.forEach { action ->
                DropdownMenuItem(
                    text = {
                        Text(
                            action.title,
                            color = if (action.destructive) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    },
                    leadingIcon = {
                        MeloXSymbolIcon(
                            action.symbol,
                            Modifier.size(20.dp),
                            if (action.destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    onClick = {
                        onDismiss()
                        action.onClick()
                    },
                )
            }
        }
    }
}

@Composable
fun MeloXActionSheet(
    visible: Boolean,
    title: String,
    actions: List<MeloXContextAction>,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    MeloXSheet(visible = true, onDismiss = onDismiss) {
        Text(
            text = title,
            style = MeloXTypography.headline.inheritAmbientFamily(),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        actions.forEach { action ->
            MeloXListRow(
                title = action.title,
                leading = {
                    MeloXSymbolIcon(
                        action.symbol,
                        Modifier.size(20.dp),
                        if (action.destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                onClick = {
                    onDismiss()
                    action.onClick()
                },
                showTopSeparator = false,
            )
        }
    }
}

@Composable
fun MeloXSearchToolbar(
    value: String,
    onValueChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MeloXTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(placeholder) },
            leadingContent = {
                MeloXSymbolIcon(MeloXSymbol.Search, Modifier.size(18.dp), MaterialTheme.colorScheme.onSurfaceVariant)
            },
        )
        IconButton(onClick = onClose) {
            MeloXSymbolIcon(MeloXSymbol.Xmark, Modifier.size(18.dp), MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun MeloXModalSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MeloXShapes.card,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
    ) {
        Column(content = content)
    }
}

@Composable
fun MeloXColorPicker(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .combinedClickable(onClick = { onColorSelected(color) }, onLongClick = null),
                contentAlignment = Alignment.Center,
            ) {
                if (color == selectedColor) {
                    MeloXSymbolIcon(MeloXSymbol.Check, Modifier.size(16.dp), Color.White)
                }
            }
        }
    }
}

data class MeloXSwipeAction(
    val label: String,
    val symbol: MeloXSymbol,
    val color: Color,
    val onInvoke: () -> Unit,
)

/**
 * Row with contextual actions. Swipe-to-reveal is replaced by long-press to
 * open a Material dropdown: the actions stay reachable without custom gesture
 * rendering.
 */
@Composable
fun MeloXSwipeRow(
    startActions: List<MeloXSwipeAction>,
    endActions: List<MeloXSwipeAction>,
    startFullSwipeActionIndex: Int = 0,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val actions = startActions + endActions
    var menuOpen by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Box(
            modifier = Modifier.combinedClickable(
                onClick = onClick,
                onLongClick = {
                    if (actions.isEmpty()) onLongClick?.invoke() else menuOpen = true
                },
            ),
        ) {
            content()
        }
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            actions.forEach { action ->
                DropdownMenuItem(
                    text = { Text(action.label) },
                    leadingIcon = { MeloXSymbolIcon(action.symbol, Modifier.size(20.dp), action.color) },
                    onClick = {
                        menuOpen = false
                        action.onInvoke()
                    },
                )
            }
        }
    }
}

@Composable
fun MeloXPinnedListPage(
    title: String,
    onNavigateBack: () -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    subtitle: String? = null,
    horizontalPadding: Dp = 20.dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(10.dp),
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                top = 12.dp,
                end = horizontalPadding,
                bottom = bottomPadding,
            ),
            verticalArrangement = verticalArrangement,
        ) {
            item(key = "melox-header:$title") {
                Column(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            MeloXSymbolIcon(
                                MeloXSymbol.ChevronLeft,
                                Modifier.size(24.dp),
                                MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Text(
                            text = title,
                            modifier = Modifier.weight(1f),
                            style = MeloXTypography.title2.inheritAmbientFamily(),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        actions?.let { Row(horizontalArrangement = Arrangement.spacedBy(8.dp), content = it) }
                    }
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MeloXTypography.subheadline.inheritAmbientFamily(),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.56f),
                        )
                    }
                }
            }
            content()
        }
    }
}
