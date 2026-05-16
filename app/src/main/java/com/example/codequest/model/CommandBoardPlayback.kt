package com.example.codequest.model

import kotlin.math.abs

/** Row / column index on the puzzle grid. */
data class GridPosition(val row: Int, val col: Int)

/** Screen-space grid: row 0 is the **top** row; moving [UP] decreases the row index. */
enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT
}

/** Degrees for [androidx.compose.ui.graphics.graphicsLayer]; neutral pose (0°) faces [Direction.UP] (antenna at top). */
fun Direction.rotationZDegrees(): Float = when (this) {
    Direction.UP -> 0f
    Direction.RIGHT -> 90f
    Direction.DOWN -> 180f
    Direction.LEFT -> 270f
}

fun Direction.turnRight(): Direction = when (this) {
    Direction.UP -> Direction.RIGHT
    Direction.RIGHT -> Direction.DOWN
    Direction.DOWN -> Direction.LEFT
    Direction.LEFT -> Direction.UP
}

fun Direction.turnLeft(): Direction = when (this) {
    Direction.UP -> Direction.LEFT
    Direction.LEFT -> Direction.DOWN
    Direction.DOWN -> Direction.RIGHT
    Direction.RIGHT -> Direction.UP
}

fun Direction.stepFrom(origin: GridPosition): GridPosition = when (this) {
    Direction.UP -> GridPosition(origin.row - 1, origin.col)
    Direction.DOWN -> GridPosition(origin.row + 1, origin.col)
    Direction.LEFT -> GridPosition(origin.row, origin.col - 1)
    Direction.RIGHT -> GridPosition(origin.row, origin.col + 1)
}

data class PlaybackStepResult(
    val commandIndex: Int,
    val command: String,
    val beforePosition: GridPosition,
    val afterPosition: GridPosition,
    val beforeDirection: Direction,
    val afterDirection: Direction,
    val selectedTargetThisStep: Boolean,
    val explanation: String,
    val isErrorStep: Boolean,
    val errorMessage: String? = null,
    val remainingTargetsAfter: Set<GridPosition>
)

/**
 * Initial pose and gold tiles for running a command-sequence playback.
 */
data class CommandBoardSetup(
    val robotRow: Int,
    val robotCol: Int,
    val facing: Direction,
    val targetTiles: List<GridPosition>
)

private const val MOVE_FORWARD = "move forward"
private const val TURN_RIGHT = "turn right"
private const val TURN_LEFT = "turn left"
private const val SELECT_RED = "select red"
private const val COLLECT_GOLD_LEGACY = "collect gold"
private const val COLLECT_GEM_LEGACY = "collect gem"

fun normalizeCommandToken(raw: String): String {
    return raw.trim().lowercase()
        .replace(COLLECT_GEM_LEGACY, SELECT_RED)
        .replace(COLLECT_GOLD_LEGACY, SELECT_RED)
}

data class PlaybackBoardConfig(
    val rows: Int,
    val cols: Int,
    val robotStart: GridPosition,
    val facing: Direction,
    val initialTargets: Set<GridPosition>
)

fun ActivityItem.playbackBoardConfig(): PlaybackBoardConfig {
    val rows = gridRows.coerceIn(3, 12)
    val cols = gridCols.coerceIn(3, 12)

    commandBoardSetup?.let { setup ->
        return PlaybackBoardConfig(
            rows = rows,
            cols = cols,
            robotStart = GridPosition(setup.robotRow, setup.robotCol),
            facing = setup.facing,
            initialTargets = setup.targetTiles.toSet()
        )
    }

    val gold = mutableSetOf<GridPosition>()
    if (goldTileCount >= 4) {
        gold.add(GridPosition(rows / 2, 0))
        gold.add(GridPosition(0, cols / 2))
        gold.add(GridPosition(rows - 1, cols / 2))
        gold.add(GridPosition(rows / 2, cols - 1))
    } else repeat(goldTileCount.coerceAtLeast(2)) {
        gold.add(GridPosition(it, cols / 2 + it % 2))
    }

    val botRow = rows / 2
    val botCol = cols / 2
    val initialFacing = Direction.RIGHT

    val goldWithoutOverlap = gold.filterNot { it.row == botRow && it.col == botCol }.toSet()
    return PlaybackBoardConfig(
        rows = rows,
        cols = cols,
        robotStart = GridPosition(botRow, botCol),
        facing = initialFacing,
        initialTargets = if (goldWithoutOverlap.isEmpty()) gold else goldWithoutOverlap
    )
}

