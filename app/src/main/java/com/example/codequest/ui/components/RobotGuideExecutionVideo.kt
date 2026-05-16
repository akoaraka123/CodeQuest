package com.example.codequest.ui.components

import androidx.annotation.RawRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.model.CommandSequencePlayback
import com.example.codequest.model.Direction
import com.example.codequest.model.GridPosition
import com.example.codequest.model.PlaybackBoardConfig
import com.example.codequest.model.PlaybackStepResult
import com.example.codequest.model.normalizeCommandToken
import com.example.codequest.model.rotationZDegrees
import com.example.codequest.ui.screens.CommandPuzzleBoardForGuide
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BadgeGold
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextPrimary
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val GUIDE_PLAYBACK_MOVE_MS = 470
private const val GUIDE_PLAYBACK_TURN_MS = 360
private const val GUIDE_PLAYBACK_STEP_PAUSE_MS = 300
private const val GUIDE_PLAYBACK_COLLECT_PRE_MS = 240
private const val GUIDE_PLAYBACK_COLLECT_POST_MS = 280
private const val GUIDE_PLAYBACK_INTRO_MS = 400

private val guideDemoBoard = PlaybackBoardConfig(
    rows = 3,
    cols = 3,
    robotStart = GridPosition(0, 0),
    facing = Direction.UP,
    initialTargets = setOf(GridPosition(1, 1))
)

private val guideDemoCommands = listOf(
    "turn right",
    "move forward",
    "turn right",
    "move forward",
    "select red"
)

private const val BUNDLED_EXECUTE_VIDEO_RAW_NAME = "tic_l2_robot_execute_demo"

/**
 * Step 5 of the Lesson 2 robot guide: shows how to execute (fill slots → Check → playback).
 * If `res/raw/tic_l2_robot_execute_demo.mp4` exists it plays in the video player; otherwise an
 * auto-playing animated demo runs inside the video frame.
 */
