package com.example.codequest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.state.CodeQuestAppState
import com.example.codequest.ui.components.CodeQuestBackButton
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.components.GradientButton
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.TextMuted
import com.example.codequest.ui.theme.TextPrimary

@Composable
fun EditProfileScreen(appState: CodeQuestAppState) {
    BackHandler { appState.backToProfileFromSettings() }
    var username by remember { mutableStateOf(appState.username) }
    var roleTitle by remember { mutableStateOf(appState.roleTitle) }
    var avatar by remember { mutableStateOf(appState.selectedAvatar) }

    SettingsScaffold(title = "Edit Profile", onBack = appState::backToProfileFromSettings) {
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = roleTitle,
                    onValueChange = { roleTitle = it },
                    label = { Text("Role title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Avatar", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                listOf("Coder", "Debugger", "Gamer", "Student").forEach { option ->
                    AvatarChoiceRow(
                        label = option,
                        selected = avatar == option,
                        onClick = { avatar = option }
                    )
                }
            }
        }
        GradientButton(text = "Save") {
            appState.saveProfile(username, roleTitle, avatar)
        }
    }
}

@Composable
fun ThemeSettingsScreen(appState: CodeQuestAppState) {
    BackHandler { appState.backToProfileFromSettings() }
    SettingsScaffold(title = "Theme Settings", onBack = appState::backToProfileFromSettings) {
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Cyber", "Midnight", "Neon Purple").forEach { theme ->
                    AvatarChoiceRow(
                        label = theme,
                        selected = appState.selectedTheme == theme,
                        onClick = { appState.setTheme(theme) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationSettingsScreen(appState: CodeQuestAppState) {
    BackHandler { appState.backToProfileFromSettings() }
    SettingsScaffold(title = "Notification Settings", onBack = appState::backToProfileFromSettings) {
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleSettingRow(
                    title = "Daily Challenge Reminder",
                    checked = appState.dailyReminderEnabled,
                    onCheckedChange = appState::setDailyReminder
                )
                ToggleSettingRow(
                    title = "Quest Progress Updates",
                    checked = appState.questUpdatesEnabled,
                    onCheckedChange = appState::setQuestUpdates
                )
                ToggleSettingRow(
                    title = "Badge Unlock Alerts",
                    checked = appState.badgeAlertsEnabled,
                    onCheckedChange = appState::setBadgeAlerts
                )
            }
        }
    }
}

@Composable
fun HelpSupportScreen(appState: CodeQuestAppState) {
    BackHandler { appState.backToProfileFromSettings() }
    SettingsScaffold(title = "Help & Support", onBack = appState::backToProfileFromSettings) {
        val faqs = listOf(
            "What is CodeQuest?" to "CodeQuest is a coding-learning game prototype.",
            "How do I unlock quests?" to "Pass challenges with at least 70% score.",
            "How do I earn badges?" to "Complete quests and hit badge milestones.",
            "How is XP calculated?" to "XP is gained from correct answers and completion."
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(faqs) { faq ->
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(faq.first, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text(faq.second, color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
            item {
                GlassCard {
                    Text(
                        "For project demo support, contact your group leader or instructor.",
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CodeQuestBackButton(onClick = onBack)
            Text(title, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp))
        }
        content()
    }
}

@Composable
private fun AvatarChoiceRow(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) PrimaryCyan.copy(alpha = 0.16f) else TextMuted.copy(alpha = 0.08f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = if (selected) PrimaryCyan else TextPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        if (selected) Text("Selected", color = PrimaryCyan, fontSize = 12.sp)
    }
}

@Composable
private fun ToggleSettingRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextPrimary)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
