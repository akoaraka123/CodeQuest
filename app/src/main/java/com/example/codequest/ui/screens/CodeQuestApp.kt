package com.example.codequest.ui.screens

import androidx.compose.runtime.Composable
import com.example.codequest.state.AppScreen
import com.example.codequest.state.rememberCodeQuestAppState

@Composable
fun CodeQuestApp() {
    val appState = rememberCodeQuestAppState()

    when (appState.currentScreen) {
        AppScreen.NOTIFICATIONS -> NotificationsScreen(appState)
        AppScreen.MAIN_TABS -> {
            when (appState.selectedTab) {
                com.example.codequest.ui.components.AppTab.HOME -> CodeQuestHomeScreen(
                    appState = appState,
                    selectedTab = appState.selectedTab,
                    onTabSelected = appState::onTabSelected
                )

                com.example.codequest.ui.components.AppTab.QUESTS -> CodeQuestQuestsScreen(
                    appState = appState,
                    selectedTab = appState.selectedTab,
                    onTabSelected = appState::onTabSelected
                )

                com.example.codequest.ui.components.AppTab.BADGES -> CodeQuestBadgesScreen(
                    appState = appState,
                    selectedTab = appState.selectedTab,
                    onTabSelected = appState::onTabSelected
                )

                com.example.codequest.ui.components.AppTab.PROFILE -> CodeQuestProfileScreen(
                    appState = appState,
                    selectedTab = appState.selectedTab,
                    onTabSelected = appState::onTabSelected
                )
            }
        }

        AppScreen.LESSON -> CodeQuestLessonScreen(appState)
        AppScreen.CHALLENGE -> CodeQuestChallengeScreen(appState)
        AppScreen.RESULT -> CodeQuestResultScreen(appState)
        AppScreen.ALL_EARNED_BADGES -> AllEarnedBadgesScreen(appState)
        AppScreen.ALL_LOCKED_BADGES -> AllLockedBadgesScreen(appState)
        AppScreen.BADGE_CATEGORIES -> BadgeCategoriesScreen(appState)
        AppScreen.ALL_ACHIEVEMENTS -> AllAchievementsScreen(appState)
        AppScreen.EDIT_PROFILE -> EditProfileScreen(appState)
        AppScreen.THEME_SETTINGS -> ThemeSettingsScreen(appState)
        AppScreen.NOTIFICATION_SETTINGS -> NotificationSettingsScreen(appState)
        AppScreen.HELP_SUPPORT -> HelpSupportScreen(appState)
    }
}
