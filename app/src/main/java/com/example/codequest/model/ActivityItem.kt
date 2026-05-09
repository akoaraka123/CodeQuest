package com.example.codequest.model

data class ActivityItem(
    val id: String,
    val lessonId: String,
    val type: ActivityType,
    val prompt: String,
    val difficultyLabel: String? = null,
    val visualType: String? = null,
    val availableCommands: List<String> = emptyList(),
    val correctSequence: List<String> = emptyList(),
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1,
    val codeSnippet: String? = null,
    val gridRows: Int = 4,
    val gridCols: Int = 4,
    /** How many gold tiles appear on the board when no [commandBoardSetup] is set. */
    val goldTileCount: Int = 0,
    val commandBoardSetup: CommandBoardSetup? = null,
    val correctFeedback: String,
    val incorrectFeedback: String,
    val processSteps: List<ProcessStep>,
    val processStepsWhenIncorrect: List<ProcessStep>? = null,
    val finalResult: String,
    val finalOutput: String? = null,
    val xpReward: Int = 20,
    /** When true, correct answers still enter guided process steps before the final-result screen. */
    val requiresProcessRevealBeforeFinal: Boolean = false
)

fun ActivityItem.effectiveProcessSteps(answerCorrect: Boolean): List<ProcessStep> =
    if (answerCorrect) processSteps
    else processStepsWhenIncorrect ?: processSteps

fun ActivityItem.slotCount(): Int =
    if (type == ActivityType.COMMAND_SEQUENCE) correctSequence.size else 0

fun ActivityItem.requiresMultipleChoice(): Boolean =
    type == ActivityType.MULTIPLE_CHOICE ||
        type == ActivityType.OUTPUT_TRACING ||
        type == ActivityType.DEBUG_CODE

/** Thinking in Code — Lesson 1 "What is a Program?" (MC-only custom flow). */
fun ActivityItem.isTicLesson1MultipleChoice(): Boolean =
    lessonId == "tic-l1" && requiresMultipleChoice()
