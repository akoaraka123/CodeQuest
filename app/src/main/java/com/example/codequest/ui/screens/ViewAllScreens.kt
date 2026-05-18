package com.example.codequest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalContentRepository
import com.example.codequest.model.Badge
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.LockedBlueGray
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun AllEarnedBadgesScreen(appState: CodeQuestAppState) {
    BackHandler { appState.backFromDetailScreen() }
    val earned = appState.badgeState().filter { appState.earnedBadgeIds.contains(it.first.id) }
    DetailScaffold("All Earned Badges", appState::backFromDetailScreen) {
        if (earned.isEmpty()) {
            item { EmptyState("No earned badges yet. Complete lessons to earn badges!") }
        } else {
            items(earned) { (badge, progress) ->
                EarnedBadgeListCard(badge = badge, progress = progress)
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
            item { EmptyState("All badges unlocked. Amazing work!") }
        } else {
            items(locked) { (badge, progress) ->
                LockedBadgeListCard(badge = badge, progress = progress)
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
            item { EmptyState("No achievements yet. Complete lessons to earn badges!") }
        } else {
            items(earned) { (badge, progress) ->
                EarnedBadgeListCard(badge = badge, progress = progress)
            }
        }
    }
}

private fun badgeAccent(badgeId: String): Color = when (badgeId) {
    "python-starter", "condition-master" -> PrimaryPurple
    "perfect-start", "python-path-finisher" -> BadgeGold
    "variable-master", "debug-learner" -> CompletedGreen
    else -> ActiveCyan
}

@Composable
private fun EarnedBadgeListCard(badge: Badge, progress: Int) {
    val accent = badgeAccent(badge.id)
    GlassCard(
        borderBrush = Brush.linearGradient(
            listOf(accent.copy(alpha = 0.7f), accent.copy(alpha = 0.2f))
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(accent.copy(alpha = 0.32f), Color.White.copy(alpha = 0.06f))
                        )
                    )
                    .border(1.5.dp, accent.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = badge.icon, fontSize = 28.sp)
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = badge.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = badge.description,
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.5f)))
                            )
                    )
                }
                Text(
                    text = "${progress} / ${badge.target}",
                    color = accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(accent.copy(alpha = 0.18f))
                    .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "✓ Earned",
                    color = accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun LockedBadgeListCard(badge: Badge, progress: Int) {
    val progressPct = (progress.toFloat() / badge.target.toFloat()).coerceIn(0f, 1f)
    val accentHint = badgeAccent(badge.id).copy(alpha = 0.18f)

    GlassCard(
        borderBrush = Brush.linearGradient(
            listOf(LockedBlueGray.copy(alpha = 0.3f), Color.White.copy(alpha = 0.04f))
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(modifier = Modifier.size(64.dp)) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(accentHint, Color.White.copy(alpha = 0.04f))
                            )
                        )
                        .border(1.dp, LockedBlueGray.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = badge.icon, fontSize = 28.sp)
                }
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1D3A))
                        .border(1.dp, LockedBlueGray.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔒", fontSize = 10.sp)
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = badge.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = badge.description,
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    if (progressPct > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressPct)
                                .height(5.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(PrimaryCyan.copy(alpha = 0.7f), PrimaryPurple.copy(alpha = 0.7f))
                                    )
                                )
                        )
                    }
                }
                Text(
                    text = "$progress / ${badge.target}",
                    color = LockedBlueGray,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(LockedBlueGray.copy(alpha = 0.12f))
                    .border(1.dp, LockedBlueGray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Locked",
                    color = LockedBlueGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
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
                Text(
                    title,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
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
