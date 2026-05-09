package com.example.codequest.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.codequest.data.LocalContentRepository
import com.example.codequest.model.ActivityItem
import com.example.codequest.model.ActivityType
import com.example.codequest.model.isTicLesson1MultipleChoice
import com.example.codequest.model.CommandSequencePlayback
import com.example.codequest.model.PlaybackStepResult
import com.example.codequest.model.playbackBoardConfig
import com.example.codequest.model.effectiveProcessSteps
import com.example.codequest.model.normalizeCommandToken
import com.example.codequest.model.slotCount
import com.example.codequest.model.Badge
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

class CodeQuestAppState {
    var selectedTab by mutableStateOf(AppTab.HOME)
        private set

    var currentScreen by mutableStateOf(AppScreen.MAIN_TABS)
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

    var currentProcessStepIndex by mutableIntStateOf(0)
        private set

    var pendingAnswerCorrect by mutableStateOf(false)
        private set

    /** MC / trace: selected option index. Command tasks: unused for selection. */
    var pendingSubmittedIndex by mutableIntStateOf(-1)
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

    var totalXP by mutableIntStateOf(50)
        private set

    var streakDays by mutableIntStateOf(7)
        private set

    var debugCorrectCount by mutableIntStateOf(0)
        private set

    var lessonCorrectThisSession by mutableIntStateOf(0)
        private set

    /** Wrong Check submissions for Thinking in Code Lesson 1 MC (max 3); reset per question. */
    var lessonOneWrongAttempts by mutableIntStateOf(0)
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
                message = "Solve today’s logic puzzle and earn bonus XP.",
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

    private val courseOrder: List<Course> = LocalContentRepository.courses.sortedBy { it.order }

    var activeCourseId by mutableStateOf("thinking-in-code")
        private set

    var completedLessonIds by mutableStateOf(setOf<String>())
        private set

    var completedCourseIds by mutableStateOf(setOf<String>())
        private set

    var unlockedCourseIds by mutableStateOf(setOf("thinking-in-code"))
        private set

    var unlockedLessonIds by mutableStateOf(setOf("tic-l1"))
        private set

    var earnedBadgeIds by mutableStateOf(emptySet<String>())
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

    fun getCourse(courseId: String): Course? = courseOrder.firstOrNull { it.id == courseId }

    fun getSelectedCourse(): Course? = selectedCourseId?.let { getCourse(it) }

    fun getSelectedLesson(): Lesson? = selectedLessonId?.let { LocalContentRepository.lessonById(it) }

    fun getActivitiesForCurrentLesson(): List<ActivityItem> =
        getSelectedLesson()?.activities.orEmpty()

    fun getCurrentActivity(): ActivityItem? =
        getActivitiesForCurrentLesson().getOrNull(currentActivityIndex)

    fun currentRevealSteps(): List<com.example.codequest.model.ProcessStep> {
        val a = getCurrentActivity() ?: return emptyList()
        return a.effectiveProcessSteps(pendingAnswerCorrect)
    }

    fun selectCourseDetailLesson(lessonId: String) {
        if (lessonId in unlockedLessonIds) {
            courseDetailFocusLessonId = lessonId
        }
    }

    private fun pickDefaultFocusLesson(course: Course): String? {
        val sorted = course.lessons.sortedBy { it.order }
        val next = sorted.firstOrNull {
            it.id in unlockedLessonIds && it.id !in completedLessonIds
        }
        return next?.id ?: sorted.firstOrNull { it.id in unlockedLessonIds }?.id
    }

    fun lessonLevelDisplay(lessonId: String, courseId: String): Int {
        val course = getCourse(courseId) ?: return 1
        val idx = course.lessons.sortedBy { it.order }.indexOfFirst { it.id == lessonId }
        return if (idx >= 0) idx + 1 else 1
    }

