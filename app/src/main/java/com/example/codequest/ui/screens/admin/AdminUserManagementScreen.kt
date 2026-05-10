package com.example.codequest.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.model.AppUser
import com.example.codequest.model.UserStatus
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.ActiveCyan
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.CompletedGreen
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.PrimaryPurple
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun AdminUserManagementScreen(
    users: List<AppUser>,
    onViewUser: (String) -> Unit,
    onAddUser: () -> Unit,
    onToggleStatus: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column {
            Text("User Management", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Local demo accounts", color = TextMuted, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(14.dp))
        GradientButton(text = "Add new user") { onAddUser() }
        Spacer(modifier = Modifier.height(14.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(users, key = { it.id }) { u ->
                UserRowCard(
                    user = u,
                    onView = { onViewUser(u.id) },
                    onToggleStatus = { onToggleStatus(u.id) }
                )
            }
            item { Spacer(modifier = Modifier.height(96.dp)) }
        }
    }
}

@Composable
private fun UserRowCard(
    user: AppUser,
    onView: () -> Unit,
    onToggleStatus: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.fullName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("@${user.username}", color = TextMuted, fontSize = 13.sp)
                    Text(
                        "${user.role.name.lowercase().replaceFirstChar { it.uppercase() }} · ${user.status.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        color = ActiveCyan,
                        fontSize = 12.sp
                    )
                }
                StatusChip(active = user.status == UserStatus.ACTIVE)
            }
            Text(
                "XP ${user.totalXP} · Lessons ${user.completedLessons} · Badges ${user.badgesEarned}",
                color = TextMuted,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SmallOutlineButton(
                    modifier = Modifier.weight(1f),
                    label = "View Details",
                    onClick = onView
                )
                SmallOutlineButton(
                    modifier = Modifier.weight(1f),
                    label = if (user.status == UserStatus.ACTIVE) "Deactivate" else "Activate",
                    accent = if (user.status == UserStatus.ACTIVE) PrimaryPurple else CompletedGreen,
                    onClick = onToggleStatus
                )
            }
        }
    }
}

@Composable
private fun StatusChip(active: Boolean) {
    val bg = if (active) CompletedGreen.copy(alpha = 0.18f) else PrimaryPurple.copy(alpha = 0.18f)
    val fg = if (active) CompletedGreen else PrimaryPurple
    Text(
        text = if (active) "Active" else "Inactive",
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun SmallOutlineButton(
    modifier: Modifier = Modifier,
    label: String,
    accent: androidx.compose.ui.graphics.Color = PrimaryCyan,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.12f))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = accent,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}
