package com.example.codequest.data

import com.example.codequest.model.Badge
import com.example.codequest.model.Lesson
import com.example.codequest.model.Question
import com.example.codequest.model.QuestionType
import com.example.codequest.model.Quest

object LocalContentRepository {
    data class AchievementSeed(
        val icon: String,
        val title: String,
        val description: String,
        val date: String
    )

    data class SettingSeed(
        val icon: String,
        val title: String,
        val trailingText: String? = null
    )

    data class CategorySeed(
        val title: String,
        val count: Int
    )

    val quests: List<Quest> = listOf(
        Quest(
            id = "syntax-hall",
            title = "Syntax Hall",
            description = "Master the basics of programming syntax.",
            icon = "\uD83C\uDFDB\uFE0F",
            order = 0,
            lessons = listOf(
                Lesson("syn-1", "syntax-hall", "Variables and Types", "Learn val, var, and basic types.", 0),
                Lesson("syn-2", "syntax-hall", "Functions", "Create and call basic functions.", 1),
                Lesson("syn-3", "syntax-hall", "String Templates", "Use \$variable interpolation.", 2),
                Lesson("syn-4", "syntax-hall", "Condition Basics", "Write basic if/else logic.", 3),
                Lesson("syn-5", "syntax-hall", "Collections Intro", "Understand list and map basics.", 4),
                Lesson("syn-6", "syntax-hall", "Null Safety Intro", "Handle nullable values safely.", 5),
                Lesson("syn-7", "syntax-hall", "Practice Round", "Review syntax in small tasks.", 6)
            )
        ),
        Quest(
            id = "logic-garden",
            title = "Logic Garden",
            description = "Conditions, operators, and boolean logic.",
            icon = "\uD83C\uDF33",
            order = 1,
            lessons = listOf(
                Lesson("log-1", "logic-garden", "If/Else Decisions", "Branch behavior using true/false conditions.", 0),
                Lesson("log-2", "logic-garden", "Boolean Operators", "Use &&, ||, and ! to combine checks.", 1),
                Lesson("log-3", "logic-garden", "Comparison Operators", "Compare values with >, <, >=, <=, ==, !=.", 2),
                Lesson("log-4", "logic-garden", "Nested Conditions", "Build decisions inside decisions.", 3),
                Lesson("log-5", "logic-garden", "Short-Circuit Logic", "Understand evaluation order with && and ||.", 4),
                Lesson("log-6", "logic-garden", "Guard Clauses", "Exit early when conditions fail.", 5),
                Lesson("log-7", "logic-garden", "Logic Practice", "Apply logic in simple scenarios.", 6)
            )
        ),
        Quest(
            id = "loop-tower",
            title = "Loop Tower",
            description = "Learn loops and control the flow.",
            icon = "\uD83D\uDDFC\uFE0F",
            order = 2,
            lessons = listOf(
                Lesson("loop-1", "loop-tower", "For Loops", "Repeat over ranges.", 0),
                Lesson("loop-2", "loop-tower", "While Loops", "Repeat until condition changes.", 1),
                Lesson("loop-3", "loop-tower", "Do-While", "Run once before checking.", 2),
                Lesson("loop-4", "loop-tower", "Break", "Stop loops safely.", 3),
                Lesson("loop-5", "loop-tower", "Continue", "Skip to next iteration.", 4),
                Lesson("loop-6", "loop-tower", "Nested Loops", "Loop inside loop.", 5),
                Lesson("loop-7", "loop-tower", "Loop Practice", "Solve repetition drills.", 6)
            )
        ),
        Quest(
            id = "array-lab",
            title = "Array Lab",
            description = "Work with arrays and collections.",
            icon = "\uD83E\uDDF1",
            order = 3,
            lessons = listOf(
                Lesson("arr-1", "array-lab", "Array Basics", "Access values by index.", 0),
                Lesson("arr-2", "array-lab", "Mutable Lists", "Add and remove items.", 1),
                Lesson("arr-3", "array-lab", "Iteration", "Loop through lists.", 2),
                Lesson("arr-4", "array-lab", "Map and Filter", "Transform and filter data.", 3),
                Lesson("arr-5", "array-lab", "Finding Values", "Use contains and indexOf.", 4),
                Lesson("arr-6", "array-lab", "Sorting", "Order collections.", 5),
                Lesson("arr-7", "array-lab", "Collection Practice", "Apply list operations.", 6)
            )
        ),
        Quest(
            id = "oop-building",
            title = "OOP Building",
            description = "Explore object-oriented programming.",
            icon = "\uD83C\uDFDB\uFE0F",
            order = 4,
            lessons = listOf(
                Lesson("oop-1", "oop-building", "Classes", "Define class blueprints.", 0),
                Lesson("oop-2", "oop-building", "Objects", "Create object instances.", 1),
                Lesson("oop-3", "oop-building", "Properties", "Store object state.", 2),
                Lesson("oop-4", "oop-building", "Methods", "Define object behavior.", 3),
                Lesson("oop-5", "oop-building", "Inheritance", "Reuse class behavior.", 4),
                Lesson("oop-6", "oop-building", "Interfaces", "Define contracts.", 5),
                Lesson("oop-7", "oop-building", "OOP Practice", "Apply class design basics.", 6)
            )
        )
    )