    fun getActiveTargetLesson(): Lesson? {
        val course = getCourse(activeCourseId) ?: return null
        val sorted = course.lessons.sortedBy { it.order }
        return sorted.firstOrNull { lid ->
            lid.id !in completedLessonIds && lid.id in unlockedLessonIds
        }
    }

    fun getActiveCourseForHome(): Course? = getCourse(activeCourseId)

    fun ensureDefaultActiveCourse() {
        if (getCourse(activeCourseId) == null) {
            activeCourseId = "thinking-in-code"
        }
    }

    fun openCourseDetail(courseId: String): Boolean {
        if (courseId !in unlockedCourseIds) return false
        if (currentScreen == AppScreen.MAIN_TABS) {
            previousTabBeforeFlow = selectedTab
        }
        selectedCourseId = courseId
        val c = getCourse(courseId)
        courseDetailFocusLessonId = c?.let { pickDefaultFocusLesson(it) }
        currentScreen = AppScreen.COURSE_DETAIL
        return true
    }

    fun backFromCourseDetail() {
        selectedCourseId = null
        courseDetailFocusLessonId = null
        selectedTab = previousTabBeforeFlow ?: AppTab.QUESTS
        currentScreen = AppScreen.MAIN_TABS
    }

    fun startLessonFromCourseDetail(): Boolean {
        val courseId = selectedCourseId ?: return false
        val lessonId = courseDetailFocusLessonId ?: pickDefaultFocusLesson(getCourse(courseId) ?: return false) ?: return false
        if (lessonId !in unlockedLessonIds) return false
        val lesson = LocalContentRepository.lessonById(lessonId) ?: return false
        if (lesson.courseId != courseId) return false
        if (lesson.activities.isEmpty()) {
            completeTextOnlyLessonFromCourse(courseId, lessonId)
            return true
        }
        activeCourseId = courseId
        selectedLessonId = lessonId
        currentActivityIndex = 0
        sessionXpEarned = 0
        lessonCorrectThisSession = 0
        lessonOneWrongAttempts = 0
        resetInteractionForActivity()
        initCommandSlots()
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
        val idx = activityCommandSlots.indexOfFirst { it == null }
        if (idx < 0) return
        val copy = activityCommandSlots.toMutableList()
        copy[idx] = command
        activityCommandSlots = copy
    }

    fun assignCommandToSlot(slotIndex: Int, command: String) {
        if (slotIndex !in activityCommandSlots.indices) return
        val copy = activityCommandSlots.toMutableList()
        copy[slotIndex] = command
        activityCommandSlots = copy
    }

    fun clearCommandSlot(slotIndex: Int) {
        if (slotIndex !in activityCommandSlots.indices) return
        val copy = activityCommandSlots.toMutableList()
        copy[slotIndex] = null
        activityCommandSlots = copy
    }

    fun clearAllCommandSlots() {
        initCommandSlots()
    }

    fun selectMcOption(answerIndex: Int) {
        pendingSubmittedIndex = answerIndex
    }

    fun activityReadyForCheck(): Boolean {
        val a = getCurrentActivity() ?: return false
        return when (a.type) {
            ActivityType.COMMAND_SEQUENCE ->
                activityCommandSlots.isNotEmpty() && activityCommandSlots.all { it != null }
            else -> pendingSubmittedIndex >= 0 && a.correctAnswerIndex >= 0
        }
    }

    private fun resetInteractionForActivity() {
        lessonInteractionState = LessonInteractionState.ACTIVITY
        currentProcessStepIndex = 0
        pendingAnswerCorrect = false
        pendingSubmittedIndex = -1
        isAnswerChecked = false
        commandPlaybackCommandsSnapshot = emptyList()
        commandPlaybackUsesReferenceSolution = false
        commandPlaybackResults = emptyList()
        playbackSummaryOverride = null
        commandPlaybackGeneration = 0
        initCommandSlots()
    }

