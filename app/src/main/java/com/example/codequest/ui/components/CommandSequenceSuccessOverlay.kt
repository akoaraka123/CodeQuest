package com.example.codequest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryPurple
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiBit(
    val nx: Float,
    val delay: Float,
    val rot: Float,
    val hue: Int,
    val fallMul: Float,
    val swayMul: Float
)

private data class BalloonSpec(
    val nx: Float,
    val speedMul: Float,
    val drift: Float,
    val sizeSp: Int
)

/**
 * Falling balloons + confetti for successful command-sequence completion only.
 * Transparent background; draws above lesson content for ~2s while [visible] is true.
 */
@Composable
fun CommandSequenceSuccessOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (!visible) {
            progress.snapTo(0f)
            return@LaunchedEffect
        }
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 2000, easing = FastOutSlowInEasing))
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(180)),
        exit = fadeOut(animationSpec = tween(220)),
        modifier = modifier
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val density = LocalDensity.current
            val wPx = with(density) { maxWidth.toPx() }
            val hPx = with(density) { maxHeight.toPx() }
            val p = progress.value

            val balloons = remember {
                List(10) {
                    BalloonSpec(
                        nx = Random.nextFloat(),
                        speedMul = 0.85f + Random.nextFloat() * 0.35f,
                        drift = Random.nextFloat() * 40f - 20f,
                        sizeSp = 24 + Random.nextInt(6)
                    )
                }
            }
            val confetti = remember {
                List(42) {
                    ConfettiBit(
                        nx = Random.nextFloat(),
                        delay = Random.nextFloat(),
                        rot = Random.nextFloat() * 360f,
                        hue = Random.nextInt(4),
                        fallMul = 0.75f + Random.nextFloat() * 0.55f,
                        swayMul = 0.5f + Random.nextFloat()
                    )
                }
            }

            Canvas(Modifier.fillMaxSize()) {
                confetti.forEach { bit ->
                    val t = ((p - bit.delay * 0.4f).coerceIn(0f, 1f))
                    val y = -24f + t * (hPx + 48f) * bit.fallMul
                    val sway =
                        sin((t * 14f + bit.nx * 8f).toDouble()).toFloat() * 18f * bit.swayMul * wPx * 0.02f
                    val x = bit.nx * wPx + sway
                    val c = when (bit.hue % 4) {
                        0 -> ActiveCyan
                        1 -> PrimaryPurple
                        2 -> CompletedGreen
                        else -> Color(0xFFFFD54F)
                    }
                    rotate(bit.rot + t * 220f, Offset(x, y)) {
                        drawRoundRect(
                            color = c.copy(alpha = 0.78f),
                            topLeft = Offset(x - 5f, y - 3f),
                            size = Size(10f, 7f),
                            cornerRadius = CornerRadius(2f, 2f)
                        )
                    }
                }
            }

            Box(Modifier.fillMaxSize()) {
                balloons.forEachIndexed { i, spec ->
                    val stagger = i * 0.06f
                    val t = ((p - stagger).coerceIn(0f, 1f))
                    val yPx = -with(density) { 36.dp.toPx() } +
                        t * (hPx + with(density) { 90.dp.toPx() }) * spec.speedMul
                    val xPx =
                        spec.nx * wPx + sin((t * 10f + spec.nx * 6f).toDouble()).toFloat() *
                            with(density) { 14.dp.toPx() }
                    Text(
                        text = "\uD83C\uDF88",
                        fontSize = spec.sizeSp.sp,
                        modifier = Modifier
                            .offset(
                                x = with(density) { xPx.toDp() } - with(density) { 14.dp },
                                y = with(density) { yPx.toDp() }
                            )
                            .rotate(spec.drift + t * 10f)
                    )
                }
            }
        }
    }
}
