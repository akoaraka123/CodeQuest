package com.example.codequest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.model.UserRole
import com.example.codequest.state.AuthState
import com.example.codequest.state.rememberCodeQuestAppState
import com.example.codequest.ui.screens.CodeQuestApp
import com.example.codequest.ui.screens.ForgotPasswordScreen
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
            var authState by rememberSaveable(stateSaver = AuthStateSaver) {
                mutableStateOf<AuthState>(AuthState.LoggedOut)
            }
            var loggedOutRoute by rememberSaveable { mutableStateOf(LoggedOutRoute.Login) }
            var loginMessage by rememberSaveable { mutableStateOf<String?>(null) }
            var showLogoutConfirmation by rememberSaveable { mutableStateOf(false) }
            var showSplash by rememberSaveable { mutableStateOf(true) }

            CodeQuestTheme {
                if (showSplash) {
                    CodeQuestSplashScreen(onFinished = { showSplash = false })
                } else {
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
                                        onForgotPasswordClick = {
                                            loginMessage = null
                                            loggedOutRoute = LoggedOutRoute.ForgotPassword
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
                                    LoggedOutRoute.ForgotPassword -> ForgotPasswordScreen(
                                        onRequestSubmitted = {
                                            loginMessage = "Password reset request sent to admin."
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
                                    showLogoutConfirmation = true
                                }
                            )
                            is AuthState.LoggedInAdmin -> AdminApp(
                                onLogout = {
                                    showLogoutConfirmation = true
                                }
                            )
                        }
                    }

                    if (showLogoutConfirmation) {
                        AlertDialog(
                            onDismissRequest = { showLogoutConfirmation = false },
                            title = { Text("Logout") },
                            text = { Text("Do you want to log out this account?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        if (authState is AuthState.LoggedInStudent) {
                                            studentAppState.resetStudentPlayProgress()
                                            studentAppState.resetSessionIdentity()
                                        }
                                        authState = AuthState.LoggedOut
                                        loggedOutRoute = LoggedOutRoute.Login
                                        loginMessage = null
                                        showLogoutConfirmation = false
                                    }
                                ) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showLogoutConfirmation = false }) {
                                    Text("No")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeQuestSplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd))),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "CodeQuest Logo",
            modifier = Modifier.size(150.dp)
        )
    }
}

private enum class LoggedOutRoute {
    Login,
    Register,
    ForgotPassword
}

private val AuthStateSaver = Saver<AuthState, List<String>>(
    save = { state ->
        when (state) {
            AuthState.LoggedOut -> listOf("logged_out", "")
            is AuthState.LoggedInStudent -> listOf("student", state.userId)
            is AuthState.LoggedInAdmin -> listOf("admin", state.adminId)
        }
    },
    restore = { saved ->
        when (saved.getOrNull(0)) {
            "student" -> AuthState.LoggedInStudent(saved.getOrNull(1).orEmpty())
            "admin" -> AuthState.LoggedInAdmin(saved.getOrNull(1).orEmpty())
            else -> AuthState.LoggedOut
        }
    }
)
