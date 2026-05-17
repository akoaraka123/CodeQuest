package com.example.codequest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import kotlin.math.min
import kotlin.math.roundToInt
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.model.ActivityItem
import com.example.codequest.model.CommandSequencePlayback
import com.example.codequest.model.ActivityType
import com.example.codequest.model.effectiveProcessSteps
import com.example.codequest.model.correctFilledCode
import com.example.codequest.model.isFillInBlank
import com.example.codequest.model.isMultiBlankCode
import com.example.codequest.model.isTicLesson1MultipleChoice
import com.example.codequest.model.Direction
import com.example.codequest.model.GridPosition
import com.example.codequest.model.PlaybackBoardConfig
import com.example.codequest.model.PlaybackStepResult
import com.example.codequest.model.normalizeCommandToken
import com.example.codequest.model.playbackBoardConfig
import com.example.codequest.model.requiresMultipleChoice
import com.example.codequest.model.rotationZDegrees
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.state.LessonInteractionState
import com.example.codequest.ui.components.CodeBlockCard
import com.example.codequest.ui.components.CommandSequenceSuccessOverlay
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.FeedbackCard
import com.example.codequest.ui.components.FinalResultCard
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.components.GuidedProcessRevealCard
import com.example.codequest.ui.components.QuestionCardHeader
import com.example.codequest.ui.components.RobotLesson2DemoGuideScreen
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CardSurface
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PlaybackErrorOrange = Color(0xFFFF8A65)
private val PlaybackErrorRed = Color(0xFFFF5252)

private const val PLAYBACK_MOVE_MS = 470
private const val PLAYBACK_TURN_MS = 360
private const val PLAYBACK_STEP_PAUSE_MS = 300
private const val PLAYBACK_COLLECT_PRE_MS = 240
private const val PLAYBACK_COLLECT_POST_MS = 280
private const val PLAYBACK_INTRO_MS = 320
private const val PLAYBACK_FINISH_MS = 400
private const val PLAYBACK_ERROR_HOLD_MS = 520

private fun remainingTargetCount(remaining: Set<GridPosition>): Int = remaining.size

private fun playbackStepCaption(step: PlaybackStepResult): String {
    val n = step.commandIndex + 1
    return if (step.isErrorStep) {
        "Step $n is incorrect:\n${step.explanation}"
    } else {
        "Step $n:\n${step.explanation}"
    }
}

private val StepHexShape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = kotlin.math.min(size.width, size.height) / 2f * 0.88f
            for (i in 0 until 6) {
                val theta = -kotlin.math.PI / 2.0 + i * kotlin.math.PI / 3.0
                val x = cx + (r * kotlin.math.cos(theta)).toFloat()
                val y = cy + (r * kotlin.math.sin(theta)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        return Outline.Generic(path)
    }
}

private fun Modifier.tileCircuitTexture(dotColor: Color): Modifier = drawWithContent {
    val step = 5.dp.toPx()
    val r = 0.85.dp.toPx()
    var y = step * 0.5f
    while (y < size.height) {
        var x = step * 0.5f
        while (x < size.width) {
            drawCircle(color = dotColor, radius = r, center = Offset(x, y))
            x += step
        }
        y += step
    }
    drawContent()
}

private fun Modifier.slotDashedRing(color: Color, cornerPx: Float): Modifier = drawWithContent {
    drawContent()
    val w = 1.dp.toPx()
    drawRoundRect(
        brush = SolidColor(color),
        topLeft = Offset(w / 2f, w / 2f),
        size = Size(size.width - w, size.height - w),
        cornerRadius = CornerRadius(cornerPx, cornerPx),
        style = Stroke(
            width = w,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(7f, 5f), 0f)
        )
    )
}

@Composable
private fun FillInBlankAnswerField(
    value: String,
    placeholder: String,
    readOnly: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Fill in the blank",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            placeholder = { Text(placeholder, color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 1,
            maxLines = 3,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ActiveCyan,
                unfocusedBorderColor = CardBorder,
                cursorColor = ActiveCyan,
                focusedContainerColor = Color.White.copy(alpha = 0.06f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
            )
        )
    }
}

