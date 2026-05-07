package com.example.codequest.model

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val icon: String,
    val lessons: List<Lesson>
)
