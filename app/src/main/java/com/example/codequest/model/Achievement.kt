package com.example.codequest.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String
)

fun ActivityItem.isRedTargetCommandSequence(): Boolean =
    type == ActivityType.COMMAND_SEQUENCE &&
        (
            visualType == "GRID_COLOR_TARGET" ||
                correctSequence.any { it.equals("select red", ignoreCase = true) }
            )
