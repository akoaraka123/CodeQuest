package com.example.codequest.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalContentRepository
import com.example.codequest.model.Course
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.LockedBlueGray
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

private enum class CourseAdminStatus {
    AVAILABLE,
    LOCKED,
    DRAFT
}

@Composable
fun AdminCourseOverviewScreen() {
    val courses = LocalContentRepository.courses.sortedBy { it.order }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text("Course Overview", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("CodeQuest learning tracks", color = TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(14.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(courses, key = { it.id }) { c ->
                CourseRow(course = c, status = demoCourseStatus(c.id))
            }
            item { Spacer(modifier = Modifier.height(96.dp)) }
        }
    }
}

@Composable
private fun CourseRow(course: Course, status: CourseAdminStatus) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(course.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text("${course.lessons.size} lessons", color = TextMuted, fontSize = 13.sp)
            StatusLine(status)
            Text(
                text = "View Lessons",
                color = ActiveCyan,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                    .clickable { /* view-only prototype */ }
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun StatusLine(status: CourseAdminStatus) {
    val (label, color) = when (status) {
        CourseAdminStatus.AVAILABLE -> "Available" to CompletedGreen
        CourseAdminStatus.LOCKED -> "Locked" to LockedBlueGray
        CourseAdminStatus.DRAFT -> "Draft" to PrimaryPurple
    }
    Text("Status: $label", color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
}

private fun demoCourseStatus(courseId: String): CourseAdminStatus = when (courseId) {
    "thinking-in-code", "programming-variables" -> CourseAdminStatus.AVAILABLE
    "thinking-python", "programming-functions", "neural-intro" -> CourseAdminStatus.LOCKED
    "algorithmic-thinking", "cs-fundamentals" -> CourseAdminStatus.DRAFT
    else -> CourseAdminStatus.AVAILABLE
}
