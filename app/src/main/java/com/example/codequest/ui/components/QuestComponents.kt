package com.example.codequest.ui.components

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CardSurface
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.LockedBlueGray
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

enum class QuestStatus {
    COMPLETED, ACTIVE, LOCKED
}

data class QuestUiModel(
    val id: String,
    val title: String,
    val description: String,
    val status: QuestStatus,
    val lessonsText: String,
    val icon: String
)

@Composable
fun QuestsHeaderSection(
    onNotificationClick: () -> Unit,
    showNotificationDot: Boolean
) {
    HeaderSection(
        onNotificationClick = onNotificationClick,
        showNotificationDot = showNotificationDot
    )
}

@Composable
fun StatsRow(
    questsCompleted: Int,
    totalQuests: Int,
    totalXp: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "✓",
            label = "Quests Completed",
            value = "$questsCompleted / $totalQuests"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "⚡",
            label = "Total EXP Earned",
            value = "$totalXp EXP"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String
) {
    GlassCard(
        modifier = modifier,
        cornerRadius = 16.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, color = ActiveCyan, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = label, color = TextMuted, fontSize = 11.sp)
                Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun DailyChallengeCard(onStartClick: () -> Unit) {
    GlassCard(
        borderBrush = Brush.linearGradient(
            listOf(PrimaryPurple.copy(alpha = 0.8f), ActiveCyan.copy(alpha = 0.45f))
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF1A2455), Color(0xFF101C45))))
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(PrimaryPurple.copy(alpha = 0.6f), ActiveCyan.copy(alpha = 0.5f))),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎯", fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Daily Challenge", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Solve a logic puzzle.", color = TextMuted, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RewardPill(text = "🔥 20")
                    RewardPill(text = "⚡ 50 EXP")
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(PrimaryPurple, ActiveCyan)))
                    .clickable { onStartClick() }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Start", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun RewardPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = TextPrimary, fontSize = 12.sp)
    }
}

@Composable
fun QuestSection(quests: List<QuestUiModel>, onQuestClick: (QuestUiModel) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        quests.forEach { quest ->
            QuestCard(quest = quest, onQuestClick = onQuestClick)
        }
    }
}

@Composable
fun QuestCard(quest: QuestUiModel, onQuestClick: (QuestUiModel) -> Unit) {
    val borderBrush = when (quest.status) {
        QuestStatus.COMPLETED -> Brush.linearGradient(listOf(CompletedGreen.copy(alpha = 0.7f), CardBorder))
        QuestStatus.ACTIVE -> Brush.linearGradient(listOf(ActiveCyan.copy(alpha = 0.8f), CardBorder))
        QuestStatus.LOCKED -> Brush.linearGradient(listOf(CardBorder, CardBorder))
    }

    GlassCard(
        modifier = Modifier.clickable(enabled = quest.status != QuestStatus.LOCKED) { onQuestClick(quest) },
        cornerRadius = 18.dp,
        borderBrush = borderBrush
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when (quest.status) {
                            QuestStatus.COMPLETED -> Brush.linearGradient(listOf(Color(0xFF093A34), Color(0xFF133A54)))
                            QuestStatus.ACTIVE -> Brush.linearGradient(listOf(Color(0xFF0A2F4A), Color(0xFF103946)))
                            QuestStatus.LOCKED -> Brush.linearGradient(listOf(Color(0xFF1F204A), Color(0xFF141A33)))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = quest.icon, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = quest.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = quest.status)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = quest.description, color = TextMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                when (quest.status) {
                    QuestStatus.COMPLETED -> {
                        Text(text = quest.lessonsText, color = CompletedGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgress(trackColor = Color.White.copy(alpha = 0.12f), fillColor = CompletedGreen, progress = 1f)
                    }

                    QuestStatus.ACTIVE -> {
                        SegmentedProgressBar(segments = 5, active = 3)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = quest.lessonsText, color = ActiveCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    QuestStatus.LOCKED -> {
                        Text(text = quest.lessonsText, color = LockedBlueGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgress(trackColor = Color.White.copy(alpha = 0.08f), fillColor = Color.Transparent, progress = 0f)
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            RightActionIcon(status = quest.status)
        }
    }
}

@Composable
private fun StatusBadge(status: QuestStatus) {
    val bgColor = when (status) {
        QuestStatus.COMPLETED -> CompletedGreen.copy(alpha = 0.18f)
        QuestStatus.ACTIVE -> ActiveCyan.copy(alpha = 0.18f)
        QuestStatus.LOCKED -> LockedBlueGray.copy(alpha = 0.2f)
    }
    val textColor = when (status) {
        QuestStatus.COMPLETED -> CompletedGreen
        QuestStatus.ACTIVE -> ActiveCyan
        QuestStatus.LOCKED -> LockedBlueGray
    }
    val text = when (status) {
        QuestStatus.COMPLETED -> "COMPLETED"
        QuestStatus.ACTIVE -> "ACTIVE"
        QuestStatus.LOCKED -> "LOCKED"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
    }
}

@Composable
private fun RightActionIcon(status: QuestStatus) {
    val symbol = when (status) {
        QuestStatus.COMPLETED -> "✓"
        QuestStatus.ACTIVE -> "›"
        QuestStatus.LOCKED -> "🔒"
    }
    val color = when (status) {
        QuestStatus.COMPLETED -> CompletedGreen
        QuestStatus.ACTIVE -> TextPrimary
        QuestStatus.LOCKED -> LockedBlueGray
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, CardBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = symbol, color = color, fontSize = 16.sp)
    }
}

@Composable
private fun LinearProgress(trackColor: Color, fillColor: Color, progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(trackColor)
    ) {
        if (progress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(fillColor)
            )
        }
    }
}

@Composable
private fun SegmentedProgressBar(segments: Int, active: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(segments) { index ->
            Box(
                modifier = Modifier
                    .size(width = 30.dp, height = 5.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (index < active) ActiveCyan else Color.White.copy(alpha = 0.12f))
            )
        }
    }
}
