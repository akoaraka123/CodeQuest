package com.example.codequest.model

data class Lesson(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String,
    val content: String,
    val order: Int,
    val example: String? = null,
    val activities: List<ActivityItem> = emptyList()
)
