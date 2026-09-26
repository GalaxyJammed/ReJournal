package com.example.rejournal.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rejournal.data.NoteSticker
import com.example.rejournal.data.StickerType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private fun getRotatedCornerOffset(
    localX: Float,
    localY: Float,
    rotationDegrees: Float
): Offset {
    val rad = Math.toRadians(rotationDegrees.toDouble())
    val cos = cos(rad).toFloat()
    val sin = sin(rad).toFloat()
    val rx = localX * cos - localY * sin
    val ry = localX * sin + localY * cos
    return Offset(rx, ry)
}

@Composable
fun StickerCanvas(
    stickers: List<NoteSticker>,
    onStickersChange: (List<NoteSticker>) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    selectedStickerId: String? = null,
    onSelectSticker: (String?) -> Unit = {},
    content: @Composable () -> Unit
) {
    var containerWidthPx by remember { mutableFloatStateOf(1f) }
    var containerHeightPx by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                if (coordinates.size.width > 0 && coordinates.size.height > 0) {
                    containerWidthPx = coordinates.size.width.toFloat()
                    containerHeightPx = coordinates.size.height.toFloat()
                }
            }
            .then(
                if (isEditable) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSelectSticker(null)
                    }
                } else Modifier
            )
    ) {
        content()

        stickers.forEach { sticker ->
            SingleStickerItem(
                sticker = sticker,
                containerWidthPx = containerWidthPx,
                containerHeightPx = containerHeightPx,
                isEditable = isEditable,
                isSelected = isEditable && (sticker.id == selectedStickerId),
                onSelect = {
                    onSelectSticker(sticker.id)
                    val reordered = stickers.filterNot { it.id == sticker.id } + sticker
                    onStickersChange(reordered)
                },
                onUpdateSticker = { updatedSticker ->
                    val newList = stickers.map { if (it.id == updatedSticker.id) updatedSticker else it }
                    onStickersChange(newList)
                },
                onDeleteSticker = {
                    onStickersChange(stickers.filterNot { it.id == sticker.id })
                    if (selectedStickerId == sticker.id) {
                        onSelectSticker(null)
                    }
                }
            )
        }
    }
}