object CommandSequencePlayback {
    fun simulate(config: PlaybackBoardConfig, commandsRaw: List<String>): List<PlaybackStepResult> {
        val cmds = commandsRaw.map { normalizeCommandToken(it) }
        var robot = config.robotStart
        var facing = config.facing
        val remaining = config.initialTargets.toMutableSet()
        val out = ArrayList<PlaybackStepResult>()

        fun inBounds(g: GridPosition): Boolean =
            g.row in 0 until config.rows && g.col in 0 until config.cols

        cmds.forEachIndexed { index, token ->
            val beforeP = robot
            val beforeD = facing
            var explanation = ""
            var isErr = false
            var errDetail: String? = null
            var didSelect = false

            when (token) {
                MOVE_FORWARD -> {
                    val dest = facing.stepFrom(robot)
                    if (!inBounds(dest)) {
                        isErr = true
                        errDetail = "The robot tries to move outside the board."
                        explanation = errDetail!!
                    } else {
                        robot = dest
                        explanation = "The robot moves one tile forward."
                    }
                }
                TURN_RIGHT -> {
                    facing = facing.turnRight()
                    explanation = "The robot turns right to face the next path."
                }
                TURN_LEFT -> {
                    facing = facing.turnLeft()
                    explanation = "The robot turns left."
                }
                SELECT_RED -> {
                    if (remaining.remove(robot)) {
                        didSelect = true
                        explanation = "The robot selects the red target on this tile."
                    } else {
                        isErr = true
                        errDetail = "The robot selected the wrong tile. select red only works on the red target."
                        explanation = errDetail!!
                    }
                }
                else -> {
                    isErr = true
                    errDetail = "Unrecognized instruction for this simulator."
                    explanation = errDetail!!
                }
            }

            val errLine = if (isErr && errDetail != null) {
                "At Step ${index + 1}: $errDetail"
            } else null

            out.add(
                PlaybackStepResult(
                    commandIndex = index,
                    command = token,
                    beforePosition = beforeP,
                    afterPosition = robot,
                    beforeDirection = beforeD,
                    afterDirection = facing,
                    selectedTargetThisStep = didSelect,
                    explanation = explanation,
                    isErrorStep = isErr,
                    errorMessage = errLine,
                    remainingTargetsAfter = remaining.toSet()
                )
            )
        }
        return out
    }

    fun prettyCommand(cmd: String): String =
        normalizeCommandToken(cmd).let {
            when (it) {
                MOVE_FORWARD -> "move forward"
                TURN_RIGHT -> "turn right"
                TURN_LEFT -> "turn left"
                SELECT_RED -> "select red"
                else -> cmd
            }
        }

    fun buildPlaybackFinalSummary(
        pendingAnswerMatchedKey: Boolean,
        results: List<PlaybackStepResult>,
        finalRemainingCount: Int
    ): String {
        val missed = maxOf(finalRemainingCount, 0)
        val firstWrongSelectAdjacentMessage =
            results.firstOrNull { step ->
                step.isErrorStep &&
                    normalizeCommandToken(step.command) == SELECT_RED &&
                    step.errorMessage?.contains("wrong tile", ignoreCase = true) == true
            }?.let { step ->
                val pos = step.beforePosition
                val stillNeeded = step.remainingTargetsAfter
                val besideRed = stillNeeded.any { t ->
                    abs(t.row - pos.row) + abs(t.col - pos.col) == 1
                }
                if (besideRed) {
                    "Almost there — you're beside the red tile, not on it. Try: move forward ×3 up the " +
                        "right edge, turn left, move forward ×2 onto red, then select red."
                } else {
                    null
                }
            }

        return when {
            pendingAnswerMatchedKey && missed == 0 ->
                "Red target found and selected."

            firstWrongSelectAdjacentMessage != null ->
                firstWrongSelectAdjacentMessage

            results.any {
                it.isErrorStep &&
                    it.errorMessage?.contains("selected the wrong tile", ignoreCase = true) == true
            } ->
                "The robot selected the wrong tile."

            missed > 0 ->
                if (results.any { normalizeCommandToken(it.command) == SELECT_RED }) {
                    "The robot did not reach the red target."
                } else {
                    "The program ended before selecting the red target."
                }

            else ->
                "The robot did not reach the red target."
        }
    }

    /**
     * Correct when the player's filled sequence runs on this board without errors
     * and every red target tile is selected (matches the puzzle prompt objective).
     * String parity with [ActivityItem.correctSequence] alone is not enough—a path that
     * matches the text key can still leave gold if layouts disagree.
     */
    fun sequenceMeetsObjective(config: PlaybackBoardConfig, slots: List<String?>): Boolean {
        if (slots.isEmpty() || slots.any { it == null }) return false
        val cmds = slots.mapNotNull { it }
        val results = simulate(config, cmds)
        return objectiveReachedFromResults(results)
    }

    /** True when the full simulated run has no error steps and no red target remains. */
    fun objectiveReachedFromResults(results: List<PlaybackStepResult>): Boolean {
        if (results.isEmpty()) return false
        if (results.any { it.isErrorStep }) return false
        return results.last().remainingTargetsAfter.isEmpty()
    }
}
