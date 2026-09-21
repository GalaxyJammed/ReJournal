package com.example.rejournal.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun Modifier.verticalScrollbar(scrollState: ScrollState, color: Color = MaterialTheme.colorScheme.onSurfaceVariant): Modifier {
    return this.then(
        Modifier.drawWithContent {
            drawContent()
            val maxValue = scrollState.maxValue
            if (maxValue > 0) {
                val viewportHeight = size.height
                val contentHeight = viewportHeight + maxValue
                val thumbHeight = (viewportHeight / contentHeight * viewportHeight)
                    .coerceIn(16.dp.toPx(), viewportHeight * 0.2f)
                val scrollFraction = scrollState.value.toFloat() / maxValue
                val thumbY = scrollFraction * (viewportHeight - thumbHeight)
                drawRoundRect(
                    color = color.copy(alpha = 0.5f),
                    topLeft = Offset(size.width - 4.dp.toPx(), thumbY),
                    size = Size(2.dp.toPx(), thumbHeight),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
        }
    )
}

@Composable
fun Modifier.verticalScrollbar(listState: LazyListState, color: Color = MaterialTheme.colorScheme.onSurfaceVariant): Modifier {
    val layoutInfo by androidx.compose.runtime.derivedStateOf { listState.layoutInfo }
    return this.then(
        Modifier.drawWithContent {
            drawContent()
            val info = layoutInfo
            val totalItems = info.totalItemsCount
            val visibleItems = info.visibleItemsInfo
            if (totalItems > 0 && visibleItems.isNotEmpty()) {
                val viewportHeight = size.height
                val firstVisible = visibleItems.first()
                val averageItemSize = if (visibleItems.isNotEmpty()) {
                    visibleItems.sumOf { it.size } / visibleItems.size.toFloat()
                } else viewportHeight
                val estimatedContentHeight = averageItemSize * totalItems
                if (estimatedContentHeight > viewportHeight) {
                    val thumbHeight = (viewportHeight / estimatedContentHeight * viewportHeight)
                        .coerceIn(16.dp.toPx(), viewportHeight * 0.2f)
                    val scrolledPastPx = firstVisible.index * averageItemSize - firstVisible.offset
                    val maxScrollPx = estimatedContentHeight - viewportHeight
                    val scrollFraction = (scrolledPastPx / maxScrollPx).coerceIn(0f, 1f)
                    val thumbY = scrollFraction * (viewportHeight - thumbHeight)
                    drawRoundRect(
                        color = color.copy(alpha = 0.5f),
                        topLeft = Offset(size.width - 4.dp.toPx(), thumbY),
                        size = Size(2.dp.toPx(), thumbHeight),
                        cornerRadius = CornerRadius(2.dp.toPx())
                    )
                }
            }
        }
    )
}