@Composable
private fun SingleStickerItem(
    sticker: NoteSticker,
    containerWidthPx: Float,
    containerHeightPx: Float,
    isEditable: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onUpdateSticker: (NoteSticker) -> Unit,
    onDeleteSticker: () -> Unit
) {
    val currentSticker = rememberUpdatedState(sticker)
    val currentOnUpdate = rememberUpdatedState(onUpdateSticker)
    val currentOnSelect = rememberUpdatedState(onSelect)

    val baseSizeDp = 64.dp
    val density = LocalDensity.current
    val baseSizePx = remember(density) { with(density) { baseSizeDp.toPx() } }

    val s = currentSticker.value
    val centerX = s.xRatio * containerWidthPx
    val centerY = s.yRatio * containerHeightPx

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (centerX - (baseSizePx / 2f)).roundToInt(),
                    y = (centerY - (baseSizePx / 2f)).roundToInt()
                )
            }
            .then(
                if (isEditable) {
                    Modifier.pointerInput(containerWidthPx, containerHeightPx) {
                        detectDragGestures(
                            onDragStart = { currentOnSelect.value() },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val cur = currentSticker.value
                                val curCenterX = cur.xRatio * containerWidthPx
                                val curCenterY = cur.yRatio * containerHeightPx

                                val newCenterX = curCenterX + dragAmount.x
                                val newCenterY = curCenterY + dragAmount.y

                                val newXRatio = (newCenterX / containerWidthPx).coerceIn(0.01f, 0.99f)
                                val newYRatio = (newCenterY / containerHeightPx).coerceIn(0.01f, 0.99f)

                                currentOnUpdate.value(
                                    cur.copy(
                                        xRatio = newXRatio,
                                        yRatio = newYRatio
                                    )
                                )
                            }
                        )
                    }
                } else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .size(baseSizeDp)
                .graphicsLayer(
                    scaleX = s.scale,
                    scaleY = s.scale,
                    rotationZ = s.rotation
                )
                .then(
                    if (isSelected) {
                        Modifier
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp)
                    } else Modifier.padding(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            when (s.type) {
                StickerType.EMOJI -> {
                    Text(
                        text = s.content,
                        fontSize = 36.sp
                    )
                }
                StickerType.IMAGE_PATH -> {
                    val bitmap = remember(s.content) {
                        try {
                            BitmapFactory.decodeFile(s.content)?.asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Sticker",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("🖼️", fontSize = 32.sp)
                    }
                }
                StickerType.DRAWABLE_RES -> {
                    Text(s.content, fontSize = 36.sp)
                }
            }
        }
    }

    if (isSelected) {
        val half = (baseSizePx / 2f) * s.scale
        val handleSizeDp = 30.dp
        val handleSizePx = remember(density) { with(density) { handleSizeDp.toPx() } }

        val topLeftOffset = getRotatedCornerOffset(-half, -half, s.rotation)
        val topRightOffset = getRotatedCornerOffset(half, -half, s.rotation)
        val bottomRightOffset = getRotatedCornerOffset(half, half, s.rotation)

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (centerX + topRightOffset.x - (handleSizePx / 2f)).roundToInt(),
                        y = (centerY + topRightOffset.y - (handleSizePx / 2f)).roundToInt()
                    )
                }
                .size(handleSizeDp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error)
                .clickable { onDeleteSticker() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Delete Sticker",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (centerX + topLeftOffset.x - (handleSizePx / 2f)).roundToInt(),
                        y = (centerY + topLeftOffset.y - (handleSizePx / 2f)).roundToInt()
                    )
                }
                .size(handleSizeDp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .pointerInput(containerWidthPx, containerHeightPx) {
                    var handlePos = Offset(
                        centerX + topLeftOffset.x,
                        centerY + topLeftOffset.y
                    )
                    detectDragGestures(
                        onDragStart = {
                            val cur = currentSticker.value
                            val cX = cur.xRatio * containerWidthPx
                            val cY = cur.yRatio * containerHeightPx
                            val h = (baseSizePx / 2f) * cur.scale
                            val rot = getRotatedCornerOffset(-h, -h, cur.rotation)
                            handlePos = Offset(cX + rot.x, cY + rot.y)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            handlePos += dragAmount

                            val cur = currentSticker.value
                            val cX = cur.xRatio * containerWidthPx
                            val cY = cur.yRatio * containerHeightPx

                            val dx = handlePos.x - cX
                            val dy = handlePos.y - cY

                            val angleDegrees = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            val calculatedRotation = (angleDegrees + 135f) % 360f

                            currentOnUpdate.value(
                                cur.copy(rotation = calculatedRotation)
                            )
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.RotateRight,
                contentDescription = "Rotate Handle",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        val baseRadiusPx = remember(baseSizePx) {
            sqrt((baseSizePx / 2f) * (baseSizePx / 2f) + (baseSizePx / 2f) * (baseSizePx / 2f))
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (centerX + bottomRightOffset.x - (handleSizePx / 2f)).roundToInt(),
                        y = (centerY + bottomRightOffset.y - (handleSizePx / 2f)).roundToInt()
                    )
                }
                .size(handleSizeDp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .pointerInput(containerWidthPx, containerHeightPx) {
                    var handlePos = Offset(
                        centerX + bottomRightOffset.x,
                        centerY + bottomRightOffset.y
                    )
                    detectDragGestures(
                        onDragStart = {
                            val cur = currentSticker.value
                            val cX = cur.xRatio * containerWidthPx
                            val cY = cur.yRatio * containerHeightPx
                            val h = (baseSizePx / 2f) * cur.scale
                            val rot = getRotatedCornerOffset(h, h, cur.rotation)
                            handlePos = Offset(cX + rot.x, cY + rot.y)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            handlePos += dragAmount

                            val cur = currentSticker.value
                            val cX = cur.xRatio * containerWidthPx
                            val cY = cur.yRatio * containerHeightPx

                            val dx = handlePos.x - cX
                            val dy = handlePos.y - cY

                            val dist = sqrt(dx * dx + dy * dy)
                            val calculatedScale = (dist / baseRadiusPx).coerceIn(0.3f, 5.0f)

                            currentOnUpdate.value(
                                cur.copy(scale = calculatedScale)
                            )
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.OpenInFull,
                contentDescription = "Resize Handle",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
