package com.example.rejournal.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp
import kotlin.math.abs

enum class ButterflyType {
    STANDARD, DETAILED, FLYING
}

enum class ButterflyCorner {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

@Composable
fun ButterflyCardWrapper(
    seed: String,
    modifier: Modifier = Modifier,
    indexOffset: Int = 0,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier,
        propagateMinConstraints = false
    ) {
        content()

        val finalHashCode = remember(seed, indexOffset) {
            seed.hashCode() + indexOffset
        }

        val shouldShow = remember(finalHashCode) {
            indexOffset == 0 || (abs(finalHashCode) % 100) < 35
        }

        val type = remember(finalHashCode) {
            val idx = abs(finalHashCode) % 3
            ButterflyType.entries[idx]
        }

        val corner = remember(finalHashCode) {
            ButterflyCorner.entries[indexOffset % 4]
        }
        
        val rotation = remember(finalHashCode) {
            val deg = ((abs(finalHashCode) shr 4) % 60) - 30
            deg.toFloat()
        }
        val sizeDp = remember(finalHashCode) {
            val s = 16 + ((abs(finalHashCode) shr 7) % 9)
            s.dp
        }

        if (shouldShow) {
            val alignment = when (corner) {
                ButterflyCorner.TOP_LEFT -> Alignment.TopStart
                ButterflyCorner.TOP_RIGHT -> Alignment.TopEnd
                ButterflyCorner.BOTTOM_LEFT -> Alignment.BottomStart
                ButterflyCorner.BOTTOM_RIGHT -> Alignment.BottomEnd
            }

            val offsetModifier = when (corner) {
                ButterflyCorner.TOP_LEFT -> Modifier.offset(x = 6.dp, y = 6.dp)
                ButterflyCorner.TOP_RIGHT -> Modifier.offset(x = (-6).dp, y = 6.dp)
                ButterflyCorner.BOTTOM_LEFT -> Modifier.offset(x = 6.dp, y = (-6).dp)
                ButterflyCorner.BOTTOM_RIGHT -> Modifier.offset(x = (-6).dp, y = (-6).dp)
            }

            Box(
                modifier = Modifier
                    .align(alignment)
                    .then(offsetModifier)
                    .size(sizeDp)
                    .graphicsLayer {
                        rotationZ = rotation
                    }
            ) {
                DecorativeButterfly(type = type)
            }
        }
    }
}

