package com.example.codequest.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalUserRepository
import com.example.codequest.model.UserStatus
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun AdminUserDetailsScreen(
    userId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    val user = LocalUserRepository.users.firstOrNull { it.id == userId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CodeQuestBackButton(onClick = onBack)
            Text(
                "User details",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (user == null) {
            Text("User not found.", color = TextMuted)
            GradientButton(text = "Back") { onBack() }
            return
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Full name", user.fullName)
                    DetailRow("Username", user.username)
                    DetailRow(
                        "Status",
                        user.status.name.lowercase().replaceFirstChar { it.uppercase() }
                    )
                    DetailRow("Role", user.role.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Total XP", "${user.totalXP} XP")
                    DetailRow("Completed lessons", "${user.completedLessons}")
                    DetailRow("Completed courses", "${user.completedCourses}")
                    DetailRow("Badges earned", "${user.badgesEarned}")
                }
            }
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Recent lessons", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    if (user.recentLessonTitles.isEmpty()) {
                        Text(
                            "No lesson activity recorded yet — progress will appear here in a future version.",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    } else {
                        user.recentLessonTitles.forEach { t ->
                            Text("• $t", color = TextMuted, fontSize = 13.sp)
                        }
                    }
                }
            }
            GradientButton(text = "Edit User") { onEdit() }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (user.status == UserStatus.ACTIVE) PrimaryPurple.copy(alpha = 0.15f)
                            else CompletedGreen.copy(alpha = 0.15f)
                        )
                        .clickable { onToggleStatus() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (user.status == UserStatus.ACTIVE) "Deactivate" else "Activate",
                        color = if (user.status == UserStatus.ACTIVE) PrimaryPurple else CompletedGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ActiveCyan.copy(alpha = 0.12f))
                        .clickable { onDelete() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Delete",
                        color = ActiveCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, fontSize = 14.sp)
        Text(value, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
