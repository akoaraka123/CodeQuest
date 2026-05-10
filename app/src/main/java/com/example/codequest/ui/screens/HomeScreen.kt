package com.example.codequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.components.HeaderSection
import com.example.codequest.ui.components.LevelProgressCard
import com.example.codequest.ui.components.WelcomeCard
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
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
    val courses = appState.getCourses()
    val activeCourse = appState.getActiveCourseForHome()
    val targetLesson = appState.getActiveTargetLesson()
    val completedInActive = activeCourse?.lessons?.count { it.id in appState.completedLessonIds } ?: 0
    val totalInActive = activeCourse?.lessons?.size ?: 0
    val campusItems = courses.map { course ->
        val statusType = when {
            course.id in appState.completedCourseIds -> CampusStatus.COMPLETED
            course.id in appState.unlockedCourseIds -> CampusStatus.ACTIVE
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
                "first-steps", "thinking-coder", "variable-starter" -> ActiveCyan
                "function-builder", "algorithm-explorer" -> PrimaryPurple
                "cs-rookie", "neural-beginner" -> BadgeGold
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
                        codePoints = appState.totalXP
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LevelProgressCard(
                        level = if (appState.totalXP == 0) 0 else (appState.totalXP / 100) + 1,
                        role = "Junior Debugger",
                        currentXp = appState.totalXP % 500,
                        targetXp = 500
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    val hasLearningProgress =
                        appState.completedLessonIds.isNotEmpty() || appState.totalXP > 0
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
