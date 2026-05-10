package com.example.codequest.ui.screens.admin

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalUserRepository
import com.example.codequest.model.AppUser
import com.example.codequest.model.UserRole
import com.example.codequest.model.UserStatus
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun AddEditUserScreen(
    userId: String?,
    onCancel: () -> Unit,
    onSaved: (String) -> Unit
) {
    val existing = userId?.let { LocalUserRepository.userById(it) }

    var fullName by remember(existing) { mutableStateOf(existing?.fullName.orEmpty()) }
    var username by remember(existing) { mutableStateOf(existing?.username.orEmpty()) }
    var password by remember(existing) { mutableStateOf(existing?.password.orEmpty()) }
    var role by remember(existing) { mutableStateOf(existing?.role ?: UserRole.STUDENT) }
    var status by remember(existing) { mutableStateOf(existing?.status ?: UserStatus.ACTIVE) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CodeQuestBackButton(onClick = onCancel)
            Text(
                if (existing == null) "Add user" else "Edit user",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            error = null
                        },
                        label = { Text("Full name") },
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
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Role", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    RoleChip("Student", role == UserRole.STUDENT) { role = UserRole.STUDENT }
                    RoleChip("Admin", role == UserRole.ADMIN) { role = UserRole.ADMIN }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Status", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    RoleChip("Active", status == UserStatus.ACTIVE) { status = UserStatus.ACTIVE }
                    RoleChip("Inactive", status == UserStatus.INACTIVE) { status = UserStatus.INACTIVE }
                }
            }

            error?.let {
                Text(it, color = PrimaryPurple, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            GradientButton(text = "Save") {
                val fn = fullName.trim()
                val un = username.trim()
                val pw = password
                when {
                    fn.isEmpty() -> error = "Full name cannot be empty."
                    un.isEmpty() -> error = "Username cannot be empty."
                    pw.isEmpty() -> error = "Password cannot be empty."
                    LocalUserRepository.isUsernameTaken(un, excludeUserId = existing?.id) ->
                        error = "Username already taken."
                    else -> {
                        if (existing == null) {
                            val id = LocalUserRepository.newUserId()
                            val u = AppUser(
                                id = id,
                                username = un,
                                password = pw,
                                fullName = fn,
                                role = role,
                                status = status,
                                totalXP = 0,
                                completedLessons = 0,
                                completedCourses = 0,
                                badgesEarned = 0,
                                recentLessonTitles = emptyList()
                            )
                            LocalUserRepository.addUser(u)
                            onSaved(id)
                        } else {
                            val u = existing.copy(
                                username = un,
                                password = pw,
                                fullName = fn,
                                role = role,
                                status = status
                            )
                            LocalUserRepository.updateUser(u)
                            onSaved(existing.id)
                        }
                    }
                }
            }
            Text(
                text = "Cancel",
                color = TextMuted,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCancel() }
                    .padding(vertical = 14.dp)
            )
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun RoleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) PrimaryCyan.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.06f)
    Text(
        text = label,
        color = if (selected) PrimaryCyan else TextPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp)
    )
}
