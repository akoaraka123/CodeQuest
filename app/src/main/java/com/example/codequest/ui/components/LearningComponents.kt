package com.example.codequest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.model.ProcessStep
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.LockedBlueGray
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

enum class CourseProgressStatus {
    COMPLETED,
    ACTIVE,
    AVAILABLE,
    LOCKED
}

data class CourseUiModel(
    val id: String,
    val title: String,
    val description: String,
    val status: CourseProgressStatus,
    val progressText: String,
    val progress: Float,
    val icon: String
)

@Composable
fun LearningPathStatsRow(
    coursesCompleted: Int,
    totalCourses: Int,
    totalXp: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatMiniCard(
            modifier = Modifier.weight(1f),
            icon = "✓",
            label = "Courses Completed",
            value = "$coursesCompleted / $totalCourses"
        )
        StatMiniCard(
            modifier = Modifier.weight(1f),
            icon = "⚡",
            label = "Total EXP Earned",
            value = "$totalXp EXP"
        )
    }
}

@Composable
private fun StatMiniCard(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String
) {
    GlassCard(modifier = modifier, cornerRadius = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, color = ActiveCyan, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = label, color = TextMuted, fontSize = 11.sp)
                Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun CourseSection(courses: List<CourseUiModel>, onCourseClick: (CourseUiModel) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        courses.forEach { course ->
            CourseCard(course = course, onClick = { onCourseClick(course) })
        }
    }
}

@Composable
fun CourseCard(course: CourseUiModel, onClick: () -> Unit) {
    val borderBrush = when (course.status) {
        CourseProgressStatus.COMPLETED ->
            Brush.linearGradient(listOf(CompletedGreen.copy(alpha = 0.7f), CardBorder))
        CourseProgressStatus.ACTIVE ->
            Brush.linearGradient(listOf(ActiveCyan.copy(alpha = 0.85f), CardBorder))
        CourseProgressStatus.AVAILABLE ->
            Brush.linearGradient(listOf(PrimaryPurple.copy(alpha = 0.55f), CardBorder))
        CourseProgressStatus.LOCKED ->
            Brush.linearGradient(listOf(CardBorder, CardBorder))
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (course.status == CourseProgressStatus.LOCKED) 0.52f else 1f)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        cornerRadius = 18.dp,
        borderBrush = borderBrush
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when (course.status) {
                            CourseProgressStatus.COMPLETED ->
                                Brush.linearGradient(listOf(Color(0xFF093A34), Color(0xFF133A54)))
                            CourseProgressStatus.ACTIVE ->
                                Brush.linearGradient(listOf(Color(0xFF0A2F4A), Color(0xFF103946)))
                            CourseProgressStatus.AVAILABLE ->
                                Brush.linearGradient(listOf(Color(0xFF1A2048), Color(0xFF152850)))
                            CourseProgressStatus.LOCKED ->
                                Brush.linearGradient(listOf(Color(0xFF1F204A), Color(0xFF141A33)))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = course.icon, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = course.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CourseStatusBadge(status = course.status)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = course.description, color = TextMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                CourseProgressLine(progress = course.progress, status = course.status)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = course.progressText,
                    color = progressSubtextColor(course.status),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            CourseRightGlyph(status = course.status)
        }
    }
}

@Composable
private fun CourseProgressLine(progress: Float, status: CourseProgressStatus) {
    val fill = when (status) {
        CourseProgressStatus.COMPLETED -> CompletedGreen
        CourseProgressStatus.ACTIVE -> ActiveCyan
        CourseProgressStatus.AVAILABLE -> PrimaryPurple.copy(alpha = 0.75f)
        CourseProgressStatus.LOCKED -> Color.Transparent
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.1f))
    ) {
        if (progress > 0f && status != CourseProgressStatus.LOCKED) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(fill)
            )
        }
    }
}

@Composable
private fun progressSubtextColor(status: CourseProgressStatus): Color = when (status) {
    CourseProgressStatus.COMPLETED -> CompletedGreen
    CourseProgressStatus.ACTIVE -> ActiveCyan
    CourseProgressStatus.AVAILABLE -> TextMuted
    CourseProgressStatus.LOCKED -> LockedBlueGray
}

