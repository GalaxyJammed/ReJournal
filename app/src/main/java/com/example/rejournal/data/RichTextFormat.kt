package com.example.rejournal.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

data class CharStyle(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val strike: Boolean = false,
    val color: Long? = null
)

data class StyledChar(val char: Char, val style: CharStyle)

data class ParseResult(
    val chars: List<StyledChar>,
    val rawToTransformed: IntArray,
    val transformedToRaw: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ParseResult
        if (chars != other.chars) return false
        if (!rawToTransformed.contentEquals(other.rawToTransformed)) return false
        if (!transformedToRaw.contentEquals(other.transformedToRaw)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = chars.hashCode()
        result = 31 * result + rawToTransformed.contentHashCode()
        result = 31 * result + transformedToRaw.contentHashCode()
        return result
    }
}

object RichTextFormat {

    private val COLOR_OPEN_REGEX = Regex("^\\[c=#([0-9A-Fa-f]{6})]")
    private const val COLOR_CLOSE = "[/c]"

    fun toStyledChars(raw: String): List<StyledChar> = parse(raw).chars

    fun parse(raw: String): ParseResult {
        val chars = mutableListOf<StyledChar>()
        val rawToTransformed = IntArray(raw.length + 1)
        val transformedToRaw = mutableListOf<Int>()

        var boldDepth = 0
        var italicDepth = 0
        var underlineDepth = 0
        var strikeDepth = 0
        val colorStack = ArrayDeque<Long>()

        var i = 0
        while (i < raw.length) {
            rawToTransformed[i] = chars.size
            val remaining = raw.substring(i)
            var matchedLen = 0
            when {
                remaining.startsWith("[b]") -> { boldDepth++; matchedLen = 3 }
                remaining.startsWith("[/b]") -> { boldDepth = (boldDepth - 1).coerceAtLeast(0); matchedLen = 4 }
                remaining.startsWith("[i]") -> { italicDepth++; matchedLen = 3 }
                remaining.startsWith("[/i]") -> { italicDepth = (italicDepth - 1).coerceAtLeast(0); matchedLen = 4 }
                remaining.startsWith("[u]") -> { underlineDepth++; matchedLen = 3 }
                remaining.startsWith("[/u]") -> { underlineDepth = (underlineDepth - 1).coerceAtLeast(0); matchedLen = 4 }
                remaining.startsWith("[s]") -> { strikeDepth++; matchedLen = 3 }
                remaining.startsWith("[/s]") -> { strikeDepth = (strikeDepth - 1).coerceAtLeast(0); matchedLen = 4 }
                remaining.startsWith(COLOR_CLOSE) -> {
                    if (colorStack.isNotEmpty()) colorStack.removeLast()
                    matchedLen = COLOR_CLOSE.length
                }
                COLOR_OPEN_REGEX.find(remaining)?.range?.first == 0 -> {
                    val match = COLOR_OPEN_REGEX.find(remaining)!!
                    colorStack.addLast(("FF" + match.groupValues[1]).toLong(16))
                    matchedLen = match.value.length
                }
            }
            if (matchedLen > 0) {
                for (k in 0 until matchedLen) {
                    rawToTransformed[i + k] = chars.size
                }
                i += matchedLen
            } else {
                transformedToRaw.add(i)
                chars.add(
                    StyledChar(
                        raw[i],
                        CharStyle(
                            bold = boldDepth > 0,
                            italic = italicDepth > 0,
                            underline = underlineDepth > 0,
                            strike = strikeDepth > 0,
                            color = colorStack.lastOrNull()
                        )
                    )
                )
                i += 1
            }
        }
        rawToTransformed[raw.length] = chars.size
        transformedToRaw.add(raw.length)
        return ParseResult(chars, rawToTransformed, transformedToRaw.toIntArray())
    }

    private fun stackFor(style: CharStyle): List<String> {
        val list = mutableListOf<String>()
        style.color?.let { list.add("c:$it") }
        if (style.bold) list.add("b")
        if (style.italic) list.add("i")
        if (style.underline) list.add("u")
        if (style.strike) list.add("s")
        return list
    }

    private fun openTagFor(tag: String): String = when {
        tag == "b" -> "[b]"
        tag == "i" -> "[i]"
        tag == "u" -> "[u]"
        tag == "s" -> "[s]"
        tag.startsWith("c:") -> {
            val hex = String.format("%06X", tag.substring(2).toLong() and 0xFFFFFF)
            "[c=#$hex]"
        }
        else -> ""
    }

    private fun closeTagFor(tag: String): String = when {
        tag.startsWith("c:") -> "[/c]"
        else -> "[/$tag]"
    }

    fun serialize(chars: List<StyledChar>): String {
        val sb = StringBuilder()
        var prevStack = emptyList<String>()
        for (sc in chars) {
            val currStack = stackFor(sc.style)
            var common = 0
            while (common < prevStack.size && common < currStack.size && prevStack[common] == currStack[common]) common++
            for (idx in prevStack.size - 1 downTo common) sb.append(closeTagFor(prevStack[idx]))
            for (idx in common until currStack.size) sb.append(openTagFor(currStack[idx]))
            sb.append(sc.char)
            prevStack = currStack
        }
        for (idx in prevStack.size - 1 downTo 0) sb.append(closeTagFor(prevStack[idx]))
        return sb.toString()
    }

    fun toAnnotatedString(chars: List<StyledChar>): AnnotatedString = buildAnnotatedString {
        append(chars.map { it.char }.joinToString(""))
        if (chars.isEmpty()) return@buildAnnotatedString
        var runStart = 0
        var current = chars.first().style
        for (idx in 1..chars.size) {
            val style = chars.getOrNull(idx)?.style
            if (style != current) {
                addStyle(current.toSpanStyle(), runStart, idx)
                runStart = idx
                if (style != null) current = style
            }
        }
    }

    private fun CharStyle.toSpanStyle(): SpanStyle = SpanStyle(
        fontWeight = if (bold) FontWeight.Bold else null,
        fontStyle = if (italic) FontStyle.Italic else null,
        textDecoration = when {
            underline && strike -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
            underline -> TextDecoration.Underline
            strike -> TextDecoration.LineThrough
            else -> null
        },
        color = color?.let { Color(it) } ?: Color.Unspecified
    )
}