    fun submitActivityCheck() {
        val a = getCurrentActivity() ?: return
        if (a.isTicLesson1MultipleChoice()) {
            isAnswerChecked = true
            val correct = pendingSubmittedIndex >= 0 && pendingSubmittedIndex == a.correctAnswerIndex
            pendingAnswerCorrect = correct
            if (!correct) {
                lessonOneWrongAttempts += 1
            }
            lessonInteractionState = LessonInteractionState.FEEDBACK
            return
        }
        isAnswerChecked = true
        pendingAnswerCorrect = when (a.type) {
            ActivityType.COMMAND_SEQUENCE ->
                CommandSequencePlayback.sequenceMeetsObjective(
                    a.playbackBoardConfig(),
                    activityCommandSlots
                )
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
        lessonInteractionState = LessonInteractionState.FEEDBACK
    }

    fun showProcessRevealFromFeedback() {
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
        grantPendingCorrectActivityXpIfApplicable()
        moveToNextLessonActivityOrCompleteBonus()
    }

    fun lesson1TryAgainAfterWrong() {
        lessonInteractionState = LessonInteractionState.ACTIVITY
        pendingSubmittedIndex = -1
        pendingAnswerCorrect = false
        isAnswerChecked = false
    }

    fun lesson1OpenCorrectAnswerReveal() {
        lessonInteractionState = LessonInteractionState.LESSON1_ANSWER_REVEAL
    }

    fun lesson1ProceedAfterCorrectFeedback() {
        grantPendingCorrectActivityXpIfApplicable()
        moveToNextLessonActivityOrCompleteBonus()
    }

    /** After viewing the correct answer (no XP for failed attempt). */
    fun lesson1ProceedAfterRevealExplanation() {
        moveToNextLessonActivityOrCompleteBonus()
    }

    private fun grantPendingCorrectActivityXpIfApplicable() {
        val a = getCurrentActivity() ?: return
        if (!pendingAnswerCorrect) return
        sessionXpEarned += a.xpReward
        totalXP += a.xpReward
        val cid = selectedCourseId ?: ""
        courseLearningXp[cid] = ((courseLearningXp[cid] ?: 0) + a.xpReward).coerceAtMost(500)
        lessonCorrectThisSession += 1
        if (a.type == ActivityType.DEBUG_CODE) {
            debugCorrectCount += 1
        }
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
        val bonusXp = 35
        sessionXpEarned += bonusXp
        totalXP += bonusXp
        courseLearningXp[course.id] = ((courseLearningXp[course.id] ?: 0) + bonusXp).coerceAtMost(500)
        applyLessonCompletionRewards(
            lesson = lesson,
            course = course,
            questionsCorrect = lessonCorrectThisSession,
            totalActivities = lesson.activities.size
        )
    }

    fun retryCurrentActivity() {
        resetInteractionForActivity()
        lessonInteractionState = LessonInteractionState.ACTIVITY
    }

    private fun applyLessonCompletionRewards(
        lesson: Lesson,
        course: Course,
        questionsCorrect: Int,
        totalActivities: Int
    ) {
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
        result = LessonSessionResult(
            courseId = course.id,
            lessonId = lesson.id,
            courseTitle = course.title,
            lessonTitle = lesson.title,
            correctCount = questionsCorrect,
            totalActivities = totalActivities,
            xpEarned = sessionXpEarned
        )
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
            next != null && next.id in unlockedLessonIds -> next.id
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
        ensureDefaultActiveCourse()
        openCourseDetail(activeCourseId)
    }

    fun backFromLessonActivity() {
        currentActivityIndex = 0
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
        if (next != null && next.id in unlockedLessonIds) {
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
        if (nextCourse != null && nextCourse.id in unlockedCourseIds) {
            openCourseDetail(nextCourse.id)
        } else {
            goToLearningPath()
        }
    }

    fun reviewCurrentLessonActivities() {
        result = null
        currentActivityIndex = 0
        sessionXpEarned = 0
        lessonCorrectThisSession = 0
        lessonOneWrongAttempts = 0
        resetInteractionForActivity()
        currentScreen = AppScreen.LESSON_ACTIVITY
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
