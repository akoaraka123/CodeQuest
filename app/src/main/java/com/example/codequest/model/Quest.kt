package com.example.codequest.model

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val order: Int,
    val lessons: List<Lesson>
)
