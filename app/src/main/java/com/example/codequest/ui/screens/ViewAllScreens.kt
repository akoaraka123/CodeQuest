package com.example.codequest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalContentRepository
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun AllEarnedBadgesScreen(appState: CodeQuestAppState) {
    BackHandler { appState.backFromDetailScreen() }
    val earned = appState.badgeState().filter { appState.earnedBadgeIds.contains(it.first.id) }
    DetailScaffold("All Earned Badges", appState::backFromDetailScreen) {
        if (earned.isEmpty()) {
            item { EmptyState("No earned badges yet.") }
        } else {
            items(earned) { (badge, progress) ->
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${badge.icon}  ${badge.title}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(badge.description, color = TextMuted, fontSize = 13.sp)
                        Text("Progress: $progress / ${badge.target}", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AllLockedBadgesScreen(appState: CodeQuestAppState) {
    BackHandler { appState.backFromDetailScreen() }
    val locked = appState.badgeState().filter { !appState.earnedBadgeIds.contains(it.first.id) }
    DetailScaffold("All Locked Badges", appState::backFromDetailScreen) {
        if (locked.isEmpty()) {
            item { EmptyState("All badges unlocked.") }
        } else {
            items(locked) { (badge, progress) ->
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🔒  ${badge.title}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(badge.description, color = TextMuted, fontSize = 13.sp)
                        Text("Progress: $progress / ${badge.target}", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeCategoriesScreen(appState: CodeQuestAppState) {
    BackHandler { appState.backFromDetailScreen() }
    DetailScaffold("Badge Categories", appState::backFromDetailScreen) {
        if (LocalContentRepository.badgeCategories.isEmpty()) {
            item { EmptyState("No badge categories available yet.") }
        } else {
            items(LocalContentRepository.badgeCategories) { category ->
                GlassCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(category.title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Text(category.count.toString(), color = TextMuted, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AllAchievementsScreen(appState: CodeQuestAppState) {
    BackHandler { appState.backFromDetailScreen() }
    val earned = appState.badgeState().filter { appState.earnedBadgeIds.contains(it.first.id) }
    DetailScaffold("All Achievements", appState::backFromDetailScreen) {
        if (earned.isEmpty()) {
            item { EmptyState("No achievements yet.") }
        } else {
            items(earned) { (badge, progress) ->
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${badge.icon}  ${badge.title}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(badge.description, color = TextMuted, fontSize = 13.sp)
                        Text("Completed: $progress / ${badge.target}", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailScaffold(
    title: String,
    onBack: () -> Unit,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                CodeQuestBackButton(onClick = onBack)
                Text(title, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        content()
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(text, color = TextMuted)
    }
}
