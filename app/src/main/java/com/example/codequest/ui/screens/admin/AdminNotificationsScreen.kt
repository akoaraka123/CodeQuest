package com.example.codequest.ui.screens.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalPasswordResetRepository
import com.example.codequest.model.PasswordResetRequest
import com.example.codequest.model.ResetRequestStatus
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@androidx.compose.runtime.Composable
fun AdminNotificationsScreen(
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                CodeQuestBackButton(onClick = onBack)
                Text(
                    "Admin Notifications",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        if (LocalPasswordResetRepository.requests.isEmpty()) {
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("No password reset requests", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("New student requests will appear here.", color = TextMuted)
                    }
                }
            }
        } else {
            items(LocalPasswordResetRepository.requests, key = { it.id }) { request ->
                PasswordResetRequestCard(request = request)
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun PasswordResetRequestCard(request: PasswordResetRequest) {
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔑", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Password Reset Request", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "${request.fullName} requested help with password recovery.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    request.username?.let {
                        Text("Username: $it", color = TextMuted, fontSize = 12.sp)
                    }
                }
                if (!request.isReadByAdmin) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryPurple, CircleShape)
                            .padding(4.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RequestPill(text = "Password Reset")
                Spacer(modifier = Modifier.width(8.dp))
                RequestPill(text = request.requestedAt)
                Spacer(modifier = Modifier.width(8.dp))
                RequestPill(text = if (request.status == ResetRequestStatus.PENDING) "Pending" else "Reviewed")
            }

            if (request.status == ResetRequestStatus.PENDING) {
                GradientButton(text = "Mark as Reviewed") {
                    LocalPasswordResetRepository.markAsReviewed(request.id)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun RequestPill(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.08f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, color = TextPrimary, fontSize = 11.sp)
    }
}
