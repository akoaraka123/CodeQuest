package com.example.codequest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.codequest.model.UserRole
import com.example.codequest.state.AuthState
import com.example.codequest.state.rememberCodeQuestAppState
import com.example.codequest.ui.screens.CodeQuestApp
import com.example.codequest.ui.screens.LoginScreen
import com.example.codequest.ui.screens.RegisterScreen
import com.example.codequest.ui.screens.admin.AdminApp
import com.example.codequest.ui.theme.CodeQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val studentAppState = rememberCodeQuestAppState()
            var authState by remember { mutableStateOf<AuthState>(AuthState.LoggedOut) }
            var loggedOutRoute by remember { mutableStateOf(LoggedOutRoute.Login) }
            var loginMessage by remember { mutableStateOf<String?>(null) }

            CodeQuestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    when (val session = authState) {
                        AuthState.LoggedOut -> {
                            when (loggedOutRoute) {
                                LoggedOutRoute.Login -> LoginScreen(
                                    onLoginSuccess = { user ->
                                        when (user.role) {
                                            UserRole.STUDENT -> {
                                                studentAppState.applyStudentSession(user)
                                                authState = AuthState.LoggedInStudent(user.id)
                                            }
                                            UserRole.ADMIN -> {
                                                authState = AuthState.LoggedInAdmin(user.id)
                                            }
                                        }
                                    },
                                    onRegisterClick = {
                                        loginMessage = null
                                        loggedOutRoute = LoggedOutRoute.Register
                                    },
                                    successMessage = loginMessage,
                                    onSuccessMessageShown = {
                                        loginMessage = null
                                    }
                                )
                                LoggedOutRoute.Register -> RegisterScreen(
                                    onRegisterSuccess = {
                                        loginMessage = "Account created successfully. Please log in."
                                        loggedOutRoute = LoggedOutRoute.Login
                                    },
                                    onLoginClick = {
                                        loginMessage = null
                                        loggedOutRoute = LoggedOutRoute.Login
                                    }
                                )
                            }
                        }
                        is AuthState.LoggedInStudent -> CodeQuestApp(
                            appState = studentAppState,
                            onLogout = {
                                studentAppState.resetStudentPlayProgress()
                                studentAppState.resetSessionIdentity()
                                authState = AuthState.LoggedOut
                            }
                        )
                        is AuthState.LoggedInAdmin -> AdminApp(
                            onLogout = { authState = AuthState.LoggedOut }
                        )
                    }
                }
            }
        }
    }
}

private enum class LoggedOutRoute {
    Login,
    Register
}
