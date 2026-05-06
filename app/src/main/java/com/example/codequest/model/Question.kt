package com.example.codequest.model

data class Question(
    val id: String,
    val questId: String,
    val type: QuestionType,
    val questionText: String,
    val codeBlock: String? = null,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val hint: String? = null,
    val xpReward: Int = 20
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    FILL_IN_THE_BLANK,
    PREDICT_OUTPUT,
    DEBUG_CODE
}
