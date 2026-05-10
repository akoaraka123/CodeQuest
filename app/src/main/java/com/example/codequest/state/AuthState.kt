package com.example.codequest.state

sealed class AuthState {
    data object LoggedOut : AuthState()

    data class LoggedInStudent(val userId: String) : AuthState()

    data class LoggedInAdmin(val adminId: String) : AuthState()
}
