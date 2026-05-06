package com.example.codequest.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.ui.text.style.TextAlign
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun CodeQuestLessonScreen(appState: CodeQuestAppState) {
    val quest = appState.getSelectedQuest()
    val lesson = appState.getCurrentLesson()
    BackHandler { appState.backFromLesson() }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CodeQuestBackButton(onClick = { appState.backFromLesson() })
                Text(
                    "Lesson",
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
                    Text(quest?.title ?: "No Quest Selected", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(lesson?.title ?: "No lesson content available yet.", color = TextPrimary, fontSize = 17.sp)
                    Text(
                        lesson?.content ?: "No lesson content available yet. Add lessons in LocalContentRepository.",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                    Text(
                        "Step ${appState.currentLessonIndex + 1} of ${quest?.lessons?.size ?: 0}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            GradientButton(
                text = "Next Lesson",
                enabled = lesson != null
            ) { appState.nextLessonOrChallenge() }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Start Challenge",
                    color = if (lesson != null) TextPrimary else TextMuted,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .background(TextMuted.copy(alpha = 0.15f), shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                        .clickable(enabled = lesson != null) { appState.startChallenge() }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
            if (lesson == null) {
                GradientButton(text = "Back to Quests") { appState.onTabSelected(com.example.codequest.ui.components.AppTab.QUESTS) }
            }
        }
    }
}