@Composable
private fun CourseStatusBadge(status: CourseProgressStatus) {
    val bg = when (status) {
        CourseProgressStatus.COMPLETED -> CompletedGreen.copy(alpha = 0.18f)
        CourseProgressStatus.ACTIVE -> ActiveCyan.copy(alpha = 0.18f)
        CourseProgressStatus.AVAILABLE -> PrimaryPurple.copy(alpha = 0.2f)
        CourseProgressStatus.LOCKED -> LockedBlueGray.copy(alpha = 0.2f)
    }
    val fg = when (status) {
        CourseProgressStatus.COMPLETED -> CompletedGreen
        CourseProgressStatus.ACTIVE -> ActiveCyan
        CourseProgressStatus.AVAILABLE -> PrimaryPurple.copy(alpha = 0.95f)
        CourseProgressStatus.LOCKED -> LockedBlueGray
    }
    val label = when (status) {
        CourseProgressStatus.COMPLETED -> "COMPLETED"
        CourseProgressStatus.ACTIVE -> "ACTIVE"
        CourseProgressStatus.AVAILABLE -> "AVAILABLE"
        CourseProgressStatus.LOCKED -> "LOCKED"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = label, color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
    }
}

@Composable
private fun CourseRightGlyph(status: CourseProgressStatus) {
    val symbol = when (status) {
        CourseProgressStatus.COMPLETED -> "✓"
        CourseProgressStatus.ACTIVE, CourseProgressStatus.AVAILABLE -> "›"
        CourseProgressStatus.LOCKED -> "🔒"
    }
    val color = when (status) {
        CourseProgressStatus.COMPLETED -> CompletedGreen
        CourseProgressStatus.ACTIVE, CourseProgressStatus.AVAILABLE -> TextPrimary
        CourseProgressStatus.LOCKED -> LockedBlueGray
    }
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, CardBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = symbol, color = color, fontSize = 16.sp)
    }
}

enum class LessonCardStatus {
    COMPLETED,
    ACTIVE,
    LOCKED
}

data class LessonUiModel(
    val id: String,
    val title: String,
    val description: String,
    val status: LessonCardStatus,
    val meta: String
)

@Composable
fun LessonSection(lessons: List<LessonUiModel>, onLessonClick: (LessonUiModel) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        lessons.forEach { lesson ->
            LessonCard(lesson = lesson, onClick = { if (lesson.status != LessonCardStatus.LOCKED) onLessonClick(lesson) })
        }
    }
}

@Composable
fun LessonCard(lesson: LessonUiModel, onClick: () -> Unit) {
    val border = when (lesson.status) {
        LessonCardStatus.COMPLETED -> Brush.linearGradient(listOf(CompletedGreen.copy(alpha = 0.65f), CardBorder))
        LessonCardStatus.ACTIVE -> Brush.linearGradient(listOf(ActiveCyan.copy(alpha = 0.8f), CardBorder))
        LessonCardStatus.LOCKED -> Brush.linearGradient(listOf(CardBorder, CardBorder))
    }
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = lesson.status != LessonCardStatus.LOCKED,
                onClick = onClick
            ),
        cornerRadius = 16.dp,
        borderBrush = border
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressPill(text = lesson.meta, active = lesson.status == LessonCardStatus.ACTIVE)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lesson.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(text = lesson.description, color = TextMuted, fontSize = 12.sp)
            }
            Text(
                text = when (lesson.status) {
                    LessonCardStatus.COMPLETED -> "✓"
                    LessonCardStatus.ACTIVE -> "›"
                    LessonCardStatus.LOCKED -> "🔒"
                },
                color = when (lesson.status) {
                    LessonCardStatus.COMPLETED -> CompletedGreen
                    LessonCardStatus.ACTIVE -> ActiveCyan
                    LessonCardStatus.LOCKED -> LockedBlueGray
                },
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ProgressPill(text: String, active: Boolean) {
    val bg = if (active) ActiveCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)
    val fg = if (active) ActiveCyan else TextMuted
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = text, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CodeBlockCard(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.35f))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(text = code, color = TextMuted, fontSize = 13.sp)
    }
}

