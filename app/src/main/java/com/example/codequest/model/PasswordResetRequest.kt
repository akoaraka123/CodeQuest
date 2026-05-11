package com.example.codequest.model

data class PasswordResetRequest(
    val id: String,
    val fullName: String,
    val username: String? = null,
    val requestedAt: String,
    val status: ResetRequestStatus = ResetRequestStatus.PENDING,
    val isReadByAdmin: Boolean = false
)

enum class ResetRequestStatus {
    PENDING,
    REVIEWED
}
