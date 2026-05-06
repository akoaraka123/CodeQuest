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
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

data class LearningStatUiModel(
    val icon: String,
    val title: String,
    val xpText: String,
    val progress: Float,
    val accent: Color
)

data class AchievementUiModel(
    val icon: String,
    val title: String,
    val description: String,
    val date: String,
    val accent: Color
)

data class SettingsItemUiModel(
    val icon: String,
    val title: String,
    val trailingText: String? = null,
    val onClick: () -> Unit = {}
)

@Composable
fun MainProfileCard(
    username: String,
    level: Int,
    role: String,
    joinedDate: String,
    streakDays: Int,
    avatarLabel: String
) {
    val avatarEmoji = when (avatarLabel) {
        "Debugger" -> "\uD83D\uDD75\uFE0F"
        "Gamer" -> "\uD83C\uDFAE"
        "Student" -> "\uD83D\uDC68\u200D\uD83C\uDF93"
        else -> "\uD83D\uDC68\u200D\uD83D\uDCBB"
    }
    GlassCard(
        cornerRadius = 20.dp,
        borderBrush = Brush.linearGradient(listOf(PrimaryPurple.copy(alpha = 0.75f), ActiveCyan.copy(alpha = 0.45f)))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(94.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF26215C), Color(0xFF151D44))))
                    .border(1.dp, PrimaryPurple.copy(alpha = 0.7f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = avatarEmoji, fontSize = 34.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = username, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 34.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "✎", color = TextMuted, fontSize = 13.sp)
                }
                Text(text = "Level $level", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                Text(text = role, color = ActiveCyan, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "📅 Joined $joinedDate", color = TextMuted, fontSize = 12.sp)
                Text(text = "🔥 $streakDays Day Streak", color = TextMuted, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(ActiveCyan.copy(alpha = 0.25f), PrimaryPurple.copy(alpha = 0.35f))))
                    .border(1.dp, ActiveCyan.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "</>", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileStatsRow(
    totalXp: Int,
    questsCompleted: Int,
    badgesEarned: Int,
    streakDays: Int
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        ProfileStatCard("💎", totalXp.toString(), "Total XP", ActiveCyan, Modifier.weight(1f))
        ProfileStatCard("✅", questsCompleted.toString(), "Quests\nCompleted", CompletedGreen, Modifier.weight(1f))
        ProfileStatCard("🛡️", badgesEarned.toString(), "Badges\nEarned", PrimaryPurple, Modifier.weight(1f))
        ProfileStatCard("🔥", streakDays.toString(), "Streak Days", BadgeGold, Modifier.weight(1f))
    }
}

@Composable
private fun ProfileStatCard(icon: String, value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, cornerRadius = 16.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Text(text = label, color = accent, fontSize = 11.sp)
        }
    }
}

@Composable
fun LearningStatsSection(items: List<LearningStatUiModel>) {
    GlassCard(cornerRadius = 18.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                LearningStatRow(item)
            }
        }
    }
}

@Composable
fun LearningStatRow(item: LearningStatUiModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = item.icon, color = item.accent, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = item.title, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.width(52.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(item.progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(item.accent)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = item.xpText, color = TextMuted, fontSize = 12.sp)
    }
}

@Composable
fun MyAchievementsSection(items: List<AchievementUiModel>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(end = 4.dp)) {
        items(items) { item ->
            GlassCard(modifier = Modifier.width(190.dp), cornerRadius = 14.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(item.accent.copy(alpha = 0.3f), Color.White.copy(alpha = 0.08f))))
                            .border(1.dp, item.accent.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.icon, color = item.accent, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = item.description, color = TextMuted, fontSize = 12.sp)
                        Text(text = item.date, color = item.accent, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun AccountSettingsSection(items: List<SettingsItemUiModel>) {
    GlassCard(cornerRadius = 18.dp) {
        Column {
            items.forEachIndexed { index, item ->
                SettingsRow(item)
                if (index != items.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsRow(item: SettingsItemUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.icon, color = ActiveCyan, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = item.title, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        if (item.trailingText != null) {
            Text(text = item.trailingText, color = TextMuted, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(text = "›", color = TextMuted, fontSize = 18.sp)
    }
}