@Composable
fun QuestionCardHeader(questionIndex: Int, total: Int, typeLabel: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Question ${questionIndex + 1} of $total", color = TextMuted, fontSize = 13.sp)
        Text(text = typeLabel.replace("_", " "), color = ActiveCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

private val WrongAccent = Color(0xFFFF8A65)
private val WrongAccentSoft = Color(0xFFFFB74D)

@Composable
fun FeedbackCard(
    correct: Boolean,
    reasonBody: String,
    /** When true, copy matches command-sequence / puzzle activities. */
    isCommandSequence: Boolean = false
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing)) +
            slideInVertically(
                animationSpec = tween(380, easing = FastOutSlowInEasing),
                initialOffsetY = { it / 10 }
            ) +
            expandVertically(
                animationSpec = tween(380, easing = FastOutSlowInEasing),
                expandFrom = Alignment.Top
            )
    ) {
        val accentBrush = if (correct) {
            Brush.linearGradient(
                listOf(CompletedGreen.copy(alpha = 0.75f), ActiveCyan.copy(alpha = 0.45f), CardBorder)
            )
        } else {
            Brush.linearGradient(
                listOf(WrongAccent.copy(alpha = 0.65f), WrongAccentSoft.copy(alpha = 0.35f), CardBorder)
            )
        }
        GlassCard(borderBrush = accentBrush, cornerRadius = 20.dp) {
            val titleText = when {
                correct && isCommandSequence -> "Correct! Let's see how your program runs."
                correct -> "Correct! You're on the right track."
                isCommandSequence -> "Not quite. Let's walk through the logic."
                else -> "Not quite. Let's walk through the process."
            }
            val showBody = reasonBody.trim().isNotEmpty() &&
                !reasonBody.trim().equals(titleText, ignoreCase = true)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = titleText,
                    color = if (correct) CompletedGreen else WrongAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
                if (showBody) {
                    Text(
                        text = reasonBody,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProcessRevealStepCard(
    step: ProcessStep,
    positionOneBased: Int,
    totalSteps: Int,
    isLastStep: Boolean,
    onNextStep: () -> Unit,
    onShowFinalResult: () -> Unit
) {
    val accentStart by animateColorAsState(
        targetValue = if (isLastStep) PrimaryPurple.copy(alpha = 0.75f) else ActiveCyan.copy(alpha = 0.55f),
        animationSpec = tween(340, easing = FastOutSlowInEasing),
        label = "processStepAccent"
    )
    GlassCard(
        borderBrush = Brush.linearGradient(
            listOf(accentStart, PrimaryPurple.copy(alpha = 0.42f), CardBorder)
        ),
        cornerRadius = 20.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryPurple.copy(alpha = 0.22f))
                    .border(1.dp, ActiveCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Step $positionOneBased of $totalSteps",
                    color = ActiveCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = step.title,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
            Text(
                text = step.explanation,
                color = TextMuted,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            step.highlightedCommand?.let { cmd ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.28f))
                        .border(1.dp, ActiveCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = cmd, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
            step.miniVisualHint?.let { hint ->
                Text(
                    text = hint,
                    color = ActiveCyan.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            step.codeBlock?.let { CodeBlockCard(code = it) }
            if (isLastStep) {
                GradientButton(text = "Show Final Result") { onShowFinalResult() }
            } else {
                GradientButton(text = "Next Step") { onNextStep() }
            }
        }
    }
}

@Composable
fun GuidedProcessRevealCard(
    steps: List<ProcessStep>,
    currentStepIndex: Int,
    onNextStep: () -> Unit,
    onShowFinal: () -> Unit
) {
    if (steps.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(320, easing = FastOutSlowInEasing)) +
                slideInVertically(tween(320)) { it / 16 } +
                expandVertically(tween(320, easing = FastOutSlowInEasing), expandFrom = Alignment.Top)
        ) {
            Text(
                text = "Guided walkthrough",
                color = ActiveCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp
            )
        }

        Crossfade(
            targetState = currentStepIndex,
            animationSpec = tween(380, easing = FastOutSlowInEasing),
            label = "processRevealStep"
        ) { index ->
            val step = steps.getOrNull(index) ?: return@Crossfade
            val total = steps.size
            val last = index >= steps.lastIndex

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(280, easing = FastOutSlowInEasing)) +
                    slideInVertically(tween(280, easing = FastOutSlowInEasing)) { full -> full / 14 } +
                    expandVertically(tween(280, easing = FastOutSlowInEasing), expandFrom = Alignment.Top)
            ) {
                ProcessRevealStepCard(
                    step = step,
                    positionOneBased = index + 1,
                    totalSteps = total,
                    isLastStep = last,
                    onNextStep = onNextStep,
                    onShowFinalResult = onShowFinal
                )
            }
        }
    }
}

@Composable
fun FinalResultCard(
    summary: String,
    finalOutput: String?
) {
    var reveal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { reveal = true }
    val scale by animateFloatAsState(
        targetValue = if (reveal) 1f else 0.88f,
        animationSpec = tween(480, easing = FastOutSlowInEasing),
        label = "finalCardScale"
    )

    val infinite = rememberInfiniteTransition(label = "finalGlow")
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.38f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    val glowBrush = Brush.linearGradient(
        listOf(
            ActiveCyan.copy(alpha = glowAlpha),
            PrimaryPurple.copy(alpha = glowAlpha * 0.82f),
            ActiveCyan.copy(alpha = glowAlpha * 0.55f)
        )
    )

    GlassCard(
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        borderBrush = glowBrush,
        cornerRadius = 22.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Final result",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            finalOutput?.let { out ->
                Text(text = "Output snapshot", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(1.5.dp, glowBrush, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = out,
                        color = ActiveCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }
            Text(
                text = summary,
                color = TextMuted,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
