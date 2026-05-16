package com.example.codequest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.ui.components.AchievementUnlockedOverlay
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.components.GoodEffortCelebrationOverlay
import com.example.codequest.ui.components.PerfectScoreCelebrationOverlay
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun CodeQuestResultScreen(appState: CodeQuestAppState) {
    val result = appState.result
    val totalActs = result?.totalActivities ?: 0
    val correctActs = result?.correctCount ?: 0
    val isPerfectScore = totalActs > 0 && correctActs == totalActs
    val isBelowPerfect = totalActs > 0 && correctActs < totalActs
    val hadScoreCelebration = isPerfectScore || isBelowPerfect

    var showPerfectCelebration by remember(result?.lessonId, result?.courseId, correctActs, totalActs) {
        mutableStateOf(isPerfectScore)
    }
    var showGoodEffortCelebration by remember(result?.lessonId, result?.courseId, correctActs, totalActs) {
        mutableStateOf(isBelowPerfect)
    }
    var scoreCelebrationFinished by remember(result?.lessonId, result?.courseId) {
        mutableStateOf(!hadScoreCelebration)
    }
    var showAchievementOverlay by remember(result?.lessonId, result?.courseId) {
        mutableStateOf(false)
    }

    fun beginAchievementChainIfNeeded() {
        if (appState.hasPendingAchievements()) {
            showAchievementOverlay = true
        } else if (hadScoreCelebration) {
            appState.returnToCourseDetailAfterLessonCompletion()
        }
    }

    fun onScoreCelebrationFinished() {
        scoreCelebrationFinished = true
        beginAchievementChainIfNeeded()
    }

    fun onAchievementStepFinished() {
        appState.consumeNextPendingAchievement()
        if (appState.hasPendingAchievements()) {
            showAchievementOverlay = true
        } else {
            showAchievementOverlay = false
            if (hadScoreCelebration) {
                appState.returnToCourseDetailAfterLessonCompletion()
            }
        }
    }

    LaunchedEffect(result?.lessonId, result?.courseId, scoreCelebrationFinished) {
        if (result != null && scoreCelebrationFinished && !showAchievementOverlay && appState.hasPendingAchievements()) {
            showAchievementOverlay = true
        }
    }

    val currentAchievement = appState.peekPendingAchievement()

    BackHandler {
        when {
            showAchievementOverlay -> onAchievementStepFinished()
            appState.result != null -> {
                if (hadScoreCelebration) {
                    appState.returnToCourseDetailAfterLessonCompletion()
                } else {
                    appState.goHome()
                }
            }
            else -> appState.goHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                CodeQuestBackButton(
                    onClick = {
                        if (appState.result != null) {
                            if (hadScoreCelebration) {
                                appState.returnToCourseDetailAfterLessonCompletion()
                            } else {
                                appState.goHome()
                            }
                        } else {
                            appState.goHome()
                        }
                    },
                    isClose = true
                )
                Text(
                    "Lesson complete",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(40.dp))
            }
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = result?.courseTitle ?: "",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = result?.lessonTitle ?: "Lesson",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                    if ((result?.totalActivities ?: 0) > 0) {
                        Text(
                            text = "Activities correct: ${result?.correctCount ?: 0} / ${result?.totalActivities ?: 0}",
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "XP earned this lesson: ${result?.xpEarned ?: 0}",
                        color = CompletedGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nice work — lesson unlocked the next step on your path.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
            GradientButton(text = "Back to Home") { appState.goHome() }
            GradientButton(text = "Next lesson") { appState.resultContinueNextLesson() }
            GradientButton(text = "Learning Path") { appState.goToLearningPath() }
            GradientButton(text = "Review lesson") { appState.reviewCurrentLessonActivities() }
        }

        if (isPerfectScore) {
            PerfectScoreCelebrationOverlay(
                scoreText = "$correctActs / $totalActs",
                visible = showPerfectCelebration,
                modifier = Modifier.fillMaxSize(),
                onAnimationFinished = {
                    showPerfectCelebration = false
                    onScoreCelebrationFinished()
                }
            )
        } else if (isBelowPerfect) {
            GoodEffortCelebrationOverlay(
                scoreText = "$correctActs / $totalActs",
                visible = showGoodEffortCelebration,
                modifier = Modifier.fillMaxSize(),
                onAnimationFinished = {
                    showGoodEffortCelebration = false
                    onScoreCelebrationFinished()
                }
            )
        }

        if (showAchievementOverlay) {
            currentAchievement?.let { achievement ->
                key(achievement.id) {
                    AchievementUnlockedOverlay(
                        achievementTitle = achievement.title,
                        achievementDescription = achievement.description,
                        achievementIcon = achievement.icon,
                        visible = true,
                        modifier = Modifier.fillMaxSize(),
                        onFinished = ::onAchievementStepFinished
                    )
                }
            }
        }
    }
}
