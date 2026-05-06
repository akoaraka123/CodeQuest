package com.example.codequest.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.codequest.data.LocalContentRepository
import com.example.codequest.model.Badge
import com.example.codequest.model.Question
import com.example.codequest.model.QuestionType
import com.example.codequest.model.Quest
import com.example.codequest.ui.components.AppTab
import kotlin.math.roundToInt

enum class AppScreen {
    MAIN_TABS,
    NOTIFICATIONS,
    LESSON,
    CHALLENGE,
    RESULT,
    ALL_EARNED_BADGES,
    ALL_LOCKED_BADGES,
    BADGE_CATEGORIES,
    ALL_ACHIEVEMENTS,
    EDIT_PROFILE,
    THEME_SETTINGS,
    NOTIFICATION_SETTINGS,
    HELP_SUPPORT
}

data class ChallengeResult(
    val questId: String,
    val score: Int,
    val percentage: Int,
    val xpEarned: Int,
    val passed: Boolean
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val timeText: String,
    val icon: String,
    val isUnread: Boolean
)

class CodeQuestAppState {
    var selectedTab by mutableStateOf(AppTab.HOME)
        private set

    var currentScreen by mutableStateOf(AppScreen.MAIN_TABS)
        private set

    var selectedQuestId by mutableStateOf<String?>(null)
        private set

    var previousTabBeforeFlow by mutableStateOf<AppTab?>(null)
        private set

    var detailParentTab by mutableStateOf<AppTab?>(null)
        private set

    var currentLessonIndex by mutableIntStateOf(0)
        private set

    var currentQuestionIndex by mutableIntStateOf(0)
        private set

    var totalXP by mutableIntStateOf(340)
        private set

    var streakDays by mutableIntStateOf(7)
        private set

    var debugCorrectCount by mutableIntStateOf(0)
        private set

    var challengeCorrectAnswers by mutableIntStateOf(0)
        private set

    var challengeXpEarned by mutableIntStateOf(0)
        private set

    var result by mutableStateOf<ChallengeResult?>(null)
        private set

    var previousTabBeforeNotifications by mutableStateOf<AppTab?>(null)
        private set

    var username by mutableStateOf("Coder!")
        private set

    var roleTitle by mutableStateOf("Junior Debugger")
        private set

    var selectedAvatar by mutableStateOf("Coder")
        private set

    var selectedTheme by mutableStateOf("Cyber")
        private set

    var dailyReminderEnabled by mutableStateOf(true)
        private set

    var questUpdatesEnabled by mutableStateOf(true)
        private set

    var badgeAlertsEnabled by mutableStateOf(true)
        private set

    var notifications by mutableStateOf(
        listOf(
            NotificationItem(
                id = "n1",
                title = "Daily Challenge Available",
                message = "Solve today’s logic puzzle and earn bonus XP.",
                type = "Challenge",
                timeText = "Just now",
                icon = "\uD83C\uDFAF",
                isUnread = true
            ),
            NotificationItem(
                id = "n2",
                title = "Quest Progress Updated",
                message = "You are currently working on Logic Garden.",
                type = "Quest",
                timeText = "10 min ago",
                icon = "\uD83C\uDF33",
                isUnread = true
            ),
            NotificationItem(
                id = "n3",
                title = "Badge Progress",
                message = "You are getting closer to unlocking Debug Hunter.",
                type = "Badge",
                timeText = "1h ago",
                icon = "\uD83D\uDC1E",
                isUnread = false
            ),
            NotificationItem(
                id = "n4",
                title = "Welcome to CodeQuest",
                message = "Start your coding journey and complete your first quest.",
                type = "System",
                timeText = "Today",
                icon = "\uD83D\uDE80",
                isUnread = false
            )
        )
    )
        private set

    private val questOrder = LocalContentRepository.quests.sortedBy { it.order }

    var completedQuestIds by mutableStateOf(setOf("syntax-hall"))
        private set

    var unlockedQuestIds by mutableStateOf(setOf("syntax-hall", "logic-garden"))
        private set

    var earnedBadgeIds by mutableStateOf(emptySet<String>())
        private set

    var badgeProgress by mutableStateOf(
        mapOf(
            "first-steps" to 1,
            "logic-learner" to 0,
            "streak-master" to streakDays,
            "debug-hunter" to 0,
            "loop-explorer" to 0,
            "freshman-hero" to completedQuestIds.size
        )
    )
        private set

    var questLearningXp by mutableStateOf(
        mutableMapOf(
            "syntax-hall" to 500,
            "logic-garden" to 280,
            "loop-tower" to 0,
            "array-lab" to 0,
            "oop-building" to 0
        )
    )
        private set

    var questCompletedLessonCounts by mutableStateOf(
        mutableMapOf(
            "syntax-hall" to 7,
            "logic-garden" to 4,
            "loop-tower" to 0,
            "array-lab" to 0,
            "oop-building" to 0
        )
    )
        private set

    fun onTabSelected(tab: AppTab) {
        selectedTab = tab
        currentScreen = AppScreen.MAIN_TABS
    }

