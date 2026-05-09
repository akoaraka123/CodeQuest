package com.example.codequest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.model.ActivityItem
import com.example.codequest.model.ActivityType
import com.example.codequest.model.effectiveProcessSteps
import com.example.codequest.model.isTicLesson1MultipleChoice
import com.example.codequest.model.CommandSequencePlayback
import com.example.codequest.model.Direction
import com.example.codequest.model.GridPosition
import com.example.codequest.model.PlaybackBoardConfig
import com.example.codequest.model.PlaybackStepResult
import com.example.codequest.model.normalizeCommandToken
import com.example.codequest.model.playbackBoardConfig
import com.example.codequest.model.rotationZDegrees
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.state.LessonInteractionState
import com.example.codequest.ui.components.CodeBlockCard
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.FeedbackCard
import com.example.codequest.ui.components.FinalResultCard
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.components.GuidedProcessRevealCard
import com.example.codequest.ui.components.QuestionCardHeader
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CardBorder
import com.example.codequest.ui.theme.CompletedGreen
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
                xp = appState.totalXP
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
            LessonInteractionState.ACTIVITY -> {
                item {
                    TaskHeaderSection(activity = activity)
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
                        GradientButton(
                            text = "Check",
                            enabled = appState.activityReadyForCheck()
                        ) { appState.submitActivityCheck() }
                    }
                } else {
                    activity.codeSnippet?.let { snippet ->
                        item { GlassCard { CodeBlockCard(code = snippet) } }
                    }
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
                        val border = if (selected) ActiveCyan else CardBorder
                        GlassCard(
                            borderBrush = Brush.linearGradient(listOf(border, CardBorder)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { appState.selectMcOption(index) }
                        ) {
                            Text(
                                text = activity.options[index],
                                color = TextPrimary,
                                fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    item {
                        GradientButton(
                            text = "Check",
                            enabled = appState.activityReadyForCheck()
                        ) { appState.submitActivityCheck() }
                    }
                }
            }

            LessonInteractionState.FEEDBACK -> {
                item {
                    TaskHeaderSection(activity = activity)
                }
                item {
                    val l1mc = activity.isTicLesson1MultipleChoice()
                    val attempts = appState.lessonOneWrongAttempts
                    val depleted = l1mc && !appState.pendingAnswerCorrect && attempts >= 3
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
                if (activity.isTicLesson1MultipleChoice()) {
                    val attempts = appState.lessonOneWrongAttempts
                    if (!appState.pendingAnswerCorrect && attempts < 3) {
                        item {
                            val left = (3 - attempts).coerceAtLeast(0)
                            Text(
                                text = "Attempts left: $left",
                                color = ActiveCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    item {
                        when {
                            appState.pendingAnswerCorrect -> {
                                val lastQ = appState.currentActivityIndex >= activities.lastIndex
                                GradientButton(text = if (lastQ) "Finish lesson" else "Next Question") {
                                    appState.lesson1ProceedAfterCorrectFeedback()
                                }
                            }
                            attempts >= 3 -> {
                                GradientButton(text = "View Correct Answer") {
                                    appState.lesson1OpenCorrectAnswerReveal()
                                }
                            }
                            else -> {
                                GradientButton(text = "Try Again") {
                                    appState.lesson1TryAgainAfterWrong()
                                }
                            }
                        }
                    }
                } else {
                    item {
                        if (appState.pendingAnswerCorrect) {
                            val walkthroughSteps =
                                activity.effectiveProcessSteps(answerCorrect = true)
                            val continueToWalkthrough =
                                activity.requiresProcessRevealBeforeFinal && walkthroughSteps.isNotEmpty()
                            GradientButton(text = "Continue") {
                                if (continueToWalkthrough) {
                                    appState.showProcessRevealFromFeedback()
                                } else {
                                    appState.showFinalResultState()
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
                if (!activity.isTicLesson1MultipleChoice()) {
                    item {
                        Text(
                            "Continue the lesson from the previous step.",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                } else {
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
                    val lastActivity = appState.currentActivityIndex >= activities.lastIndex
                    val nextLabel =
                        if (lastActivity) "Finish lesson" else "Next question"
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        GradientButton(text = nextLabel) { appState.proceedAfterFinalResult() }
                        GradientButton(text = "Retry") { appState.retryCurrentActivity() }
                        GradientButton(text = if (lastActivity) "Learning path" else "Continue lesson") {
                            appState.backFromLessonActivity()
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
    val screenH = LocalConfiguration.current.screenHeightDp.dp
    val rootPad = if (compactScreen) 10.dp else 12.dp
    val sectionGap = if (compactScreen) 6.dp else 8.dp
    val boardWeight = if (compactScreen) 1.02f else 1.12f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(horizontal = rootPad, vertical = rootPad),
        verticalArrangement = Arrangement.spacedBy(sectionGap)
    ) {
        LessonActivityTopBar(
            onBack = onBack,
            activityIndex = appState.currentActivityIndex,
            totalActivities = activities.size,
            xp = appState.totalXP
        )
        QuestionCardHeader(
            questionIndex = appState.currentActivityIndex,
            total = activities.size,
            typeLabel = activity.type.name
        )
        TaskHeaderSection(activity = activity)

        Box(modifier = Modifier.weight(boardWeight, fill = true)) {
            CommandPuzzleBoard(
                boardCfg = boardCfg,
                robotRow = boardCfg.robotStart.row.toFloat(),
                robotCol = boardCfg.robotStart.col.toFloat(),
                rotationDegrees = boardCfg.facing.rotationZDegrees(),
                remainingTargets = boardCfg.initialTargets,
                topStatusText = "red target",
                trailingHint = null,
                flashTargetAt = null,
                modifier = Modifier.fillMaxSize(),
                highlightCell = null,
                warnCell = null,
                emphasizeRobotIssue = false,
                visualType = activity.visualType,
                boardAspectRatio = if (compactScreen) 1.18f else null
            )
        }

        CommandSlotPanel(
            slots = appState.activityCommandSlots,
            onSlotClick = { i -> appState.clearCommandSlot(i) },
            compactMode = true
        )
        CommandChipRow(
            commands = activity.availableCommands,
            onCommandClick = { appState.fillNextCommandSlot(it) }
        )
        Box(modifier = Modifier.height((screenH * 0.075f).coerceIn(46.dp, 58.dp))) {
            GradientButton(
                text = "Check",
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
                Text(text = xp.toString(), color = CompletedGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
private fun TaskHeaderSection(activity: ActivityItem, muted: Boolean = false) {
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
    boardAlpha: Float = 1f,
    boardAspectRatio: Float? = null
) {
    val rows = boardCfg.rows
    val cols = boardCfg.cols
    val renderedAspectRatio = boardAspectRatio ?: cols.toFloat() / rows.toFloat()
    GlassCard(
        modifier = modifier,
        borderBrush = Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.35f),
                ActiveCyan.copy(alpha = boardAlpha.coerceIn(0.35f, 1f)),
                PrimaryPurple.copy(alpha = 0.4f),
                CardBorder
            )
        ),
        cornerRadius = 22.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                    .aspectRatio(renderedAspectRatio)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0E1530))
                    .border(1.dp, ActiveCyan.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            ) {
                val density = LocalDensity.current
                val isColorTargetBoard = visualType == "GRID_COLOR_TARGET"
                val gridInset = if (isColorTargetBoard) 2.dp else 8.dp
                val gap = if (isColorTargetBoard) 2.dp else 3.dp
                val gapPx = density.run { gap.toPx() }
                val usableW = density.run { maxWidth.toPx() } - gapPx * (cols + 1)
                val usableH = density.run { maxHeight.toPx() } - gapPx * (rows + 1)
                val innerCell = minOf(usableW / cols, usableH / rows)
                Column(
                    modifier = Modifier.padding(gridInset),
                    verticalArrangement = Arrangement.spacedBy(gap)
                ) {
                    for (r in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(gap)
                        ) {
                            for (c in 0 until cols) {
                                val pos = GridPosition(r, c)
                                val hasTarget = remainingTargets.contains(pos)
                                val flashing = flashTargetAt == pos
                                val highlight = highlightCell == pos
                                val warned = warnCell == pos
                                val isEmptyCell = isColorTargetBoard && pos == GridPosition(1, 1)
                                val isRedTargetCell = isColorTargetBoard && hasTarget
                                val pulse by animateFloatAsState(
                                    targetValue = if (flashing) 1f else 0.55f,
                                    animationSpec = tween(180, easing = FastOutSlowInEasing),
                                    label = "targetPulse"
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .graphicsLayer { alpha = boardAlpha.coerceIn(0.4f, 1f) }
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when {
                                                warned -> Color(0xFF291C18)
                                                isRedTargetCell -> Color(0xFF5A1F2A)
                                                isColorTargetBoard && isEmptyCell -> Color(0xFF152046)
                                                isColorTargetBoard -> Color(0xFF164A8C)
                                                else -> Color(0xFF152046)
                                            }
                                        )
                                        .border(
                                            if (warned) 2.dp else 1.dp,
                                            when {
                                                warned -> PlaybackErrorOrange.copy(alpha = if (highlight) 0.98f else 0.82f)
                                                highlight -> CompletedGreen.copy(alpha = 0.75f)
                                                isRedTargetCell -> Color(0xFFFF6B6B).copy(alpha = 0.78f)
                                                flashing -> PrimaryPurple.copy(alpha = 0.95f)
                                                else -> ActiveCyan.copy(alpha = 0.12f)
                                            },
                                            RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasTarget && !isColorTargetBoard) {
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

                val cellPx = innerCell.coerceAtLeast(density.run { 8.dp.toPx() })
                val iconPx = density.run { 22.dp.toPx() }
                val padPx = density.run { gridInset.toPx() }
                val offX =
                    padPx + gapPx + robotCol * cellPx + (cellPx - iconPx) / 2
                val offY =
                    padPx + gapPx + robotRow * cellPx + (cellPx - iconPx) / 2

                Box(
                    modifier = Modifier.offset {
                        IntOffset(offX.roundToInt(), offY.roundToInt())
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "◉",
                        color = if (emphasizeRobotIssue) {
                            PlaybackErrorOrange.copy(alpha = 0.95f)
                        } else {
                            CompletedGreen.copy(alpha = 0.92f)
                        },
                        fontSize = 22.sp,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = rotationDegrees
                        }
                    )
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
    sequenceMismatchStartIndex: Int?,
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
                val firstWrongIndex =
                    listOfNotNull(firstErrorIndex, goalFailureIndex, sequenceMismatchStartIndex).minOrNull()
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
    var sequenceMismatchStartIndex by remember(playbackGen) { mutableStateOf<Int?>(null) }

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
        sequenceMismatchStartIndex = null
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
                    delay(PLAYBACK_STEP_PAUSE_MS.toLong())
                }
                "turn right", "turn left" -> {
                    rotAnim.animateTo(step.afterDirection.rotationZDegrees(), turnSpec)
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
        if (!objectiveMet && !appState.commandPlaybackUsesReferenceSolution) {
            val referenceResults = CommandSequencePlayback.simulate(boardCfg, activity.correctSequence)
            val sharedCount = minOf(results.size, referenceResults.size)
            val mismatch = (0 until sharedCount).firstOrNull { idx ->
                val userStep = results[idx]
                val refStep = referenceResults[idx]
                normalizeCommandToken(userStep.command) != normalizeCommandToken(refStep.command) ||
                    userStep.afterPosition != refStep.afterPosition ||
                    userStep.afterDirection != refStep.afterDirection ||
                    userStep.remainingTargetsAfter != refStep.remainingTargetsAfter ||
                    userStep.isErrorStep
            } ?: run {
                if (results.size != referenceResults.size) sharedCount else null
            }
            if (mismatch != null) {
                sequenceMismatchStartIndex = mismatch
            }
        }
        summaryPrepared = CommandSequencePlayback.buildPlaybackFinalSummary(
            pendingAnswerMatchedKey = objectiveMet,
            results = results,
            finalRemainingCount = finalLeft
        )
        delay(PLAYBACK_FINISH_MS.toLong())
        playbackComplete = true
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

    val boardAspectRatioBoost = if (compactMode) 1.28f else 1f
    val boardAspect = (boardCfg.cols.toFloat() / boardCfg.rows.toFloat()) * boardAspectRatioBoost
    val revealSpacing = if (compactMode) 8.dp else 10.dp
    val attemptWasCorrect = appState.pendingAnswerCorrect
    val viewingReference = appState.commandPlaybackUsesReferenceSolution

    Column(verticalArrangement = Arrangement.spacedBy(revealSpacing)) {
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
            boardAspectRatio = boardAspect,
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
            sequenceMismatchStartIndex = sequenceMismatchStartIndex,
            compactMode = compactMode
        )
        PlaybackExplanationCard(
            textBody = explanationCaption,
            isError = (errorStepEmphasis || goalFailureShowing) ||
                (explainFormatError && explanationCaption.isNotBlank())
        )
        if (playbackComplete && summaryPrepared != null) {
            val actionSpacing = if (compactMode) 6.dp else 8.dp
            Column(verticalArrangement = Arrangement.spacedBy(actionSpacing)) {
                NeonOutlineRowButton(
                    text = "↻  Replay playback",
                    onClick = { appState.requestCommandPlaybackReplay() },
                    accent = ActiveCyan,
                    compactMode = compactMode
                )
                if (!attemptWasCorrect && !viewingReference) {
                    NeonOutlineRowButton(
                        text = "Show correct playback",
                        onClick = { appState.requestCorrectCommandPlayback() },
                        accent = ActiveCyan,
                        compactMode = compactMode
                    )
                }
                if (attemptWasCorrect) {
                    GradientButton(text = "Continue to result") {
                        summaryPrepared?.let { appState.showFinalResultState(it) }
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

@Composable
private fun CommandSlotPanel(
    slots: List<String?>,
    onSlotClick: (Int) -> Unit,
    compactMode: Boolean = false
) {
    val panelPadding = if (compactMode) 10.dp else 14.dp
    val rowSpacing = if (compactMode) 6.dp else 8.dp
    val rowVerticalPadding = if (compactMode) 9.dp else 12.dp
    GlassCard(
        borderBrush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), CardBorder)),
        cornerRadius = 18.dp
    ) {
        Column(
            modifier = Modifier.padding(panelPadding),
            verticalArrangement = Arrangement.spacedBy(rowSpacing)
        ) {
            Text(
                "Your program",
                color = TextMuted,
                fontSize = if (compactMode) 11.sp else 12.sp,
                fontWeight = FontWeight.Medium
            )
            slots.forEachIndexed { index, cmd ->
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
                            .background(Color.Black.copy(alpha = 0.28f))
                            .border(
                                1.dp,
                                if (cmd == null) CardBorder.copy(alpha = 0.45f) else ActiveCyan.copy(alpha = 0.45f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSlotClick(index) }
                            .padding(horizontal = 12.dp, vertical = rowVerticalPadding)
                    ) {
                        Text(
                            text = cmd ?: "tap a command below to fill",
                            color = if (cmd == null) TextMuted.copy(alpha = 0.65f) else TextPrimary,
                            fontSize = if (compactMode) 13.sp else 14.sp
                        )
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
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        commands.forEach { cmd ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.95f))
                    .clickable { onCommandClick(cmd) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
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
