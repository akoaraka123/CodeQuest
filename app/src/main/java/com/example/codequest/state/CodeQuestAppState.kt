package com.example.codequest.state

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.codequest.data.LocalContentRepository
import com.example.codequest.model.ActivityCompletionRecord
import com.example.codequest.model.ActivityItem
import com.example.codequest.model.ActivityType
import com.example.codequest.model.fillInAnswerMatches
import com.example.codequest.model.isMultiBlankCode
import com.example.codequest.model.isTicLesson1MultipleChoice
import com.example.codequest.model.multiBlankAnswersMatch
import com.example.codequest.model.requiresMultipleChoice
import com.example.codequest.model.CommandSequencePlayback
import com.example.codequest.model.PlaybackStepResult
import com.example.codequest.model.playbackBoardConfig
import com.example.codequest.model.effectiveProcessSteps
import com.example.codequest.model.normalizeCommandToken
import com.example.codequest.model.slotCount
import com.example.codequest.model.Achievement
import com.example.codequest.model.AppUser
import com.example.codequest.model.Badge
import com.example.codequest.model.isRedTargetCommandSequence
import com.example.codequest.model.Course
import com.example.codequest.model.Lesson
import com.example.codequest.ui.components.AppTab

enum class AppScreen {
    MAIN_TABS,
    NOTIFICATIONS,
    COURSE_DETAIL,
    LESSON_ACTIVITY,
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

data class LessonSessionResult(
    val courseId: String,
    val lessonId: String,
    val courseTitle: String,
    val lessonTitle: String,
    val correctCount: Int,
    val totalActivities: Int,
    val xpEarned: Int
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

internal object CodeQuestPreferences {
    private var appContext: Context? = null
    const val PROGRESS_SCHEMA_VERSION = 3

    fun install(context: Context) {
        appContext = context.applicationContext
    }

    fun progressPrefs(currentUserId: String) =
        requireNotNull(appContext) { "CodeQuestPreferences must be installed before app state is used." }
            .getSharedPreferences(
                "codequest_progress_${currentUserId.ifEmpty { "default" }}",
                Context.MODE_PRIVATE
            )
}

class CodeQuestAppState {
    private val maxWrongAttempts = 3
    private var currentUserId: String = ""

    private fun prefs() = CodeQuestPreferences.progressPrefs(currentUserId)

    private fun loadProgressFromPrefs() {
        val p = prefs()
        if (p.getInt("progressSchemaVersion", 0) != CodeQuestPreferences.PROGRESS_SCHEMA_VERSION) {
            p.edit()
                .clear()
                .putInt("progressSchemaVersion", CodeQuestPreferences.PROGRESS_SCHEMA_VERSION)
                .apply()
            return
        }
        completedLessonIds = p.getStringSet("completedLessonIds", emptySet())?.toSet() ?: emptySet()
        completedActivityIds = p.getStringSet("completedActivityIds", emptySet())?.toSet() ?: emptySet()
        completedCourseIds = p.getStringSet("completedCourseIds", emptySet())?.toSet() ?: emptySet()
        earnedBadgeIds = p.getStringSet("earnedBadgeIds", emptySet())?.toSet() ?: emptySet()
        earnedAchievementIds = p.getStringSet("earnedAchievementIds", emptySet())?.toSet() ?: emptySet()
        totalXP = p.getInt("totalXP", 0)
        streakDays = p.getInt("streakDays", 0)
        debugCorrectCount = p.getInt("debugCorrectCount", 0)
        activeCourseId = p.getString("activeCourseId", "thinking-in-code") ?: "thinking-in-code"
        ticL2RobotGuideSeen = p.getBoolean("ticL2RobotGuideSeen", false)
        roleTitle = p.getString("roleTitle", "Junior Debugger") ?: "Junior Debugger"
        selectedAvatar = p.getString("selectedAvatar", "Coder") ?: "Coder"
        selectedTheme = p.getString("selectedTheme", "Cyber") ?: "Cyber"
        hasSeenOnboarding = p.getBoolean("hasSeenOnboarding", false)

        val badgeStr = p.getString("badgeProgress", "") ?: ""
        badgeProgress = if (badgeStr.isEmpty()) {
            mapOf("first-steps" to 0, "thinking-coder" to 0, "variable-starter" to 0,
                "function-builder" to 0, "algorithm-explorer" to 0, "cs-rookie" to 0, "neural-beginner" to 0)
        } else {
            badgeStr.split(",").mapNotNull {
                val parts = it.split("=")
                if (parts.size == 2) parts[0] to (parts[1].toIntOrNull() ?: 0) else null
            }.toMap()
        }

        val xpStr = p.getString("courseLearningXp", "") ?: ""
        val xpLoaded = if (xpStr.isEmpty()) emptyMap() else xpStr.split(",").mapNotNull {
            val parts = it.split("="); if (parts.size == 2) parts[0] to (parts[1].toIntOrNull() ?: 0) else null
        }.toMap()
        courseLearningXp = courseOrder.associate { it.id to 0 }.toMutableMap().also { it.putAll(xpLoaded) }

        val countStr = p.getString("courseCompletedLessonCounts", "") ?: ""
        val countLoaded = if (countStr.isEmpty()) emptyMap() else countStr.split(",").mapNotNull {
            val parts = it.split("="); if (parts.size == 2) parts[0] to (parts[1].toIntOrNull() ?: 0) else null
        }.toMap()
        courseCompletedLessonCounts = courseOrder.associate { it.id to 0 }.toMutableMap().also { it.putAll(countLoaded) }

        val recordStr = p.getString("activityCompletionRecords", "") ?: ""
        activityCompletionRecords = if (recordStr.isBlank()) {
            emptyMap()
        } else {
            recordStr.split(";").mapNotNull { token ->
                val parts = token.split("|")
                if (parts.size != 3) return@mapNotNull null
                val id = parts[0]
                val correct = parts[1].toBooleanStrictOrNull() ?: false
                val exp = parts[2].toIntOrNull() ?: 0
                id to ActivityCompletionRecord(id, correct, exp)
            }.toMap()
        }
        syncExpTotalsFromCompletionRecords()
    }

    private fun saveProgress() {
        if (currentUserId.isEmpty()) return
        prefs().edit().apply {
            putInt("progressSchemaVersion", CodeQuestPreferences.PROGRESS_SCHEMA_VERSION)
            putStringSet("completedLessonIds", completedLessonIds)
            putStringSet("completedActivityIds", completedActivityIds)
            putStringSet("completedCourseIds", completedCourseIds)
            putStringSet("earnedBadgeIds", earnedBadgeIds)
            putStringSet("earnedAchievementIds", earnedAchievementIds)
            putInt("totalXP", totalXP)
            putInt("streakDays", streakDays)
            putInt("debugCorrectCount", debugCorrectCount)
            putString("activeCourseId", activeCourseId)
            putBoolean("ticL2RobotGuideSeen", ticL2RobotGuideSeen)
            putString("roleTitle", roleTitle)
            putString("selectedAvatar", selectedAvatar)
            putString("selectedTheme", selectedTheme)
            putBoolean("hasSeenOnboarding", hasSeenOnboarding)
            putString("badgeProgress", badgeProgress.entries.joinToString(",") { "${it.key}=${it.value}" })
            putString("courseLearningXp", courseLearningXp.entries.joinToString(",") { "${it.key}=${it.value}" })
            putString("courseCompletedLessonCounts", courseCompletedLessonCounts.entries.joinToString(",") { "${it.key}=${it.value}" })
            putString(
                "activityCompletionRecords",
                activityCompletionRecords.values.joinToString(";") {
                    "${it.activityId}|${it.correct}|${it.xpGranted}"
                }
            )
            apply()
        }
    }

    private fun syncExpTotalsFromCompletionRecords() {
        totalXP = activityCompletionRecords.values.sumOf { record ->
            if (record.correct) record.xpGranted else 0
        }
        courseLearningXp = courseOrder.associate { course ->
            course.id to courseExp(course.id)
        }.toMutableMap()
    }

    var selectedTab by mutableStateOf(AppTab.HOME)
        private set

    var currentScreen by mutableStateOf(AppScreen.MAIN_TABS)
        private set

    var hasSeenOnboarding by mutableStateOf(false)
        private set

    var showOnboarding by mutableStateOf(false)
        private set

    var onboardingStepIndex by mutableStateOf(0)
        private set

    var selectedCourseId by mutableStateOf<String?>(null)
        private set

    var selectedLessonId by mutableStateOf<String?>(null)
        private set

    /** Lesson highlighted on course detail; Start uses this. */
    var courseDetailFocusLessonId by mutableStateOf<String?>(null)
        private set

    var previousTabBeforeFlow by mutableStateOf<AppTab?>(null)
        private set

    var detailParentTab by mutableStateOf<AppTab?>(null)
        private set

    var currentActivityIndex by mutableIntStateOf(0)
        private set

    var lessonInteractionState by mutableStateOf(LessonInteractionState.ACTIVITY)
        private set

    /** Step index for [LessonInteractionState.ROBOT_DEMO_GUIDE] (Lesson 2 robot intro). */
    var robotDemoGuideStep by mutableIntStateOf(0)
        private set

    var ticL2RobotGuideSeen by mutableStateOf(false)
        private set

    var currentProcessStepIndex by mutableIntStateOf(0)
        private set

    var pendingAnswerCorrect by mutableStateOf(false)
        private set

    /** MC / trace: selected option index. Command tasks: unused for selection. */
    var pendingSubmittedIndex by mutableIntStateOf(-1)
        private set

    /** Fill-in-the-blank activities: learner's typed answer. */
    var fillInAnswer by mutableStateOf("")
        private set

    var multiBlankAnswers by mutableStateOf<List<String>>(emptyList())
        private set

    var currentBlankIndex by mutableIntStateOf(0)
        private set

    var isAnswerChecked by mutableStateOf(false)
        private set

    var activityCommandSlots by mutableStateOf(listOf<String?>())
        private set

    /** Snapshotted program when entering process reveal for command-sequence playback. */
    var commandPlaybackCommandsSnapshot by mutableStateOf<List<String>>(emptyList())
        private set

    /** True when process reveal is playing the lesson's reference solution instead of user attempt. */
    var commandPlaybackUsesReferenceSolution by mutableStateOf(false)
        private set

    var commandPlaybackResults by mutableStateOf<List<PlaybackStepResult>>(emptyList())
        private set

    /** Bumps when a new command playback session starts (re-seeds animation state). */
    var commandPlaybackGeneration by mutableIntStateOf(0)
        private set

    /** Overrides [ActivityItem.finalResult] after command playback finishes. */
    var playbackSummaryOverride by mutableStateOf<String?>(null)
        private set

    /**
     * After a successful command-sequence playback we show the success balloons once; replay on the
     * same attempt does not repeat. Cleared by [resetInteractionForActivity].
     */
    var commandSequenceSuccessBalloonsShownForAttempt by mutableStateOf(false)
        private set

    fun markCommandSequenceSuccessBalloonsShown() {
        commandSequenceSuccessBalloonsShownForAttempt = true
    }

    var totalXP by mutableIntStateOf(0)
        private set

    var streakDays by mutableIntStateOf(0)
        private set

    var debugCorrectCount by mutableIntStateOf(0)
        private set

    var lessonCorrectThisSession by mutableIntStateOf(0)
        private set

    /** Wrong Check submissions for Thinking in Code Lesson 1 MC (max 3); reset per question. */
    var lessonOneWrongAttempts by mutableIntStateOf(0)
        private set

    var hasViewedCorrectAnswer by mutableStateOf(false)
        private set

    var isAnswerRevealed by mutableStateOf(false)
        private set

    var isActivityCompleted by mutableStateOf(false)
        private set

    var isActivityCorrect by mutableStateOf(false)
        private set

    var canRetry by mutableStateOf(true)
        private set

    var sessionXpEarned by mutableIntStateOf(0)
        private set

    var result by mutableStateOf<LessonSessionResult?>(null)
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
                message = "Solve today’s logic puzzle and earn bonus EXP.",
                type = "Challenge",
                timeText = "Just now",
                icon = "\uD83C\uDFAF",
                isUnread = true
            ),
            NotificationItem(
                id = "n2",
                title = "Learning Path Updated",
                message = "Continue Thinking in Code to build strong foundations.",
                type = "Course",
                timeText = "10 min ago",
                icon = "\uD83D\uDCDA",
                isUnread = true
            ),
            NotificationItem(
                id = "n3",
                title = "Badge Progress",
                message = "You are getting closer to unlocking new badges.",
                type = "Badge",
                timeText = "1h ago",
                icon = "\uD83D\uDC1E",
                isUnread = false
            ),
            NotificationItem(
                id = "n4",
                title = "Welcome to CodeQuest",
                message = "Start your learning path from the Quests tab.",
                type = "System",
                timeText = "Today",
                icon = "\uD83D\uDE80",
                isUnread = false
            )
        )
    )
        private set

    private val courseOrder: List<Course> = LocalContentRepository.visibleCourses

    var activeCourseId by mutableStateOf("thinking-in-code")
        private set

    var completedLessonIds by mutableStateOf(setOf<String>())
        private set

    /**
     * Activity IDs ([ActivityItem.id]) answered correctly and proceeded past feedback—persists for the
     * app session. Prevents redoing the same question after leaving and re-opening the lesson.
     */
    var completedActivityIds by mutableStateOf(setOf<String>())
        private set

    /** Snapshots for review-only playback; keys match [ActivityItem.id]. */
    var activityCompletionRecords by mutableStateOf(mapOf<String, ActivityCompletionRecord>())
        private set

    /**
     * When true, the current activity is opened as read-only review (completed earlier).
     * Editing, Check, and EXP grants are disabled.
     */
    var lessonReviewMode by mutableStateOf(false)
        private set

    var completedCourseIds by mutableStateOf(setOf<String>())
        private set

    /** Shown when opening a lesson is blocked because every activity is already completed. */
    var lessonEntryBlockedNotice by mutableStateOf<String?>(null)
        private set

    var unlockedCourseIds by mutableStateOf(setOf("thinking-in-code"))
        private set

    var unlockedLessonIds by mutableStateOf(setOf("tic-l1"))
        private set

    /**
     * Unlocks every course and lesson and allows retaking completed content for QA / content editing.
     * Toggle from Profile → Demo mode.
     */
    var demoModeEnabled by mutableStateOf(false)
        private set

    private val allLessonIds: Set<String> by lazy {
        courseOrder.flatMap { course -> course.lessons.map { it.id } }.toSet()
    }

    private val allCourseIds: Set<String> by lazy {
        courseOrder.map { it.id }.toSet()
    }

    fun isCourseUnlocked(courseId: String): Boolean {
        if (demoModeEnabled) return true
        val idx = courseOrder.indexOfFirst { it.id == courseId }
        if (idx < 0) return false
        if (idx == 0) return true
        return courseOrder[idx - 1].id in completedCourseIds
    }

    fun isLessonUnlocked(lessonId: String): Boolean {
        if (demoModeEnabled) return true
        val course = courseOrder.firstOrNull { c -> c.lessons.any { it.id == lessonId } } ?: return false
        if (!isCourseUnlocked(course.id)) return false
        val sorted = course.lessons.sortedBy { it.order }
        val idx = sorted.indexOfFirst { it.id == lessonId }
        if (idx < 0) return false
        if (idx == 0) return true
        return sorted[idx - 1].id in completedLessonIds
    }

    fun effectiveUnlockedLessonIds(): Set<String> {
        if (demoModeEnabled) return allLessonIds
        return courseOrder.flatMap { course ->
            if (!isCourseUnlocked(course.id)) emptyList()
            else {
                val sorted = course.lessons.sortedBy { it.order }
                buildList {
                    for ((i, lesson) in sorted.withIndex()) {
                        if (i == 0 || sorted[i - 1].id in completedLessonIds) add(lesson.id)
                        else break
                    }
                }
            }
        }.toSet()
    }

    fun applyDemoMode(enabled: Boolean) {
        demoModeEnabled = enabled
        lessonEntryBlockedNotice = null
    }

    var earnedBadgeIds by mutableStateOf(emptySet<String>())
        private set

    var earnedAchievementIds by mutableStateOf(emptySet<String>())
        private set

    var pendingUnlockedAchievements by mutableStateOf(listOf<Achievement>())
        private set

    var badgeProgress by mutableStateOf(
        mapOf(
            "first-steps" to 0,
            "thinking-coder" to 0,
            "variable-starter" to 0,
            "function-builder" to 0,
            "algorithm-explorer" to 0,
            "cs-rookie" to 0,
            "neural-beginner" to 0
        )
    )
        private set

    var courseLearningXp by mutableStateOf(
        courseOrder.associate { it.id to 0 }.toMutableMap()
    )
        private set

    var courseCompletedLessonCounts by mutableStateOf(
        courseOrder.associate { it.id to 0 }.toMutableMap()
    )
        private set

    fun onTabSelected(tab: AppTab) {
        selectedTab = tab
        currentScreen = AppScreen.MAIN_TABS
    }

    fun startOnboardingGuide() {
        selectedTab = AppTab.HOME
        currentScreen = AppScreen.MAIN_TABS
        onboardingStepIndex = 0
        showOnboarding = true
    }

    fun nextOnboardingStep(totalSteps: Int) {
        if (onboardingStepIndex >= totalSteps - 1) {
            finishOnboarding()
        } else {
            onboardingStepIndex += 1
        }
    }

    fun finishOnboarding() {
        hasSeenOnboarding = true
        showOnboarding = false
        onboardingStepIndex = 0
        saveProgress()
    }

    fun skipOnboarding() {
        finishOnboarding()
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

    fun getCourses(): List<Course> = courseOrder

    fun visibleCompletedCourseCount(): Int =
        completedCourseIds.count { completedId -> courseOrder.any { it.id == completedId } }

    fun totalExp(): Int = totalXP

    fun courseExp(courseId: String): Int = (courseLearningXp[courseId] ?: 0).coerceAtMost(500)

    fun levelFromExp(): Int {
        val exp = totalExp()
        return if (exp == 0) 0 else (exp / 500) + 1
    }

    fun levelCurrentExp(): Int = totalExp() % 500

    fun getCourse(courseId: String): Course? = courseOrder.firstOrNull { it.id == courseId }

    fun getSelectedCourse(): Course? = selectedCourseId?.let { getCourse(it) }

    fun getSelectedLesson(): Lesson? = selectedLessonId?.let { LocalContentRepository.lessonById(it) }

    fun getActivitiesForCurrentLesson(): List<ActivityItem> =
        getSelectedLesson()?.activities.orEmpty()

    fun getCurrentActivity(): ActivityItem? =
        getActivitiesForCurrentLesson().getOrNull(currentActivityIndex)

    /** First activity index not in [completedActivityIds], or -1 if all are completed. */
    fun firstIncompleteActivityIndex(lesson: Lesson): Int =
        lesson.activities.indexOfFirst { it.id !in completedActivityIds }

    fun lessonHasIncompleteActivity(lesson: Lesson): Boolean =
        lesson.activities.isNotEmpty() && firstIncompleteActivityIndex(lesson) >= 0

    fun lessonHasAnyCompletedActivity(lesson: Lesson): Boolean =
        lesson.activities.any { it.id in completedActivityIds }

    fun currentRevealSteps(): List<com.example.codequest.model.ProcessStep> {
        val a = getCurrentActivity() ?: return emptyList()
        return a.effectiveProcessSteps(pendingAnswerCorrect)
    }

    fun selectCourseDetailLesson(lessonId: String) {
        if (isLessonUnlocked(lessonId)) {
            courseDetailFocusLessonId = lessonId
        }
    }

    private fun pickDefaultFocusLesson(course: Course): String? {
        val sorted = course.lessons.sortedBy { it.order }
        if (demoModeEnabled) return sorted.firstOrNull()?.id
        val next = sorted.firstOrNull {
            isLessonUnlocked(it.id) && it.id !in completedLessonIds
        }
        return next?.id ?: sorted.firstOrNull { isLessonUnlocked(it.id) }?.id
    }

    fun lessonLevelDisplay(lessonId: String, courseId: String): Int {
        val course = getCourse(courseId) ?: return 1
        val idx = course.lessons.sortedBy { it.order }.indexOfFirst { it.id == lessonId }
        return if (idx >= 0) idx + 1 else 1
    }

    fun getActiveTargetLesson(): Lesson? {
        val course = getActiveCourseForHome() ?: return null
        val sorted = course.lessons.sortedBy { it.order }
        return sorted.firstOrNull { lid ->
            lid.id !in completedLessonIds && isLessonUnlocked(lid.id)
        }
    }

    fun getActiveCourseForHome(): Course? =
        courseOrder.firstOrNull { isCourseUnlocked(it.id) && it.id !in completedCourseIds }
            ?: courseOrder.lastOrNull { isCourseUnlocked(it.id) }

    fun ensureDefaultActiveCourse() {
        val active = getCourse(activeCourseId)
        if (active == null || active.id in completedCourseIds) {
            activeCourseId = courseOrder.firstOrNull {
                isCourseUnlocked(it.id) && it.id !in completedCourseIds
            }?.id ?: "thinking-in-code"
        }
    }

    fun openCourseDetail(courseId: String): Boolean {
        if (!isCourseUnlocked(courseId)) return false
        if (currentScreen == AppScreen.MAIN_TABS) {
            previousTabBeforeFlow = selectedTab
        }
        lessonEntryBlockedNotice = null
        selectedCourseId = courseId
        val c = getCourse(courseId)
        courseDetailFocusLessonId = c?.let { pickDefaultFocusLesson(it) }
        currentScreen = AppScreen.COURSE_DETAIL
        return true
    }

    fun backFromCourseDetail() {
        lessonEntryBlockedNotice = null
        selectedCourseId = null
        courseDetailFocusLessonId = null
        selectedTab = previousTabBeforeFlow ?: AppTab.QUESTS
        currentScreen = AppScreen.MAIN_TABS
    }

    private fun shouldShowTicL2RobotGuide(lessonId: String, startAtActivityIndex: Int?): Boolean {
        if (lessonId != "tic-l2" || ticL2RobotGuideSeen || lessonReviewMode) return false
        val startIdx = startAtActivityIndex ?: 0
        return startIdx == 0
    }

    private fun applyRobotDemoGuideIfNeeded(lessonId: String, startAtActivityIndex: Int?) {
        if (shouldShowTicL2RobotGuide(lessonId, startAtActivityIndex)) {
            robotDemoGuideStep = 0
            lessonInteractionState = LessonInteractionState.ROBOT_DEMO_GUIDE
        } else {
            lessonInteractionState = LessonInteractionState.ACTIVITY
        }
    }

    fun advanceRobotDemoGuide() {
        if (lessonInteractionState != LessonInteractionState.ROBOT_DEMO_GUIDE) return
        if (robotDemoGuideStep < robotDemoGuideStepCount() - 1) {
            robotDemoGuideStep += 1
        } else {
            completeRobotDemoGuide()
        }
    }

    fun completeRobotDemoGuide() {
        ticL2RobotGuideSeen = true
        robotDemoGuideStep = 0
        lessonInteractionState = LessonInteractionState.ACTIVITY
        initCommandSlots()
        saveProgress()
    }

    fun robotDemoGuideStepCount(): Int = 6

    fun startLessonFromCourseDetail(startAtActivityIndex: Int? = null): Boolean {
        val courseId = selectedCourseId ?: return false
        val lessonId = courseDetailFocusLessonId ?: pickDefaultFocusLesson(getCourse(courseId) ?: return false) ?: return false
        if (!isLessonUnlocked(lessonId)) return false
        val lesson = LocalContentRepository.lessonById(lessonId) ?: return false
        if (lesson.courseId != courseId) return false
        lessonEntryBlockedNotice = null
        lessonReviewMode = false
        if (lesson.activities.isEmpty()) {
            completeTextOnlyLessonFromCourse(courseId, lessonId)
            return true
        }
        if (demoModeEnabled) {
            completedActivityIds = completedActivityIds - lesson.activities.map { it.id }.toSet()
            completedLessonIds = completedLessonIds - lessonId
            activeCourseId = courseId
            selectedLessonId = lessonId
            currentActivityIndex = startAtActivityIndex
                ?.coerceIn(0, lesson.activities.lastIndex)
                ?: 0
            sessionXpEarned = 0
            lessonCorrectThisSession = 0
            lessonOneWrongAttempts = 0
            resetInteractionForActivity()
            initCommandSlots()
            applyRobotDemoGuideIfNeeded(lesson.id, startAtActivityIndex)
            currentScreen = AppScreen.LESSON_ACTIVITY
            return true
        }
        if (lesson.id in completedLessonIds) {
            completedActivityIds = completedActivityIds + lesson.activities.map { it.id }.toSet()
        }
        val resumeIdx = lesson.activities.indexOfFirst { it.id !in completedActivityIds }
        if (resumeIdx < 0) {
            lessonEntryBlockedNotice =
                "You already completed every question in this lesson. Retakes are disabled."
            return false
        }
        activeCourseId = courseId
        selectedLessonId = lessonId
        currentActivityIndex = startAtActivityIndex
            ?.coerceIn(0, lesson.activities.lastIndex)
            ?: resumeIdx
        sessionXpEarned = 0
        lessonCorrectThisSession = 0
        lessonOneWrongAttempts = 0
        resetInteractionForActivity()
        initCommandSlots()
        applyRobotDemoGuideIfNeeded(lesson.id, startAtActivityIndex)
        currentScreen = AppScreen.LESSON_ACTIVITY
        return true
    }

    private fun completeTextOnlyLessonFromCourse(courseId: String, lessonId: String) {
        val lesson = LocalContentRepository.lessonById(lessonId) ?: return
        val course = getCourse(courseId) ?: return
        val readXp = 25
        sessionXpEarned = readXp
        totalXP += readXp
        courseLearningXp[course.id] = ((courseLearningXp[course.id] ?: 0) + readXp).coerceAtMost(500)
        lessonCorrectThisSession = 0
        selectedLessonId = lessonId
        selectedCourseId = courseId
        applyLessonCompletionRewards(lesson, course, questionsCorrect = 0, totalActivities = 0)
    }

    fun completeTextOnlyFromLessonIntro() {
        val lesson = getSelectedLesson() ?: return
        val course = getSelectedCourse() ?: return
        if (lesson.activities.isNotEmpty()) return
        val readXp = 25
        sessionXpEarned = readXp
        totalXP += readXp
        courseLearningXp[course.id] = ((courseLearningXp[course.id] ?: 0) + readXp).coerceAtMost(500)
        lessonCorrectThisSession = 0
        applyLessonCompletionRewards(lesson, course, questionsCorrect = 0, totalActivities = 0)
    }

    fun initCommandSlots() {
        val a = getCurrentActivity()
        activityCommandSlots = if (a != null && a.type == ActivityType.COMMAND_SEQUENCE) {
            List(a.slotCount()) { null }
        } else {
            emptyList()
        }
    }

    fun fillNextCommandSlot(command: String) {
        if (lessonReviewMode) return
        val idx = activityCommandSlots.indexOfFirst { it == null }
        if (idx < 0) return
        val copy = activityCommandSlots.toMutableList()
        copy[idx] = command
        activityCommandSlots = copy
    }

    fun assignCommandToSlot(slotIndex: Int, command: String) {
        if (lessonReviewMode) return
        if (slotIndex !in activityCommandSlots.indices) return
        val copy = activityCommandSlots.toMutableList()
        copy[slotIndex] = command
        activityCommandSlots = copy
    }

    fun clearCommandSlot(slotIndex: Int) {
        if (lessonReviewMode) return
        if (slotIndex !in activityCommandSlots.indices) return
        val copy = activityCommandSlots.toMutableList()
        copy[slotIndex] = null
        activityCommandSlots = copy
    }

    fun clearAllCommandSlots() {
        if (lessonReviewMode) return
        initCommandSlots()
    }

    fun selectMcOption(answerIndex: Int) {
        if (lessonReviewMode) return
        if (!canRetry || isActivityCompleted || isAnswerRevealed) return
        pendingSubmittedIndex = answerIndex
    }

    fun updateFillInAnswer(value: String) {
        if (lessonReviewMode) return
        if (!canRetry || isActivityCompleted || isAnswerRevealed) return
        fillInAnswer = value
    }

    fun currentMultiBlankChoices(): List<String> {
        val a = getCurrentActivity() ?: return emptyList()
        return a.codeBlanks.getOrNull(currentBlankIndex)?.choices.orEmpty()
    }

    fun currentMultiBlankLabel(): String {
        val a = getCurrentActivity() ?: return "Choose the answer"
        if (!a.isMultiBlankCode()) return "Choose the answer"
        return "Blank ${currentBlankIndex + 1} of ${a.codeBlanks.size}"
    }

    fun selectMultiBlankChoice(choice: String) {
        if (lessonReviewMode) return
        if (!canRetry || isActivityCompleted || isAnswerRevealed) return
        val a = getCurrentActivity() ?: return
        if (!a.isMultiBlankCode()) return
        val size = a.codeBlanks.size
        if (size == 0) return
        val safeIndex = currentBlankIndex.coerceIn(0, size - 1)
        multiBlankAnswers = List(size) { index ->
            if (index == safeIndex) choice else multiBlankAnswers.getOrNull(index).orEmpty()
        }
        fillInAnswer = multiBlankAnswers.joinToString(" | ")
        // No auto-advance — user navigates blanks explicitly with Next/Back
    }

    fun navigateBlankForward() {
        val a = getCurrentActivity() ?: return
        if (!a.isMultiBlankCode()) return
        if (currentBlankIndex < a.codeBlanks.size - 1) {
            currentBlankIndex += 1
        }
    }

    fun navigateBlankBack() {
        if (currentBlankIndex > 0) {
            currentBlankIndex -= 1
        }
    }

    fun activityReadyForCheck(): Boolean {
        if (isActivityCompleted || isAnswerRevealed) return false
        val a = getCurrentActivity() ?: return false
        return when (a.type) {
            ActivityType.COMMAND_SEQUENCE ->
                activityCommandSlots.isNotEmpty() && activityCommandSlots.all { it != null }
            ActivityType.FILL_IN_BLANK ->
                if (a.isMultiBlankCode()) {
                    multiBlankAnswers.size == a.codeBlanks.size && multiBlankAnswers.all { it.isNotBlank() }
                } else {
                    fillInAnswer.isNotBlank()
                }
            else -> pendingSubmittedIndex >= 0 && a.correctAnswerIndex >= 0
        }
    }

    private fun resetInteractionForActivity() {
        lessonInteractionState = LessonInteractionState.ACTIVITY
        currentProcessStepIndex = 0
        pendingAnswerCorrect = false
        pendingSubmittedIndex = -1
        fillInAnswer = ""
        val current = getCurrentActivity()
        multiBlankAnswers = List(current?.codeBlanks?.size ?: 0) { "" }
        currentBlankIndex = 0
        isAnswerChecked = false
        hasViewedCorrectAnswer = false
        isAnswerRevealed = false
        isActivityCompleted = false
        isActivityCorrect = false
        canRetry = true
        commandPlaybackCommandsSnapshot = emptyList()
        commandPlaybackUsesReferenceSolution = false
        commandPlaybackResults = emptyList()
        playbackSummaryOverride = null
        commandPlaybackGeneration = 0
        commandSequenceSuccessBalloonsShownForAttempt = false
        initCommandSlots()
    }

    fun submitActivityCheck() {
        if (lessonReviewMode) return
        val a = getCurrentActivity() ?: return
        isAnswerChecked = true
        pendingAnswerCorrect = when (a.type) {
            ActivityType.COMMAND_SEQUENCE ->
                CommandSequencePlayback.sequenceMeetsObjective(
                    a.playbackBoardConfig(),
                    activityCommandSlots
                )
            ActivityType.FILL_IN_BLANK ->
                if (a.isMultiBlankCode()) a.multiBlankAnswersMatch(multiBlankAnswers) else a.fillInAnswerMatches(fillInAnswer)
            ActivityType.MULTIPLE_CHOICE,
            ActivityType.OUTPUT_TRACING,
            ActivityType.DEBUG_CODE ->
                pendingSubmittedIndex >= 0 && pendingSubmittedIndex == a.correctAnswerIndex
        }
        if (a.type == ActivityType.COMMAND_SEQUENCE) {
            // Command-sequence: run process playback immediately after Check.
            showProcessRevealFromFeedback()
            return
        }
        if (!pendingAnswerCorrect) {
            lessonOneWrongAttempts += 1
            canRetry = !wrongAttemptsDepleted()
        } else {
            isActivityCorrect = true
            canRetry = false
        }
        lessonInteractionState = LessonInteractionState.FEEDBACK
    }

    fun attemptsRemaining(): Int =
        (maxWrongAttempts - lessonOneWrongAttempts).coerceAtLeast(0)

    fun wrongAttemptsDepleted(): Boolean =
        lessonOneWrongAttempts >= maxWrongAttempts

    fun showProcessRevealFromFeedback() {
        if (lessonReviewMode) return
        val a = getCurrentActivity()
        if (a?.isTicLesson1MultipleChoice() == true) return
        if (a?.type == ActivityType.COMMAND_SEQUENCE) {
            commandPlaybackCommandsSnapshot = activityCommandSlots.mapNotNull { it }
            commandPlaybackUsesReferenceSolution = false
            val cfg = a.playbackBoardConfig()
            commandPlaybackResults = CommandSequencePlayback.simulate(cfg, commandPlaybackCommandsSnapshot)
            commandPlaybackGeneration += 1
            currentProcessStepIndex = 0
        } else {
            currentProcessStepIndex = 0
        }
        lessonInteractionState = LessonInteractionState.PROCESS_REVEAL
    }

    /** Re-run the same command playback from reset board state while staying on PROCESS_REVEAL. */
    fun requestCommandPlaybackReplay() {
        if (lessonInteractionState != LessonInteractionState.PROCESS_REVEAL) return
        val a = getCurrentActivity() ?: return
        if (a.type != ActivityType.COMMAND_SEQUENCE) return
        if (commandPlaybackCommandsSnapshot.isEmpty()) return
        commandPlaybackGeneration += 1
    }

    /** Play the instructor/reference sequence in process reveal so the learner can compare paths. */
    fun requestCorrectCommandPlayback() {
        if (lessonInteractionState != LessonInteractionState.PROCESS_REVEAL) return
        val a = getCurrentActivity() ?: return
        if (a.type != ActivityType.COMMAND_SEQUENCE) return
        if (a.correctSequence.isEmpty()) return
        commandPlaybackCommandsSnapshot = a.correctSequence.map { normalizeCommandToken(it) }
        commandPlaybackUsesReferenceSolution = true
        val cfg = a.playbackBoardConfig()
        commandPlaybackResults = CommandSequencePlayback.simulate(cfg, commandPlaybackCommandsSnapshot)
        commandPlaybackGeneration += 1
    }

    fun nextProcessStep() {
        val steps = currentRevealSteps()
        if (currentProcessStepIndex < steps.lastIndex) {
            currentProcessStepIndex += 1
        }
    }

    fun showFinalResultState(playbackSummaryOverride: String? = null) {
        this.playbackSummaryOverride = playbackSummaryOverride
        lessonInteractionState = LessonInteractionState.FINAL_RESULT
    }

    fun proceedAfterFinalResult() {
        if (lessonReviewMode) return
        grantPendingCorrectActivityXpIfApplicable()
        moveToNextLessonActivityOrCompleteBonus()
    }

    fun lesson1TryAgainAfterWrong() {
        if (lessonReviewMode) return
        if (!canRetry || isAnswerRevealed || isActivityCompleted) return
        val a = getCurrentActivity()
        lessonInteractionState = LessonInteractionState.ACTIVITY
        pendingSubmittedIndex = -1
        pendingAnswerCorrect = false
        isAnswerChecked = false
        currentBlankIndex = 0
        if (a?.isMultiBlankCode() == true) {
            // Keep existing answers so the user can review and edit individual blanks
        } else {
            fillInAnswer = ""
            multiBlankAnswers = List(a?.codeBlanks?.size ?: 0) { "" }
        }
    }

    fun lesson1OpenCorrectAnswerReveal() {
        if (lessonReviewMode || pendingAnswerCorrect || !isAnswerChecked) return
        hasViewedCorrectAnswer = true
        isAnswerRevealed = true
        canRetry = false
        markCurrentActivityCompletedIncorrect()
        lessonInteractionState = LessonInteractionState.LESSON1_ANSWER_REVEAL
    }

    fun skipCurrentActivityAfterWrong() {
        if (lessonReviewMode || pendingAnswerCorrect || !isAnswerChecked) return
        canRetry = false
        markCurrentActivityCompletedIncorrect()
        moveToNextLessonActivityOrCompleteBonus()
    }

    fun lesson1ProceedAfterCorrectFeedback() {
        if (lessonReviewMode) return
        grantPendingCorrectActivityXpIfApplicable()
        moveToNextLessonActivityOrCompleteBonus()
    }

    /** After viewing the correct answer (no EXP for failed attempt). */
    fun lesson1ProceedAfterRevealExplanation() {
        if (lessonReviewMode) return
        moveToNextLessonActivityOrCompleteBonus()
    }

    private fun buildCompletionRecord(a: ActivityItem): ActivityCompletionRecord {
        val playbackSummary = playbackSummaryOverride ?: when (a.type) {
            ActivityType.COMMAND_SEQUENCE -> {
                val results = commandPlaybackResults
                if (results.isEmpty()) null
                else CommandSequencePlayback.buildPlaybackFinalSummary(
                    pendingAnswerMatchedKey = pendingAnswerCorrect,
                    results = results,
                    finalRemainingCount = results.last().remainingTargetsAfter.size
                )
            }
            else -> null
        }
        return ActivityCompletionRecord(
            activityId = a.id,
            correct = true,
            xpGranted = a.xpReward,
            selectedMcIndex = if (a.requiresMultipleChoice()) pendingSubmittedIndex.takeIf { it >= 0 } else null,
            fillInAnswer = if (a.type == ActivityType.FILL_IN_BLANK) {
                if (a.isMultiBlankCode()) multiBlankAnswers.joinToString(" | ").takeIf { it.isNotBlank() }
                else fillInAnswer.takeIf { it.isNotBlank() }
            } else null,
            commandTokens = if (a.type == ActivityType.COMMAND_SEQUENCE) {
                activityCommandSlots.mapNotNull { it }
            } else {
                null
            },
            playbackSummary = playbackSummary
        )
    }

    private fun buildIncorrectCompletionRecord(a: ActivityItem): ActivityCompletionRecord =
        ActivityCompletionRecord(
            activityId = a.id,
            correct = false,
            xpGranted = 0,
            selectedMcIndex = if (a.requiresMultipleChoice()) pendingSubmittedIndex.takeIf { it >= 0 } else null,
            fillInAnswer = if (a.type == ActivityType.FILL_IN_BLANK) {
                if (a.isMultiBlankCode()) multiBlankAnswers.joinToString(" | ").takeIf { it.isNotBlank() }
                else fillInAnswer.takeIf { it.isNotBlank() }
            } else null
        )

    private fun markCurrentActivityCompletedIncorrect() {
        val a = getCurrentActivity() ?: return
        if (a.type == ActivityType.COMMAND_SEQUENCE) return
        if (a.id in completedActivityIds && !demoModeEnabled) return

        activityCompletionRecords = activityCompletionRecords + (a.id to buildIncorrectCompletionRecord(a))
        completedActivityIds = completedActivityIds + a.id
        isActivityCompleted = true
        isActivityCorrect = false
        syncExpTotalsFromCompletionRecords()
        saveProgress()
    }

    private fun grantPendingCorrectActivityXpIfApplicable() {
        val a = getCurrentActivity() ?: return
        if (!pendingAnswerCorrect) return
        if (a.id in completedActivityIds && !demoModeEnabled) return

        val record = buildCompletionRecord(a)
        activityCompletionRecords = activityCompletionRecords + (a.id to record)

        completedActivityIds = completedActivityIds + a.id
        isActivityCompleted = true
        isActivityCorrect = true
        canRetry = false
        sessionXpEarned += a.xpReward
        syncExpTotalsFromCompletionRecords()
        lessonCorrectThisSession += 1
        if (a.type == ActivityType.DEBUG_CODE) {
            debugCorrectCount += 1
        }
        checkAchievementsAfterActivity(a)
        saveProgress()
    }

    private fun moveToNextLessonActivityOrCompleteBonus() {
        lessonOneWrongAttempts = 0
        val activities = getActivitiesForCurrentLesson()
        if (currentActivityIndex < activities.lastIndex) {
            currentActivityIndex += 1
            resetInteractionForActivity()
            lessonInteractionState = LessonInteractionState.ACTIVITY
            return
        }
        val lesson = getSelectedLesson() ?: return
        val course = getSelectedCourse() ?: return
        syncExpTotalsFromCompletionRecords()
        applyLessonCompletionRewards(
            lesson = lesson,
            course = course,
            questionsCorrect = lessonCorrectThisSession,
            totalActivities = lesson.activities.size
        )
    }

    fun retryCurrentActivity() {
        if (lessonReviewMode) return
        if (!canRetry || isAnswerRevealed || isActivityCompleted) return
        resetInteractionForActivity()
        lessonInteractionState = LessonInteractionState.ACTIVITY
    }

    /**
     * After reviewing a completed command-sequence playback, go to the next unfinished question
     * or return to the course detail screen.
     */
    fun continueFromCompletedReviewToNextOrExit() {
        if (!lessonReviewMode) return
        val lesson = getSelectedLesson() ?: run {
            lessonReviewMode = false
            backFromLessonActivity()
            return
        }
        lessonReviewMode = false
        playbackSummaryOverride = null
        val nextIdx = firstIncompleteActivityIndex(lesson)
        if (nextIdx < 0) {
            resetInteractionForActivity()
            selectedLessonId = null
            currentScreen = AppScreen.COURSE_DETAIL
            return
        }
        currentActivityIndex = nextIdx
        resetInteractionForActivity()
        lessonInteractionState = LessonInteractionState.ACTIVITY
    }

    /**
     * Opens a finished activity in read-only review (playback / chosen answers). No EXP.
     */
    fun openCompletedActivityReview(courseId: String, lessonId: String, activityIndex: Int): Boolean {
        val lesson = LocalContentRepository.lessonById(lessonId) ?: return false
        if (lesson.courseId != courseId) return false
        val act = lesson.activities.getOrNull(activityIndex) ?: return false
        if (act.id !in completedActivityIds) return false
        val record = activityCompletionRecords[act.id] ?: return false

        lessonEntryBlockedNotice = null
        activeCourseId = courseId
        selectedCourseId = courseId
        selectedLessonId = lessonId
        courseDetailFocusLessonId = lessonId
        currentActivityIndex = activityIndex
        lessonReviewMode = true
        hasViewedCorrectAnswer = !record.correct
        isAnswerRevealed = !record.correct
        isActivityCompleted = true
        isActivityCorrect = record.correct
        canRetry = false
        lessonOneWrongAttempts = 0
        sessionXpEarned = 0
        lessonCorrectThisSession = 0

        when (act.type) {
            ActivityType.COMMAND_SEQUENCE -> {
                val cmds = record.commandTokens ?: return false
                activityCommandSlots = cmds.map { it }
                commandPlaybackCommandsSnapshot = cmds
                commandPlaybackUsesReferenceSolution = false
                val cfg = act.playbackBoardConfig()
                commandPlaybackResults = CommandSequencePlayback.simulate(cfg, cmds)
                commandPlaybackGeneration += 1
                playbackSummaryOverride = record.playbackSummary
                pendingAnswerCorrect = true
                isAnswerChecked = true
                lessonInteractionState = LessonInteractionState.PROCESS_REVEAL
            }
            ActivityType.FILL_IN_BLANK -> {
                fillInAnswer = record.fillInAnswer.orEmpty()
                multiBlankAnswers = if (act.isMultiBlankCode()) {
                    val parts = fillInAnswer.split(" | ")
                    List(act.codeBlanks.size) { index -> parts.getOrNull(index).orEmpty() }
                } else {
                    emptyList()
                }
                currentBlankIndex = 0
                pendingAnswerCorrect = record.correct
                isAnswerChecked = true
                lessonInteractionState = LessonInteractionState.FEEDBACK
            }
            ActivityType.MULTIPLE_CHOICE,
            ActivityType.OUTPUT_TRACING,
            ActivityType.DEBUG_CODE -> {
                pendingSubmittedIndex = record.selectedMcIndex ?: -1
                pendingAnswerCorrect = record.correct
                isAnswerChecked = true
                lessonInteractionState = LessonInteractionState.FEEDBACK
            }
        }
        currentScreen = AppScreen.LESSON_ACTIVITY
        return true
    }

    private fun applyLessonCompletionRewards(
        lesson: Lesson,
        course: Course,
        questionsCorrect: Int,
        totalActivities: Int
    ) {
        completedActivityIds = completedActivityIds + lesson.activities.map { it.id }.toSet()
        completedLessonIds = completedLessonIds + lesson.id
        val doneCount = course.lessons.count { it.id in completedLessonIds }
        courseCompletedLessonCounts[course.id] = doneCount
        unlockNextLesson(course.id, lesson.id)
        if (course.lessons.all { it.id in completedLessonIds }) {
            completedCourseIds = completedCourseIds + course.id
            unlockNextCourse(course.id)
        }
        activeCourseId = course.id
        updateBadges()
        checkAchievementsAfterLesson(
            course = course,
            questionsCorrect = questionsCorrect,
            totalActivities = totalActivities
        )
        result = LessonSessionResult(
            courseId = course.id,
            lessonId = lesson.id,
            courseTitle = course.title,
            lessonTitle = lesson.title,
            correctCount = questionsCorrect,
            totalActivities = totalActivities,
            xpEarned = sessionXpEarned
        )
        saveProgress()
        currentScreen = AppScreen.RESULT
    }

    /**
     * After the PERFECT / GOOD EFFORT animation (or skipping it), returns to this course's
     * Course Detail lesson path — not Home.
     */
    fun returnToCourseDetailAfterLessonCompletion() {
        val r = result ?: return
        val course = getCourse(r.courseId) ?: return
        activeCourseId = course.id
        selectedCourseId = course.id
        val next = LocalContentRepository.nextLessonInCourse(course.id, r.lessonId)
        courseDetailFocusLessonId = when {
            next != null && isLessonUnlocked(next.id) -> next.id
            else -> pickDefaultFocusLesson(course)
        }
        result = null
        currentActivityIndex = 0
        sessionXpEarned = 0
        lessonCorrectThisSession = 0
        lessonOneWrongAttempts = 0
        selectedLessonId = null
        resetInteractionForActivity()
        currentScreen = AppScreen.COURSE_DETAIL
    }

    private fun unlockNextLesson(courseId: String, completedLessonId: String) {
        val next = LocalContentRepository.nextLessonInCourse(courseId, completedLessonId)
        if (next != null) {
            unlockedLessonIds = unlockedLessonIds + next.id
        }
    }

    private fun unlockNextCourse(completedCourseId: String) {
        val next = LocalContentRepository.nextCourseAfter(completedCourseId) ?: return
        unlockedCourseIds = unlockedCourseIds + next.id
        val firstLesson = next.lessons.minByOrNull { it.order }
        if (firstLesson != null) {
            unlockedLessonIds = unlockedLessonIds + firstLesson.id
        }
    }

    fun continueLearning() {
        val target = getActiveCourseForHome()
        if (target != null) {
            openCourseDetail(target.id)
        } else {
            ensureDefaultActiveCourse()
            openCourseDetail(activeCourseId)
        }
    }

    fun backFromLessonActivity() {
        lessonReviewMode = false
        resetInteractionForActivity()
        sessionXpEarned = 0
        lessonCorrectThisSession = 0
        lessonOneWrongAttempts = 0
        selectedLessonId = null
        currentScreen = AppScreen.COURSE_DETAIL
    }

    fun goHome() {
        selectedTab = AppTab.HOME
        currentScreen = AppScreen.MAIN_TABS
        selectedCourseId = null
        selectedLessonId = null
        courseDetailFocusLessonId = null
    }

    fun goToLearningPath() {
        selectedTab = AppTab.QUESTS
        currentScreen = AppScreen.MAIN_TABS
        selectedCourseId = null
        selectedLessonId = null
        courseDetailFocusLessonId = null
    }

    fun resultContinueNextLesson() {
        val r = result ?: run {
            goHome()
            return
        }
        val course = getCourse(r.courseId) ?: run {
            goHome()
            return
        }
        val next = LocalContentRepository.nextLessonInCourse(course.id, r.lessonId)
        result = null
        selectedCourseId = course.id
        activeCourseId = course.id
        if (next != null && isLessonUnlocked(next.id)) {
            courseDetailFocusLessonId = next.id
            currentScreen = AppScreen.COURSE_DETAIL
        } else {
            courseDetailFocusLessonId = pickDefaultFocusLesson(course)
            currentScreen = AppScreen.COURSE_DETAIL
        }
    }

    fun openNextCourseFromResult() {
        val r = result ?: run {
            goHome()
            return
        }
        val nextCourse = LocalContentRepository.nextCourseAfter(r.courseId)
        result = null
        if (nextCourse != null && isCourseUnlocked(nextCourse.id)) {
            openCourseDetail(nextCourse.id)
        } else {
            goToLearningPath()
        }
    }

    fun reviewCurrentLessonActivities() {
        val r = result
        val lessonId = r?.lessonId ?: selectedLessonId
        if (lessonId == null) return
        val lesson = LocalContentRepository.lessonById(lessonId) ?: return
        if (lesson.id in completedLessonIds) {
            completedActivityIds = completedActivityIds + lesson.activities.map { it.id }.toSet()
        }
        if (demoModeEnabled) {
            completedActivityIds = completedActivityIds - lesson.activities.map { it.id }.toSet()
            completedLessonIds = completedLessonIds - lessonId
        }
        val resumeIdx = lesson.activities.indexOfFirst { it.id !in completedActivityIds }
        if (resumeIdx < 0 && !demoModeEnabled) {
            lessonEntryBlockedNotice =
                "You already completed every question in this lesson. Retakes are disabled."
            result = null
            val cid = r?.courseId ?: selectedCourseId ?: activeCourseId
            selectedCourseId = cid
            activeCourseId = cid
            courseDetailFocusLessonId = lessonId
            selectedLessonId = null
            currentScreen = AppScreen.COURSE_DETAIL
            return
        }
        result = null
        selectedLessonId = lessonId
        selectedCourseId = r?.courseId ?: selectedCourseId
        activeCourseId = r?.courseId ?: activeCourseId
        lessonReviewMode = false
        currentActivityIndex = resumeIdx
        sessionXpEarned = 0
        lessonCorrectThisSession = 0
        lessonOneWrongAttempts = 0
        resetInteractionForActivity()
        initCommandSlots()
        applyRobotDemoGuideIfNeeded(lessonId, if (resumeIdx == 0) 0 else resumeIdx)
        currentScreen = AppScreen.LESSON_ACTIVITY
    }

    fun hasPendingAchievements(): Boolean = pendingUnlockedAchievements.isNotEmpty()

    fun peekPendingAchievement(): Achievement? = pendingUnlockedAchievements.firstOrNull()

    fun consumeNextPendingAchievement() {
        if (pendingUnlockedAchievements.isNotEmpty()) {
            pendingUnlockedAchievements = pendingUnlockedAchievements.drop(1)
        }
    }

    fun earnedAchievementsForDisplay(): List<Achievement> =
        LocalContentRepository.achievements.filter { it.id in earnedAchievementIds }

    private fun grantAchievementIfNew(achievementId: String) {
        if (achievementId in earnedAchievementIds) return
        val achievement = LocalContentRepository.achievementById(achievementId) ?: return
        earnedAchievementIds = earnedAchievementIds + achievementId
        pendingUnlockedAchievements = pendingUnlockedAchievements + achievement
        syncBadgeForAchievement(achievementId)
        val updates = badgeProgress.toMutableMap()
        updates[achievementId] = 1
        badgeProgress = updates
    }

    private fun syncBadgeForAchievement(achievementId: String) {
        when (achievementId) {
            "first-steps", "thinking-coder" -> earnedBadgeIds = earnedBadgeIds + achievementId
        }
    }

    private fun checkAchievementsAfterActivity(activity: ActivityItem) {
        if (completedActivityIds.isNotEmpty()) {
            grantAchievementIfNew("first-steps")
        }
        if (activity.type == ActivityType.DEBUG_CODE || activity.type == ActivityType.FILL_IN_BLANK) {
            grantAchievementIfNew("debug-learner")
        }
        if (activity.isRedTargetCommandSequence()) {
            grantAchievementIfNew("red-target-finder")
        }
    }

    private fun checkAchievementsAfterLesson(
        course: Course,
        questionsCorrect: Int,
        totalActivities: Int
    ) {
        if (completedLessonIds.isNotEmpty() || completedActivityIds.isNotEmpty()) {
            grantAchievementIfNew("first-steps")
        }
        if (totalActivities > 0 && questionsCorrect == totalActivities) {
            grantAchievementIfNew("perfect-start")
        }
        if (course.id == "thinking-in-code" &&
            course.lessons.all { it.id in completedLessonIds }
        ) {
            grantAchievementIfNew("thinking-coder")
        }
        if ("tic-l1" in completedLessonIds) {
            grantAchievementIfNew("program-reader")
        }
        // Programming with Variables – per-lesson achievements
        if ("pvar-l1" in completedLessonIds) {
            grantAchievementIfNew("pvar-lesson1")
        }
        if ("pvar-l2" in completedLessonIds) {
            grantAchievementIfNew("naming-pro")
        }
        if ("pvar-l3" in completedLessonIds) {
            grantAchievementIfNew("type-detective")
        }
        if ("pvar-l4" in completedLessonIds) {
            grantAchievementIfNew("value-updater")
        }
        if (course.id == "programming-variables" &&
            course.lessons.all { it.id in completedLessonIds }
        ) {
            grantAchievementIfNew("variable-master")
        }
        if ("tp-l1" in completedLessonIds) {
            grantAchievementIfNew("python-printer")
        }
        if ("tp-l2" in completedLessonIds) {
            grantAchievementIfNew("order-reader")
        }
        if ("tp-l3" in completedLessonIds) {
            grantAchievementIfNew("comment-helper")
        }
        if ("tp-l4" in completedLessonIds) {
            grantAchievementIfNew("error-fixer")
        }
        if (course.id == "thinking-python" &&
            course.lessons.all { it.id in completedLessonIds }
        ) {
            grantAchievementIfNew("python-thinker")
        }
        if ("pio-l1" in completedLessonIds) {
            grantAchievementIfNew("output-beginner")
        }
        if ("pio-l2" in completedLessonIds) {
            grantAchievementIfNew("print-master")
        }
        if ("pio-l3" in completedLessonIds) {
            grantAchievementIfNew("input-explorer")
        }
        if ("pio-l4" in completedLessonIds) {
            grantAchievementIfNew("ipo-learner")
        }
        if (course.id == "python-input-output" &&
            course.lessons.all { it.id in completedLessonIds }
        ) {
            grantAchievementIfNew("input-output-champion")
        }
        if ("pc-l1" in completedLessonIds) {
            grantAchievementIfNew("condition-beginner")
        }
        if ("pc-l2" in completedLessonIds) {
            grantAchievementIfNew("if-starter")
        }
        if ("pc-l3" in completedLessonIds) {
            grantAchievementIfNew("else-explorer")
        }
        if ("pc-l4" in completedLessonIds) {
            grantAchievementIfNew("compare-coder")
        }
        if (course.id == "python-conditions" &&
            course.lessons.all { it.id in completedLessonIds }
        ) {
            grantAchievementIfNew("condition-master")
        }
        val visibleCourseIds = getCourses().map { it.id }.toSet()
        if (visibleCourseIds.all { it in completedCourseIds }) {
            grantAchievementIfNew("python-path-finisher")
        }
    }

    private fun updateBadges() {
        val updates = badgeProgress.toMutableMap()
        if (completedLessonIds.isNotEmpty()) {
            earnedBadgeIds = earnedBadgeIds + "first-steps"
            updates["first-steps"] = 1
        }
        if (completedCourseIds.contains("thinking-in-code")) {
            earnedBadgeIds = earnedBadgeIds + "thinking-coder"
            updates["thinking-coder"] = 1
        }
        if (completedCourseIds.contains("programming-variables")) {
            earnedBadgeIds = earnedBadgeIds + "variable-starter"
            updates["variable-starter"] = 1
        }
        if (completedCourseIds.contains("programming-functions")) {
            earnedBadgeIds = earnedBadgeIds + "function-builder"
            updates["function-builder"] = 1
        }
        if (completedCourseIds.contains("algorithmic-thinking")) {
            earnedBadgeIds = earnedBadgeIds + "algorithm-explorer"
            updates["algorithm-explorer"] = 1
        }
        if (completedCourseIds.contains("cs-fundamentals")) {
            earnedBadgeIds = earnedBadgeIds + "cs-rookie"
            updates["cs-rookie"] = 1
        }
        if (completedCourseIds.contains("neural-intro")) {
            earnedBadgeIds = earnedBadgeIds + "neural-beginner"
            updates["neural-beginner"] = 1
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
        saveProgress()
        backToProfileFromSettings()
    }

    fun setTheme(theme: String) {
        selectedTheme = theme
        saveProgress()
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

    fun applyStudentSession(user: AppUser) {
        resetStudentPlayProgress()
        currentUserId = user.id
        loadProgressFromPrefs()
        username = user.fullName.ifBlank { "Coder!" }
        selectedTab = AppTab.HOME
        currentScreen = AppScreen.MAIN_TABS
        showOnboarding = !hasSeenOnboarding
        onboardingStepIndex = 0
    }

    fun resetSessionIdentity() {
        username = "Coder!"
        roleTitle = "Junior Debugger"
    }

    /**
     * Clears session progress so a newly logged-in student starts like a fresh install:
     * no EXP, no streak, first lesson unlocked only, no completed lessons/badges.
     */
    fun resetStudentPlayProgress() {
        selectedTab = AppTab.HOME
        currentScreen = AppScreen.MAIN_TABS
        selectedCourseId = null
        selectedLessonId = null
        courseDetailFocusLessonId = null
        previousTabBeforeFlow = null
        detailParentTab = null
        currentActivityIndex = 0
        lessonInteractionState = LessonInteractionState.ACTIVITY
        currentProcessStepIndex = 0
        pendingAnswerCorrect = false
        pendingSubmittedIndex = -1
        fillInAnswer = ""
        isAnswerChecked = false
        hasViewedCorrectAnswer = false
        isAnswerRevealed = false
        isActivityCompleted = false
        isActivityCorrect = false
        canRetry = true
        activityCommandSlots = emptyList()
        commandPlaybackCommandsSnapshot = emptyList()
        commandPlaybackUsesReferenceSolution = false
        commandPlaybackResults = emptyList()
        commandPlaybackGeneration = 0
        playbackSummaryOverride = null
        commandSequenceSuccessBalloonsShownForAttempt = false
        totalXP = 0
        streakDays = 0
        debugCorrectCount = 0
        lessonCorrectThisSession = 0
        lessonOneWrongAttempts = 0
        hasViewedCorrectAnswer = false
        sessionXpEarned = 0
        result = null
        previousTabBeforeNotifications = null
        showOnboarding = false
        onboardingStepIndex = 0
        activeCourseId = "thinking-in-code"
        completedLessonIds = emptySet()
        completedActivityIds = emptySet()
        activityCompletionRecords = emptyMap()
        lessonReviewMode = false
        completedCourseIds = emptySet()
        lessonEntryBlockedNotice = null
        demoModeEnabled = false
        ticL2RobotGuideSeen = false
        robotDemoGuideStep = 0
        unlockedCourseIds = setOf("thinking-in-code")
        unlockedLessonIds = setOf("tic-l1")
        earnedBadgeIds = emptySet()
        earnedAchievementIds = emptySet()
        pendingUnlockedAchievements = emptyList()
        badgeProgress = mapOf(
            "first-steps" to 0,
            "thinking-coder" to 0,
            "variable-starter" to 0,
            "function-builder" to 0,
            "algorithm-explorer" to 0,
            "cs-rookie" to 0,
            "neural-beginner" to 0
        )
        courseLearningXp = courseOrder.associate { it.id to 0 }.toMutableMap()
        courseCompletedLessonCounts = courseOrder.associate { it.id to 0 }.toMutableMap()
    }

    fun resetAllProgressForDebug() {
        val uid = currentUserId
        resetStudentPlayProgress()
        if (uid.isNotEmpty()) {
            CodeQuestPreferences.progressPrefs(uid).edit().clear().apply()
        }
    }
}
