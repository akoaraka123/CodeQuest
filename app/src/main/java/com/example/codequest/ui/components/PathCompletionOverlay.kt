package com.example.codequest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun PathCompletionCelebrationOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
    onContinue: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "path_complete")
    val scale by transition.animateFloat(
        initialValue = 0.93f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophy_scale"
    )
    val glowAlpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500))
    ) {
        Box(
            modifier = modifier
                .background(Color.Black.copy(alpha = 0.88f))
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(BadgeGold.copy(alpha = glowAlpha))
                        .border(2.dp, BadgeGold.copy(alpha = glowAlpha + 0.3f), CircleShape)
                        .graphicsLayer { scaleX = scale; scaleY = scale },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏆", fontSize = 56.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Congratulations,",
                    color = BadgeGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Python Coder!",
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "You completed the CodeQuest\nPython learning path.",
                    color = TextMuted,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 23.sp
                )
                Text(
                    text = "Stay tuned for the next update.",
                    color = ActiveCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.horizontalGradient(listOf(BadgeGold, CompletedGreen))
                        )
                        .clickable { onContinue() }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Continue  →",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RatingModal(
    modifier: Modifier = Modifier,
    onSubmit: (rating: Int, comment: String) -> Unit,
    onSkip: () -> Unit
) {
    var selectedRating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.88f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Rate CodeQuest",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "How was your learning experience?",
                    color = TextMuted,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        Text(
                            text = if (star <= selectedRating) "★" else "☆",
                            fontSize = 40.sp,
                            color = if (star <= selectedRating) BadgeGold else TextMuted,
                            modifier = Modifier
                                .clickable { selectedRating = star }
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    if (comment.isEmpty()) {
                        Text(
                            text = "Add a comment (optional)",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                        maxLines = 4
                    )
                }

                GradientButton(
                    text = "Submit Rating",
                    enabled = selectedRating > 0,
                    onClick = { if (selectedRating > 0) onSubmit(selectedRating, comment) }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSkip() }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Maybe Later",
                        color = TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
