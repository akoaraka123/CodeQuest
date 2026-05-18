package com.example.codequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.ui.components.AppTab
import com.example.codequest.ui.components.BadgeUi
import com.example.codequest.ui.components.BottomNavigationBar
import com.example.codequest.ui.components.CampusCardUi
import com.example.codequest.ui.components.CampusMapSection
import com.example.codequest.ui.components.CampusStatus
import com.example.codequest.ui.components.CurrentMissionCard
import com.example.codequest.ui.components.EarnedBadgesSection
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.components.HeaderSection
import com.example.codequest.ui.components.LevelProgressCard
import com.example.codequest.ui.components.WelcomeCard
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary
import com.example.codequest.state.CodeQuestAppState

@Composable
fun CodeQuestHomeScreen(
    appState: CodeQuestAppState,
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    appState.ensureDefaultActiveCourse()
    val totalExp = appState.totalExp()
    val courses = appState.getCourses()
    val activeCourse = appState.getActiveCourseForHome()
    val targetLesson = appState.getActiveTargetLesson()
    val completedInActive = activeCourse?.lessons?.count { it.id in appState.completedLessonIds } ?: 0
    val totalInActive = activeCourse?.lessons?.size ?: 0
    val campusItems = courses.map { course ->
        val statusType = when {
            course.id in appState.completedCourseIds -> CampusStatus.COMPLETED
            appState.isCourseUnlocked(course.id) -> CampusStatus.ACTIVE
            else -> CampusStatus.LOCKED
        }
        CampusCardUi(
            id = course.id,
            title = course.title,
            status = when (statusType) {
                CampusStatus.COMPLETED -> "Completed"
                CampusStatus.ACTIVE -> "Available"
                CampusStatus.LOCKED -> "Locked"
            },
            icon = course.icon,
            statusType = statusType,
            isEnabled = statusType != CampusStatus.LOCKED
        )
    }

    val earnedBadges = appState.badgeState()
        .filter { appState.earnedBadgeIds.contains(it.first.id) }
        .take(3)
        .map {
            val accent = when (it.first.id) {
                "first-steps" -> ActiveCyan
                "python-starter" -> PrimaryPurple
                "variable-master" -> CompletedGreen
                "input-output-champion" -> ActiveCyan
                "condition-master" -> PrimaryPurple
                "perfect-start" -> BadgeGold
                "debug-learner" -> CompletedGreen
                "python-path-finisher" -> BadgeGold
                else -> ActiveCyan
            }
            BadgeUi(it.first.title, it.first.description, it.first.icon, accent)
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
    ) {
        BackgroundGlow()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    HeaderSection(
                        onNotificationClick = { appState.openNotifications() },
                        showNotificationDot = appState.hasUnreadNotifications()
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    WelcomeCard(
                        displayName = appState.username,
                        dayStreak = appState.streakDays,
                        codePoints = totalExp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LevelProgressCard(
                        level = appState.levelFromExp(),
                        role = "Junior Debugger",
                        currentXp = appState.levelCurrentExp(),
                        targetXp = 500
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    val hasLearningProgress =
                        appState.completedLessonIds.isNotEmpty() || totalExp > 0
                    GradientButton(
                        text = if (hasLearningProgress) "Continue Quest" else "Start Quest",
                        enabled = activeCourse != null,
                        onClick = { appState.continueLearning() }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "Current Mission",
                        action = "View All  >",
                        onActionClick = { onTabSelected(AppTab.QUESTS) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CurrentMissionCard(
                        missionTitle = activeCourse?.title ?: "Thinking in Code",
                        missionDescription = targetLesson?.let { "Lesson: ${it.title}" }
                            ?: "Open the Learning Path and start your first lesson.",
                        missionProgressText = if (activeCourse != null && totalInActive > 0) {
                            "Progress: $completedInActive of $totalInActive lessons completed"
                        } else {
                            "Tap to continue"
                        },
                        onClick = { appState.continueLearning() },
                        progressSegments = totalInActive.coerceAtLeast(1),
                        progressFilled = completedInActive.coerceIn(0, totalInActive.coerceAtLeast(1))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(title = "Cyber Campus Map")
                    Spacer(modifier = Modifier.height(10.dp))
                    if (campusItems.isEmpty()) {
                        Text("No courses available yet", color = TextMuted)
                    } else {
                        CampusMapSection(campusItems) { item ->
                            if (item.isEnabled) appState.openCourseDetail(item.id)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "Earned Badges",
                        action = "View All  >",
                        onActionClick = { onTabSelected(AppTab.BADGES) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (earnedBadges.isEmpty()) {
                        Text("No badges earned yet.", color = TextMuted)
                    } else {
                        EarnedBadgesSection(earnedBadges)
                    }
                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }

        if (appState.showOnboarding) {
            OnboardingOverlay(
                stepIndex = appState.onboardingStepIndex,
                onNext = { appState.nextOnboardingStep(OnboardingSteps.size) },
                onSkip = appState::skipOnboarding
            )
        }
    }
}

private data class OnboardingStepUi(
    val focus: String,
    val title: String,
    val message: String
)

private val OnboardingSteps = listOf(
    OnboardingStepUi(
        focus = "Welcome",
        title = "Welcome to CodeQuest!",
        message = "CodeQuest helps you learn Python step by step through lessons, challenges, EXP, and badges."
    ),
    OnboardingStepUi(
        focus = "Notification bell",
        title = "Notifications",
        message = "Check here for reminders, achievements, and important updates."
    ),
    OnboardingStepUi(
        focus = "Start Quest button",
        title = "Start your quest",
        message = "Tap Start Quest to continue your current Python learning activity."
    ),
    OnboardingStepUi(
        focus = "Current Mission card",
        title = "Current Mission",
        message = "This shows the lesson or challenge you are currently working on."
    ),
    OnboardingStepUi(
        focus = "Cyber Campus Map",
        title = "Learning Path",
        message = "Choose a Python challenge here. Locked challenges open after you finish the previous one."
    ),
    OnboardingStepUi(
        focus = "Earned Badges",
        title = "Badges and Achievements",
        message = "Earn badges as you complete lessons, get perfect scores, and finish challenges."
    ),
    OnboardingStepUi(
        focus = "Bottom navigation",
        title = "Navigation",
        message = "Use Home, Quests, Badges, and Profile to move around CodeQuest."
    )
)

@Composable
private fun OnboardingOverlay(
    stepIndex: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val safeIndex = stepIndex.coerceIn(0, OnboardingSteps.lastIndex)
    val step = OnboardingSteps[safeIndex]
    val isLast = safeIndex == OnboardingSteps.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(PrimaryCyan, PrimaryPurple)),
                    shape = RoundedCornerShape(22.dp)
                ),
            cornerRadius = 22.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Step ${safeIndex + 1} of ${OnboardingSteps.size}",
                    color = ActiveCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = step.focus,
                    color = TextMuted,
                    fontSize = 13.sp
                )
                Text(
                    text = step.title,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = step.message,
                    color = TextMuted,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onSkip) {
                        Text("Skip", color = TextMuted)
                    }
                    Box(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.weight(1.4f)) {
                        GradientButton(
                            text = if (isLast) "Finish" else "Next",
                            onClick = onNext
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp
        )
        if (action != null) {
            Text(
                text = action,
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(enabled = onActionClick != null) { onActionClick?.invoke() }
            )
        }
    }
}

@Composable
private fun BackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(PrimaryPurple.copy(alpha = 0.18f))
                .align(Alignment.TopEnd)
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(120.dp))
                .background(PrimaryCyan.copy(alpha = 0.12f))
                .align(Alignment.TopStart)
        )
    }
}
