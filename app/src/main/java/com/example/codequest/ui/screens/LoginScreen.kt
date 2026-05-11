package com.example.codequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalLoginAttemptRepository
import com.example.codequest.data.LocalPasswordResetRepository
import com.example.codequest.data.LocalUserRepository
import com.example.codequest.model.AppUser
import com.example.codequest.model.UserRole
import com.example.codequest.model.UserStatus
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

private val FullNameRegex = Regex("^[A-Za-zÑñ ]+$")
private val UsernamePasswordRegex = Regex("^[A-Za-z0-9Ññ]+$")

@Composable
fun LoginScreen(
    onLoginSuccess: (AppUser) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    successMessage: String? = null,
    onSuccessMessageShown: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AuthHeader(message = "Sign in to continue")
        Spacer(modifier = Modifier.height(36.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        error = null
                        onSuccessMessageShown()
                    },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        error = null
                        onSuccessMessageShown()
                    },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                successMessage?.let {
                    Text(
                        text = it,
                        color = PrimaryCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                error?.let {
                    Text(
                        text = it,
                        color = PrimaryPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GradientButton(text = "Login") {
            if (username.trim().isEmpty() || password.isEmpty()) {
                error = "Please input username or password."
            } else if (containsInvalidLoginCharacters(username, password)) {
                error = "Special characters are not allowed. Ñ and ñ are allowed."
            } else if (LocalLoginAttemptRepository.isBlocked(username)) {
                error = "Too many failed attempts. Please use Forgot Password or contact the admin."
            } else {
                val u = LocalUserRepository.authenticate(username, password)
                if (u != null) {
                    LocalLoginAttemptRepository.reset(username)
                    onLoginSuccess(u)
                } else {
                    val attemptsLeft = LocalLoginAttemptRepository.recordFailure(username)
                    error = if (attemptsLeft == 0) {
                        "Too many failed attempts. Please use Forgot Password or contact the admin."
                    } else {
                        "Invalid username or password. Attempts left: $attemptsLeft"
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        AuthActionText(
            leadingText = "",
            actionText = "Forgot Password?",
            onClick = onForgotPasswordClick
        )

        Spacer(modifier = Modifier.height(14.dp))
        AuthActionText(
            leadingText = "Don’t have an account?",
            actionText = "Register",
            onClick = onRegisterClick
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Demo: student / 1234  •  admin / admin123",
            color = TextMuted,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ForgotPasswordScreen(
    onRequestSubmitted: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AuthHeader(message = "Request password help")
        Spacer(modifier = Modifier.height(30.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        error = null
                    },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        error = null
                    },
                    label = { Text("Username optional") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                error?.let {
                    Text(
                        text = it,
                        color = PrimaryPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GradientButton(text = "Submit Request") {
            val trimmedFullName = fullName.trim()
            val trimmedUsername = username.trim()

            error = when {
                trimmedFullName.isEmpty() -> "Full Name cannot be empty."
                !FullNameRegex.matches(fullName) ->
                    "Full name can only contain letters, spaces, Ñ, and ñ."
                trimmedUsername.isNotEmpty() && !UsernamePasswordRegex.matches(trimmedUsername) ->
                    "Username can only contain letters, numbers, Ñ, and ñ."
                else -> null
            }

            if (error == null) {
                LocalPasswordResetRepository.addRequest(
                    fullName = trimmedFullName,
                    username = trimmedUsername
                )
                onRequestSubmitted()
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        AuthActionText(
            leadingText = "",
            actionText = "Back to Login",
            onClick = onLoginClick
        )
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: (AppUser) -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AuthHeader(message = "Create your student account")
        Spacer(modifier = Modifier.height(30.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        error = null
                    },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        error = null
                    },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        error = null
                    },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        error = null
                    },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                error?.let {
                    Text(
                        text = it,
                        color = PrimaryPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GradientButton(text = "Create Account") {
            val trimmedFullName = fullName.trim()
            val trimmedUsername = username.trim()

            error = when {
                trimmedFullName.isEmpty() -> "Full Name cannot be empty."
                trimmedUsername.isEmpty() -> "Username cannot be empty."
                password.isEmpty() -> "Password cannot be empty."
                confirmPassword.isEmpty() -> "Confirm Password cannot be empty."
                !FullNameRegex.matches(fullName) ->
                    "Full name can only contain letters, spaces, Ñ, and ñ."
                !UsernamePasswordRegex.matches(username) ->
                    "Username can only contain letters, numbers, Ñ, and ñ."
                !UsernamePasswordRegex.matches(password) ->
                    "Password can only contain letters, numbers, Ñ, and ñ."
                !UsernamePasswordRegex.matches(confirmPassword) ->
                    "Password can only contain letters, numbers, Ñ, and ñ."
                password != confirmPassword -> "Passwords do not match."
                LocalUserRepository.isUsernameTaken(trimmedUsername, excludeUserId = null) ->
                    "Username is already taken."
                else -> null
            }

            if (error == null) {
                val newUser = AppUser(
                    id = LocalUserRepository.newUserId(),
                    username = trimmedUsername,
                    password = password,
                    fullName = trimmedFullName,
                    role = UserRole.STUDENT,
                    status = UserStatus.ACTIVE
                )
                LocalUserRepository.addUser(newUser)
                onRegisterSuccess(newUser)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        AuthActionText(
            leadingText = "Already have an account?",
            actionText = "Login",
            onClick = onLoginClick
        )
    }
}

private fun containsInvalidLoginCharacters(username: String, password: String): Boolean {
    val invalidUsername = username.isNotEmpty() && !UsernamePasswordRegex.matches(username)
    val invalidPassword = password.isNotEmpty() && !UsernamePasswordRegex.matches(password)
    return invalidUsername || invalidPassword
}

@Composable
private fun AuthHeader(message: String) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        text = "CodeQuest",
        color = TextPrimary,
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Debug Academy",
        color = PrimaryCyan,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = message,
        color = TextMuted,
        fontSize = 14.sp
    )
}

@Composable
private fun AuthActionText(
    leadingText: String,
    actionText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingText.isNotBlank()) {
            Text(
                text = "$leadingText ",
                color = TextMuted,
                fontSize = 14.sp
            )
        }
        Text(
            text = actionText,
            color = PrimaryCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
