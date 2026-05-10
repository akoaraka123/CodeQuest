package com.example.codequest.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.codequest.model.AppUser
import com.example.codequest.model.UserRole
import com.example.codequest.model.UserStatus
import java.util.UUID

object LocalUserRepository {

    val users: SnapshotStateList<AppUser> = mutableStateListOf()

    init {
        users.addAll(seedUsers())
    }

    fun authenticate(username: String, password: String): AppUser? {
        val u = users.firstOrNull {
            it.username.equals(username.trim(), ignoreCase = true) && it.password == password
        } ?: return null
        if (u.status != UserStatus.ACTIVE) return null
        return u
    }

    fun userById(id: String): AppUser? = users.firstOrNull { it.id == id }

    fun isUsernameTaken(username: String, excludeUserId: String?): Boolean {
        val t = username.trim()
        if (t.isEmpty()) return false
        return users.any { it.id != excludeUserId && it.username.equals(t, ignoreCase = true) }
    }

    fun addUser(user: AppUser) {
        users.add(user)
    }

    fun updateUser(user: AppUser) {
        val i = users.indexOfFirst { it.id == user.id }
        if (i >= 0) users[i] = user
    }

    fun deleteUser(id: String) {
        val i = users.indexOfFirst { it.id == id }
        if (i >= 0) users.removeAt(i)
    }

    fun setUserStatus(id: String, status: UserStatus) {
        val u = userById(id) ?: return
        updateUser(u.copy(status = status))
    }

    fun toggleUserStatus(id: String) {
        val u = userById(id) ?: return
        val next = if (u.status == UserStatus.ACTIVE) UserStatus.INACTIVE else UserStatus.ACTIVE
        updateUser(u.copy(status = next))
    }

    fun newUserId(): String = "u-${UUID.randomUUID()}"

    private fun seedUsers(): List<AppUser> = listOf(
        AppUser(
            id = "u-admin",
            username = "admin",
            password = "admin123",
            fullName = "Admin User",
            role = UserRole.ADMIN,
            status = UserStatus.ACTIVE,
            totalXP = 0,
            completedLessons = 0,
            completedCourses = 0,
            badgesEarned = 0,
            recentLessonTitles = emptyList()
        ),
        AppUser(
            id = "u-student",
            username = "student",
            password = "1234",
            fullName = "Student Demo",
            role = UserRole.STUDENT,
            status = UserStatus.ACTIVE,
            totalXP = 120,
            completedLessons = 2,
            completedCourses = 0,
            badgesEarned = 1,
            recentLessonTitles = listOf("What is a Program?", "Instructions and Sequences")
        ),
        AppUser(
            id = "u-maria",
            username = "maria",
            password = "1234",
            fullName = "Maria Santos",
            role = UserRole.STUDENT,
            status = UserStatus.ACTIVE,
            totalXP = 340,
            completedLessons = 5,
            completedCourses = 1,
            badgesEarned = 2,
            recentLessonTitles = listOf("What is a Variable?", "Naming Variables")
        ),
        AppUser(
            id = "u-john",
            username = "john",
            password = "1234",
            fullName = "John Reyes",
            role = UserRole.STUDENT,
            status = UserStatus.ACTIVE,
            totalXP = 210,
            completedLessons = 3,
            completedCourses = 0,
            badgesEarned = 1,
            recentLessonTitles = listOf("Python Syntax Basics")
        ),
        AppUser(
            id = "u-ana",
            username = "ana",
            password = "1234",
            fullName = "Ana Cruz",
            role = UserRole.STUDENT,
            status = UserStatus.INACTIVE,
            totalXP = 90,
            completedLessons = 1,
            completedCourses = 0,
            badgesEarned = 0,
            recentLessonTitles = emptyList()
        ),
        AppUser(
            id = "u-mark",
            username = "mark",
            password = "1234",
            fullName = "Mark Dela Peña",
            role = UserRole.STUDENT,
            status = UserStatus.ACTIVE,
            totalXP = 480,
            completedLessons = 8,
            completedCourses = 2,
            badgesEarned = 3,
            recentLessonTitles = listOf("Breaking Down Problems", "Tracing Examples", "How Computers Store Data")
        )
    )
}
