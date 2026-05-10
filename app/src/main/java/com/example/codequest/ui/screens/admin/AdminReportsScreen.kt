package com.example.codequest.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalUserRepository
import com.example.codequest.model.UserRole
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun AdminReportsScreen() {
    val students = LocalUserRepository.users.filter { it.role == UserRole.STUDENT }
    val totalStudents = students.size
    val avgXp = if (students.isNotEmpty()) students.sumOf { it.totalXP } / students.size else 0
    val totalLessonsDone = students.sumOf { it.completedLessons }
    val top = students.maxByOrNull { it.totalXP }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Reports & Progress", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Demo summaries from local accounts", color = TextMuted, fontSize = 13.sp)

        ReportCard(label = "Total students", value = "$totalStudents")
        ReportCard(label = "Average XP", value = "$avgXp XP")
        ReportCard(label = "Completed lessons (sum)", value = "$totalLessonsDone")

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Top student (demo)", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                if (top != null) {
                    Text("${top.fullName} — ${top.totalXP} XP", color = BadgeGold, fontSize = 15.sp)
                    Text("@${top.username}", color = TextMuted, fontSize = 13.sp)
                } else {
                    Text("No student data yet.", color = TextMuted, fontSize = 13.sp)
                }
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Progress summary", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text(
                    "Use User Management for per-student stats. This dashboard rolls up placeholder XP and lesson counts for the school prototype.",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
private fun ReportCard(label: String, value: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, color = TextMuted, fontSize = 13.sp)
            Text(value, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(listOf(ActiveCyan.copy(alpha = 0.5f), PrimaryPurple.copy(alpha = 0.5f)))
                    )
            )
        }
    }
}