@Composable
fun RobotGuideExecutionVideoStep(
    modifier: Modifier = Modifier,
    @RawRes bundledVideoResId: Int? = null
) {
    val context = LocalContext.current
    val resolvedVideoResId = remember(bundledVideoResId) {
        val explicit = bundledVideoResId?.takeIf { it != 0 }
        val fromName = context.resources.getIdentifier(
            BUNDLED_EXECUTE_VIDEO_RAW_NAME,
            "raw",
            context.packageName
        ).takeIf { it != 0 }
        (explicit ?: fromName)?.takeIf { resId ->
            runCatching {
                context.resources.openRawResourceFd(resId).use { }
                true
            }.getOrDefault(false)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Step 5 — Watch How to Execute",
            color = BadgeGold,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Fill all slots, tap Check, then watch the robot run.",
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        VideoFrame(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (resolvedVideoResId != null) {
                RobotGuideBundledVideoPlayer(videoResId = resolvedVideoResId)
            } else {
                RobotGuideAnimatedExecutionDemo()
            }
        }

        Text(
            text = "Tip: Tap Check after every slot is filled.",
            color = CompletedGreen,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun VideoFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(listOf(PrimaryPurple, ActiveCyan, BadgeGold)),
                shape = RoundedCornerShape(18.dp)
            )
            .background(Color(0xFF080E1E))
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0C1428))
        ) {
            content()
        }
        Text(
            text = "VIDEO",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(PrimaryPurple.copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun RobotGuideAnimatedExecutionDemo() {
    val boardCfg = guideDemoBoard
    val results = remember {
        CommandSequencePlayback.simulate(boardCfg, guideDemoCommands)
    }
    var playbackKey by remember { mutableIntStateOf(0) }

    val rowAnim = remember(playbackKey) { Animatable(boardCfg.robotStart.row.toFloat()) }
    val colAnim = remember(playbackKey) { Animatable(boardCfg.robotStart.col.toFloat()) }
    val rotAnim = remember(playbackKey) { Animatable(boardCfg.facing.rotationZDegrees()) }

    var displayedTargets by remember(playbackKey) { mutableStateOf(boardCfg.initialTargets.toSet()) }
    var flashCell by remember(playbackKey) { mutableStateOf<GridPosition?>(null) }
    var statusText by remember(playbackKey) { mutableStateOf("Running demo…") }
    var caption by remember(playbackKey) { mutableStateOf("Fill slots → tap Check") }

    LaunchedEffect(playbackKey) {
        rowAnim.snapTo(boardCfg.robotStart.row.toFloat())
        colAnim.snapTo(boardCfg.robotStart.col.toFloat())
        rotAnim.snapTo(boardCfg.facing.rotationZDegrees())
        displayedTargets = boardCfg.initialTargets.toSet()
        flashCell = null
        delay(GUIDE_PLAYBACK_INTRO_MS.toLong())
        statusText = "Running…"
        caption = "Robot runs each command"
        delay(600)
        runGuidePlayback(
            results = results,
            rowAnim = rowAnim,
            colAnim = colAnim,
            rotAnim = rotAnim,
            onTargetsUpdate = { displayedTargets = it },
            onFlash = { flashCell = it },
            onCaption = { caption = it }
        )
        flashCell = null
        statusText = "Done!"
        caption = "Reach red, then Select red"
        delay(1400)
        playbackKey++
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CommandPuzzleBoardForGuide(
            boardCfg = boardCfg,
            robotRow = rowAnim.value,
            robotCol = colAnim.value,
            rotationDegrees = rotAnim.value,
            remainingTargets = displayedTargets,
            topStatusText = "red target",
            trailingHint = statusText,
            flashTargetAt = flashCell,
            highlightCell = GridPosition(rowAnim.value.toInt(), colAnim.value.toInt()),
            warnCell = null,
            emphasizeRobotIssue = false,
            visualType = "GRID_COLOR_TARGET",
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
        Text(
            text = caption,
            color = ActiveCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC0A1020))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private suspend fun runGuidePlayback(
    results: List<PlaybackStepResult>,
    rowAnim: Animatable<Float, *>,
    colAnim: Animatable<Float, *>,
    rotAnim: Animatable<Float, *>,
    onTargetsUpdate: (Set<GridPosition>) -> Unit,
    onFlash: (GridPosition?) -> Unit,
    onCaption: (String) -> Unit
) {
    val moveSpec = tween<Float>(durationMillis = GUIDE_PLAYBACK_MOVE_MS, easing = FastOutSlowInEasing)
    val turnSpec = tween<Float>(durationMillis = GUIDE_PLAYBACK_TURN_MS, easing = FastOutSlowInEasing)

    for (step in results) {
        onCaption("Running: ${step.command}")
        delay((GUIDE_PLAYBACK_STEP_PAUSE_MS / 2).coerceAtLeast(120).toLong())
        when (normalizeCommandToken(step.command)) {
            "move forward" -> {
                coroutineScope {
                    launch { rowAnim.animateTo(step.afterPosition.row.toFloat(), moveSpec) }
                    launch { colAnim.animateTo(step.afterPosition.col.toFloat(), moveSpec) }
                }
                rotAnim.snapTo(step.afterDirection.rotationZDegrees())
                delay(GUIDE_PLAYBACK_STEP_PAUSE_MS.toLong())
            }
            "turn right" -> {
                rotAnim.animateTo(rotAnim.value + 90f, turnSpec)
                delay(GUIDE_PLAYBACK_STEP_PAUSE_MS.toLong())
            }
            "turn left" -> {
                rotAnim.animateTo(rotAnim.value - 90f, turnSpec)
                delay(GUIDE_PLAYBACK_STEP_PAUSE_MS.toLong())
            }
            "select red" -> {
                onFlash(step.beforePosition)
                delay(GUIDE_PLAYBACK_COLLECT_PRE_MS.toLong())
                onFlash(null)
                delay(GUIDE_PLAYBACK_COLLECT_POST_MS.toLong())
            }
            else -> delay(GUIDE_PLAYBACK_STEP_PAUSE_MS.toLong())
        }
        onTargetsUpdate(step.remainingTargetsAfter)
    }
}
