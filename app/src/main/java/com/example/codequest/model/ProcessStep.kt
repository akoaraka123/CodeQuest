package com.example.codequest.model

data class ProcessStep(
    val stepNumber: Int,
    val title: String,
    val explanation: String,
    val codeBlock: String? = null,
    val highlightedCommand: String? = null,
    val miniVisualHint: String? = null
)
