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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.ui.components.AppTab
import com.example.codequest.ui.components.BottomNavigationBar
import com.example.codequest.ui.components.CourseProgressStatus
import com.example.codequest.ui.components.CourseSection
import com.example.codequest.ui.components.CourseUiModel
import com.example.codequest.ui.components.DailyChallengeCard
import com.example.codequest.ui.components.LearningPathStatsRow
import com.example.codequest.ui.components.QuestsHeaderSection
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@Composable
fun CodeQuestQuestsScreen(
    appState: CodeQuestAppState,
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val courses = appState.getCourses().map { course ->
        val sorted = course.lessons.sortedBy { it.order }
        val done = sorted.count { it.id in appState.completedLessonIds }
        val total = sorted.size
        val progress = if (total > 0) done.toFloat() / total.coerceAtLeast(1) else 0f
        val status = when {
            course.id in appState.completedCourseIds -> CourseProgressStatus.COMPLETED
            !appState.isCourseUnlocked(course.id) -> CourseProgressStatus.LOCKED
            course.id == appState.activeCourseId -> CourseProgressStatus.ACTIVE
            else -> CourseProgressStatus.AVAILABLE
        }
        val progressText = when (status) {
            CourseProgressStatus.COMPLETED -> "$done / $total lessons"
            CourseProgressStatus.LOCKED -> "Locked — finish the previous course"
            CourseProgressStatus.ACTIVE, CourseProgressStatus.AVAILABLE ->
                "$done of $total lessons completed"
        }
        CourseUiModel(
            id = course.id,
            title = course.title,
            description = course.description,
            status = status,
            progressText = progressText,
            progress = progress,
            icon = course.icon
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
                        text = "Learning Path",
                        color = TextPrimary,
                        fontSize = 28.sp
                    )
                }
                item {
                    LearningPathStatsRow(
                        coursesCompleted = appState.visibleCompletedCourseCount(),
                        totalCourses = appState.getCourses().size,
                        totalXp = appState.totalExp()
                    )
                }
                item { DailyChallengeCard(onStartClick = { appState.continueLearning() }) }
                item {
                    Text(
                        text = "Courses",
                        color = TextPrimary,
                        fontSize = 30.sp
                    )
                }
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(PrimaryCyan.copy(alpha = 0.4f)))
                }
                item {
                    if (courses.isEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("No courses available yet", color = TextPrimary, fontSize = 18.sp)
                            Text("Add course content to LocalContentRepository to begin.", color = TextMuted, fontSize = 13.sp)
                        }
                    } else {
                        CourseSection(courses) { selected ->
                            if (selected.status == CourseProgressStatus.LOCKED) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Complete the previous quest first.")
                                }
                            } else {
                                appState.openCourseDetail(selected.id)
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp, start = 16.dp, end = 16.dp),
            snackbar = { data ->
                Snackbar(
                    containerColor = Color(0xFF1A2455),
                    contentColor = TextPrimary,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )

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
