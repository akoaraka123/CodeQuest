package com.example.codequest.model

data class AppRating(
    val id: String,
    val studentId: String,
    val studentName: String,
    val rating: Int,
    val comment: String,
    val submittedAt: String
)