    val questionsByQuestId: Map<String, List<Question>> = mapOf(
        "logic-garden" to listOf(
            Question(
                id = "lg-q1",
                questId = "logic-garden",
                type = QuestionType.MULTIPLE_CHOICE,
                questionText = "Which condition checks if score is between 50 and 100?",
                options = listOf("score > 50 || score < 100", "score >= 50 && score <= 100", "score == 50..100", "score in 50 until 100 && score > 100"),
                correctAnswerIndex = 1,
                explanation = "Use && so both lower and upper checks must be true.",
                hint = "Both conditions must pass."
            ),
            Question(
                id = "lg-q2",
                questId = "logic-garden",
                type = QuestionType.FILL_IN_THE_BLANK,
                questionText = "Fill the operator so mark 70 and above passes.",
                codeBlock = "val passed = mark ____ 70",
                options = listOf(">=", ">", "==", "!="),
                correctAnswerIndex = 0,
                explanation = ">= includes 70 itself."
            ),
            Question(
                id = "lg-q3",
                questId = "logic-garden",
                type = QuestionType.PREDICT_OUTPUT,
                questionText = "Predict output:",
                codeBlock = "val a = true\nval b = false\nprintln(a || b && false)",
                options = listOf("true", "false", "error", "null"),
                correctAnswerIndex = 0,
                explanation = "&& runs first, then ||."
            ),
            Question(
                id = "lg-q4",
                questId = "logic-garden",
                type = QuestionType.DEBUG_CODE,
                questionText = "Fix this to check non-admin users:",
                codeBlock = "if (role = \"admin\") { showAdminPanel() }",
                options = listOf("if (role == \"admin\")", "if (role != \"admin\")", "if (!role == \"admin\")", "if (role.equals)"),
                correctAnswerIndex = 1,
                explanation = "Requirement says non-admin, so use !="
            ),
            Question(
                id = "lg-q5",
                questId = "logic-garden",
                type = QuestionType.MULTIPLE_CHOICE,
                questionText = "Which expression means either premium OR has coupon?",
                options = listOf("isPremium && hasCoupon", "isPremium || hasCoupon", "!isPremium && !hasCoupon", "isPremium == hasCoupon"),
                correctAnswerIndex = 1,
                explanation = "Use OR when either condition is enough."
            )
        )
    )

    val badges: List<Badge> = emptyList()

    val settingsItems = listOf(
        SettingSeed("\uD83D\uDC64", "Edit Profile", null),
        SettingSeed("\uD83C\uDFA8", "Theme", "Cyber"),
        SettingSeed("\uD83D\uDD14", "Notifications", null),
        SettingSeed("\u2753", "Help & Support", null)
    )

    val profileAchievements: List<AchievementSeed> = emptyList()

    val badgeCategories: List<CategorySeed> = emptyList()
}
