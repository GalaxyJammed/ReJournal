package com.example.rejournal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.CharStyle
import com.example.rejournal.data.RichTextFormat
import com.example.rejournal.data.StyledChar

enum class ListMode { NONE, BULLET, NUMBER }

data class RichNoteValue(
    val chars: List<StyledChar> = emptyList(),
    val selection: TextRange = TextRange.Zero,
    val activeStyle: CharStyle = CharStyle(),
    val listMode: ListMode = ListMode.NONE
)

fun richNoteValueFromRaw(raw: String): RichNoteValue =
    RichNoteValue(chars = RichTextFormat.toStyledChars(raw))

fun RichNoteValue.toRawText(): String = RichTextFormat.serialize(chars)

private val presetColors = listOf(
    Color(0xFFE57373), Color(0xFFFFB74D), Color(0xFFFFF176),
    Color(0xFF81C784), Color(0xFF64B5F6), Color(0xFFBA68C8)
)

private fun applyEdit(
    old: RichNoteValue,
    newText: String,
    newSelection: TextRange
): RichNoteValue {
    val oldText = old.chars.joinToString("") { it.char.toString() }
    if (oldText == newText) {
        val synced = if (newSelection.collapsed && newSelection.start > 0 && newSelection.start <= old.chars.size) {
            old.chars[newSelection.start - 1].style
        } else old.activeStyle
        return old.copy(selection = newSelection, activeStyle = synced)
    }

    var prefix = 0
    while (prefix < oldText.length && prefix < newText.length && oldText[prefix] == newText[prefix]) prefix++
    var suffix = 0
    while (suffix < oldText.length - prefix && suffix < newText.length - prefix &&
        oldText[oldText.length - 1 - suffix] == newText[newText.length - 1 - suffix]
    ) suffix++

    val removedEnd = oldText.length - suffix
    val insertedText = newText.substring(prefix, newText.length - suffix)

    val newChars = old.chars.toMutableList()
    if (prefix < removedEnd) newChars.subList(prefix, removedEnd).clear()
    val insertedStyled = insertedText.map { StyledChar(it, old.activeStyle) }
    newChars.addAll(prefix, insertedStyled)

    var resultChars: MutableList<StyledChar> = newChars
    var resultSelection = newSelection

    if (insertedText == "\n" && old.listMode != ListMode.NONE) {
        val plainSoFar = resultChars.joinToString("") { it.char.toString() }
        val prefixStr = if (old.listMode == ListMode.BULLET) {
            "• "
        } else {
            "${nextListNumber(plainSoFar, prefix + 1)}. "
        }
        val prefixStyled = prefixStr.map { StyledChar(it, old.activeStyle) }
        resultChars.addAll(prefix + 1, prefixStyled)
        resultSelection = TextRange(prefix + 1 + prefixStr.length)
    }

    return old.copy(chars = resultChars, selection = resultSelection)
}

private fun nextListNumber(text: String, fromIndex: Int): Int {
    val before = text.substring(0, fromIndex.coerceAtMost(text.length))
    val lines = before.split("\n")
    for (i in lines.size - 2 downTo 0) {
        val match = Regex("^(\\d+)\\. ").find(lines[i])
        if (match != null) return match.groupValues[1].toInt() + 1
        if (lines[i].isBlank()) continue
        break
    }
    return 1
}

private fun toggleBooleanStyle(
    value: RichNoteValue,
    onValueChange: (RichNoteValue) -> Unit,
    pick: (CharStyle) -> Boolean,
    apply: (CharStyle, Boolean) -> CharStyle
) {
    val sel = value.selection
    if (!sel.collapsed) {
        val range = sel.min until sel.max
        val allActive = range.all { pick(value.chars[it].style) }
        val newChars = value.chars.toMutableList()
        for (i in range) newChars[i] = newChars[i].copy(style = apply(newChars[i].style, !allActive))
        onValueChange(value.copy(chars = newChars))
    } else {
        onValueChange(value.copy(activeStyle = apply(value.activeStyle, !pick(value.activeStyle))))
    }
}

private fun applyColor(value: RichNoteValue, onValueChange: (RichNoteValue) -> Unit, colorLong: Long) {
    val sel = value.selection
    if (!sel.collapsed) {
        val range = sel.min until sel.max
        val allSame = range.all { value.chars[it].style.color == colorLong }
        val newColor = if (allSame) null else colorLong
        val newChars = value.chars.toMutableList()
        for (i in range) newChars[i] = newChars[i].copy(style = newChars[i].style.copy(color = newColor))
        onValueChange(value.copy(chars = newChars))
    } else {
        val newColor = if (value.activeStyle.color == colorLong) null else colorLong
        onValueChange(value.copy(activeStyle = value.activeStyle.copy(color = newColor)))
    }
}

