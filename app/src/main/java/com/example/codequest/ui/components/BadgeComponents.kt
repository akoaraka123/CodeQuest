package com.example.codequest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.LockedBlueGray
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

data class EarnedBadgeUi(
    val title: String,
    val description: String,
    val icon: String,
    val accent: Color
)

data class LockedBadgeUi(
    val title: String,
    val description: String,
    val icon: String,
    val progressText: String,
    val progress: Float
)

data class CategoryUi(
    val title: String,
    val count: Int
)

@Composable
fun BadgeMetricsRow(
    badgesEarned: Int,
    dayStreak: Int,
    totalXp: Int
) {
    GlassCard(cornerRadius = 18.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricColumn(icon = "🛡️", value = badgesEarned.toString(), label = "Badges Earned")
            DividerLine()
            MetricColumn(icon = "🔥", value = dayStreak.toString(), label = "Day Streak")
            DividerLine()
            MetricColumn(icon = "⚡", value = totalXp.toString(), label = "Total EXP")
        }
    }
}

@Composable
private fun MetricColumn(icon: String, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Text(text = label, color = TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .height(34.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.12f))
    )
}

@Composable
fun NextBadgeProgressCard(
    badgeName: String,
    progressText: String,
    progressPercentText: String,
    progress: Float
) {
    GlassCard(
        cornerRadius = 20.dp,
        borderBrush = Brush.linearGradient(
            listOf(
                PrimaryPurple.copy(alpha = 0.75f),
                ActiveCyan.copy(alpha = 0.45f)
            )
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Next Badge to Unlock", color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = badgeName, color = PrimaryPurple, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                GradientProgressBar(progress = progress)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = progressText, color = TextMuted, fontSize = 12.sp)
                    Text(text = progressPercentText, color = TextMuted, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF29215D), Color(0xFF1A1D48))))
                    .border(1.dp, PrimaryPurple.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🐞", fontSize = 34.sp)
            }
        }
    }
}

@Composable
private fun GradientProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(PrimaryCyan, PrimaryPurple)))
        )
    }
}

@Composable
fun EarnedBadgesPanel(badges: List<EarnedBadgeUi>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(end = 4.dp)
    ) {
        items(badges) { badge ->
            GlassCard(
                modifier = Modifier.width(150.dp),
                cornerRadius = 16.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        badge.accent.copy(alpha = 0.34f),
                                        Color.White.copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .border(1.dp, badge.accent.copy(alpha = 0.85f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badge.icon, color = badge.accent, fontSize = 27.sp)
                    }
                    Spacer(modifier = Modifier.height(9.dp))
                    Text(text = badge.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = badge.description, color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(badge.accent.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Earned", color = badge.accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun LockedBadgesPanel(badges: List<LockedBadgeUi>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(end = 4.dp)
    ) {
        items(badges) { badge ->
            GlassCard(
                modifier = Modifier.width(160.dp),
                cornerRadius = 16.dp,
                borderBrush = Brush.linearGradient(listOf(LockedBlueGray.copy(alpha = 0.45f), CardBorder))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                            .border(1.dp, LockedBlueGray.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badge.icon, color = LockedBlueGray, fontSize = 25.sp)
                    }
                    Spacer(modifier = Modifier.height(9.dp))
                    Text(text = badge.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = badge.description, color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        if (badge.progress > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(badge.progress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PrimaryPurple.copy(alpha = 0.8f))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "🔒  ${badge.progressText}", color = LockedBlueGray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BadgeCategoriesSection(categories: List<CategoryUi>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val selected = category.title == "All Badges"
            val border = if (selected) ActiveCyan else CardBorder
            val textColor = if (selected) ActiveCyan else TextPrimary
            val bgColor = if (selected) ActiveCyan.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.03f)
            GlassCard(
                modifier = Modifier.width(108.dp),
                cornerRadius = 14.dp,
                borderBrush = Brush.linearGradient(listOf(border.copy(alpha = 0.85f), CardBorder))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .border(1.dp, border, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = category.title, color = textColor, fontSize = 12.sp)
                        Text(text = category.count.toString(), color = if (selected) ActiveCyan else TextMuted, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RecentAchievementsSection(
    title: String,
    subtitle: String,
    xpText: String,
    timeText: String
) {
    GlassCard(cornerRadius = 18.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(ActiveCyan.copy(alpha = 0.3f), PrimaryPurple.copy(alpha = 0.25f))))
                    .border(1.dp, ActiveCyan.copy(alpha = 0.65f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "</>", color = ActiveCyan, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = subtitle, color = TextMuted, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = xpText, color = ActiveCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = timeText, color = TextMuted, fontSize = 12.sp)
            }
        }
    }
}
