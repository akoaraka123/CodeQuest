package com.example.codequest.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

private data class RobotGuideStep(
    val emoji: String,
    val title: String,
    val body: String,
    val tip: String? = null,
    val isVideoStep: Boolean = false
)

private val robotGuideSteps = listOf(
    RobotGuideStep(
        emoji = "🤖",
        title = "Welcome to Robot Puzzles",
        body = "In Lesson 2 you will program a robot on a grid. Your job is to reach the red target tile using commands in the right order.",
        tip = "Take your time — this guide walks you through each part of the screen."
    ),
    RobotGuideStep(
        emoji = "🗺️",
        title = "Step 1 — Read the Grid",
        body = "The robot starts on one tile and faces a direction. The antenna on top shows where Move forward will go. The glowing red square is the goal you must reach.",
        tip = "Watch the antenna before you plan your moves."
    ),
    RobotGuideStep(
        emoji = "📋",
        title = "Step 2 — Fill the Command Slots",
        body = "Tap a command chip (Move forward, Turn right, Turn left, Select red) to place it in the next empty slot. Commands run from top to bottom, left to right.",
        tip = "Tap a filled slot to remove that command and try again."
    ),
    RobotGuideStep(
        emoji = "↩️",
        title = "Step 3 — Turns Matter",
        body = "Turn right and Turn left rotate the robot before the next move. After a turn, Move forward goes in the new direction — not where the robot was facing before.",
        tip = "Use turns to line up with the target, then move forward."
    ),
    RobotGuideStep(
        emoji = "✅",
        title = "Step 4 — Tap Check",
        body = "When every slot is filled, tap the Check button. Your program will run on the grid so you can see if the robot reaches the red target.",
        tip = "Next you'll watch a short video showing how execution works."
    ),
    RobotGuideStep(
        emoji = "🎬",
        title = "Step 5 — Watch How to Execute",
        body = "",
        isVideoStep = true
    )
)

@Composable
fun RobotLesson2DemoGuideScreen(
    step: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    val safeStep = step.coerceIn(0, robotGuideSteps.lastIndex)
    val isLast = safeStep >= totalSteps - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CodeQuestBackButton(onClick = onBack)
            Text(
                text = "Robot guide · ${safeStep + 1}/$totalSteps",
                color = TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        GuideProgressBar(current = safeStep, total = totalSteps)

        AnimatedContent(
            targetState = safeStep,
            transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(160))
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = "robotGuideStep"
        ) { animatedStep ->
            val stepContent = robotGuideSteps[animatedStep.coerceIn(robotGuideSteps.indices)]
            if (stepContent.isVideoStep) {
                RobotGuideExecutionVideoStep(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    GuideStepCard(content = stepContent)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onSkip) {
                Text(
                    text = "Skip guide",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        }

        GradientButton(
            text = if (isLast) "Start puzzles" else "Next",
            onClick = onNext
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun GuideProgressBar(current: Int, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(total) { idx ->
            val active = idx <= current
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (active) {
                            Brush.horizontalGradient(listOf(PrimaryPurple, ActiveCyan))
                        } else {
                            Brush.horizontalGradient(
                                listOf(CardBorder.copy(alpha = 0.5f), CardBorder.copy(alpha = 0.35f))
                            )
                        }
                    )
            )
        }
    }
}

@Composable
private fun GuideStepCard(content: RobotGuideStep) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(listOf(PrimaryPurple, PrimaryCyan, BadgeGold)),
                shape = RoundedCornerShape(22.dp)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        PrimaryPurple.copy(alpha = 0.28f),
                        Color(0xFF12182E).copy(alpha = 0.94f),
                        PrimaryPurple.copy(alpha = 0.16f)
                    )
                )
            )
            .padding(horizontal = 22.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = content.emoji, fontSize = 48.sp)
            Text(
                text = content.title,
                color = BadgeGold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
            Text(
                text = content.body,
                color = TextPrimary,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )
            content.tip?.let { tip ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CompletedGreen.copy(alpha = 0.12f))
                        .border(1.dp, CompletedGreen.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Tip: $tip",
                        color = CompletedGreen,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