private fun toggleList(value: RichNoteValue, onValueChange: (RichNoteValue) -> Unit, mode: ListMode) {
    val newMode = if (value.listMode == mode) ListMode.NONE else mode
    var newValue = value.copy(listMode = newMode)

    if (newMode != ListMode.NONE) {
        val text = newValue.chars.joinToString("") { it.char.toString() }
        val cursor = newValue.selection.min
        val lineStart = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
        val restOfLine = text.substring(lineStart)
        val alreadyHasPrefix = restOfLine.startsWith("• ") || Regex("^\\d+\\. ").containsMatchIn(restOfLine)
        if (!alreadyHasPrefix) {
            val prefixStr = if (newMode == ListMode.BULLET) "• " else "1. "
            val prefixStyled = prefixStr.map { StyledChar(it, newValue.activeStyle) }
            val newChars = newValue.chars.toMutableList()
            newChars.addAll(lineStart, prefixStyled)
            newValue = newValue.copy(chars = newChars, selection = TextRange(lineStart + prefixStr.length))
        }
    }
    onValueChange(newValue)
}

@Composable
fun RichNoteEditor(
    value: RichNoteValue,
    onValueChange: (RichNoteValue) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 120.dp,
    fillAvailableSpace: Boolean = false,
    placeholder: String = ""
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val plainText = remember(value.chars) { value.chars.joinToString("") { it.char.toString() } }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (fillAvailableSpace) Modifier.weight(1f, fill = true) else Modifier.height(minHeight))
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            if (plainText.isEmpty() && placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            BasicTextField(
                value = androidx.compose.ui.text.input.TextFieldValue(plainText, value.selection),
                onValueChange = { newTfv -> onValueChange(applyEdit(value, newTfv.text, newTfv.selection)) },
                modifier = Modifier.fillMaxSize(),
                visualTransformation = {
                    TransformedText(RichTextFormat.toAnnotatedString(value.chars), OffsetMapping.Identity)
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
            )
        }

        Text(
            "Style",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ToggleIcon(
                icon = Icons.Filled.FormatBold,
                label = "Bold",
                active = isSelectionOrActive(value) { it.bold },
                onClick = { toggleBooleanStyle(value, onValueChange, { it.bold }) { s, v -> s.copy(bold = v) } }
            )
            ToggleIcon(
                icon = Icons.Filled.FormatItalic,
                label = "Italic",
                active = isSelectionOrActive(value) { it.italic },
                onClick = { toggleBooleanStyle(value, onValueChange, { it.italic }) { s, v -> s.copy(italic = v) } }
            )
            ToggleIcon(
                icon = Icons.Filled.FormatUnderlined,
                label = "Underline",
                active = isSelectionOrActive(value) { it.underline },
                onClick = { toggleBooleanStyle(value, onValueChange, { it.underline }) { s, v -> s.copy(underline = v) } }
            )
            ToggleIcon(
                icon = Icons.Filled.FormatStrikethrough,
                label = "Strikethrough",
                active = isSelectionOrActive(value) { it.strike },
                onClick = { toggleBooleanStyle(value, onValueChange, { it.strike }) { s, v -> s.copy(strike = v) } }
            )
            ToggleIcon(
                icon = Icons.Filled.FormatListBulleted,
                label = "Bullet list",
                active = value.listMode == ListMode.BULLET,
                onClick = { toggleList(value, onValueChange, ListMode.BULLET) }
            )
            ToggleIcon(
                icon = Icons.Filled.FormatListNumbered,
                label = "Numbered list",
                active = value.listMode == ListMode.NUMBER,
                onClick = { toggleList(value, onValueChange, ListMode.NUMBER) }
            )
            ToggleIcon(
                icon = Icons.Filled.Palette,
                label = "Text color",
                active = value.activeStyle.color != null || showColorPicker,
                onClick = { showColorPicker = !showColorPicker }
            )
        }

        if (showColorPicker) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(presetColors) { color ->
                    val colorLong = color.toArgb().toLong() and 0xFFFFFFFFL
                    val isActive = value.activeStyle.color == colorLong
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color, CircleShape)
                            .then(
                                if (isActive) Modifier.background(Color.Black.copy(alpha = 0.15f), CircleShape)
                                else Modifier
                            )
                            .clickable { applyColor(value, onValueChange, colorLong) }
                    )
                }
            }
        }
    }
}

private fun isSelectionOrActive(value: RichNoteValue, pick: (CharStyle) -> Boolean): Boolean {
    val sel = value.selection
    return if (!sel.collapsed) {
        val range = sel.min until sel.max
        !range.isEmpty() && range.all { pick(value.chars[it].style) }
    } else {
        pick(value.activeStyle)
    }
}

@Composable
private fun ToggleIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val background = if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val tint = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(8.dp))
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = label, tint = tint)
        }
    }
}