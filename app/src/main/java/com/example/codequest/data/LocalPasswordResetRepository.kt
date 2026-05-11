package com.example.codequest.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.codequest.model.PasswordResetRequest
import com.example.codequest.model.ResetRequestStatus
import java.util.UUID

object LocalPasswordResetRepository {
    val requests: SnapshotStateList<PasswordResetRequest> = mutableStateListOf()

    fun addRequest(fullName: String, username: String?) {
        requests.add(
            0,
            PasswordResetRequest(
                id = "reset-${UUID.randomUUID()}",
                fullName = fullName,
                username = username?.trim()?.ifBlank { null },
                requestedAt = "Just now"
            )
        )
    }

    fun hasUnreadRequests(): Boolean = requests.any { !it.isReadByAdmin }

    fun markAsReviewed(id: String) {
        val index = requests.indexOfFirst { it.id == id }
        if (index >= 0) {
            requests[index] = requests[index].copy(
                status = ResetRequestStatus.REVIEWED,
                isReadByAdmin = true
            )
        }
    }
}
