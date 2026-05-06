package com.example.codequest.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.ui.components.AppTab
import com.example.codequest.ui.components.BottomNavigationBar
import com.example.codequest.ui.components.DailyChallengeCard
import com.example.codequest.ui.components.QuestStatus
import com.example.codequest.ui.components.QuestSection
import com.example.codequest.ui.components.QuestUiModel
import com.example.codequest.ui.components.QuestsHeaderSection
import com.example.codequest.ui.components.StatsRow
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary
import com.example.codequest.state.CodeQuestAppState

@Composable
fun CodeQuestQuestsScreen(
    appState: CodeQuestAppState,
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    val quests = appState.getQuests().map { quest ->
        val status = when {
            appState.completedQuestIds.contains(quest.id) -> QuestStatus.COMPLETED
            appState.unlockedQuestIds.contains(quest.id) -> QuestStatus.ACTIVE
            else -> QuestStatus.LOCKED
        }
        val lessonsText = when (status) {
            QuestStatus.COMPLETED -> "${appState.questCompletedLessonCounts[quest.id] ?: quest.lessons.size} / ${quest.lessons.size} lessons"
            QuestStatus.ACTIVE -> "${appState.questCompletedLessonCounts[quest.id] ?: 0} of ${quest.lessons.size} lessons completed"
            QuestStatus.LOCKED -> "0 / ${quest.lessons.size} lessons"
        }
        QuestUiModel(
            id = quest.id,
            title = quest.title,
            description = quest.description,
            status = status,
            lessonsText = lessonsText,
            icon = quest.icon
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
    ) {
        QuestsBackgroundGlow()

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuestsHeaderSection(
                        onNotificationClick = { appState.openNotifications() },
                        showNotificationDot = appState.hasUnreadNotifications()
                    )
                }
                item {
                    Text(
                        text = "Quest Journey",
                        color = TextPrimary,
                        fontSize = 28.sp
                    )
                }
                item {
                    StatsRow(
                        questsCompleted = appState.completedQuestIds.size,
                        totalQuests = appState.getQuests().size,
                        totalXp = appState.totalXP
                    )
                }
                item { DailyChallengeCard(onStartClick = { appState.continueActiveQuest() }) }
                item {
                    Text(
                        text = "Your Quests",
                        color = TextPrimary,
                        fontSize = 30.sp
                    )
                }
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(PrimaryCyan.copy(alpha = 0.4f)))
                }
                item {
                    if (quests.isEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("No quests available yet", color = TextPrimary, fontSize = 18.sp)
                            Text("Add quest content to LocalContentRepository to begin.", color = TextMuted, fontSize = 13.sp)
                        }
                    } else {
                        QuestSection(quests) { selected ->
                            appState.startQuest(selected.id)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
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
private fun QuestsBackgroundGlow() {
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
