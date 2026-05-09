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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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

private data class SparkleParticle(
    val nx: Float,
    val ny: Float,
    val rot: Float,
    val cyan: Boolean,
    val sizeMul: Float
)

@Composable
fun PerfectScoreCelebrationOverlay(
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
                initialScale = 0.92f
            ),
        exit = fadeOut(animationSpec = tween(260, easing = FastOutSlowInEasing)) +
            scaleOut(
                animationSpec = tween(220, easing = FastOutSlowInEasing),
                targetScale = 1.04f
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OverlayNavy.copy(alpha = 0.92f))
        ) {
            CyberSparkles(Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PERFECT!",
                    color = BadgeGold,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = scoreText,
                    color = CompletedGreen,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "✦ • ✦ • ✦",
                    color = PrimaryPurple.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 12.dp),
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Great work, coder!",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                RobotMascotPulse()
            }
        }
    }
}

@Composable
private fun CyberSparkles(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "sparkles")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val sparkles = remember {
        List(56) {
            SparkleParticle(
                nx = Random.nextFloat(),
                ny = Random.nextFloat(),
                rot = Random.nextFloat() * 360f,
                cyan = Random.nextBoolean(),
                sizeMul = 0.6f + Random.nextFloat() * 1.4f
            )
        }
    }
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val baseR = minOf(w, h) * 0.0065f
        sparkles.forEach { s ->
            val x = s.nx * w
            val y = s.ny * h
            val wave = phase * 12.566f + s.nx * 18f + s.ny * 11f
            val twinkle = 0.28f + 0.62f * (((sin(wave.toDouble()).toFloat()) + 1f) * 0.5f)
            val c = if (s.cyan) ActiveCyan else PrimaryPurple
            val r = baseR * s.sizeMul
            rotate(s.rot, pivot = Offset(x, y)) {
                drawCircle(
                    color = c.copy(alpha = twinkle.coerceIn(0.18f, 0.92f)),
                    radius = r,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color.White.copy(alpha = twinkle * 0.35f),
                    radius = r * 0.45f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
private fun RobotMascotPulse() {
    var settled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { settled = true }
    val scale by animateFloatAsState(
        targetValue = if (settled) 1f else 0.78f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "robotBounce"
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(width = 140.dp, height = 52.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ActiveCyan.copy(alpha = 0.45f),
                            PrimaryPurple.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )
        Text(
            text = "🤖",
            fontSize = 76.sp,
            modifier = Modifier
                .scale(scale)
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}
