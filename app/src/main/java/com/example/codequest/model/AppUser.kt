package com.example.codequest.model

data class AppUser(
    val id: String,
    val username: String,
    val password: String,
    val fullName: String,
    val role: UserRole,
    val status: UserStatus,
    val totalXP: Int = 0,
    val completedLessons: Int = 0,
    val completedCourses: Int = 0,
    val badgesEarned: Int = 0,
    /** Demo-only labels for the details screen */
    val recentLessonTitles: List<String> = emptyList()
)
