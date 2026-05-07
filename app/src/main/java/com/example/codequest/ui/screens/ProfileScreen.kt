package com.example.codequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalContentRepository
import com.example.codequest.ui.components.AccountSettingsSection
import com.example.codequest.ui.components.AppTab
import com.example.codequest.ui.components.BottomNavigationBar
import com.example.codequest.ui.components.HeaderSection
import com.example.codequest.ui.components.LearningStatUiModel
import com.example.codequest.ui.components.LearningStatsSection
import com.example.codequest.ui.components.MainProfileCard
import com.example.codequest.ui.components.AchievementUiModel
import com.example.codequest.ui.components.MyAchievementsSection
import com.example.codequest.ui.components.ProfileStatsRow
import com.example.codequest.ui.components.SettingsItemUiModel
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
fun CodeQuestProfileScreen(
    appState: CodeQuestAppState,
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    val learningRows = appState.getCourses().mapIndexed { index, course ->
        val value = appState.courseLearningXp[course.id] ?: 0
        val accent = when (index % 5) {
            0 -> ActiveCyan
            1 -> CompletedGreen
            2 -> PrimaryPurple
            3 -> Color(0xFFC06BFF)
            else -> Color(0xFF4D8CFF)
        }
        LearningStatUiModel(
            icon = when (index % 5) {
                0 -> "</>"
                1 -> "\uD83E\uDDE0"
                2 -> "\u21BB"
                3 -> "\u25A6"
                else -> "\u25FC"
            },
            title = course.title.take(10),
            xpText = "$value / 500 XP",
            progress = value / 500f,
            accent = accent
        )
    }
    val achievements = LocalContentRepository.profileAchievements.map {
        val accent = when (it.title) {
            "Logic Learner" -> PrimaryPurple
            "Streak Master" -> BadgeGold
            else -> ActiveCyan
        }
        AchievementUiModel(it.icon, it.title, it.description, it.date, accent)
    }
    val settingsItems = listOf(
        SettingsItemUiModel("\uD83D\uDC64", "Edit Profile", onClick = { appState.openEditProfile() }),
        SettingsItemUiModel("\uD83C\uDFA8", "Theme", trailingText = appState.selectedTheme, onClick = { appState.openThemeSettings() }),
        SettingsItemUiModel("\uD83D\uDD14", "Notifications", onClick = { appState.openNotificationSettings() }),
        SettingsItemUiModel("\u2753", "Help & Support", onClick = { appState.openHelpSupport() })
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
    ) {
        ProfileBackgroundGlow()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HeaderSection(
                    onNotificationClick = { appState.openNotifications() },
                    showNotificationDot = appState.hasUnreadNotifications()
                )
            }
            item { TitleOnlyHeader("Profile") }
            item {
                MainProfileCard(
                    username = appState.username,
                    level = if (appState.totalXP == 0) 0 else (appState.totalXP / 100) + 1,
                    role = appState.roleTitle,
                    joinedDate = "Apr 12, 2025",
                    streakDays = appState.streakDays,
                    avatarLabel = appState.selectedAvatar
                )
            }
            item {
                ProfileStatsRow(
                    totalXp = appState.totalXP,
                    lessonsCompleted = appState.completedLessonIds.size,
                    coursesCompleted = appState.completedCourseIds.size,
                    badgesEarned = appState.earnedBadgeIds.size
                )
            }
            item { SectionHeader("Learning Stats") }
            item {
                if (learningRows.isEmpty()) {
                    Text("No learning stats available yet.", color = TextMuted)
                } else {
                    LearningStatsSection(learningRows)
                }
            }
            item { SectionHeader("My Achievements", "View All  >", onActionClick = { appState.openAllAchievements() }) }
            item {
                if (achievements.isEmpty()) {
                    Text("No achievements yet", color = TextMuted)
                } else {
                    MyAchievementsSection(achievements)
                }
            }
            item { SectionHeader("Account & Settings") }
            item { AccountSettingsSection(settingsItems) }
            item { Spacer(modifier = Modifier.height(112.dp)) }
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
private fun TitleOnlyHeader(title: String) {
    Text(text = title, color = TextPrimary, fontSize = 26.sp)
}

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, color = TextPrimary, fontSize = 30.sp)
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
private fun ProfileBackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(PrimaryPurple.copy(alpha = 0.16f))
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
