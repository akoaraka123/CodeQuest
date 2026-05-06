package com.example.codequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun CodeQuestResultScreen(appState: CodeQuestAppState) {
    val result = appState.result
    BackHandler { appState.goHome() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            CodeQuestBackButton(onClick = { appState.goHome() }, isClose = true)
            Text(
                "Challenge Result",
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
                Text("Score: ${result?.score ?: 0}", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Percentage: ${result?.percentage ?: 0}%", color = TextPrimary, fontSize = 16.sp)
                Text("XP Earned: ${result?.xpEarned ?: 0}", color = TextPrimary, fontSize = 16.sp)
                Text(
                    if (result?.passed == true) "Passed" else "Failed",
                    color = if (result?.passed == true) CompletedGreen else Color(0xFFFF8A80),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (result?.passed != true) {
                    Text("You need at least 70% to unlock next quest.", color = TextMuted)
                }
            }
        }
        GradientButton(text = "Back to Home") { appState.goHome() }
        GradientButton(text = "Retry Challenge") { appState.retryChallenge() }
        if (result?.passed != true) {
            GradientButton(text = "Review Lesson") { appState.reviewLesson() }
        }
        if (result?.passed == true) {
            GradientButton(text = "Next Quest") { appState.openNextQuestAfterPass() }
        }
    }
}
