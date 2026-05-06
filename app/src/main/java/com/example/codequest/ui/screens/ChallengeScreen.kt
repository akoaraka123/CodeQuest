package com.example.codequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun CodeQuestChallengeScreen(appState: CodeQuestAppState) {
    val question = appState.getCurrentQuestion()
    val questions = appState.getQuestionsForSelectedQuest()
    var selectedOption by remember(appState.currentQuestionIndex) { mutableIntStateOf(-1) }
    var answered by remember(appState.currentQuestionIndex) { mutableStateOf(false) }
    var correct by remember(appState.currentQuestionIndex) { mutableStateOf(false) }
    var explanation by remember(appState.currentQuestionIndex) { mutableStateOf("") }
    var showLeaveDialog by remember { mutableStateOf(false) }
    BackHandler { showLeaveDialog = true }

    if (question == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CodeQuestBackButton(onClick = { appState.backFromChallenge() })
                Text(
                    "Challenge",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(40.dp))
            }
            Text("No challenge questions available yet.", color = TextPrimary)
            Text("Add question content in LocalContentRepository.", color = TextMuted)
            GradientButton(text = "Back to Quests") { appState.onTabSelected(com.example.codequest.ui.components.AppTab.QUESTS) }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CodeQuestBackButton(onClick = { showLeaveDialog = true })
                Text(
                    "Challenge",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(40.dp))
            }
            Text("Question ${appState.currentQuestionIndex + 1} of ${questions.size}", color = TextMuted)
        }
        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(question.type.name.replace("_", " "), color = ActiveCyan, fontSize = 12.sp)
                    Text(question.questionText, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    if (question.codeBlock != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Text(question.codeBlock, color = TextMuted, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        items(question.options.size) { index ->
            val option = question.options[index]
            val selected = selectedOption == index
            val border = if (selected) ActiveCyan else CardBorder
            GlassCard(
                borderBrush = Brush.linearGradient(listOf(border, CardBorder)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !answered) { selectedOption = index }
            ) {
                Text(option, color = TextPrimary, fontSize = 15.sp)
            }
        }
        item {
            if (answered) {
                Text(
                    text = if (correct) "Correct!" else "Incorrect. Try again on next one.",
                    color = if (correct) CompletedGreen else Color(0xFFFF8A80),
                    fontWeight = FontWeight.Bold
                )
                Text(explanation, color = TextMuted, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (!answered) {
                GradientButton(text = "Submit Answer") {
                    if (selectedOption >= 0) {
                        val result = appState.submitAnswer(selectedOption)
                        correct = result.first
                        explanation = result.second
                        answered = true
                    }
                }
            } else {
                GradientButton(text = "Next Question") { appState.moveToNextQuestionOrResult() }
            }
        }
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Leave challenge?") },
            text = { Text("Your current attempt will be reset.") },
            confirmButton = {
                TextButton(onClick = {
                    showLeaveDialog = false
                    appState.backFromChallenge()
                }) { Text("Leave") }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) { Text("Cancel") }
            }
        )
    }
}
