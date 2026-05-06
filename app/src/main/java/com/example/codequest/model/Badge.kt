package com.example.codequest.model

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val target: Int = 1
)
