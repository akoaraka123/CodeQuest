package com.example.codequest.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.model.Lesson
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.ActiveCyan
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun CodeQuestCourseDetailScreen(appState: CodeQuestAppState) {
    val course = appState.getSelectedCourse()
    BackHandler { appState.backFromCourseDetail() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CodeQuestBackButton(onClick = { appState.backFromCourseDetail() })
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF1A2238))
                    .border(1.dp, CompletedGreen.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡", fontSize = 14.sp)
                    Text(
                        text = "${appState.totalXP}",
                        color = CompletedGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (course == null) {
            Text("No course selected.", color = TextMuted)
            return@Column
        }

        val sortedLessons = course.lessons.sortedBy { it.order }
        val focusLessonId = appState.courseDetailFocusLessonId ?: sortedLessons.firstOrNull {
            it.id in appState.unlockedLessonIds
        }?.id
        val focusLesson = sortedLessons.firstOrNull { it.id == focusLessonId }
            ?: sortedLessons.firstOrNull { it.id in appState.unlockedLessonIds }

        val levelDisplay = focusLesson?.let {
            appState.lessonLevelDisplay(it.id, course.id)
        } ?: 1

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = course.title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                lineHeight = 34.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "LEVEL $levelDisplay",
                color = PrimaryPurple.copy(alpha = 0.92f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        LessonPathDotsRow(
            lessons = sortedLessons,
            unlocked = appState.unlockedLessonIds,
            completed = appState.completedLessonIds,
            focusedId = focusLesson?.id
        )

        QuestHeroPlaceholder(courseIconText = course.icon)

        FocusLessonActivitiesCard(
            focusLesson = focusLesson,
            appState = appState,
            courseId = course.id
        )

        val canStartFocus = focusLesson != null &&
            focusLesson.id in appState.unlockedLessonIds &&
            focusLesson.id !in appState.completedLessonIds
        val primaryLessonButtonLabel = when {
            focusLesson == null -> "Start"
            focusLesson.id in appState.completedLessonIds -> "Review lesson"
            appState.lessonHasAnyCompletedActivity(focusLesson) && appState.lessonHasIncompleteActivity(focusLesson) ->
                "Continue lesson"
            else -> "Start"
        }
        GradientButton(text = primaryLessonButtonLabel) {
            if (focusLesson != null && focusLesson.id in appState.unlockedLessonIds) {
                appState.selectCourseDetailLesson(focusLesson.id)
                appState.startLessonFromCourseDetail()
            }
        }

        appState.lessonEntryBlockedNotice?.let { notice ->
            Text(
                text = notice,
                color = ActiveCyan.copy(alpha = 0.92f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Lesson path",
            color = TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )

        GlassCard(cornerRadius = 20.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                sortedLessons.forEachIndexed { idx, lesson ->
                    val locked = lesson.id !in appState.unlockedLessonIds
                    val done = lesson.id in appState.completedLessonIds
                    val chosen = lesson.id == focusLesson?.id
                    val subtitle = lesson.pathCardSubtitle?.takeIf { it.isNotBlank() }
                        ?: lesson.activities.take(2).joinToString(" • ") { act ->
                            act.prompt.take(48) + if (act.prompt.length > 48) "…" else ""
                        }
                    LessonSelectableRow(
                        indexLabel = "Lesson ${idx + 1}",
                        title = lesson.title,
                        subtitle = subtitle,
                        locked = locked,
                        completed = done,
                        selected = chosen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !locked) {
                                appState.selectCourseDetailLesson(lesson.id)
                            }
                    )
                    if (idx < sortedLessons.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(start = 6.dp)
                                .background(CardBorder.copy(alpha = 0.35f))
                        )
                    }
                }
            }
        }

        Text(
            text = if (!canStartFocus && focusLesson != null && focusLesson.id in appState.completedLessonIds) {
                "Completed lesson — questions you already solved cannot be retaken."
            } else {
                "Choose a lesson, then tap Start to continue where you left off (completed questions stay locked)."
            },
            color = TextMuted.copy(alpha = 0.82f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun LessonPathDotsRow(
    lessons: List<Lesson>,
    unlocked: Set<String>,
    completed: Set<String>,
    focusedId: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        lessons.forEach { lesson ->
            val locked = lesson.id !in unlocked
            val done = lesson.id in completed
            val focus = lesson.id == focusedId
            val color = when {
                locked -> Color.White.copy(alpha = 0.12f)
                focus -> ActiveCyan
                done -> CompletedGreen.copy(alpha = 0.85f)
                else -> Color.White.copy(alpha = 0.35f)
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .width(if (focus) 22.dp else 10.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun QuestHeroPlaceholder(courseIconText: String) {
    val sweep = rememberInfiniteTransition(label = "heroSweep")
    val shift by sweep.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroShift"
    )
    val glow = 0.45f + shift * 0.25f
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        borderBrush = Brush.linearGradient(
            listOf(
                ActiveCyan.copy(alpha = glow),
                PrimaryPurple.copy(alpha = glow * 0.8f),
                CardBorder
            )
        ),
        cornerRadius = 28.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryPurple.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(120f + shift * 80f, 90f),
                        radius = 220f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = courseIconText, fontSize = 44.sp)
                Text(
                    text = "Quest node",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "⟨ build ⟩  ::  run  ::  reflect",
                    color = ActiveCyan.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun FocusLessonActivitiesCard(
    focusLesson: Lesson?,
    appState: CodeQuestAppState,
    courseId: String
) {
    GlassCard(cornerRadius = 22.dp) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Active track",
                color = ActiveCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
            if (focusLesson == null) {
                Text("Unlock a lesson to begin.", color = TextMuted, fontSize = 14.sp)
                return@Column
            }
            Text(
                text = focusLesson.title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = focusLesson.description,
                color = TextMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            val nextIdx = appState.firstIncompleteActivityIndex(focusLesson)
            val lessonDone = focusLesson.id in appState.completedLessonIds
            focusLesson.activities.forEachIndexed { i, act ->
                val completed = act.id in appState.completedActivityIds
                val isCurrent = !lessonDone && nextIdx >= 0 && i == nextIdx
                val isLocked = !completed && !isCurrent
                val statusText = when {
                    completed -> "Completed — replay"
                    isCurrent -> "Continue"
                    else -> "Not started"
                }
                val rowHighlight = isCurrent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (rowHighlight) ActiveCyan.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.04f)
                        )
                        .border(
                            1.dp,
                            if (rowHighlight) ActiveCyan.copy(alpha = 0.35f) else CardBorder.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(
                            enabled = (completed || isCurrent) && focusLesson.id in appState.unlockedLessonIds
                        ) {
                            appState.selectCourseDetailLesson(focusLesson.id)
                            if (completed) {
                                appState.openCompletedActivityReview(courseId, focusLesson.id, i)
                            } else {
                                appState.startLessonFromCourseDetail()
                            }
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (i == 0) "Guided practice" else "Question ${i + 1}",
                            color = if (rowHighlight) TextPrimary else TextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = act.prompt,
                            color = if (rowHighlight) TextMuted else TextMuted.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = statusText,
                            color = when {
                                completed -> CompletedGreen.copy(alpha = 0.9f)
                                isCurrent -> ActiveCyan
                                else -> TextMuted.copy(alpha = 0.75f)
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isLocked) {
                            Text(
                                text = "Locked",
                                color = TextMuted.copy(alpha = 0.55f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonSelectableRow(
    indexLabel: String,
    title: String,
    subtitle: String,
    locked: Boolean,
    completed: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                when {
                    selected -> ActiveCyan.copy(alpha = 0.16f)
                    completed -> CompletedGreen.copy(alpha = 0.08f)
                    else -> Color.Transparent
                }
            )
            .border(
                1.dp,
                when {
                    selected -> ActiveCyan.copy(alpha = 0.55f)
                    completed -> CompletedGreen.copy(alpha = 0.35f)
                    else -> Color.Transparent
                },
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (locked) "🔒" else if (completed) "✓" else "○",
            fontSize = 18.sp
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = indexLabel.uppercase(),
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Text(
                text = title,
                color = if (locked) TextMuted.copy(alpha = 0.45f) else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = TextMuted.copy(alpha = if (locked) 0.35f else 0.75f),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