    fun openNotifications() {
        previousTabBeforeNotifications = selectedTab
        currentScreen = AppScreen.NOTIFICATIONS
        notifications = notifications.map { it.copy(isUnread = false) }
    }

    fun backFromNotifications() {
        selectedTab = previousTabBeforeNotifications ?: selectedTab
        currentScreen = AppScreen.MAIN_TABS
    }

    fun hasUnreadNotifications(): Boolean = notifications.any { it.isUnread }

    fun getQuests(): List<Quest> = questOrder

    fun getCurrentQuest(): Quest? {
        val active = questOrder.firstOrNull { unlockedQuestIds.contains(it.id) && !completedQuestIds.contains(it.id) }
        return active ?: questOrder.firstOrNull()
    }

    fun getSelectedQuest(): Quest? = questOrder.firstOrNull { it.id == selectedQuestId }

    fun getCurrentLesson() = getSelectedQuest()?.lessons?.getOrNull(currentLessonIndex)

    fun getQuestionsForSelectedQuest(): List<Question> {
        val questId = selectedQuestId ?: return emptyList()
        return LocalContentRepository.questionsByQuestId[questId].orEmpty()
    }

    fun getCurrentQuestion(): Question? = getQuestionsForSelectedQuest().getOrNull(currentQuestionIndex)

    fun startQuest(questId: String): Boolean {
        if (!unlockedQuestIds.contains(questId)) return false
        if (currentScreen == AppScreen.MAIN_TABS) {
            previousTabBeforeFlow = selectedTab
        }
        selectedQuestId = questId
        currentLessonIndex = 0
        currentScreen = AppScreen.LESSON
        return true
    }

    fun continueActiveQuest() {
        val quest = getCurrentQuest() ?: return
        startQuest(quest.id)
    }

    fun nextLessonOrChallenge() {
        val lessons = getSelectedQuest()?.lessons.orEmpty()
        if (lessons.isEmpty()) {
            startChallenge()
            return
        }
        if (currentLessonIndex < lessons.lastIndex) {
            currentLessonIndex += 1
        } else {
            startChallenge()
        }
    }

    fun startChallenge() {
        currentQuestionIndex = 0
        challengeCorrectAnswers = 0
        challengeXpEarned = 0
        currentScreen = AppScreen.CHALLENGE
    }

    fun submitAnswer(answerIndex: Int): Pair<Boolean, String> {
        val question = getCurrentQuestion() ?: return false to "No question available."
        val correct = question.correctAnswerIndex == answerIndex
        if (correct) {
            challengeCorrectAnswers += 1
            challengeXpEarned += question.xpReward
            val questId = selectedQuestId ?: ""
            val current = questLearningXp[questId] ?: 0
            questLearningXp[questId] = (current + question.xpReward).coerceAtMost(500)
            if (question.type == QuestionType.DEBUG_CODE) {
                debugCorrectCount += 1
            }
        }
        return correct to question.explanation
    }

    fun moveToNextQuestionOrResult() {
        val questions = getQuestionsForSelectedQuest()
        if (currentQuestionIndex < questions.lastIndex) {
            currentQuestionIndex += 1
            return
        }
        finishChallenge()
    }

    fun finishChallenge() {
        val questions = getQuestionsForSelectedQuest()
        if (questions.isEmpty()) {
            currentScreen = AppScreen.RESULT
            result = ChallengeResult(selectedQuestId.orEmpty(), 0, 0, 0, false)
            return
        }
        val percentage = ((challengeCorrectAnswers.toFloat() / questions.size) * 100f).roundToInt()
        val passed = percentage >= 70
        val questId = selectedQuestId.orEmpty()
        if (passed) {
            completedQuestIds = completedQuestIds + questId
            unlockNextQuest(questId)
            totalXP += challengeXpEarned + 40
            val lessonCount = getSelectedQuest()?.lessons?.size ?: 0
            questCompletedLessonCounts[questId] = lessonCount
        } else {
            totalXP += challengeXpEarned
        }
        updateBadges()
        result = ChallengeResult(
            questId = questId,
            score = challengeCorrectAnswers,
            percentage = percentage,
            xpEarned = challengeXpEarned,
            passed = passed
        )
        currentScreen = AppScreen.RESULT
    }

    private fun unlockNextQuest(questId: String) {
        val currentIndex = questOrder.indexOfFirst { it.id == questId }
        if (currentIndex == -1 || currentIndex == questOrder.lastIndex) return
        val nextQuest = questOrder[currentIndex + 1]
        unlockedQuestIds = unlockedQuestIds + nextQuest.id
    }

    fun retryChallenge() {
        startChallenge()
    }

    fun backFromLesson() {
        selectedTab = previousTabBeforeFlow ?: AppTab.QUESTS
        currentScreen = AppScreen.MAIN_TABS
    }

    fun backFromChallenge() {
        currentQuestionIndex = 0
        challengeCorrectAnswers = 0
        challengeXpEarned = 0
        currentScreen = AppScreen.LESSON
    }

