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
import com.example.codequest.ui.screens.admin.AdminApp
import com.example.codequest.ui.theme.CodeQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val studentAppState = rememberCodeQuestAppState()
            var authState by remember { mutableStateOf<AuthState>(AuthState.LoggedOut) }

            CodeQuestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    when (val session = authState) {
                        AuthState.LoggedOut -> LoginScreen(
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
                            }
                        )
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
