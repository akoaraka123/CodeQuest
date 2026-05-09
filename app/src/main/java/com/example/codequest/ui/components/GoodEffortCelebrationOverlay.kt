package com.example.codequest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextPrimary
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay

private val OverlayNavy = Color(0xFF0A0E1A)

private val AmberAccent = Color(0xFFFFB74D)
private val CautionYellow = Color(0xFFFFCA28)

private data class EffortSparkle(
    val nx: Float,
    val ny: Float,
    val rot: Float,
    val flavor: Int,
    val sizeMul: Float
)

@Composable
fun GoodEffortCelebrationOverlay(
    scoreText: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit = {}
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(4000)
            onAnimationFinished()
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
            scaleIn(
                animationSpec = tween(420, easing = FastOutSlowInEasing),
                initialScale = 0.93f
            ),
        exit = fadeOut(animationSpec = tween(260, easing = FastOutSlowInEasing)) +
            scaleOut(
                animationSpec = tween(220, easing = FastOutSlowInEasing),
                targetScale = 1.03f
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OverlayNavy.copy(alpha = 0.92f))
        ) {
            GoodEffortSparkles(Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(PrimaryPurple, ActiveCyan, BadgeGold, PrimaryPurple)
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    PrimaryPurple.copy(alpha = 0.35f),
                                    Color(0xFF12182E).copy(alpha = 0.92f),
                                    PrimaryPurple.copy(alpha = 0.22f)
                                )
                            )
                        )
                        .padding(horizontal = 22.dp, vertical = 18.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "GOOD EFFORT!",
                            color = BadgeGold,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = scoreText,
                            color = CompletedGreen,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "You're getting better with every try!",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    lineHeight = 22.sp
                )

                Text(
                    text = "Keep practicing, coder!",
                    color = ActiveCyan.copy(alpha = 0.95f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp),
                    letterSpacing = 0.4.sp
                )

                Spacer(modifier = Modifier.height(26.dp))

                GoodEffortRobot()
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                CautionStripeBar(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun GoodEffortSparkles(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "effortSpark")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val sparkles = remember {
        List(48) {
            EffortSparkle(
                nx = Random.nextFloat(),
                ny = Random.nextFloat(),
                rot = Random.nextFloat() * 360f,
                flavor = Random.nextInt(3),
                sizeMul = 0.65f + Random.nextFloat() * 1.35f
            )
        }
    }
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val baseR = minOf(w, h) * 0.007f
        sparkles.forEach { s ->
            val x = s.nx * w
            val y = s.ny * h
            val wave = phase * 12.566f + s.nx * 17f + s.ny * 9f
            val twinkle = 0.25f + 0.62f * (((sin(wave.toDouble()).toFloat()) + 1f) * 0.5f)
            val baseColor = when (s.flavor % 3) {
                0 -> ActiveCyan
                1 -> PrimaryPurple
                else -> AmberAccent
            }
            val r = baseR * s.sizeMul
            rotate(s.rot, pivot = Offset(x, y)) {
                drawCircle(
                    color = when (s.flavor % 3) {
                        2 -> BadgeGold.copy(alpha = twinkle.coerceIn(0.22f, 0.9f))
                        else -> baseColor.copy(alpha = twinkle.coerceIn(0.18f, 0.88f))
                    },
                    radius = r,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color.White.copy(alpha = twinkle * 0.32f),
                    radius = r * 0.45f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
private fun CautionStripeBar(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val segments = 24
        val segW = size.width / segments + 1f
        for (i in 0..segments + 2) {
            val left = i * segW
            drawRect(
                color = if (i % 2 == 0) CautionYellow.copy(alpha = 0.92f) else Color(0xFF1B1B1B),
                topLeft = Offset(left - segW * 0.35f, 0f),
                size = Size(segW + 2f, size.height)
            )
        }
        drawRect(
            color = ActiveCyan.copy(alpha = 0.65f),
            topLeft = Offset(0f, 0f),
            size = Size(size.width, 1.5f)
        )
    }
}

@Composable
private fun GoodEffortRobot() {
    var settled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { settled = true }
    val scale by animateFloatAsState(
        targetValue = if (settled) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "effortRobot"
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(width = 134.dp, height = 48.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BadgeGold.copy(alpha = 0.28f),
                            PrimaryPurple.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )
        Text(
            text = "🤖",
            fontSize = 72.sp,
            modifier = Modifier
                .scale(scale)
                .padding(bottom = 2.dp),
            textAlign = TextAlign.Center
        )
    }
}