    fun reviewLesson() {
        currentLessonIndex = 0
        currentScreen = AppScreen.LESSON
    }

    fun goHome() {
        selectedTab = AppTab.HOME
        currentScreen = AppScreen.MAIN_TABS
    }

    fun openNextQuestAfterPass() {
        val currentId = selectedQuestId ?: return
        val currentIndex = questOrder.indexOfFirst { it.id == currentId }
        if (currentIndex == -1 || currentIndex == questOrder.lastIndex) {
            goHome()
            return
        }
        val next = questOrder[currentIndex + 1]
        if (unlockedQuestIds.contains(next.id)) {
            startQuest(next.id)
        } else {
            goHome()
        }
    }

    private fun updateBadges() {
        val updates = badgeProgress.toMutableMap()

        if (completedQuestIds.isNotEmpty()) {
            earnedBadgeIds = earnedBadgeIds + "first-steps"
            updates["first-steps"] = 1
        }
        if (completedQuestIds.contains("logic-garden")) {
            earnedBadgeIds = earnedBadgeIds + "logic-learner"
            updates["logic-learner"] = 1
        }
        updates["streak-master"] = streakDays
        updates["debug-hunter"] = debugCorrectCount
        if (completedQuestIds.contains("loop-tower")) {
            earnedBadgeIds = earnedBadgeIds + "loop-explorer"
            updates["loop-explorer"] = 1
        }
        updates["freshman-hero"] = completedQuestIds.size
        if (completedQuestIds.size >= 5) {
            earnedBadgeIds = earnedBadgeIds + "freshman-hero"
        }

        val debugTarget = LocalContentRepository.badges.firstOrNull { it.id == "debug-hunter" }?.target
        if (debugTarget != null && debugCorrectCount >= debugTarget) {
            earnedBadgeIds = earnedBadgeIds + "debug-hunter"
        }
        val streakTarget = LocalContentRepository.badges.firstOrNull { it.id == "streak-master" }?.target
        if (streakTarget != null && streakDays >= streakTarget) {
            earnedBadgeIds = earnedBadgeIds + "streak-master"
        }
        badgeProgress = updates
    }

    fun badgeState(): List<Pair<Badge, Int>> {
        return LocalContentRepository.badges.map { badge ->
            badge to (badgeProgress[badge.id] ?: 0)
        }
    }

    fun openAllEarnedBadges() {
        detailParentTab = AppTab.BADGES
        selectedTab = AppTab.BADGES
        currentScreen = AppScreen.ALL_EARNED_BADGES
    }

    fun openAllLockedBadges() {
        detailParentTab = AppTab.BADGES
        selectedTab = AppTab.BADGES
        currentScreen = AppScreen.ALL_LOCKED_BADGES
    }

    fun openBadgeCategories() {
        detailParentTab = AppTab.BADGES
        selectedTab = AppTab.BADGES
        currentScreen = AppScreen.BADGE_CATEGORIES
    }

    fun openAllAchievements() {
        detailParentTab = AppTab.PROFILE
        selectedTab = AppTab.PROFILE
        currentScreen = AppScreen.ALL_ACHIEVEMENTS
    }

    fun backFromDetailScreen() {
        selectedTab = detailParentTab ?: AppTab.BADGES
        currentScreen = AppScreen.MAIN_TABS
    }

    fun openEditProfile() {
        detailParentTab = AppTab.PROFILE
        selectedTab = AppTab.PROFILE
        currentScreen = AppScreen.EDIT_PROFILE
    }

    fun openThemeSettings() {
        detailParentTab = AppTab.PROFILE
        selectedTab = AppTab.PROFILE
        currentScreen = AppScreen.THEME_SETTINGS
    }

    fun openNotificationSettings() {
        detailParentTab = AppTab.PROFILE
        selectedTab = AppTab.PROFILE
        currentScreen = AppScreen.NOTIFICATION_SETTINGS
    }

    fun openHelpSupport() {
        detailParentTab = AppTab.PROFILE
        selectedTab = AppTab.PROFILE
        currentScreen = AppScreen.HELP_SUPPORT
    }

    fun saveProfile(newUsername: String, newRoleTitle: String, avatar: String) {
        username = newUsername.ifBlank { "Coder!" }
        roleTitle = newRoleTitle.ifBlank { "Junior Debugger" }
        selectedAvatar = avatar
        backToProfileFromSettings()
    }

    fun setTheme(theme: String) {
        selectedTheme = theme
    }

    fun setDailyReminder(enabled: Boolean) {
        dailyReminderEnabled = enabled
    }

    fun setQuestUpdates(enabled: Boolean) {
        questUpdatesEnabled = enabled
    }

    fun setBadgeAlerts(enabled: Boolean) {
        badgeAlertsEnabled = enabled
    }

    fun backToProfileFromSettings() {
        selectedTab = AppTab.PROFILE
        currentScreen = AppScreen.MAIN_TABS
    }
}
