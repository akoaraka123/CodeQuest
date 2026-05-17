package com.example.codequest.data

object LocalLoginAttemptRepository {
    private const val MaxFailedAttempts = 5
    private val failedAttemptsByUsername = mutableMapOf<String, Int>()

    fun isBlocked(username: String): Boolean =
        failedAttemptsByUsername[username.normalizedUsername()] ?: 0 >= MaxFailedAttempts

    fun recordFailure(username: String): Int {
        val normalized = username.normalizedUsername()
        val failures = ((failedAttemptsByUsername[normalized] ?: 0) + 1).coerceAtMost(MaxFailedAttempts)
        failedAttemptsByUsername[normalized] = failures
        return (MaxFailedAttempts - failures).coerceAtLeast(0)
    }

    fun reset(username: String) {
        
        failedAttemptsByUsername.remove(username.normalizedUsername())
    }

    private fun String.normalizedUsername(): String = trim().lowercase()
}
