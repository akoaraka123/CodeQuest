package com.example.codequest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CardSurface
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.LockedBlueGray
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.ProgressMint
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary
import com.example.codequest.ui.theme.TextSecondary

data class CampusCardUi(
    val id: String,
    val title: String,
    val status: String,
    val icon: String,
    val statusType: CampusStatus,
    val isEnabled: Boolean
)

enum class CampusStatus { COMPLETED, ACTIVE, LOCKED }

data class BadgeUi(
    val title: String,
    val description: String,
    val icon: String,
    val accent: Color
)

enum class AppTab {
    HOME, QUESTS, BADGES, PROFILE
}

@Composable
fun HeaderSection(
    onNotificationClick: () -> Unit = {},
    showNotificationDot: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(PrimaryCyan.copy(0.25f), PrimaryPurple.copy(0.3f))))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "</>", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "CodeQuest", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                Text(text = "Debug Academy", color = TextMuted, fontSize = 12.sp)
            }
        }

        Box(modifier = Modifier.clickable { onNotificationClick() }) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CardSurface.copy(alpha = 0.7f))
                    .border(1.dp, CardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83D\uDD14", fontSize = 15.sp)
            }
            if (showNotificationDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple)
                )
            }
        }
    }
}

@Composable
fun WelcomeCard(dayStreak: Int, codePoints: Int) {
    GlassCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(PrimaryCyan.copy(0.35f), PrimaryPurple.copy(0.4f))))
                    .border(1.dp, CardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83D\uDC68\u200D\uD83D\uDCBB", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Welcome back, Coder!", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatPill("\uD83D\uDD25 $dayStreak  Day Streak")
                    StatPill("\uD83D\uDC8E $codePoints  Code Points")
                }
            }
        }
    }
}

@Composable
private fun StatPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun LevelProgressCard(
    level: Int,
    role: String,
    currentXp: Int,
    targetXp: Int
) {
    GlassCard(
        borderBrush = Brush.linearGradient(
            listOf(
                PrimaryCyan.copy(alpha = 0.7f),
                PrimaryPurple.copy(alpha = 0.45f)
            )
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(PrimaryCyan.copy(0.22f), PrimaryPurple.copy(0.3f))))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "</>", color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Level $level", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                Text(text = role, color = TextMuted, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                ProgressBar(progress = (currentXp.toFloat() / targetXp.toFloat()).coerceIn(0f, 1f))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "$currentXp / $targetXp XP", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

@Composable
fun CurrentMissionCard(
    missionTitle: String,
    missionDescription: String,
    missionProgressText: String,
    onClick: () -> Unit,
    progressSegments: Int = 5,
    progressFilled: Int = 0
) {
    GlassCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF0A2F4A), Color(0xFF103946)))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83C\uDF33", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = missionTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(CompletedGreen.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "ACTIVE", color = CompletedGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = missionDescription,
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                SegmentedProgress(
                    segments = progressSegments.coerceAtLeast(1),
                    active = progressFilled.coerceIn(0, progressSegments.coerceAtLeast(1))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = missionProgressText, color = ActiveCyan, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\u203A", color = TextPrimary, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun CampusMapSection(
    campusItems: List<CampusCardUi>,
    onCampusClick: (CampusCardUi) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 4.dp)
    ) {
        items(campusItems) { item ->
            val borderColor = when (item.statusType) {
                CampusStatus.ACTIVE -> ActiveCyan
                CampusStatus.COMPLETED -> CompletedGreen.copy(alpha = 0.5f)
                CampusStatus.LOCKED -> CardBorder
            }
            val statusColor = when (item.statusType) {
                CampusStatus.ACTIVE -> ActiveCyan
                CampusStatus.COMPLETED -> CompletedGreen
                CampusStatus.LOCKED -> LockedBlueGray
            }
            val statusIcon = when (item.statusType) {
                CampusStatus.ACTIVE -> "●"
                CampusStatus.COMPLETED -> "✓"
                CampusStatus.LOCKED -> "\uD83D\uDD12"
            }

            GlassCard(
                modifier = Modifier
                    .width(122.dp)
                    .clickable(enabled = item.isEnabled) { onCampusClick(item) },
                cornerRadius = 16.dp,
                borderBrush = Brush.linearGradient(listOf(borderColor.copy(alpha = 0.65f), CardBorder))
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.icon, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = item.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "$statusIcon ${item.status}", color = statusColor, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun EarnedBadgesSection(badges: List<BadgeUi>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 4.dp)
    ) {
        items(badges) { badge ->
            GlassCard(
                modifier = Modifier.width(220.dp),
                cornerRadius = 16.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        badge.accent.copy(alpha = 0.3f),
                                        Color.White.copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .border(1.dp, badge.accent.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badge.icon, color = badge.accent, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = badge.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = badge.description, color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomItem(
                label = "Home",
                icon = "\u2302",
                selected = selectedTab == AppTab.HOME,
                onClick = { onTabSelected(AppTab.HOME) }
            )
            BottomItem(
                label = "Quests",
                icon = "\u2691",
                selected = selectedTab == AppTab.QUESTS,
                onClick = { onTabSelected(AppTab.QUESTS) }
            )
            BottomItem(
                label = "Badges",
                icon = "\u2605",
                selected = selectedTab == AppTab.BADGES,
                onClick = { onTabSelected(AppTab.BADGES) }
            )
            BottomItem(
                label = "Profile",
                icon = "\u25CC",
                selected = selectedTab == AppTab.PROFILE,
                onClick = { onTabSelected(AppTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun BottomItem(
    label: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) PrimaryCyan else TextMuted
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(text = icon, color = color, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = label, color = color, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 22.dp,
    borderBrush: Brush = Brush.linearGradient(listOf(CardBorder, CardBorder)),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(CardSurface.copy(alpha = 0.75f))
            .border(1.dp, borderBrush, RoundedCornerShape(cornerRadius))
            .padding(14.dp)
    ) {
        content()
    }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit = {}) {
    GradientButton(text = text, enabled = true, onClick = onClick)
}

@Composable
fun GradientButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (enabled) {
                    Brush.horizontalGradient(listOf(PrimaryCyan, PrimaryPurple))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFF2B3557), Color(0xFF1D2744)))
                }
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (enabled) Color.White else TextMuted,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(text = "\u2192", color = if (enabled) Color.White else TextMuted, fontSize = 22.sp)
        }
    }
}

@Composable
fun CodeQuestBackButton(
    onClick: () -> Unit,
    isClose: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(CardSurface.copy(alpha = 0.75f))
            .border(1.dp, CardBorder, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isClose) "✕" else "←",
            color = PrimaryCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.08f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(PrimaryCyan, ProgressMint)))
        )
    }
}

@Composable
private fun SegmentedProgress(segments: Int, active: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(segments) { index ->
            val isActive = index < active
            Box(
                modifier = Modifier
                    .size(width = 22.dp, height = 5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isActive) ActiveCyan else Color.White.copy(alpha = 0.12f))
            )
        }
    }
}
