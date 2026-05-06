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
import com.example.codequest.data.LocalContentRepository
import com.example.codequest.ui.components.AppTab
import com.example.codequest.ui.components.BadgeCategoriesSection
import com.example.codequest.ui.components.BadgeMetricsRow
import com.example.codequest.ui.components.BottomNavigationBar
import com.example.codequest.ui.components.CategoryUi
import com.example.codequest.ui.components.EarnedBadgeUi
import com.example.codequest.ui.components.EarnedBadgesPanel
import com.example.codequest.ui.components.HeaderSection
import com.example.codequest.ui.components.LockedBadgeUi
import com.example.codequest.ui.components.LockedBadgesPanel
import com.example.codequest.ui.components.NextBadgeProgressCard
import com.example.codequest.ui.components.RecentAchievementsSection
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
fun CodeQuestBadgesScreen(
    appState: CodeQuestAppState,
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    val badgePairs = appState.badgeState()
    val earnedBadges = badgePairs.filter { appState.earnedBadgeIds.contains(it.first.id) }
        .take(3)
        .map {
            val accent = when (it.first.id) {
                "streak-master" -> BadgeGold
                "logic-learner" -> PrimaryPurple
                else -> ActiveCyan
            }
            EarnedBadgeUi(it.first.title, it.first.description, it.first.icon, accent)
        }
    val lockedBadges = badgePairs.filter { !appState.earnedBadgeIds.contains(it.first.id) }
        .take(3)
        .map {
            val percent = (it.second.toFloat() / it.first.target.toFloat()).coerceIn(0f, 1f)
            LockedBadgeUi(
                title = it.first.title,
                description = it.first.description,
                icon = it.first.icon,
                progressText = "${it.second} / ${it.first.target}",
                progress = percent
            )
        }
    val categories = LocalContentRepository.badgeCategories.map { CategoryUi(it.title, it.count) }
    val nextBadge = lockedBadges.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
    ) {
        BadgesBackgroundGlow()
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
            item {
                BadgeMetricsRow(
                    badgesEarned = appState.earnedBadgeIds.size,
                    dayStreak = appState.streakDays,
                    totalXp = appState.totalXP
                )
            }
            item {
                NextBadgeProgressCard(
                    badgeName = nextBadge?.title ?: "No badges available yet",
                    progressText = nextBadge?.progressText ?: "0 / 0",
                    progressPercentText = if (nextBadge != null) "${(nextBadge.progress * 100).toInt()}%" else "0%",
                    progress = nextBadge?.progress ?: 0f
                )
            }
            item { SectionHeader(title = "Earned Badges", action = "View All  >", onActionClick = { appState.openAllEarnedBadges() }) }
            item {
                if (badgePairs.isEmpty()) {
                    Text("No badges available yet", color = TextMuted)
                } else if (earnedBadges.isEmpty()) {
                    Text("No badges earned yet", color = TextMuted)
                } else {
                    EarnedBadgesPanel(earnedBadges)
                }
            }
            item { SectionHeader(title = "Locked Badges", action = "View All  >", onActionClick = { appState.openAllLockedBadges() }) }
            item {
                if (lockedBadges.isEmpty()) {
                    Text("No locked badges available", color = TextMuted)
                } else {
                    LockedBadgesPanel(lockedBadges)
                }
            }
            item { SectionHeader(title = "Badge Categories", action = "View All  >", onActionClick = { appState.openBadgeCategories() }) }
            item {
                if (categories.isEmpty()) {
                    Text("No badge categories available", color = TextMuted)
                } else {
                    BadgeCategoriesSection(categories)
                }
            }
            item { SectionHeader(title = "Recent Achievements") }
            item {
                if (earnedBadges.isEmpty()) {
                    Text("No achievements yet", color = TextMuted)
                } else {
                    RecentAchievementsSection(
                        title = "First Steps",
                        subtitle = "Completed your first quest",
                        xpText = "+100 XP",
                        timeText = "2h ago"
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(96.dp)) }
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
            fontSize = 30.sp
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
private fun BadgesBackgroundGlow() {
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
