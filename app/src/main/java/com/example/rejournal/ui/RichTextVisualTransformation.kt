package com.example.rejournal.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.rejournal.data.RichTextFormat


class RichTextVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val result = RichTextFormat.parse(text.text)
        val annotated = RichTextFormat.toAnnotatedString(result.chars)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val idx = offset.coerceIn(0, result.rawToTransformed.size - 1)
                return result.rawToTransformed[idx]
            }
            override fun transformedToOriginal(offset: Int): Int {
                val idx = offset.coerceIn(0, result.transformedToRaw.size - 1)
                return result.transformedToRaw[idx]
            }
        }
        return TransformedText(annotated, mapping)
    }
}