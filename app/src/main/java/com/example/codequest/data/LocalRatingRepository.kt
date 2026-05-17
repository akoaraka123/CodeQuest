package com.example.codequest.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.example.codequest.model.AppRating

object LocalRatingRepository {
    val ratings = mutableStateListOf<AppRating>()
    private var unreadCount by mutableIntStateOf(0)

    fun submit(rating: AppRating) {
        ratings.removeAll { it.studentId == rating.studentId }
        ratings.add(0, rating)
        unreadCount++
    }

    fun hasUnreadRatings(): Boolean = unreadCount > 0

    fun markAllRead() {
        unreadCount = 0
    }

    fun averageRating(): Float =
        if (ratings.isEmpty()) 0f else ratings.sumOf { it.rating }.toFloat() / ratings.size
}