@Composable
private fun FillInBlankCodeView(
    code: String,
    selectedAnswer: String,
    resultState: String?,
    selectedAnswers: List<String> = emptyList(),
    activeBlankIndex: Int = -1
) {
    val hasAnyAnswer = selectedAnswer.isNotBlank() || selectedAnswers.any { it.isNotBlank() }
    val blankBg by animateColorAsState(
        targetValue = when (resultState) {
            "correct" -> Color(0xFF00E676).copy(alpha = 0.22f)
            "wrong"   -> Color(0xFFFF5252).copy(alpha = 0.22f)
            else      -> if (hasAnyAnswer) ActiveCyan.copy(alpha = 0.18f)
                         else Color.White.copy(alpha = 0.10f)
        },
        animationSpec = tween(350),
        label = "blankBg"
    )
    val blankTextColor by animateColorAsState(
        targetValue = when (resultState) {
            "correct" -> Color(0xFF00E676)
            "wrong"   -> Color(0xFFFF5252)
            else      -> if (hasAnyAnswer) ActiveCyan else TextMuted
        },
        animationSpec = tween(350),
        label = "blankTextColor"
    )
    val isMultiBlankActive = selectedAnswers.isNotEmpty() && activeBlankIndex >= 0 && resultState == null
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1526))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        val normalizedCode = code.replace(Regex("_{3,}"), "___")
        val parts = normalizedCode.split("___")
        Text(
            text = buildAnnotatedString {
                parts.forEachIndexed { index, part ->
                    if (index > 0) {
                        val blankIdx = index - 1
                        val answer = selectedAnswers.getOrNull(blankIdx) ?: selectedAnswer
                        val displayAnswer = if (answer.isNotBlank()) answer else "     "
                        val (bg, tc) = when {
                            !isMultiBlankActive -> blankBg to blankTextColor
                            blankIdx == activeBlankIndex -> ActiveCyan.copy(alpha = 0.30f) to ActiveCyan
                            answer.isNotBlank() -> ActiveCyan.copy(alpha = 0.10f) to ActiveCyan.copy(alpha = 0.55f)
                            else -> Color.White.copy(alpha = 0.08f) to TextMuted
                        }
                        pushStyle(SpanStyle(background = bg, color = tc, fontWeight = FontWeight.Bold))
                        append(" $displayAnswer ")
                        pop()
                    }
                    append(part)
                }
            },
            fontFamily = FontFamily.Monospace,
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun FillInAnswerChipRow(
    choices: List<String>,
    selectedAnswer: String,
    enabled: Boolean,
    label: String = "Choose the answer",
    onChoiceClick: (String) -> Unit
) {
    val scroll = rememberScrollState()
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            choices.forEach { choice ->
                val isSelected = choice == selectedAnswer
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) ActiveCyan.copy(alpha = 0.18f)
                            else Color.White.copy(alpha = 0.95f)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) ActiveCyan else Color(0xFFE2E8F5),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable(enabled = enabled) { onChoiceClick(choice) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = choice,
                        color = if (isSelected) ActiveCyan else Color(0xFF18224A),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun BlankNavButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (enabled) ActiveCyan.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.05f)
            )
            .border(
                1.dp,
                if (enabled) ActiveCyan.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(20.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (enabled) ActiveCyan else TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MultiBlankChoicePanel(
    currentBlankIndex: Int,
    totalBlanks: Int,
    choices: List<String>,
    selectedAnswer: String,
    enabled: Boolean,
    onChoiceClick: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val isFirst = currentBlankIndex == 0
    val isLast = currentBlankIndex >= totalBlanks - 1
    val currentAnswered = selectedAnswer.isNotBlank()
    val scroll = rememberScrollState()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isFirst) {
                BlankNavButton(label = "← Back", enabled = enabled, onClick = onBack)
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }
            Text(
                text = "Blank ${currentBlankIndex + 1} of $totalBlanks",
                color = ActiveCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            if (!isLast) {
                BlankNavButton(label = "Next →", enabled = enabled && currentAnswered, onClick = onNext)
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            choices.forEach { choice ->
                val isSelected = choice == selectedAnswer
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) ActiveCyan.copy(alpha = 0.18f)
                            else Color.White.copy(alpha = 0.95f)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) ActiveCyan else Color(0xFFE2E8F5),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable(enabled = enabled) { onChoiceClick(choice) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = choice,
                        color = if (isSelected) ActiveCyan else Color(0xFF18224A),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun CommandSequenceCheckButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (enabled) {
                    Brush.horizontalGradient(listOf(PrimaryCyan, PrimaryPurple))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFF2C354D), Color(0xFF1A2234)))
                }
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Check",
                color = if (enabled) Color.White else TextMuted.copy(alpha = 0.78f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "\u2192",
                color = if (enabled) Color.White else TextMuted.copy(alpha = 0.62f),
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun CodeQuestLessonActivityScreen(appState: CodeQuestAppState) {
    val activity = appState.getCurrentActivity()
    val activities = appState.getActivitiesForCurrentLesson()
    val lesson = appState.getSelectedLesson()
    var showLeaveDialog by remember { mutableStateOf(false) }
    BackHandler { showLeaveDialog = true }

    if (activity == null || lesson == null || activities.isEmpty()) {
        EmptyLessonActivityLayout(
            onBack = { showLeaveDialog = true }
        )
        if (showLeaveDialog) {
            LeaveLessonDialog(onDismiss = { showLeaveDialog = false }, onLeave = { appState.backFromLessonActivity() })
        }
        return
    }

    val state = appState.lessonInteractionState
    if (state == LessonInteractionState.ROBOT_DEMO_GUIDE && lesson.id == "tic-l2") {
        RobotLesson2DemoGuideScreen(
            step = appState.robotDemoGuideStep,
            totalSteps = appState.robotDemoGuideStepCount(),
            onNext = { appState.advanceRobotDemoGuide() },
            onSkip = { appState.completeRobotDemoGuide() },
            onBack = { showLeaveDialog = true }
        )
        if (showLeaveDialog) {
            LeaveLessonDialog(onDismiss = { showLeaveDialog = false }, onLeave = { appState.backFromLessonActivity() })
        }
        return
    }

    val revealSteps = appState.currentRevealSteps()
    val compactScreen = LocalConfiguration.current.screenHeightDp <= 820
    val rootVerticalSpacing = if (compactScreen) 8.dp else 12.dp
    val rootVerticalPadding = if (compactScreen) 8.dp else 12.dp
    val useSingleScreenCommandLayout =
        state == LessonInteractionState.ACTIVITY && activity.type == ActivityType.COMMAND_SEQUENCE

    if (useSingleScreenCommandLayout) {
        CommandSequenceActivitySingleScreen(
            appState = appState,
            activity = activity,
            activities = activities,
            compactScreen = compactScreen,
            onBack = { showLeaveDialog = true }
        )
        if (showLeaveDialog) {
            LeaveLessonDialog(onDismiss = { showLeaveDialog = false }, onLeave = { appState.backFromLessonActivity() })
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = rootVerticalPadding),
        verticalArrangement = Arrangement.spacedBy(rootVerticalSpacing)
    ) {
            item {
                LessonActivityTopBar(
                onBack = { showLeaveDialog = true },
                activityIndex = appState.currentActivityIndex,
                totalActivities = activities.size,
                xp = appState.totalExp()
            )
        }

        item {
            QuestionCardHeader(
                questionIndex = appState.currentActivityIndex,
                total = activities.size,
                typeLabel = activity.type.name
            )
        }

        when (state) {
            LessonInteractionState.ROBOT_DEMO_GUIDE -> Unit
            LessonInteractionState.ACTIVITY -> {
                item {
                    TaskHeaderSection(
                        activity = activity,
                        codeSnippet = activity.codeSnippet,
                        fillInSelectedAnswer = if (activity.isFillInBlank()) appState.fillInAnswer else null,
                        fillInSelectedAnswers = if (activity.isMultiBlankCode()) appState.multiBlankAnswers else emptyList(),
                        fillInActiveBlankIndex = if (activity.isMultiBlankCode()) appState.currentBlankIndex else -1
                    )
                }
                if (activity.type == ActivityType.COMMAND_SEQUENCE) {
                    item {
                        val boardCfg = remember(activity.id) { activity.playbackBoardConfig() }
                        CommandPuzzleBoard(
                            boardCfg = boardCfg,
                            robotRow = boardCfg.robotStart.row.toFloat(),
                            robotCol = boardCfg.robotStart.col.toFloat(),
                            rotationDegrees = boardCfg.facing.rotationZDegrees(),
                            remainingTargets = boardCfg.initialTargets,
                            topStatusText = "red target",
                            trailingHint = null,
                            flashTargetAt = null,
                            modifier = Modifier.fillMaxWidth(),
                            highlightCell = null,
                            warnCell = null,
                            emphasizeRobotIssue = false,
                            visualType = activity.visualType
                        )
                    }
                    item {
                        CommandSlotPanel(
                            slots = appState.activityCommandSlots,
                            onSlotClick = { i -> appState.clearCommandSlot(i) }
                        )
                    }
                    item {
                        CommandChipRow(
                            commands = activity.availableCommands,
                            onCommandClick = { appState.fillNextCommandSlot(it) }
                        )
                    }
                    item {
                        CommandSequenceCheckButton(
                            enabled = appState.activityReadyForCheck()
                        ) { appState.submitActivityCheck() }
                    }
                } else if (activity.isFillInBlank()) {
                    if (activity.isMultiBlankCode()) {
                        item {
                            MultiBlankChoicePanel(
                                currentBlankIndex = appState.currentBlankIndex,
                                totalBlanks = activity.codeBlanks.size,
                                choices = appState.currentMultiBlankChoices(),
                                selectedAnswer = appState.multiBlankAnswers.getOrNull(appState.currentBlankIndex).orEmpty(),
                                enabled = !appState.lessonReviewMode,
                                onChoiceClick = appState::selectMultiBlankChoice,
                                onBack = appState::navigateBlankBack,
                                onNext = appState::navigateBlankForward
                            )
                        }
                        if (!appState.lessonReviewMode) {
                            item {
                                GradientButton(
                                    text = "Check",
                                    enabled = appState.activityReadyForCheck()
                                ) { appState.submitActivityCheck() }
                            }
                        }
                    } else if (activity.options.isNotEmpty()) {
                        item {
                            FillInAnswerChipRow(
                                choices = activity.options,
                                selectedAnswer = appState.fillInAnswer,
                                enabled = !appState.lessonReviewMode,
                                onChoiceClick = { choice ->
                                    val next = if (appState.fillInAnswer == choice) "" else choice
                                    appState.updateFillInAnswer(next)
                                }
                            )
                        }
                        if (!appState.lessonReviewMode) {
                            item {
                                GradientButton(
                                    text = "Check",
                                    enabled = appState.activityReadyForCheck()
                                ) { appState.submitActivityCheck() }
                            }
                        }
                    } else {
                        item {
                            FillInBlankAnswerField(
                                value = appState.fillInAnswer,
                                placeholder = activity.fillInPlaceholder ?: "Type your fix here…",
                                readOnly = appState.lessonReviewMode,
                                onValueChange = appState::updateFillInAnswer
                            )
                        }
                        if (!appState.lessonReviewMode) {
                            item {
                                GradientButton(
                                    text = "Check",
                                    enabled = appState.activityReadyForCheck()
                                ) { appState.submitActivityCheck() }
                            }
                        }
                    }
                } else {
                    if (activity.isTicLesson1MultipleChoice()) {
                        item {
                            val left = (3 - appState.lessonOneWrongAttempts).coerceAtLeast(0)
                            Text(
                                text = "Attempts left: $left",
                                color = ActiveCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                    items(count = activity.options.size) { index ->
                        val selected = appState.pendingSubmittedIndex == index
                        val correctIdx = activity.correctAnswerIndex
                        val showCorrect = appState.lessonReviewMode && index == correctIdx
                        val border = when {
                            showCorrect -> CompletedGreen
                            selected -> ActiveCyan
                            else -> CardBorder
                        }
                        GlassCard(
                            borderBrush = Brush.linearGradient(listOf(border, CardBorder)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !appState.lessonReviewMode) {
                                    appState.selectMcOption(index)
                                }
                        ) {
                            Text(
                                text = activity.options[index],
                                color = TextPrimary,
                                fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    if (!appState.lessonReviewMode) {
                        item {
                            GradientButton(
                                text = "Check",
                                enabled = appState.activityReadyForCheck()
                            ) { appState.submitActivityCheck() }
                        }
                    }
                }
            }

            LessonInteractionState.FEEDBACK -> {
                item {
                    TaskHeaderSection(
                        activity = activity,
                        codeSnippet = activity.codeSnippet,
                        fillInSelectedAnswer = if (activity.isFillInBlank()) appState.fillInAnswer else null,
                        fillInSelectedAnswers = if (activity.isMultiBlankCode()) appState.multiBlankAnswers else emptyList(),
                        fillInResultState = if (activity.isFillInBlank()) {
                            if (appState.pendingAnswerCorrect) "correct" else "wrong"
                        } else {
                            null
                        }
                    )
                }
                if (appState.lessonReviewMode && activity.isFillInBlank() && activity.options.isEmpty()) {
                    item {
                        GlassCard {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Your answer",
                                    color = ActiveCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = appState.fillInAnswer.ifBlank { "—" },
                                    color = TextPrimary,
                                    fontSize = 15.sp
                                )
                                if (!appState.pendingAnswerCorrect && activity.fillInAcceptedAnswers.isNotEmpty()) {
                                    Text(
                                        text = "Accepted fix: ${activity.fillInAcceptedAnswers.first()}",
                                        color = CompletedGreen,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
                if (appState.lessonReviewMode && activity.requiresMultipleChoice()) {
                    items(count = activity.options.size) { index ->
                        val selected = appState.pendingSubmittedIndex == index
                        val correctIdx = activity.correctAnswerIndex
                        val isCorrectOption = index == correctIdx
                        val border = when {
                            isCorrectOption -> CompletedGreen
                            selected -> ActiveCyan
                            else -> CardBorder
                        }
                        GlassCard(
                            borderBrush = Brush.linearGradient(listOf(border, CardBorder)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = activity.options[index],
                                color = TextPrimary,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            )
                        }
                    }
                }
                item {
                    val depleted = !appState.pendingAnswerCorrect && appState.wrongAttemptsDepleted()
                    val reasonBody = when {
                        depleted ->
                            "You have used all attempts.\n\n${activity.incorrectFeedback}"
                        appState.pendingAnswerCorrect -> activity.correctFeedback
                        else -> activity.incorrectFeedback
                    }
                    FeedbackCard(
                        correct = appState.pendingAnswerCorrect,
                        reasonBody = reasonBody,
                        isCommandSequence = activity.type == ActivityType.COMMAND_SEQUENCE
                    )
                }
                if (activity.type != ActivityType.COMMAND_SEQUENCE) {
                    if (!appState.lessonReviewMode &&
                        !appState.pendingAnswerCorrect &&
                        !appState.wrongAttemptsDepleted()
                    ) {
                        item {
                            Text(
                                text = "Attempts left: ${appState.attemptsRemaining()}",
                                color = ActiveCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    item {
                        if (appState.lessonReviewMode) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                GradientButton(text = "Continue to next question") {
                                    appState.continueFromCompletedReviewToNextOrExit()
                                }
                                GradientButton(text = "Back to lesson path") {
                                    appState.backFromLessonActivity()
                                }
                            }
                        } else {
                            if (appState.pendingAnswerCorrect) {
                                val lastQ = appState.currentActivityIndex >= activities.lastIndex
                                GradientButton(text = if (lastQ) "Finish lesson" else "Next Question") {
                                    appState.lesson1ProceedAfterCorrectFeedback()
                                }
                            } else {
                                val lastQ = appState.currentActivityIndex >= activities.lastIndex
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (appState.canRetry && !appState.wrongAttemptsDepleted()) {
                                        GradientButton(text = "Try Again") {
                                            appState.lesson1TryAgainAfterWrong()
                                        }
                                    }
                                    GradientButton(text = if (lastQ) "Finish lesson" else "Next Question") {
                                        appState.skipCurrentActivityAfterWrong()
                                    }
                                    GradientButton(text = "View Answer") {
                                        appState.lesson1OpenCorrectAnswerReveal()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        if (appState.lessonReviewMode) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                GradientButton(text = "Continue to next question") {
                                    appState.continueFromCompletedReviewToNextOrExit()
                                }
                                GradientButton(text = "Back to lesson path") {
                                    appState.backFromLessonActivity()
                                }
                            }
                        } else if (appState.pendingAnswerCorrect) {
                            val walkthroughSteps =
                                activity.effectiveProcessSteps(answerCorrect = true)
                            val continueToWalkthrough =
                                !activity.isFillInBlank() &&
                                    activity.requiresProcessRevealBeforeFinal &&
                                    walkthroughSteps.isNotEmpty()
                            GradientButton(text = if (activity.isFillInBlank()) "Continue to next question" else "Continue") {
                                if (activity.isFillInBlank()) {
                                    appState.lesson1ProceedAfterCorrectFeedback()
                                } else if (continueToWalkthrough) {
                                    appState.showProcessRevealFromFeedback()
                                } else {
                                    appState.showFinalResultState()
                                }
                            }
                        } else if (activity.isFillInBlank()) {
                            val attempts = appState.lessonOneWrongAttempts
                            if (attempts >= 3) {
                                GradientButton(text = "View Correct Answer") {
                                    appState.lesson1OpenCorrectAnswerReveal()
                                }
                            } else {
                                GradientButton(text = "Try Again") {
                                    appState.lesson1TryAgainAfterWrong()
                                }
                            }
                        } else {
                            GradientButton(text = "View Correct Answer") {
                                appState.showProcessRevealFromFeedback()
                            }
                        }
                    }
                }
            }

            LessonInteractionState.LESSON1_ANSWER_REVEAL -> {
                if (activity.isFillInBlank()) {
                    item {
                        TaskHeaderSection(
                            activity = activity,
                            muted = true,
                            codeSnippet = activity.codeSnippet,
                            fillInSelectedAnswers = if (activity.isMultiBlankCode()) activity.codeBlanks.map { it.correctAnswer } else emptyList()
                        )
                    }
                    item {
                        GlassCard {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Correct answer",
                                    color = ActiveCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.6.sp
                                )
                                Text(
                                    text = if (activity.isMultiBlankCode()) activity.correctFilledCode()
                                        else activity.fillInAcceptedAnswers.firstOrNull().orEmpty(),
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 22.sp
                                )
                                Text(
                                    text = "Explanation",
                                    color = ActiveCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.6.sp
                                )
                                Text(
                                    text = activity.finalResult,
                                    color = TextMuted,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                    item {
                        val lastQ = appState.currentActivityIndex >= activities.lastIndex
                        GradientButton(text = if (lastQ) "Finish lesson" else "Next Question") {
                            appState.lesson1ProceedAfterRevealExplanation()
                        }
                    }
                } else if (activity.requiresMultipleChoice()) {
                    item {
                        TaskHeaderSection(activity = activity, muted = true)
                    }
                    item {
                        GlassCard {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Correct answer",
                                    color = ActiveCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.6.sp
                                )
                                val idx = activity.correctAnswerIndex
                                if (idx in activity.options.indices) {
                                    Text(
                                        text = activity.options[idx],
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 22.sp
                                    )
                                }
                                Text(
                                    text = "Explanation",
                                    color = ActiveCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.6.sp
                                )
                                Text(
                                    text = activity.finalResult,
                                    color = TextMuted,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                    item {
                        val lastQ = appState.currentActivityIndex >= activities.lastIndex
                        GradientButton(text = if (lastQ) "Finish lesson" else "Next Question") {
                            appState.lesson1ProceedAfterRevealExplanation()
                        }
                    }
                } else {
                    item {
                        Text(
                            "Continue the lesson from the previous step.",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            LessonInteractionState.PROCESS_REVEAL -> {
                when (activity.type) {
                    ActivityType.COMMAND_SEQUENCE -> {
                        item {
                            key(appState.commandPlaybackGeneration) {
                                CommandSequenceRevealBlock(
                                    activity = activity,
                                    appState = appState,
                                    compactMode = compactScreen
                                )
                            }
                        }
                    }
                    else -> {
                        item {
                            TaskHeaderSection(activity = activity, muted = true)
                        }
                        item {
                            if (revealSteps.isNotEmpty()) {
                                GuidedProcessRevealCard(
                                    steps = revealSteps,
                                    currentStepIndex = appState.currentProcessStepIndex,
                                    onNextStep = { appState.nextProcessStep() },
                                    onShowFinal = { appState.showFinalResultState() }
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    GlassCard {
                                        Text(
                                            "No extra steps for this one — view the final result next.",
                                            color = TextMuted,
                                            fontSize = 14.sp
                                        )
                                    }
                                    GradientButton(text = "Show Final Result") {
                                        appState.showFinalResultState()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            LessonInteractionState.FINAL_RESULT -> {
                item {
                    TaskHeaderSection(activity = activity, muted = true)
                }
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(420, easing = FastOutSlowInEasing)) +
                            slideInVertically(tween(420, easing = FastOutSlowInEasing)) { it / 14 } +
                            expandVertically(
                                tween(420, easing = FastOutSlowInEasing),
                                expandFrom = Alignment.Top
                            )
                    ) {
                        FinalResultCard(
                            summary = appState.playbackSummaryOverride ?: activity.finalResult,
                            finalOutput = activity.finalOutput
                        )
                    }
                }
                item {
                    if (appState.lessonReviewMode) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            GradientButton(text = "Continue to next question") {
                                appState.continueFromCompletedReviewToNextOrExit()
                            }
                            GradientButton(text = "Back to lesson path") {
                                appState.backFromLessonActivity()
                            }
                        }
                    } else {
                        val lastActivity = appState.currentActivityIndex >= activities.lastIndex
                        val nextLabel =
                            if (lastActivity) "Finish lesson" else "Next question"
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            GradientButton(text = nextLabel) { appState.proceedAfterFinalResult() }
                            if (!appState.pendingAnswerCorrect &&
                                !appState.hasViewedCorrectAnswer &&
                                !appState.isAnswerRevealed &&
                                appState.canRetry
                            ) {
                                GradientButton(text = "Retry") { appState.retryCurrentActivity() }
                            }
                            GradientButton(text = if (lastActivity) "Learning path" else "Continue lesson") {
                                appState.backFromLessonActivity()
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLeaveDialog) {
        LeaveLessonDialog(onDismiss = { showLeaveDialog = false }, onLeave = { appState.backFromLessonActivity() })
    }
}

@Composable
private fun CommandSequenceActivitySingleScreen(
    appState: CodeQuestAppState,
    activity: ActivityItem,
    activities: List<ActivityItem>,
    compactScreen: Boolean,
    onBack: () -> Unit
) {
    val boardCfg = remember(activity.id) { activity.playbackBoardConfig() }
    val scrollState = rememberScrollState()
    val rootPad = if (compactScreen) 10.dp else 12.dp
    val sectionGap = if (compactScreen) 6.dp else 8.dp
    val readOnlyCs = appState.lessonReviewMode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(horizontal = rootPad, vertical = rootPad)
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(sectionGap)
    ) {
        LessonActivityTopBar(
            onBack = onBack,
            activityIndex = appState.currentActivityIndex,
            totalActivities = activities.size,
            xp = appState.totalExp()
        )
        QuestionCardHeader(
            questionIndex = appState.currentActivityIndex,
            total = activities.size,
            typeLabel = activity.type.name
        )
        TaskHeaderSection(activity = activity)

        CommandPuzzleBoard(
            boardCfg = boardCfg,
            robotRow = boardCfg.robotStart.row.toFloat(),
            robotCol = boardCfg.robotStart.col.toFloat(),
            rotationDegrees = boardCfg.facing.rotationZDegrees(),
            remainingTargets = boardCfg.initialTargets,
            topStatusText = "red target",
            trailingHint = null,
            flashTargetAt = null,
            modifier = Modifier.fillMaxWidth(),
            highlightCell = null,
            warnCell = null,
            emphasizeRobotIssue = false,
            visualType = activity.visualType
        )

        CommandSlotPanel(
            slots = appState.activityCommandSlots,
            onSlotClick = { i -> if (!readOnlyCs) appState.clearCommandSlot(i) },
            compactMode = true,
            readOnly = readOnlyCs
        )
        if (!readOnlyCs) {
            CommandChipRow(
                commands = activity.availableCommands,
                onCommandClick = { appState.fillNextCommandSlot(it) }
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (!readOnlyCs) {
            CommandSequenceCheckButton(
                enabled = appState.activityReadyForCheck()
            ) { appState.submitActivityCheck() }
        }
    }
}

@Composable
private fun LessonActivityTopBar(
    onBack: () -> Unit,
    activityIndex: Int,
    totalActivities: Int,
    xp: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CodeQuestBackButton(onClick = onBack)
        LearningProgressDots(
            currentIndex = activityIndex.coerceAtLeast(0),
            total = totalActivities.coerceAtLeast(1)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1C2438))
                .border(1.dp, CompletedGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "⚡", fontSize = 14.sp)
                Text(text = "$xp EXP", color = CompletedGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LearningProgressDots(currentIndex: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(total) { idx ->
            val lit = idx <= currentIndex
            Box(
                modifier = Modifier
                    .size(if (lit) 8.dp else 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (lit) Color.White.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.22f))
            )
        }
    }
}

@Composable
private fun TaskHeaderSection(
    activity: ActivityItem,
    muted: Boolean = false,
    codeSnippet: String? = null,
    fillInSelectedAnswer: String? = null,
    fillInSelectedAnswers: List<String> = emptyList(),
    fillInResultState: String? = null,
    fillInActiveBlankIndex: Int = -1
) {
    GlassCard(
        borderBrush = Brush.linearGradient(
            listOf(
                if (muted) CardBorder.copy(alpha = 0.5f) else ActiveCyan.copy(alpha = 0.45f),
                PrimaryPurple.copy(alpha = 0.35f),
                CardBorder
            )
        )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "✦", color = ActiveCyan, fontSize = 13.sp)
                Text(
                    text = activity.difficultyLabel ?: "Activity",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
            val snippet = codeSnippet?.takeIf { it.isNotBlank() }
            if (snippet != null) {
                if (activity.isFillInBlank()) {
                    FillInBlankCodeView(
                        code = snippet,
                        selectedAnswer = fillInSelectedAnswer.orEmpty(),
                        resultState = fillInResultState,
                        selectedAnswers = fillInSelectedAnswers,
                        activeBlankIndex = fillInActiveBlankIndex
                    )
                } else {
                    CodeBlockCard(code = snippet)
                }
            }
            Text(
                text = activity.prompt,
                color = if (muted) TextMuted else TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun CommandPuzzleBoardForGuide(
    boardCfg: PlaybackBoardConfig,
    robotRow: Float,
    robotCol: Float,
    rotationDegrees: Float,
    remainingTargets: Set<GridPosition>,
    topStatusText: String,
    trailingHint: String?,
    flashTargetAt: GridPosition?,
    highlightCell: GridPosition?,
    warnCell: GridPosition?,
    emphasizeRobotIssue: Boolean = false,
    visualType: String? = null,
    modifier: Modifier = Modifier
) {
    CommandPuzzleBoard(
        boardCfg = boardCfg,
        robotRow = robotRow,
        robotCol = robotCol,
        rotationDegrees = rotationDegrees,
        remainingTargets = remainingTargets,
        topStatusText = topStatusText,
        trailingHint = trailingHint,
        flashTargetAt = flashTargetAt,
        highlightCell = highlightCell,
        warnCell = warnCell,
        emphasizeRobotIssue = emphasizeRobotIssue,
        visualType = visualType,
        modifier = modifier
    )
}

@Composable
private fun CommandPuzzleBoard(
    boardCfg: PlaybackBoardConfig,
    robotRow: Float,
    robotCol: Float,
    rotationDegrees: Float,
    remainingTargets: Set<GridPosition>,
    topStatusText: String,
    trailingHint: String?,
    flashTargetAt: GridPosition?,
    highlightCell: GridPosition?,
    warnCell: GridPosition?,
    emphasizeRobotIssue: Boolean = false,
    visualType: String? = null,
    modifier: Modifier = Modifier,
    boardAlpha: Float = 1f
) {
    val rows = boardCfg.rows
    val cols = boardCfg.cols
    /** Width:height of the interactive grid (3×3 boards use a 1:1 square). */
    val gridAspectRatio =
        if (cols == rows) {
            1f
        } else {
            cols.toFloat() / rows.toFloat()
        }
    val boardCorner = 18.dp
    val tileCorner = 10.dp
    GlassCard(
        modifier = modifier,
        borderBrush = Brush.linearGradient(
            listOf(
                ActiveCyan.copy(alpha = 0.42f * boardAlpha.coerceIn(0.45f, 1f)),
                Color.White.copy(alpha = 0.22f),
                PrimaryPurple.copy(alpha = 0.38f),
                CardBorder.copy(alpha = 0.95f)
            )
        ),
        cornerRadius = 22.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "◈", color = PrimaryPurple, fontSize = 14.sp)
                    Text(
                        text = topStatusText,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                trailingHint?.let {
                    Text(text = it, color = ActiveCyan.copy(alpha = 0.85f), fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(gridAspectRatio)
                    .clip(RoundedCornerShape(boardCorner))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF14264A).copy(alpha = 0.92f),
                                Color(0xFF0A132E),
                                Color(0xFF060C20)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                ActiveCyan.copy(alpha = 0.55f),
                                PrimaryPurple.copy(alpha = 0.35f),
                                ActiveCyan.copy(alpha = 0.28f)
                            )
                        ),
                        shape = RoundedCornerShape(boardCorner)
                    )
            ) {
                val density = LocalDensity.current
                val isColorTargetBoard = visualType == "GRID_COLOR_TARGET"
                val gridInset = if (isColorTargetBoard) 8.dp else 10.dp
                val gap = 5.dp
                val gapPx = density.run { gap.toPx() }
                val padPx = density.run { gridInset.toPx() }
                val innerW = density.run { maxWidth.toPx() } - 2f * padPx
                val innerH = density.run { maxHeight.toPx() } - 2f * padPx
                val cellPxFromWidth = (innerW - gapPx * (cols - 1)) / cols.toFloat()
                val cellPxFromHeight = (innerH - gapPx * (rows - 1)) / rows.toFloat()
                val cellPx =
                    min(cellPxFromWidth, cellPxFromHeight)
                        .coerceAtLeast(density.run { 8.dp.toPx() })

                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(gridInset)
                    ) {
                        val arm = 11.dp.toPx()
                        val ins = 4.dp.toPx()
                        val cyan = ActiveCyan.copy(alpha = 0.48f)
                        val sw = 1.35.dp.toPx()
                        drawLine(cyan, Offset(ins, ins), Offset(ins + arm, ins), sw)
                        drawLine(cyan, Offset(ins, ins), Offset(ins, ins + arm), sw)
                        drawLine(cyan, Offset(size.width - ins, ins), Offset(size.width - ins - arm, ins), sw)
                        drawLine(cyan, Offset(size.width - ins, ins), Offset(size.width - ins, ins + arm), sw)
                        drawLine(cyan, Offset(ins, size.height - ins), Offset(ins + arm, size.height - ins), sw)
                        drawLine(cyan, Offset(ins, size.height - ins), Offset(ins, size.height - ins - arm), sw)
                        drawLine(cyan, Offset(size.width - ins, size.height - ins), Offset(size.width - ins - arm, size.height - ins), sw)
                        drawLine(cyan, Offset(size.width - ins, size.height - ins), Offset(size.width - ins, size.height - ins - arm), sw)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(gridInset),
                        verticalArrangement = Arrangement.spacedBy(gap)
                    ) {
                        for (r in 0 until rows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f, fill = true),
                                horizontalArrangement = Arrangement.spacedBy(gap),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (c in 0 until cols) {
                                    val pos = GridPosition(r, c)
                                    val hasTarget = remainingTargets.contains(pos)
                                    val flashing = flashTargetAt == pos
                                    val highlight = highlightCell == pos
                                    val warned = warnCell == pos
                                    // Subtle “path” accent only on the true center of a 3×3 color-target board.
                                    // On 4×4 (and other sizes), (1,1) is not the center—do not dim that tile.
                                    val isEmptyCell =
                                        isColorTargetBoard &&
                                            rows == 3 &&
                                            cols == 3 &&
                                            pos == GridPosition(1, 1)
                                    val isRedTargetCell = isColorTargetBoard && hasTarget
                                    val pulse by animateFloatAsState(
                                        targetValue = if (flashing) 1f else 0.55f,
                                        animationSpec = tween(180, easing = FastOutSlowInEasing),
                                        label = "targetPulse"
                                    )
                                    val tileTexture =
                                        if (!warned) {
                                            Modifier.tileCircuitTexture(
                                                when {
                                                    isRedTargetCell -> Color(0xFFFF8C98).copy(alpha = 0.14f)
                                                    isEmptyCell -> ActiveCyan.copy(alpha = 0.09f)
                                                    else -> ActiveCyan.copy(alpha = 0.11f)
                                                }
                                            )
                                        } else {
                                            Modifier
                                        }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .graphicsLayer { alpha = boardAlpha.coerceIn(0.4f, 1f) }
                                            .clip(RoundedCornerShape(tileCorner))
                                            .background(
                                                when {
                                                    warned -> Color(0xFF291C18)
                                                    isRedTargetCell -> Color(0xFF6E2434)
                                                    isColorTargetBoard && isEmptyCell -> Color(0xFF121C38)
                                                    isColorTargetBoard -> Color(0xFF1A4A8F)
                                                    else -> Color(0xFF152046)
                                                }
                                            )
                                            .then(tileTexture)
                                            .border(
                                                width = when {
                                                    isRedTargetCell -> 2.dp
                                                    warned -> 2.dp
                                                    else -> 1.dp
                                                },
                                                color = when {
                                                    warned -> PlaybackErrorOrange.copy(alpha = if (highlight) 0.98f else 0.82f)
                                                    highlight -> CompletedGreen.copy(alpha = 0.75f)
                                                    isRedTargetCell -> Color(0xFFFF5A6B).copy(alpha = if (flashing) 0.95f else 0.88f)
                                                    flashing -> PrimaryPurple.copy(alpha = 0.95f)
                                                    else -> ActiveCyan.copy(alpha = 0.18f)
                                                },
                                                shape = RoundedCornerShape(tileCorner)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            isRedTargetCell -> {
                                                Box(
                                                    modifier = Modifier
                                                        .matchParentSize()
                                                        .padding(4.dp)
                                                        .background(
                                                            Color(0xFFFF4D67).copy(alpha = 0.14f),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                )
                                                Text(
                                                    text = "⌖",
                                                    color = Color(0xFFFF7A88).copy(alpha = pulse.coerceIn(0.55f, 1f)),
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            hasTarget && !isColorTargetBoard -> {
                                                Text(
                                                    text = "◎",
                                                    color = PrimaryPurple.copy(alpha = pulse),
                                                    fontSize = if (flashing) 18.sp else 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val iconPx = density.run { 34.dp.toPx() }
                    val offX =
                        padPx + robotCol * (cellPx + gapPx) + (cellPx - iconPx) / 2f
                    val offY =
                        padPx + robotRow * (cellPx + gapPx) + (cellPx - iconPx) / 2f

                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(offX.roundToInt(), offY.roundToInt())
                            }
                            .size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (emphasizeRobotIssue) {
                                        PlaybackErrorOrange.copy(alpha = 0.16f)
                                    } else {
                                        ActiveCyan.copy(alpha = 0.14f)
                                    },
                                    CircleShape
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (emphasizeRobotIssue) {
                                        PlaybackErrorOrange.copy(alpha = 0.75f)
                                    } else {
                                        ActiveCyan.copy(alpha = 0.48f)
                                    },
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = "\uD83E\uDD16",
                            fontSize = 18.sp,
                            modifier = Modifier.graphicsLayer {
                                rotationZ = rotationDegrees
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaybackCommandSlots(
    commands: List<String>,
    highlightedIndex: Int,
    playbackComplete: Boolean,
    results: List<PlaybackStepResult>,
    /** Inclusive last command index that playback actually ran (remaining rows are skipped after first error). */
    maxReplayedInclusive: Int,
    objectiveMet: Boolean,
    goalFailureIndex: Int?,
    compactMode: Boolean
) {
    val panelPadding = if (compactMode) 10.dp else 14.dp
    val rowSpacing = if (compactMode) 6.dp else 8.dp
    val rowVerticalPadding = if (compactMode) 8.dp else 12.dp
    GlassCard(
        borderBrush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), CardBorder)),
        cornerRadius = 18.dp
    ) {
        Column(
            modifier = Modifier.padding(panelPadding),
            verticalArrangement = Arrangement.spacedBy(rowSpacing)
        ) {
            Text("Your program", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            commands.forEachIndexed { index, cmd ->
                val stepResult = results.getOrNull(index)
                val rowIsError = stepResult?.isErrorStep == true
                val rowIsGoalFailure = playbackComplete && goalFailureIndex == index && !rowIsError
                val firstErrorIndex =
                    results.indexOfFirst { it.isErrorStep }.takeIf { it >= 0 }
                // Only cascade “wrong” styling from real simulator failures or goal-not-met—not from
                // comparison to [activity.correctSequence]. Alternate valid paths can diverge from the
                // authored reference at step 0 and incorrectly painted every row orange otherwise.
                val firstWrongIndex =
                    listOfNotNull(firstErrorIndex, goalFailureIndex).minOrNull()
                val rowWrongCascade = playbackComplete && firstWrongIndex != null && index >= firstWrongIndex
                val isSkipped = playbackComplete && index > maxReplayedInclusive && !rowWrongCascade
                val rowReplayed =
                    index <= maxReplayedInclusive ||
                        (highlightedIndex >= 0 && index < highlightedIndex)
                val active =
                    !playbackComplete && !isSkipped && highlightedIndex == index
                val replayedOk = rowReplayed && !rowIsError && !isSkipped && objectiveMet
                val rowMarkedWrong = rowIsError || rowIsGoalFailure || rowWrongCascade
                val borderColor = when {
                    isSkipped ->
                        Brush.horizontalGradient(
                            listOf(
                                TextMuted.copy(alpha = 0.25f),
                                CardBorder.copy(alpha = 0.3f),
                                CardBorder.copy(alpha = 0.2f)
                            )
                        )
                    active && rowIsGoalFailure ->
                        Brush.linearGradient(
                            listOf(PlaybackErrorRed.copy(alpha = 0.88f), PlaybackErrorOrange, CardBorder)
                        )
                    active && rowMarkedWrong ->
                        Brush.linearGradient(
                            listOf(PlaybackErrorRed.copy(alpha = 0.92f), PlaybackErrorOrange, CardBorder)
                        )
                    active ->
                        Brush.linearGradient(
                            listOf(ActiveCyan.copy(alpha = 0.9f), PrimaryPurple.copy(alpha = 0.65f), CardBorder)
                        )
                    playbackComplete && rowIsError ->
                        Brush.horizontalGradient(
                            listOf(
                                PlaybackErrorOrange.copy(alpha = 0.45f),
                                PlaybackErrorOrange.copy(alpha = 0.2f),
                                CardBorder.copy(alpha = 0.35f)
                            )
                        )
                    playbackComplete && rowIsGoalFailure ->
                        Brush.horizontalGradient(
                            listOf(
                                PlaybackErrorOrange.copy(alpha = 0.45f),
                                PlaybackErrorOrange.copy(alpha = 0.2f),
                                CardBorder.copy(alpha = 0.35f)
                            )
                        )
                    playbackComplete && rowMarkedWrong ->
                        Brush.horizontalGradient(
                            listOf(
                                PlaybackErrorOrange.copy(alpha = 0.45f),
                                PlaybackErrorOrange.copy(alpha = 0.2f),
                                CardBorder.copy(alpha = 0.35f)
                            )
                        )
                    replayedOk ->
                        Brush.horizontalGradient(
                            listOf(CompletedGreen.copy(alpha = 0.35f), CardBorder.copy(alpha = 0.35f))
                        )
                    else -> Brush.linearGradient(listOf(CardBorder.copy(alpha = 0.45f), CardBorder.copy(alpha = 0.35f)))
                }
                val bgTone = when {
                    isSkipped -> Color.Black.copy(alpha = 0.2f)
                    active && rowIsGoalFailure -> PlaybackErrorOrange.copy(alpha = 0.14f)
                    active && rowMarkedWrong -> PlaybackErrorOrange.copy(alpha = 0.14f)
                    active -> ActiveCyan.copy(alpha = 0.1f)
                    playbackComplete && rowIsGoalFailure -> PlaybackErrorOrange.copy(alpha = 0.08f)
                    playbackComplete && rowMarkedWrong -> PlaybackErrorOrange.copy(alpha = 0.08f)
                    replayedOk -> CompletedGreen.copy(alpha = 0.06f)
                    else -> Color.Black.copy(alpha = 0.28f)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${index + 1}",
                        color = TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.width(22.dp),
                        textAlign = TextAlign.End
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgTone)
                            .border(
                                width = if (active) 2.dp else 1.dp,
                                brush = borderColor,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = rowVerticalPadding)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (active && rowMarkedWrong) {
                                Text(text = "⚠", color = PlaybackErrorOrange, fontSize = 15.sp)
                            }
                            Text(
                                text = cmd,
                                color = when {
                                    isSkipped -> TextMuted.copy(alpha = 0.72f)
                                    active && rowIsGoalFailure -> PlaybackErrorOrange
                                    active && rowMarkedWrong -> PlaybackErrorOrange
                                    playbackComplete && rowIsGoalFailure -> PlaybackErrorOrange.copy(alpha = 0.88f)
                                    playbackComplete && rowMarkedWrong -> PlaybackErrorOrange.copy(alpha = 0.88f)
                                    else -> TextPrimary
                                },
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            if (playbackComplete && !isSkipped) {
                                val showErrorMarker = rowMarkedWrong
                                val showCheck = replayedOk && !showErrorMarker
                                Text(
                                    text = when {
                                        showErrorMarker -> "!"
                                        showCheck -> "✓"
                                        else -> "•"
                                    },
                                    color = when {
                                        showErrorMarker -> PlaybackErrorOrange
                                        showCheck -> CompletedGreen
                                        else -> TextMuted.copy(alpha = 0.75f)
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (playbackComplete && isSkipped) {
                                Text(
                                    text = "—",
                                    color = TextMuted.copy(alpha = 0.55f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaybackExplanationCard(textBody: String, isError: Boolean) {
    AnimatedVisibility(
        visible = textBody.isNotBlank(),
        enter = fadeIn(tween(260, easing = FastOutSlowInEasing)) +
            slideInVertically(tween(260, easing = FastOutSlowInEasing)) { it / 22 }
    ) {
        GlassCard(
            borderBrush = if (isError) {
                Brush.linearGradient(
                    listOf(
                        PlaybackErrorRed.copy(alpha = 0.75f),
                        PlaybackErrorOrange.copy(alpha = 0.85f),
                        CardBorder
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        PrimaryPurple.copy(alpha = 0.45f),
                        ActiveCyan.copy(alpha = 0.28f),
                        CardBorder
                    )
                )
            },
            cornerRadius = 16.dp
        ) {
            Text(
                text = textBody,
                color = if (isError) {
                    PlaybackErrorOrange.copy(alpha = 0.95f)
                } else {
                    TextPrimary
                },
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
private fun NeonOutlineRowButton(
    text: String,
    onClick: () -> Unit,
    accent: Color,
    compactMode: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                1.5.dp,
                accent.copy(alpha = if (enabled) 0.8f else 0.35f),
                RoundedCornerShape(22.dp)
            )
            .background(Color.White.copy(alpha = if (enabled) 0.06f else 0.03f))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = if (compactMode) 11.dp else 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) accent else accent.copy(alpha = 0.45f),
            fontWeight = FontWeight.SemiBold,
            fontSize = if (compactMode) 14.sp else 16.sp
        )
    }
}

@Composable
private fun CommandSequenceRevealBlock(
    activity: ActivityItem,
    appState: CodeQuestAppState,
    compactMode: Boolean
) {
    val boardCfg = remember(activity.id) { activity.playbackBoardConfig() }
    val playbackGen = appState.commandPlaybackGeneration
    val cmds = appState.commandPlaybackCommandsSnapshot
    val playbackResults = appState.commandPlaybackResults

    val rowAnim = remember(playbackGen) { Animatable(boardCfg.robotStart.row.toFloat()) }
    val colAnim = remember(playbackGen) { Animatable(boardCfg.robotStart.col.toFloat()) }
    val rotAnim = remember(playbackGen) { Animatable(boardCfg.facing.rotationZDegrees()) }

    var highlightedLine by remember(playbackGen) { mutableIntStateOf(-1) }
    var explanationCaption by remember(playbackGen) { mutableStateOf("") }
    var displayedTargets by remember(playbackGen) { mutableStateOf(boardCfg.initialTargets.toSet()) }
    var flashCell by remember(playbackGen) { mutableStateOf<GridPosition?>(null) }
    var playbackComplete by remember(playbackGen) { mutableStateOf(false) }
    var summaryPrepared by remember(playbackGen) { mutableStateOf<String?>(null) }
    var maxReplayedStepInclusive by remember(playbackGen) { mutableIntStateOf(-1) }
    var goalFailureHighlightIndex by remember(playbackGen) { mutableStateOf<Int?>(null) }
    var objectiveMetState by remember(playbackGen) { mutableStateOf(false) }
    var showSuccessBalloons by remember(playbackGen) { mutableStateOf(false) }
    var suppressPlaybackActionButtons by remember(playbackGen) { mutableStateOf(false) }

    LaunchedEffect(playbackGen, activity.id) {
        val results = appState.commandPlaybackResults
        rowAnim.snapTo(boardCfg.robotStart.row.toFloat())
        colAnim.snapTo(boardCfg.robotStart.col.toFloat())
        rotAnim.snapTo(boardCfg.facing.rotationZDegrees())
        highlightedLine = -1
        explanationCaption = ""
        displayedTargets = boardCfg.initialTargets.toSet()
        flashCell = null
        playbackComplete = false
        summaryPrepared = null
        maxReplayedStepInclusive = -1
        goalFailureHighlightIndex = null
        objectiveMetState = false
        delay(PLAYBACK_INTRO_MS.toLong())
        if (results.isEmpty()) {
            appState.showFinalResultState(
                CommandSequencePlayback.buildPlaybackFinalSummary(
                    pendingAnswerMatchedKey = false,
                    results = emptyList(),
                    finalRemainingCount = boardCfg.initialTargets.size
                )
            )
            return@LaunchedEffect
        }
        val moveSpec = tween<Float>(durationMillis = PLAYBACK_MOVE_MS, easing = FastOutSlowInEasing)
        val turnSpec = tween<Float>(durationMillis = PLAYBACK_TURN_MS, easing = FastOutSlowInEasing)

        var firstErrorIndex: Int? = null
        for (i in results.indices) {
            val step = results[i]
            highlightedLine = i
            explanationCaption = playbackStepCaption(step)
            delay((PLAYBACK_STEP_PAUSE_MS / 2).coerceAtLeast(120).toLong())
            val token = normalizeCommandToken(step.command)
            when (token) {
                "move forward" -> {
                    coroutineScope {
                        launch {
                            rowAnim.animateTo(step.afterPosition.row.toFloat(), moveSpec)
                        }
                        launch {
                            colAnim.animateTo(step.afterPosition.col.toFloat(), moveSpec)
                        }
                    }
                    rotAnim.snapTo(step.afterDirection.rotationZDegrees())
                    delay(PLAYBACK_STEP_PAUSE_MS.toLong())
                }
                "turn right" -> {
                    rotAnim.animateTo(rotAnim.value + 90f, turnSpec)
                    delay(PLAYBACK_STEP_PAUSE_MS.toLong())
                }
                "turn left" -> {
                    rotAnim.animateTo(rotAnim.value - 90f, turnSpec)
                    delay(PLAYBACK_STEP_PAUSE_MS.toLong())
                }
                "select red" -> {
                    flashCell = step.beforePosition
                    delay(PLAYBACK_COLLECT_PRE_MS.toLong())
                    flashCell = null
                    delay(PLAYBACK_COLLECT_POST_MS.toLong())
                }
                else -> delay(PLAYBACK_STEP_PAUSE_MS.toLong())
            }
            displayedTargets = step.remainingTargetsAfter
            maxReplayedStepInclusive = i
            delay((PLAYBACK_STEP_PAUSE_MS / 3).coerceAtLeast(100).toLong())
            if (step.isErrorStep) {
                if (firstErrorIndex == null) {
                    firstErrorIndex = i
                    delay(PLAYBACK_ERROR_HOLD_MS.toLong())
                }
            }
        }
        flashCell = null
        highlightedLine = firstErrorIndex ?: -1
        val finalLeft = results.last().remainingTargetsAfter.size
        if (finalLeft > 0) {
            highlightedLine = results.lastIndex
            goalFailureHighlightIndex = results.lastIndex
        }
        explanationCaption = ""
        val objectiveMet = CommandSequencePlayback.objectiveReachedFromResults(results)
        objectiveMetState = objectiveMet
        summaryPrepared = CommandSequencePlayback.buildPlaybackFinalSummary(
            pendingAnswerMatchedKey = objectiveMet,
            results = results,
            finalRemainingCount = finalLeft
        )
        delay(PLAYBACK_FINISH_MS.toLong())

        val eligibleSuccessBalloons =
            !appState.lessonReviewMode &&
                appState.pendingAnswerCorrect &&
                !appState.commandPlaybackUsesReferenceSolution &&
                CommandSequencePlayback.objectiveReachedFromResults(results) &&
                !appState.commandSequenceSuccessBalloonsShownForAttempt

        if (eligibleSuccessBalloons) {
            suppressPlaybackActionButtons = true
        }
        playbackComplete = true
        if (eligibleSuccessBalloons) {
            showSuccessBalloons = true
            delay(2000)
            showSuccessBalloons = false
            appState.markCommandSequenceSuccessBalloonsShown()
        }
        suppressPlaybackActionButtons = false
    }

    val rr = rowAnim.value.toInt().coerceIn(0, boardCfg.rows - 1)
    val cc = colAnim.value.toInt().coerceIn(0, boardCfg.cols - 1)
    val robotHighlightCell = GridPosition(rr, cc)
    val emphasizedStep = playbackResults.getOrNull(highlightedLine)
    val boardWarnTile = when {
        emphasizedStep?.isErrorStep == true -> emphasizedStep.beforePosition
        goalFailureHighlightIndex != null -> robotHighlightCell
        else -> null
    }
    /** Keep error tint on robot/tile while highlighting the failing line (during run and after early stop). */
    val errorStepEmphasis = emphasizedStep?.isErrorStep == true
    val goalFailureShowing = goalFailureHighlightIndex != null
    val explainFormatError =
        explanationCaption.startsWith("Step ") && explanationCaption.contains("incorrect", ignoreCase = true)

    val revealSpacing = if (compactMode) 8.dp else 10.dp
    val attemptWasCorrect = appState.pendingAnswerCorrect
    val viewingReference = appState.commandPlaybackUsesReferenceSolution

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(revealSpacing)
        ) {
        Text(
            text = if (viewingReference) "Auto playback (correct solution)" else "Auto playback",
            color = ActiveCyan,
            fontSize = if (compactMode) 12.sp else 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp
        )
        CommandPuzzleBoard(
            boardCfg = boardCfg,
            robotRow = rowAnim.value,
            robotCol = colAnim.value,
            rotationDegrees = rotAnim.value,
            remainingTargets = displayedTargets,
            topStatusText = "red target",
            trailingHint = if (playbackComplete) "Ready" else "Running…",
            flashTargetAt = flashCell,
            highlightCell = robotHighlightCell,
            warnCell = boardWarnTile,
            emphasizeRobotIssue = errorStepEmphasis,
            modifier = Modifier.fillMaxWidth(),
            visualType = activity.visualType
        )
        PlaybackCommandSlots(
            commands = cmds,
            highlightedIndex = highlightedLine,
            playbackComplete = playbackComplete,
            results = playbackResults,
            maxReplayedInclusive = maxReplayedStepInclusive,
            objectiveMet = objectiveMetState,
            goalFailureIndex = goalFailureHighlightIndex,
            compactMode = compactMode
        )
        PlaybackExplanationCard(
            textBody = explanationCaption,
            isError = (errorStepEmphasis || goalFailureShowing) ||
                (explainFormatError && explanationCaption.isNotBlank())
        )
        if (playbackComplete && summaryPrepared != null && !suppressPlaybackActionButtons) {
            val actionSpacing = if (compactMode) 6.dp else 8.dp
            Column(verticalArrangement = Arrangement.spacedBy(actionSpacing)) {
                NeonOutlineRowButton(
                    text = "↻  Replay playback",
                    onClick = { appState.requestCommandPlaybackReplay() },
                    accent = ActiveCyan,
                    compactMode = compactMode
                )
                if (appState.lessonReviewMode) {
                    GradientButton(text = "Next question") {
                        appState.continueFromCompletedReviewToNextOrExit()
                    }
                    NeonOutlineRowButton(
                        text = "Back to lesson path",
                        onClick = { appState.backFromLessonActivity() },
                        accent = TextMuted,
                        compactMode = compactMode
                    )
                } else {
                    if (!attemptWasCorrect && !viewingReference) {
                        NeonOutlineRowButton(
                            text = "Show correct playback",
                            onClick = { appState.requestCorrectCommandPlayback() },
                            accent = ActiveCyan,
                            compactMode = compactMode
                        )
                    }
                    if (attemptWasCorrect) {
                        val lastActivity =
                            appState.currentActivityIndex >= appState.getActivitiesForCurrentLesson().lastIndex
                        GradientButton(text = if (lastActivity) "Finish lesson" else "Next question") {
                            appState.proceedAfterFinalResult()
                        }
                        NeonOutlineRowButton(
                            text = "Continue lesson",
                            onClick = { appState.backFromLessonActivity() },
                            accent = TextMuted,
                            compactMode = compactMode
                        )
                    } else {
                        GradientButton(text = "Retry activity") {
                            appState.retryCurrentActivity()
                        }
                        NeonOutlineRowButton(
                            text = "Back to lesson",
                            onClick = { appState.backFromLessonActivity() },
                            accent = TextMuted,
                            compactMode = compactMode
                        )
                    }
                }
            }
        }
        }
        CommandSequenceSuccessOverlay(
            visible = showSuccessBalloons,
            modifier = Modifier.matchParentSize()
        )
    }
}

@Composable
private fun CommandSlotPanel(
    slots: List<String?>,
    onSlotClick: (Int) -> Unit,
    compactMode: Boolean = false,
    readOnly: Boolean = false
) {
    val panelPadding = if (compactMode) 12.dp else 16.dp
    val rowSpacing = if (compactMode) 8.dp else 10.dp
    val rowVerticalPadding = if (compactMode) 10.dp else 12.dp
    val nextFillIndex = slots.indexOfFirst { it == null }.takeIf { it >= 0 }
    val slotCorner = 12.dp
    val slotCornerPx = LocalDensity.current.run { slotCorner.toPx() }
    GlassCard(
        borderBrush = Brush.linearGradient(
            listOf(
                ActiveCyan.copy(alpha = 0.38f),
                Color.White.copy(alpha = 0.12f),
                PrimaryPurple.copy(alpha = 0.3f),
                CardBorder
            )
        ),
        cornerRadius = 20.dp
    ) {
        Column(
            modifier = Modifier.padding(panelPadding),
            verticalArrangement = Arrangement.spacedBy(rowSpacing)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "</>",
                    color = ActiveCyan,
                    fontSize = if (compactMode) 13.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Your program",
                    color = TextPrimary,
                    fontSize = if (compactMode) 14.sp else 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                )
            }
            slots.forEachIndexed { index, cmd ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height(28.dp)
                            .clip(StepHexShape)
                            .background(CardSurface.copy(alpha = 0.88f))
                            .border(1.dp, ActiveCyan.copy(alpha = 0.55f), StepHexShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = ActiveCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    val slotBorder: Modifier = when {
                        cmd != null ->
                            Modifier.border(1.dp, ActiveCyan.copy(alpha = 0.5f), RoundedCornerShape(slotCorner))
                        index == nextFillIndex ->
                            Modifier.border(2.dp, ActiveCyan.copy(alpha = 0.88f), RoundedCornerShape(slotCorner))
                        else ->
                            Modifier.slotDashedRing(TextMuted.copy(alpha = 0.42f), slotCornerPx)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(slotCorner))
                            .background(
                                if (cmd == null) {
                                    Color(0xFF0A142A).copy(alpha = 0.72f)
                                } else {
                                    Color(0xFF0E1E3D).copy(alpha = 0.88f)
                                }
                            )
                            .then(slotBorder)
                            .clickable(enabled = !readOnly) { onSlotClick(index) }
                            .padding(horizontal = 12.dp, vertical = rowVerticalPadding)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = cmd
                                    ?: if (readOnly) "—"
                                    else "tap a command below to fill",
                                color = if (cmd == null) {
                                    TextMuted.copy(alpha = 0.68f)
                                } else {
                                    TextPrimary
                                },
                                fontSize = if (compactMode) 13.sp else 14.sp,
                                fontWeight = if (cmd == null) FontWeight.Normal else FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                repeat(2) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        repeat(3) {
                                            Box(
                                                modifier = Modifier
                                                    .size(3.dp)
                                                    .clip(CircleShape)
                                                    .background(TextMuted.copy(alpha = 0.28f))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandChipRow(
    commands: List<String>,
    onCommandClick: (String) -> Unit
) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll)
            .padding(top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        commands.forEach { cmd ->
            val glyph = when (cmd.lowercase()) {
                "move forward" -> "↑"
                "turn right" -> "↻"
                "select red" -> "⌖"
                "turn left" -> "↺"
                else -> "•"
            }
            Row(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = Color.Black.copy(alpha = 0.28f),
                        spotColor = Color.Black.copy(alpha = 0.28f)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.98f))
                    .border(1.dp, Color(0xFFE2E8F5), RoundedCornerShape(14.dp))
                    .clickable { onCommandClick(cmd) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = glyph,
                    color = Color(0xFF223356),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = cmd,
                    color = Color(0xFF18224A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmptyLessonActivityLayout(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CodeQuestBackButton(onClick = onBack)
        Text(
            text = "No activity here yet.",
            color = TextMuted,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Composable
private fun LeaveLessonDialog(onDismiss: () -> Unit, onLeave: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Leave lesson?") },
        text = {
            Text("Your place in this run will reset when you leave. Progress you already saved stays.")
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onLeave()
            }) { Text("Leave") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Stay") }
        }
    )
}
