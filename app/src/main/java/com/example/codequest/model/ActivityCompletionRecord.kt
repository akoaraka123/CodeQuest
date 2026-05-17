package com.example.codequest.model

/**
 * Snapshot of a successfully completed activity for session resume and review-only replay.
 * EXP is tracked via [com.example.codequest.state.CodeQuestAppState]; this holds display state.
 */
data class ActivityCompletionRecord(
    val activityId: String,
    val correct: Boolean,
    val xpGranted: Int,
    val selectedMcIndex: Int? = null,
    val fillInAnswer: String? = null,
    /** Normalized command tokens for command-sequence activities. */
    val commandTokens: List<String>? = null,
    val playbackSummary: String? = null
)