@Composable
fun DecorativeButterfly(
    type: ButterflyType,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val isDark = surfaceColor.luminance() < 0.5f

    val colorScheme = MaterialTheme.colorScheme
    val wingColor = remember(isDark, colorScheme) {
        if (isDark) {
            colorScheme.primary.copy(alpha = 0.85f)
        } else {
            colorScheme.secondary.copy(alpha = 0.45f)
        }
    }

    val bodyColor = remember(isDark, colorScheme) {
        if (isDark) {
            colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
        } else {
            colorScheme.primary.copy(alpha = 0.75f)
        }
    }

    val paths = remember(type) {
        when (type) {
            ButterflyType.STANDARD -> {
                listOf(
                    PathParser().parsePathString("M31.066 13.628c-5.686-3.184-11.96-2.039-16.737 2.968 5.664 3.11 11.99 2.022 16.737-2.968z").toPath(),
                    PathParser().parsePathString("M3.619 2.465c6.227 1.758 9.829 6.968 9.668 13.838-6.16-1.794-9.838-7.001-9.668-13.838v0z").toPath(),
                    PathParser().parsePathString("M13.92 17.77c5.325 2.108 7.058 6.404 4.701 11.425-5.25-2.126-7.057-6.43-4.701-11.425v0z").toPath(),
                    PathParser().parsePathString("M12.927 17.421c-5.176-2.124-9.441-0.286-11.402 4.827 5.138 2.061 9.458 0.267 11.402-4.827z").toPath()
                )
            }
            ButterflyType.DETAILED -> {
                listOf(
                    PathParser().parsePathString("M222.97 43.094l-17.72 5.937c18.246 54.362 27.075 112.164 24.406 174.47l-.875 20.5 16.032-12.78c47.213-37.597 81.827-86.216 102.282-146.095l-17.688-6.063c-17.04 49.88-44.08 90.906-80.656 124.22.156-56.652-8.915-109.937-25.78-160.188zM53.812 51.22C51.09 160.79 110.03 244.245 208.75 273c2.573-110.1-55.144-193.608-154.938-221.78zm335.156 150.374c-58.436-.03-115.656 25.943-163.5 76.094 90.775 49.848 192.148 32.407 268.217-47.563-34.172-19.135-69.658-28.513-104.718-28.53zM136.936 277.03c-52.45.582-94.1 32.36-116.687 91.25 82.336 33.03 151.56 4.26 182.72-77.374-23.333-9.574-45.51-14.102-66.032-13.875zm81.97 19.47c-37.76 80.056-8.793 149.03 75.343 183.094 37.786-80.46 9.994-149.316-75.344-183.094z").toPath()
                )
            }
            ButterflyType.FLYING -> {
                listOf(
                    PathParser().parsePathString("M40.36,19.924c-0.55,0-1.094,0.035-1.634,0.093c0.058-0.539,0.093-1.084,0.093-1.634c0-4.033-1.571-7.824-4.423-10.676c-2.851-2.852-6.642-4.422-10.675-4.422c-4.032,0-7.824,1.571-10.675,4.423c-4.136,4.136-5.354,10.09-3.678,15.31c-2.209,0.48-4.24,1.574-5.877,3.211c-4.65,4.65-4.65,12.217,0,16.867c2.253,2.253,5.248,3.494,8.433,3.494c0.083,0,0.164-0.01,0.246-0.012c-0.062,3.134,1.09,6.287,3.476,8.673c2.252,2.253,5.247,3.494,8.433,3.494s6.181-1.241,8.434-3.494c1.67-1.67,2.729-3.717,3.2-5.869c1.482,0.478,3.043,0.736,4.646,0.736c0.001,0,0.001,0,0.001,0c4.032,0,7.823-1.57,10.675-4.422s4.423-6.643,4.423-10.676c0-4.032-1.571-7.824-4.423-10.675C48.184,21.495,44.393,19.924,40.36,19.924z").toPath(),
                    PathParser().parsePathString("M58.01,16.458c-3.07-3.07-7.188-4.593-11.391-4.336c0.258-4.188-1.265-8.319-4.335-11.39c-0.977-0.977-2.559-0.977-3.535,0s-0.977,2.559,0,3.535c2.683,2.684,3.584,6.592,2.352,10.2c-0.308,0.902-0.076,1.901,0.599,2.575c0.675,0.675,1.673,0.907,2.576,0.598c3.605-1.23,7.515-0.33,10.199,2.353c0.488,0.488,1.128,0.732,1.768,0.732s1.28-0.244,1.768-0.732C58.987,19.017,58.987,17.434,58.01,16.458z").toPath()
                )
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        when (type) {
            ButterflyType.STANDARD -> {
                val scaleX = width / 32f
                val scaleY = height / 32f
                
                drawContext.canvas.save()
                drawContext.canvas.scale(scaleX, scaleY)
                
                drawPath(paths[0], color = wingColor)
                drawPath(paths[1], color = wingColor)
                drawPath(paths[2], color = bodyColor)
                drawPath(paths[3], color = bodyColor)
                
                drawContext.canvas.restore()
            }
            ButterflyType.DETAILED -> {
                val scaleX = width / 512f
                val scaleY = height / 512f

                drawContext.canvas.save()
                drawContext.canvas.scale(scaleX, scaleY)
                
                drawPath(paths[0], color = wingColor)
                drawPath(paths[0], color = bodyColor.copy(alpha = 0.5f), style = Stroke(width = 2f))
                
                drawContext.canvas.restore()
            }
            ButterflyType.FLYING -> {
                val scaleX = width / 58.746f
                val scaleY = height / 58.746f

                drawContext.canvas.save()
                drawContext.canvas.scale(scaleX, scaleY)
                
                drawPath(paths[0], color = wingColor)
                drawPath(paths[1], color = bodyColor)
                
                drawContext.canvas.restore()
            }
        }
    }
}
