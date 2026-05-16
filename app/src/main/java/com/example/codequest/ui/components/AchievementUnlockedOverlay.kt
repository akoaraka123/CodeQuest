package com.example.codequest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay

private val OverlayNavy = Color(0xFF0A0E1A)

private data class AchievementSparkle(
    val nx: Float,
    val ny: Float,
    val rot: Float,
    val cyan: Boolean,
    val sizeMul: Float
)

@Composable
fun AchievementUnlockedOverlay(
    achievementTitle: String,
    achievementDescription: String,
    achievementIcon: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    var iconSettled by remember(achievementTitle, visible) { mutableStateOf(false) }
    var finished by remember(achievementTitle, visible) { mutableStateOf(false) }
    LaunchedEffect(visible, achievementTitle) {
        if (visible) {
            finished = false
            iconSettled = false
            delay(80)
            iconSettled = true
            delay(2200)
            if (!finished) {
                finished = true
                onFinished()
            }
        }
    }
    fun finishOnce() {
        if (!finished) {
            finished = true
            onFinished()
        }
    }

    val iconScale by animateFloatAsState(
        targetValue = if (iconSettled && visible) 1f else 0.72f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "achievementIconScale"
    )

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
            scaleIn(
                animationSpec = tween(420, easing = FastOutSlowInEasing),
                initialScale = 0.9f
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
            AchievementSparkleField(Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Achievement Unlocked!",
                    color = BadgeGold,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    PrimaryPurple.copy(alpha = 0.35f),
                                    Color(0xFF141A33),
                                    ActiveCyan.copy(alpha = 0.12f)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    ActiveCyan.copy(alpha = 0.75f),
                                    BadgeGold.copy(alpha = 0.55f),
                                    CardBorder
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 22.dp, vertical = 26.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .scale(iconScale)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            ActiveCyan.copy(alpha = 0.35f),
                                            PrimaryPurple.copy(alpha = 0.2f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .border(1.5.dp, ActiveCyan.copy(alpha = 0.55f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = achievementIcon, fontSize = 42.sp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = achievementTitle,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = achievementDescription,
                            color = TextMuted,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GradientButton(text = "Continue") { finishOnce() }
            }
        }
    }
}

@Composable
private fun AchievementSparkleField(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "achievementSparkles")
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
        List(48) {
            AchievementSparkle(
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
            }
        }
    }
}
