package com.example.codequest.data

import com.example.codequest.model.Achievement
import com.example.codequest.model.ActivityItem
import com.example.codequest.model.ActivityType
import com.example.codequest.model.Badge
import com.example.codequest.model.CodeBlank
import com.example.codequest.model.CommandBoardSetup
import com.example.codequest.model.Direction
import com.example.codequest.model.GridPosition
import com.example.codequest.model.Course
import com.example.codequest.model.Lesson
import com.example.codequest.model.ProcessStep

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

    val courses: List<Course> = listOf(
        courseThinkingInCode().copy(title = "Thinking in Python"),
        courseProgrammingWithVariables().copy(title = "Python Variables"),
        coursePythonInputOutput(),
        coursePythonConditions(),
        courseThinkingInPython().copy(order = 100),
        courseProgrammingWithFunctions().copy(order = 101),
        courseAlgorithmicThinking().copy(order = 102),
        courseCsFundamentals().copy(order = 103),
        courseNeuralNetworks().copy(order = 104)
    )

    private val visibleCourseIds = setOf(
        "thinking-in-code",
        "programming-variables",
        "python-input-output",
        "python-conditions"
    )

    val visibleCourses: List<Course> =
        courses.filter { it.id in visibleCourseIds }.sortedBy { it.order }

    val achievements: List<Achievement> = listOf(
        Achievement(
            id = "first-steps",
            title = "First Steps",
            description = "Complete your first activity or lesson.",
            icon = "👣"
        ),
        Achievement(
            id = "perfect-start",
            title = "Perfect Start",
            description = "Get a perfect score in any lesson.",
            icon = "⭐"
        ),
        Achievement(
            id = "red-target-finder",
            title = "Red Target Finder",
            description = "Complete a red-target command-sequence challenge.",
            icon = "🎯"
        ),
        Achievement(
            id = "debug-learner",
            title = "Debug Learner",
            description = "Complete a debugging activity.",
            icon = "🐛"
        ),
        Achievement(
            id = "program-reader",
            title = "Program Reader",
            description = "Complete all 5 questions in What is a Program?",
            icon = "📖"
        ),
        Achievement(
            id = "thinking-coder",
            title = "Thinking Coder",
            description = "Finish the Thinking in Code course.",
            icon = "💡"
        ),
        Achievement(
            id = "pvar-lesson1",
            title = "Variable Starter",
            description = "Complete What is a Variable? in Programming with Variables.",
            icon = "📦"
        ),
        Achievement(
            id = "naming-pro",
            title = "Naming Pro",
            description = "Complete Naming Variables in Programming with Variables.",
            icon = "🏷️"
        ),
        Achievement(
            id = "type-detective",
            title = "Type Detective",
            description = "Complete Data Types in Programming with Variables.",
            icon = "🔍"
        ),
        Achievement(
            id = "value-updater",
            title = "Value Updater",
            description = "Complete Updating Values in Programming with Variables.",
            icon = "🔄"
        ),
        Achievement(
            id = "variable-master",
            title = "Variable Master",
            description = "Complete the full Programming with Variables course.",
            icon = "🏆"
        ),
        Achievement(
            id = "python-printer",
            title = "Python Printer",
            description = "Complete Python Prints Output.",
            icon = "🖨️"
        ),
        Achievement(
            id = "order-reader",
            title = "Order Reader",
            description = "Complete Reading Code in Order.",
            icon = "📚"
        ),
        Achievement(
            id = "comment-helper",
            title = "Comment Helper",
            description = "Complete Comments and Clear Code.",
            icon = "💬"
        ),
        Achievement(
            id = "error-fixer",
            title = "Error Fixer",
            description = "Complete Simple Python Errors.",
            icon = "🛠️"
        ),
        Achievement(
            id = "python-thinker",
            title = "Python Thinker",
            description = "Complete the full Thinking in Python course.",
            icon = "🐍"
        ),
        Achievement(
            id = "output-beginner",
            title = "Output Beginner",
            description = "Complete What is Output?",
            icon = "📤"
        ),
        Achievement(
            id = "print-master",
            title = "Print Master",
            description = "Complete Using print().",
            icon = "🖨️"
        ),
        Achievement(
            id = "input-explorer",
            title = "Input Explorer",
            description = "Complete What is Input?",
            icon = "📥"
        ),
        Achievement(
            id = "ipo-learner",
            title = "IPO Learner",
            description = "Complete Combining Input and Output.",
            icon = "🔁"
        ),
        Achievement(
            id = "input-output-champion",
            title = "Input Output Champion",
            description = "Complete the full Python Input and Output challenge.",
            icon = "🏆"
        ),
        Achievement(
            id = "condition-beginner",
            title = "Condition Beginner",
            description = "Complete What is a Condition?",
            icon = "✅"
        ),
        Achievement(
            id = "if-starter",
            title = "If Starter",
            description = "Complete Using if Statements.",
            icon = "🔎"
        ),
        Achievement(
            id = "else-explorer",
            title = "Else Explorer",
            description = "Complete Using else.",
            icon = "🔀"
        ),
        Achievement(
            id = "compare-coder",
            title = "Compare Coder",
            description = "Complete Comparing Values.",
            icon = "⚖️"
        ),
        Achievement(
            id = "condition-master",
            title = "Condition Master",
            description = "Complete the full Python Conditions challenge.",
            icon = "🏅"
        ),
        Achievement(
            id = "python-path-finisher",
            title = "Python Path Finisher",
            description = "Complete all 4 Python challenges.",
            icon = "🎓"
        )
    )

    fun achievementById(id: String): Achievement? = achievements.firstOrNull { it.id == id }

    val badges: List<Badge> = listOf(
        Badge("first-steps", "First Steps", "Complete your first lesson.", "👣", 1),
        Badge("python-starter", "Python Starter", "Complete the Thinking in Python challenge.", "🧠", 1),
        Badge("variable-master", "Variable Master", "Complete the Python Variables challenge.", "📦", 1),
        Badge("input-output-champion", "Input Output Champion", "Complete the Python Input and Output challenge.", "⌨️", 1),
        Badge("condition-master", "Condition Master", "Complete the Python Conditions challenge.", "🔀", 1),
        Badge("perfect-start", "Perfect Start", "Get a perfect score in any lesson.", "⭐", 1),
        Badge("debug-learner", "Debug Learner", "Use feedback to finish a lesson after mistakes.", "🐞", 1),
        Badge("python-path-finisher", "Python Path Finisher", "Complete all 4 Python challenges.", "🏆", 4)
    )

    val settingsItems = listOf(
        SettingSeed("\uD83D\uDC64", "Edit Profile", null),
        SettingSeed("\uD83C\uDFA8", "Theme", "Cyber"),
        SettingSeed("\uD83D\uDD14", "Notifications", null),
        SettingSeed("\u2753", "Help & Support", null)
    )

    val profileAchievements: List<AchievementSeed> = emptyList()
    val badgeCategories: List<CategorySeed> = emptyList()

    fun courseById(id: String): Course? = courses.firstOrNull { it.id == id }

    fun lessonById(lessonId: String): Lesson? =
        courses.asSequence().flatMap { it.lessons.asSequence() }.firstOrNull { it.id == lessonId }

    fun courseIdForLesson(lessonId: String): String? = lessonById(lessonId)?.courseId

    fun nextCourseAfter(courseId: String): Course? {
        val ordered = visibleCourses
        val idx = ordered.indexOfFirst { it.id == courseId }
        if (idx == -1 || idx >= ordered.lastIndex) return null
        return ordered[idx + 1]
    }

    fun nextLessonInCourse(courseId: String, currentLessonId: String): Lesson? {
        val course = courseById(courseId) ?: return null
        val sorted = course.lessons.sortedBy { it.order }
        val i = sorted.indexOfFirst { it.id == currentLessonId }
        if (i == -1 || i >= sorted.lastIndex) return null
        return sorted[i + 1]
    }

    private fun courseThinkingInCode(): Course = Course(
        id = "thinking-in-code",
        title = "Thinking in Code",
        description = "Learn Python basics and robot sequencing challenges.",
        order = 0,
        icon = "🧠",
        lessons = listOf(
            Lesson(
                id = "tic-l1",
                courseId = "thinking-in-code",
                title = "What is a Program?",
                description = "Read simple Python programs and predict what they print.",
                content = "A Python program is a set of step-by-step instructions the computer runs in order. " +
                    "Start with print() and simple variables to see how code produces output.",
                order = 0,
                example = "print(\"Hello\") → runs top to bottom → shows Hello on the screen.",
                activities = listOf(
                    ActivityItem(
                        id = "tic-l1-a1",
                        lessonId = "tic-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What does this Python program print?",
                        difficultyLabel = "Python basics",
                        codeSnippet = "print(\"Hello, Python!\")",
                        options = listOf("Hello, Python!", "print", "Python", "Error"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! print() displays the text inside the quotes.",
                        incorrectFeedback = "Not quite. Trace what print() sends to the screen.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Find the print statement",
                                explanation = "print() is a Python instruction that shows output.",
                                codeBlock = "print(\"Hello, Python!\")"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Read the string",
                                explanation = "The text in quotes is the message to display.",
                                highlightedCommand = "print",
                                miniVisualHint = "→ text output"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "See the result",
                                explanation = "The program prints Hello, Python! on the screen.",
                                miniVisualHint = "Hello, Python!"
                            )
                        ),
                        finalResult = "The output is Hello, Python!",
                        finalOutput = "Hello, Python!"
                    ),
                    ActivityItem(
                        id = "tic-l1-a2",
                        lessonId = "tic-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What does this Python program print?",
                        difficultyLabel = "Python basics",
                        codeSnippet = "name = \"Alex\"\nprint(name)",
                        options = listOf("Alex", "name", "\"Alex\"", "Error"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! The variable name stores Alex, and print shows that value.",
                        incorrectFeedback = "Not quite. Follow the variable, then the print line.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Store the value",
                                explanation = "name is assigned the text Alex.",
                                codeBlock = "name = \"Alex\""
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Print the variable",
                                explanation = "print(name) outputs whatever is stored in name.",
                                highlightedCommand = "print",
                                miniVisualHint = "→ Alex"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Confirm the output",
                                explanation = "The screen shows Alex.",
                                miniVisualHint = "Alex"
                            )
                        ),
                        finalResult = "The output is Alex.",
                        finalOutput = "Alex"
                    ),
                    ActivityItem(
                        id = "tic-l1-a3",
                        lessonId = "tic-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What does this Python program print?",
                        difficultyLabel = "Python basics",
                        codeSnippet = "a = 2\nb = 3\nprint(a + b)",
                        options = listOf("23", "5", "a + b", "Error"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! Python adds the numbers 2 and 3.",
                        incorrectFeedback = "Not quite. Add the values in a and b before printing.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Read the variables",
                                explanation = "a is 2 and b is 3.",
                                codeBlock = "a = 2\nb = 3"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Evaluate the expression",
                                explanation = "a + b means 2 + 3.",
                                highlightedCommand = "+",
                                miniVisualHint = "= 5"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Print the result",
                                explanation = "print shows 5.",
                                miniVisualHint = "5"
                            )
                        ),
                        finalResult = "The output is 5.",
                        finalOutput = "5"
                    ),
                    ActivityItem(
                        id = "tic-l1-a4",
                        lessonId = "tic-l1",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "This Python program should print Ready, but it crashes. What should be fixed?",
                        difficultyLabel = "Python debug",
                        codeSnippet = "pritn(\"Ready\")",
                        options = listOf(
                            "Change pritn to print",
                            "Delete the quotes",
                            "Change Ready to ready only",
                            "Add a second print line"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Python only recognizes print, not pritn.",
                        incorrectFeedback = "Look for the misspelled function name.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Read the error clue",
                                explanation = "pritn is not a valid Python function name.",
                                codeBlock = "pritn(\"Ready\")"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Find the typo",
                                explanation = "The correct built-in function is print.",
                                highlightedCommand = "print",
                                miniVisualHint = "spelling matters"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Apply the fix",
                                explanation = "Replace pritn with print so Ready is displayed.",
                                miniVisualHint = "print(\"Ready\")"
                            )
                        ),
                        finalResult = "Fix: use print(\"Ready\").",
                        finalOutput = "Ready"
                    ),
                    ActivityItem(
                        id = "tic-l1-a5",
                        lessonId = "tic-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What does this Python program print?",
                        difficultyLabel = "Python basics",
                        codeSnippet = "score = 10\nbonus = 5\nprint(score + bonus)",
                        options = listOf("105", "15", "score + bonus", "Error"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! The program runs in order: store values, then print their sum.",
                        incorrectFeedback = "Not quite. Instructions run top to bottom—add, then print.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Run assignments in order",
                                explanation = "score becomes 10, then bonus becomes 5.",
                                codeBlock = "score = 10\nbonus = 5"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Process the sum",
                                explanation = "score + bonus evaluates to 15.",
                                highlightedCommand = "+",
                                miniVisualHint = "10 + 5"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Produce output",
                                explanation = "print displays 15.",
                                miniVisualHint = "15"
                            )
                        ),
                        finalResult = "The output is 15.",
                        finalOutput = "15"
                    )
                ),
                pathCardSubtitle = "Read and trace simple Python programs line by line."
            ),
            Lesson(
                id = "tic-l2",
                courseId = "thinking-in-code",
                title = "Instructions and Sequences",
                description = "Order matters—chain steps to reach a goal.",
                content = "Computers execute instructions in order unless control flow says otherwise. " +
                    "The right sequence is what turns a plan into a successful run.",
                order = 1,
                example = "Move → collect → turn → move → collect on a small grid.",
                activities = listOf(
                    ActivityItem(
                        id = "tic-l2-gems",
                        lessonId = "tic-l2",
                        type = ActivityType.COMMAND_SEQUENCE,
                        prompt = "Complete the program. Find the red target.",
                        difficultyLabel = "Foundational skill",
                        visualType = "GRID_COLOR_TARGET",
                        availableCommands = listOf(
                            "move forward",
                            "turn right",
                            "select red",
                            "turn left"
                        ),
                        correctSequence = listOf(
                            "turn right",
                            "move forward",
                            "turn right",
                            "move forward",
                            "select red"
                        ),
                        gridRows = 3,
                        gridCols = 3,
                        goldTileCount = 0,
                        // 3×3: top-left (0,0), facing up; center (1,1) is the red target.
                        commandBoardSetup = CommandBoardSetup(
                            robotRow = 0,
                            robotCol = 0,
                            facing = Direction.UP,
                            targetTiles = listOf(GridPosition(1, 1))
                        ),
                        correctFeedback = "Correct! Let's see how your program runs.",
                        incorrectFeedback = "Not quite. Let's walk through the logic.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Face along the top row",
                                explanation = "From facing up, turn right so forward points toward the center of the grid.",
                                highlightedCommand = "turn right",
                                miniVisualHint = "↻ → face right"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Slide to the middle column",
                                explanation = "Move forward one tile along the top row toward the center.",
                                highlightedCommand = "move forward",
                                miniVisualHint = "◇ → one cell forward"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Face downward",
                                explanation = "Turn right again so forward points toward the bottom row—toward the red tile.",
                                highlightedCommand = "turn right",
                                miniVisualHint = "↻ → face down"
                            ),
                            ProcessStep(
                                stepNumber = 4,
                                title = "Step onto the red tile",
                                explanation = "Move forward into the center cell where the red target sits.",
                                highlightedCommand = "move forward",
                                miniVisualHint = "◇ → onto red"
                            ),
                            ProcessStep(
                                stepNumber = 5,
                                title = "Finish the run",
                                explanation = "Select red only works while standing on the red target tile.",
                                highlightedCommand = "select red",
                                miniVisualHint = "✓ objective met"
                            )
                        ),
                        processStepsWhenIncorrect = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Replay your sequence mentally",
                                explanation = "Walk through each filled slot in order. Forward always follows the robot’s current facing—turn first when you need a new direction.",
                                miniVisualHint = "⚠ facing matters"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Check target selection",
                                explanation = "select red only succeeds on the red tile; use turns and moves to reach it first.",
                                highlightedCommand = "select red"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Compare to the intended path",
                                explanation = "Adjust the earliest step where your robot’s position or facing no longer matches the goal.",
                                miniVisualHint = "Tip: tap a slot to clear it"
                            )
                        ),
                        finalResult = "Red target found and selected.",
                        finalOutput = null
                    ),
                    ActivityItem(
                        id = "tic-l2-gems-2",
                        lessonId = "tic-l2",
                        type = ActivityType.COMMAND_SEQUENCE,
                        prompt = "Complete the program. Find the red target.",
                        difficultyLabel = "Foundational skill",
                        visualType = "GRID_COLOR_TARGET",
                        availableCommands = listOf(
                            "move forward",
                            "turn right",
                            "select red",
                            "turn left"
                        ),
                        // 4×4: bottom-right (3,3) facing up → reach red at (0,1) [row 1, col 2 in 1-based],
                        // then select red. Validated only via [CommandSequencePlayback.simulate], not string match.
                        correctSequence = listOf(
                            "move forward",
                            "move forward",
                            "move forward",
                            "turn left",
                            "move forward",
                            "move forward",
                            "select red"
                        ),
                        gridRows = 4,
                        gridCols = 4,
                        goldTileCount = 0,
                        commandBoardSetup = CommandBoardSetup(
                            robotRow = 3,
                            robotCol = 3,
                            facing = Direction.UP,
                            targetTiles = listOf(GridPosition(0, 1))
                        ),
                        correctFeedback = "Correct! Let's see how your program runs.",
                        incorrectFeedback = "Not quite. Let's walk through the logic.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Climb the right edge",
                                explanation = "While facing up from the bottom-right corner, move forward along the right column toward the top.",
                                highlightedCommand = "move forward",
                                miniVisualHint = "◇ forward follows facing"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Face along the top row",
                                explanation = "After reaching the top-right tile, turn left so forward points toward the red target.",
                                highlightedCommand = "turn left",
                                miniVisualHint = "↻ → face left"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Slide to the red tile",
                                explanation = "Move forward along the top row until you stand on the red target.",
                                highlightedCommand = "move forward",
                                miniVisualHint = "◇ → onto red"
                            ),
                            ProcessStep(
                                stepNumber = 4,
                                title = "Select on the target only",
                                explanation = "select red succeeds only when the robot is on the red tile.",
                                highlightedCommand = "select red",
                                miniVisualHint = "✓ finish"
                            )
                        ),
                        processStepsWhenIncorrect = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Trace facing and forward",
                                explanation = "Forward always moves one tile in the direction the robot faces—turn first when you need to change lanes.",
                                miniVisualHint = "⚠ facing matters"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Check select red",
                                explanation = "select red only works on the red target tile after you move onto it.",
                                highlightedCommand = "select red"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Replay step by step",
                                explanation = "Compare your commands to the board: stay in bounds and end on the red tile.",
                                miniVisualHint = "Tip: tap a slot to clear it"
                            )
                        ),
                        finalResult = "Red target found and selected.",
                        finalOutput = null
                    ),
                    ActivityItem(
                        id = "tic-l2-gems-3",
                        lessonId = "tic-l2",
                        type = ActivityType.COMMAND_SEQUENCE,
                        prompt = "Complete the program. Find the red target.",
                        difficultyLabel = "Foundational skill",
                        visualType = "GRID_COLOR_TARGET",
                        availableCommands = listOf(
                            "move forward",
                            "turn right",
                            "select red",
                            "turn left"
                        ),
                        // 3×3: bottom-right (2,2), facing up — same antenna pose as Q1/Q2; red at top-left (0,0).
                        correctSequence = listOf(
                            "move forward",
                            "move forward",
                            "turn left",
                            "move forward",
                            "move forward",
                            "select red"
                        ),
                        gridRows = 3,
                        gridCols = 3,
                        goldTileCount = 0,
                        commandBoardSetup = CommandBoardSetup(
                            robotRow = 2,
                            robotCol = 2,
                            facing = Direction.UP,
                            targetTiles = listOf(GridPosition(0, 0))
                        ),
                        correctFeedback = "Correct! Let's see how your program runs.",
                        incorrectFeedback = "Not quite. Let's walk through the logic.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Climb the right edge",
                                explanation = "From the bottom-right corner facing up, move forward along the right column.",
                                highlightedCommand = "move forward",
                                miniVisualHint = "◇ forward follows facing"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Face along the top row",
                                explanation = "After reaching the top-right tile, turn left so forward points toward the top-left.",
                                highlightedCommand = "turn left",
                                miniVisualHint = "↻ → face left"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Slide to the red tile",
                                explanation = "Move forward along the top row until you stand on the red target.",
                                highlightedCommand = "move forward",
                                miniVisualHint = "◇ → onto red"
                            ),
                            ProcessStep(
                                stepNumber = 4,
                                title = "Select on the target only",
                                explanation = "select red succeeds only when the robot is on the red tile.",
                                highlightedCommand = "select red",
                                miniVisualHint = "✓ finish"
                            )
                        ),
                        processStepsWhenIncorrect = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Replay your sequence mentally",
                                explanation = "Walk through each command in order. Turn before you move when you need a new direction.",
                                miniVisualHint = "⚠ facing matters"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Check target selection",
                                explanation = "select red only succeeds on the red tile.",
                                highlightedCommand = "select red"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Replay step by step",
                                explanation = "Stay in bounds and end on the red tile before selecting.",
                                miniVisualHint = "Tip: tap a slot to clear it"
                            )
                        ),
                        finalResult = "Red target found and selected.",
                        finalOutput = null
                    ),
                    ActivityItem(
                        id = "tic-l2-gems-4",
                        lessonId = "tic-l2",
                        type = ActivityType.COMMAND_SEQUENCE,
                        prompt = "Complete the program. Find the red target.",
                        difficultyLabel = "Foundational skill",
                        visualType = "GRID_COLOR_TARGET",
                        availableCommands = listOf(
                            "move forward",
                            "turn right",
                            "select red",
                            "turn left"
                        ),
                        // 4×4: bottom-right (3,3), facing up — same as Q2; red at top-right (0,3).
                        correctSequence = listOf(
                            "move forward",
                            "move forward",
                            "move forward",
                            "select red"
                        ),
                        gridRows = 4,
                        gridCols = 4,
                        goldTileCount = 0,
                        commandBoardSetup = CommandBoardSetup(
                            robotRow = 3,
                            robotCol = 3,
                            facing = Direction.UP,
                            targetTiles = listOf(GridPosition(0, 3))
                        ),
                        correctFeedback = "Correct! Let's see how your program runs.",
                        incorrectFeedback = "Not quite. Let's walk through the logic.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Climb the right edge",
                                explanation = "While facing up from the bottom-right corner, move forward along the right column toward the top.",
                                highlightedCommand = "move forward",
                                miniVisualHint = "◇ forward follows facing"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Step onto the red tile",
                                explanation = "Keep moving forward until you reach the red target in the top-right corner.",
                                highlightedCommand = "move forward",
                                miniVisualHint = "◇ → onto red"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Select on the target only",
                                explanation = "select red succeeds only when the robot is on the red tile.",
                                highlightedCommand = "select red",
                                miniVisualHint = "✓ finish"
                            )
                        ),
                        processStepsWhenIncorrect = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Trace facing and forward",
                                explanation = "Forward moves one tile in the direction the robot faces—turn first when changing lanes.",
                                miniVisualHint = "⚠ facing matters"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Check select red",
                                explanation = "You must be on the red tile before select red will work.",
                                highlightedCommand = "select red"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Compare to the board",
                                explanation = "Adjust the earliest step where position or facing no longer matches the goal.",
                                miniVisualHint = "Tip: tap a slot to clear it"
                            )
                        ),
                        finalResult = "Red target found and selected.",
                        finalOutput = null
                    ),
                    ActivityItem(
                        id = "tic-l2-gems-5",
                        lessonId = "tic-l2",
                        type = ActivityType.COMMAND_SEQUENCE,
                        prompt = "Complete the program. Find the red target.",
                        difficultyLabel = "Foundational skill",
                        visualType = "GRID_COLOR_TARGET",
                        availableCommands = listOf(
                            "move forward",
                            "turn right",
                            "select red",
                            "turn left"
                        ),
                        // 3×3: bottom-left (2,0), facing up; red at bottom-right (2,2).
                        correctSequence = listOf(
                            "turn right",
                            "move forward",
                            "move forward",
                            "select red"
                        ),
                        gridRows = 3,
                        gridCols = 3,
                        goldTileCount = 0,
                        commandBoardSetup = CommandBoardSetup(
                            robotRow = 2,
                            robotCol = 0,
                            facing = Direction.UP,
                            targetTiles = listOf(GridPosition(2, 2))
                        ),
                        correctFeedback = "Correct! Let's see how your program runs.",
                        incorrectFeedback = "Not quite. Let's walk through the logic.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Face along the bottom row",
                                explanation = "From facing up, turn right so forward points right along the bottom row.",
                                highlightedCommand = "turn right",
                                miniVisualHint = "↻ → face right"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Move across the bottom",
                                explanation = "Move forward twice along the bottom row toward the red tile.",
                                highlightedCommand = "move forward",
                                miniVisualHint = "◇ → ◇ along bottom"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Select on the target",
                                explanation = "You should be standing on the red tile at the bottom-right before selecting.",
                                highlightedCommand = "select red",
                                miniVisualHint = "✓ finish"
                            )
                        ),
                        processStepsWhenIncorrect = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Replay your sequence mentally",
                                explanation = "Walk through each command in order. Turn first so forward follows the bottom row.",
                                miniVisualHint = "⚠ facing matters"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Check target selection",
                                explanation = "select red only succeeds on the red tile.",
                                highlightedCommand = "select red"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Replay step by step",
                                explanation = "Stay in bounds and end on the red tile before selecting.",
                                miniVisualHint = "Tip: tap a slot to clear it"
                            )
                        ),
                        finalResult = "Red target found and selected.",
                        finalOutput = null
                    )
                )
            ),
            Lesson(
                id = "tic-l3",
                courseId = "thinking-in-code",
                title = "Input, Process, Output",
                description = "Trace Python programs step by step: input → process → output.",
                content = "Every useful Python program follows Input → Process → Output (IPO). " +
                    "Input stores data, process transforms it, and output shows the result with print().",
                order = 2,
                example = "Input: price = 10  →  Process: total = price + 2  →  Output: print(total) → 12",
                activities = listOf(
                    ActivityItem(
                        id = "tic-l3-a1",
                        lessonId = "tic-l3",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "Trace the IPO steps in this Python program. What does it print?",
                        difficultyLabel = "Python · IPO",
                        codeSnippet = "# Input\nminutes = 30\nrate = 2\n# Process\ncost = minutes * rate\n# Output\nprint(cost)",
                        options = listOf("32", "60", "cost", "Error"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! Input values are multiplied (process), then print shows the output.",
                        incorrectFeedback = "Follow IPO: read inputs, compute cost, then print.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Input",
                                explanation = "minutes and rate store the incoming values (30 and 2).",
                                codeBlock = "minutes = 30\nrate = 2",
                                miniVisualHint = "← data in"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Process",
                                explanation = "cost = minutes * rate calculates 30 × 2 = 60.",
                                highlightedCommand = "*",
                                miniVisualHint = "30 * 2 = 60"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Output",
                                explanation = "print(cost) displays the processed result on the screen.",
                                highlightedCommand = "print",
                                miniVisualHint = "→ 60"
                            )
                        ),
                        finalResult = "IPO: inputs 30 and 2 → process multiply → output 60.",
                        finalOutput = "60"
                    ),
                    ActivityItem(
                        id = "tic-l3-a2",
                        lessonId = "tic-l3",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "Trace the IPO steps in this Python program. What does it print?",
                        difficultyLabel = "Python · IPO",
                        codeSnippet = "# Input\nitem = \"Apple\"\n# Process\nlabel = \"Item: \" + item\n# Output\nprint(label)",
                        options = listOf("Apple", "Item: Apple", "Item:", "Error"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! Text input is combined (process), then printed (output).",
                        incorrectFeedback = "Input stores Apple, process builds a label, output prints it.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Input",
                                explanation = "item stores the text Apple — this is the input data.",
                                codeBlock = "item = \"Apple\"",
                                miniVisualHint = "← text in"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Process",
                                explanation = "Joining strings builds label = \"Item: Apple\".",
                                highlightedCommand = "+",
                                miniVisualHint = "Item: + Apple"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Output",
                                explanation = "print(label) shows the processed message.",
                                highlightedCommand = "print",
                                miniVisualHint = "→ Item: Apple"
                            )
                        ),
                        finalResult = "IPO: input Apple → process join text → output Item: Apple.",
                        finalOutput = "Item: Apple"
                    ),
                    ActivityItem(
                        id = "tic-l3-a3",
                        lessonId = "tic-l3",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "Trace the IPO steps in this Python program. What does it print?",
                        difficultyLabel = "Python · IPO",
                        codeSnippet = "# Input\nusername = \"coder\"\n# Process\nmessage = \"Welcome, \" + username\n# Output\nprint(message)",
                        options = listOf("coder", "Welcome, coder", "Welcome,", "Error"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! Login-style input becomes a welcome message, then prints.",
                        incorrectFeedback = "Track input (username), process (build message), output (print).",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Input",
                                explanation = "username stores who signed in — the input for this program.",
                                codeBlock = "username = \"coder\"",
                                miniVisualHint = "← user in"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Process",
                                explanation = "The program builds message by combining welcome text + username.",
                                highlightedCommand = "+",
                                miniVisualHint = "Welcome, + coder"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Output",
                                explanation = "print(message) is the output step the user sees.",
                                highlightedCommand = "print",
                                miniVisualHint = "→ Welcome, coder"
                            )
                        ),
                        finalResult = "IPO: input username → process greeting → output Welcome, coder.",
                        finalOutput = "Welcome, coder"
                    ),
                    ActivityItem(
                        id = "tic-l3-a4",
                        lessonId = "tic-l3",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "Trace the IPO steps in this Python program. What does it print?",
                        difficultyLabel = "Python · IPO",
                        codeSnippet = "# Input\ncoins = 8\nbonus = 2\n# Process\ntotal = coins + bonus\n# Output\nprint(total)",
                        options = listOf("82", "10", "coins + bonus", "Error"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! Two inputs are added in process, then printed as output.",
                        incorrectFeedback = "Add coins and bonus (process) before print (output).",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Input",
                                explanation = "coins and bonus hold the values collected (8 and 2).",
                                codeBlock = "coins = 8\nbonus = 2",
                                miniVisualHint = "← numbers in"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Process",
                                explanation = "total = coins + bonus adds the inputs: 8 + 2 = 10.",
                                highlightedCommand = "+",
                                miniVisualHint = "8 + 2 = 10"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Output",
                                explanation = "print(total) shows the final processed value.",
                                highlightedCommand = "print",
                                miniVisualHint = "→ 10"
                            )
                        ),
                        finalResult = "IPO: inputs 8 and 2 → process add → output 10.",
                        finalOutput = "10"
                    ),
                    ActivityItem(
                        id = "tic-l3-a5",
                        lessonId = "tic-l3",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "Trace the IPO steps in this Python program. What does it print?",
                        difficultyLabel = "Python · IPO",
                        codeSnippet = "# Input\nprice = 10\ntax = 2\n# Process\ntotal = price + tax\n# Output\nprint(total)",
                        options = listOf("102", "12", "8", "price + tax"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! A receipt-style IPO: price and tax in, sum processed, total printed.",
                        incorrectFeedback = "Read price/tax (input), add (process), print total (output).",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Input",
                                explanation = "price and tax are the input values (like on a receipt).",
                                codeBlock = "price = 10\ntax = 2",
                                miniVisualHint = "← receipt in"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Process",
                                explanation = "total = price + tax computes the amount to pay: 12.",
                                highlightedCommand = "+",
                                miniVisualHint = "10 + 2 = 12"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Output",
                                explanation = "print(total) displays the final result to the user.",
                                highlightedCommand = "print",
                                miniVisualHint = "→ 12"
                            )
                        ),
                        finalResult = "IPO: inputs price & tax → process sum → output 12.",
                        finalOutput = "12"
                    )
                )
            ),
            Lesson(
                id = "tic-l4",
                courseId = "thinking-in-code",
                title = "Debugging Simple Logic",
                description = "Type the fix — fill in the blank to debug Python programs.",
                content = "Debugging Python means finding the line or symbol that is wrong and typing the correction.",
                order = 3,
                example = "if score ___ 60: → type >= so a score of 60 prints PASS.",
                activities = listOf(
                    ActivityItem(
                        id = "tic-l4-a1",
                        lessonId = "tic-l4",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "This program should print PASS when score is 60. Fill in the correct comparison on line 2.",
                        difficultyLabel = "Python debug · fill in",
                        codeSnippet = "score = 60\nif score ___ 60:\n    print(\"PASS\")\nelse:\n    print(\"REVIEW\")",
                        options = listOf(">", ">=", "==", "<="),
                        fillInPlaceholder = "Type the comparison (e.g. >=)",
                        fillInAcceptedAnswers = listOf(
                            ">=",
                            "score >= 60",
                            "if score >= 60"
                        ),
                        correctFeedback = "Nice catch. The boundary value 60 must be included with >=.",
                        incorrectFeedback = "Check the comparison on line 2 — 60 should pass.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Read the requirement",
                                explanation = "\"At least 60\" means 60 should pass.",
                                miniVisualHint = ">= includes 60"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Inspect the comparison",
                                explanation = "Using > 60 excludes 60, which causes the mismatch."
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Apply the minimal fix",
                                explanation = "Type >= so the condition matches the requirement."
                            )
                        ),
                        finalResult = "Boundary bug fixed: score >= 60.",
                        finalOutput = null
                    ),
                    ActivityItem(
                        id = "tic-l4-a2",
                        lessonId = "tic-l4",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "This program should print 20. Fill in the correct update on line 2.",
                        difficultyLabel = "Python debug · fill in",
                        codeSnippet = "total = 10\ntotal = total + ___\nprint(total)",
                        options = listOf("5", "10", "15", "20"),
                        fillInPlaceholder = "Type what to add (e.g. 10)",
                        fillInAcceptedAnswers = listOf(
                            "10",
                            "total + 10",
                            "total=total+10"
                        ),
                        correctFeedback = "Correct! 10 + 10 = 20 after the second assignment.",
                        incorrectFeedback = "Trace how total changes — you need 20 after the update.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Trace assignments", explanation = "total starts at 10, then becomes 15 if you add 5."),
                            ProcessStep(stepNumber = 2, title = "Check the goal", explanation = "The program should end with total equal to 20."),
                            ProcessStep(stepNumber = 3, title = "Adjust the math", explanation = "Fill in 10 so the second line adds ten more.")
                        ),
                        finalResult = "Use total = total + 10 to print 20.",
                        finalOutput = "20"
                    ),
                    ActivityItem(
                        id = "tic-l4-a3",
                        lessonId = "tic-l4",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Line 2 should match line 1 so Hi prints twice. Fill in the corrected second print.",
                        difficultyLabel = "Python debug · fill in",
                        codeSnippet = "print(\"Hi\")\nprint(___)",
                        options = listOf("\"Hi\"", "'Hi'", "Hi", "\"hi\""),
                        fillInPlaceholder = "Type the fixed print(...) line",
                        fillInAcceptedAnswers = listOf(
                            "\"Hi\"",
                            "'Hi'",
                            "print(\"Hi\")",
                            "print('Hi')"
                        ),
                        correctFeedback = "Correct! Matching quotes on line 2 lets the program run both prints.",
                        incorrectFeedback = "Compare the quote marks — line 2 must match line 1.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Inspect line 2", explanation = "print('Hi\") mixes quote styles and breaks the program."),
                            ProcessStep(stepNumber = 2, title = "Match quotes", explanation = "Strings must open and close with the same kind of quote."),
                            ProcessStep(stepNumber = 3, title = "Fix and rerun", explanation = "Type print(\"Hi\") for the second line.")
                        ),
                        finalResult = "Fix the second line: print(\"Hi\").",
                        finalOutput = "Hi\nHi"
                    ),
                    ActivityItem(
                        id = "tic-l4-a4",
                        lessonId = "tic-l4",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "This program should print even when n is 4. Fill in the correct operator on line 2.",
                        difficultyLabel = "Python debug · fill in",
                        codeSnippet = "n = 4\nif n % 2 ___ 0:\n    print(\"even\")\nelse:\n    print(\"odd\")",
                        options = listOf("=", "==", "!=", ">="),
                        fillInPlaceholder = "Type the comparison operator",
                        fillInAcceptedAnswers = listOf(
                            "==",
                            "n % 2 == 0",
                            "if n % 2 == 0"
                        ),
                        correctFeedback = "Correct! Use == to compare values, not = (which assigns).",
                        incorrectFeedback = "Line 2 needs a comparison operator, not assignment.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Read the goal",
                                explanation = "The program checks whether n is divisible by 2."
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Spot the bug",
                                explanation = "A single = assigns a value; == tests equality."
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Type the fix",
                                explanation = "Fill in == so the condition works when n is 4."
                            )
                        ),
                        finalResult = "Use == to compare: if n % 2 == 0:",
                        finalOutput = "even"
                    ),
                    ActivityItem(
                        id = "tic-l4-a5",
                        lessonId = "tic-l4",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "This program should print Ada. Fill in the correct variable name on line 2.",
                        difficultyLabel = "Python debug · fill in",
                        codeSnippet = "name = \"Ada\"\nprint(___)",
                        options = listOf("name", "\"Ada\"", "Name", "n"),
                        fillInPlaceholder = "Type what goes inside print(...)",
                        fillInAcceptedAnswers = listOf(
                            "name",
                            "print(name)"
                        ),
                        correctFeedback = "Correct! The variable is name — spelling must match exactly.",
                        incorrectFeedback = "Check the variable declared on line 1 — print must use the same name.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Find the variable",
                                explanation = "Line 1 stores the text Ada in name."
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Match the identifier",
                                explanation = "print must use the exact same variable name."
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Fill in the fix",
                                explanation = "Type name inside print so Ada is displayed."
                            )
                        ),
                        finalResult = "Use print(name) to show Ada.",
                        finalOutput = "Ada"
                    )
                )
            )
        )
    )

    private fun courseProgrammingWithVariables(): Course = Course(
        id = "programming-variables",
        title = "Programming with Variables",
        description = "Learn how Python stores, names, reads, and updates values using variables.",
        order = 1,
        icon = "📦",
        lessons = listOf(

            // ── Lesson 1: What is a Variable? ────────────────────────────────────────
            Lesson(
                id = "pvar-l1",
                courseId = "programming-variables",
                title = "What is a Variable?",
                description = "Learn how Python stores values using names.",
                content = "A variable is a name for a value. In Python, age = 18 means the name age stores the value 18. Then print(age) shows the stored value.",
                order = 0,
                example = "age = 18  →  Python remembers 18 under the name age.",
                pathCardSubtitle = "Learn what a variable is and how to use one.",
                activities = listOf(
                    // Q1 – MC: what is a variable?
                    ActivityItem(
                        id = "pvar-l1-a1",
                        lessonId = "pvar-l1",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "What is a variable in Python?",
                        difficultyLabel = "Core concept",
                        codeSnippet = "age = 18\nprint(age)",
                        options = listOf(
                            "A name that stores a value",
                            "A button on the keyboard",
                            "A type of screen",
                            "A picture in the app"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! age is the variable name, and it stores the value 18.",
                        incorrectFeedback = "Not quite. In Python, a variable is a name that stores a value.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "What a variable does",
                                explanation = "A variable gives a name to a value, like age for 18."),
                            ProcessStep(stepNumber = 2, title = "How it looks in Python",
                                explanation = "score = 10 means the name score stores the value 10.",
                                codeBlock = "score = 10"),
                            ProcessStep(stepNumber = 3, title = "Why it matters",
                                explanation = "After a value has a name, you can use that name again.")
                        ),
                        finalResult = "A variable is a name that stores a value.",
                        xpReward = 25
                    ),
                    // Q2 – OUTPUT_TRACING: print(name)
                    ActivityItem(
                        id = "pvar-l1-a2",
                        lessonId = "pvar-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What will this code print?",
                        difficultyLabel = "Tracing output",
                        codeSnippet = "name = \"Ada\"\nprint(name)",
                        options = listOf("name", "Ada", "print", "Error"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! name stores Ada, so print(name) displays Ada.",
                        incorrectFeedback = "Not quite. print(name) shows the value stored inside name.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Store the value",
                                explanation = "name = \"Ada\" means the name variable stores the text Ada.",
                                codeBlock = "name = \"Ada\""),
                            ProcessStep(stepNumber = 2, title = "Print the variable",
                                explanation = "print(name) shows the value stored in name.",
                                highlightedCommand = "print(name)", miniVisualHint = "→ Ada"),
                            ProcessStep(stepNumber = 3, title = "Confirm the output",
                                explanation = "The screen shows Ada, not the word name.",
                                miniVisualHint = "Ada")
                        ),
                        finalResult = "The output is Ada.",
                        finalOutput = "Ada",
                        xpReward = 25
                    ),
                    // Q3 – FILL_IN_BLANK: print(age)
                    ActivityItem(
                        id = "pvar-l1-a3",
                        lessonId = "pvar-l1",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank to print the value of age.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "age = 18\nprint(___)",
                        options = listOf("age", "\"age\"", "Age", "18age"),
                        fillInAcceptedAnswers = listOf("age"),
                        correctFeedback = "Correct! age stores 18, so print(age) displays 18.",
                        incorrectFeedback = "Not quite. Python needs the exact variable name age. Variable names are case-sensitive.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Identify the variable",
                                explanation = "age = 18 stores 18 under the name age.",
                                codeBlock = "age = 18"),
                            ProcessStep(stepNumber = 2, title = "Use the variable name",
                                explanation = "To show the stored value, put the variable name inside print(...).",
                                highlightedCommand = "print(age)"),
                            ProcessStep(stepNumber = 3, title = "Why not the others?",
                                explanation = "\"age\" is text, not the variable. Age is different from age. 18age cannot be a Python variable name.")
                        ),
                        finalResult = "print(age) outputs 18.",
                        finalOutput = "18",
                        xpReward = 25
                    ),
                    // Q4 – MC: storing a value
                    ActivityItem(
                        id = "pvar-l1-a4",
                        lessonId = "pvar-l1",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "In Python, which line correctly stores the number 10 in a variable named score?",
                        difficultyLabel = "Syntax check",
                        codeSnippet = "variable_name = value",
                        options = listOf(
                            "score = 10",
                            "10 = score",
                            "score == 10",
                            "print = score 10"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! In Python, the variable name goes on the left, then =, then the value goes on the right.",
                        incorrectFeedback = "Not quite. To store a value in Python, write the variable name first, then =, then the value.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Store a value",
                                explanation = "Python stores values like this: variable_name = value.",
                                codeBlock = "score = 10"),
                            ProcessStep(stepNumber = 2, title = "Common mistakes",
                                explanation = "10 = score is backwards. score == 10 asks a question instead of storing a value."),
                            ProcessStep(stepNumber = 3, title = "Confirm",
                                explanation = "score = 10 creates a variable named score and stores the number 10.")
                        ),
                        finalResult = "score = 10 correctly stores 10 in the variable score.",
                        xpReward = 25
                    ),
                    // Q5 – FILL_IN_BLANK: print(city)
                    ActivityItem(
                        id = "pvar-l1-a5",
                        lessonId = "pvar-l1",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank to display the city.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "city = \"Gensan\"\nprint(___)",
                        options = listOf("city", "\"city\"", "City", "gensan"),
                        fillInAcceptedAnswers = listOf("city"),
                        correctFeedback = "Correct! city stores \"Gensan\", so print(city) displays Gensan.",
                        incorrectFeedback = "Not quite. The program needs the exact variable name city.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "What is stored?",
                                explanation = "city = \"Gensan\" stores the text Gensan.",
                                codeBlock = "city = \"Gensan\""),
                            ProcessStep(stepNumber = 2, title = "Use the variable",
                                explanation = "print(city) shows the value stored inside city.",
                                highlightedCommand = "print(city)"),
                            ProcessStep(stepNumber = 3, title = "Case sensitivity",
                                explanation = "City and city are different names in Python. gensan is not the variable name in the code.")
                        ),
                        finalResult = "print(city) outputs Gensan.",
                        finalOutput = "Gensan",
                        xpReward = 25
                    )
                )
            ),

            // ── Lesson 2: Naming Variables ───────────────────────────────────────────
            Lesson(
                id = "pvar-l2",
                courseId = "programming-variables",
                title = "Naming Variables",
                description = "Choose valid and readable Python variable names.",
                content = "A variable name should be valid and easy to read. Use letters, numbers, and underscores. Do not start with a number, and do not use spaces or hyphens.",
                order = 1,
                example = "student_score = 95  →  clear, valid name.",
                pathCardSubtitle = "Learn the rules for valid and readable variable names.",
                activities = listOf(
                    // Q1 – FILL_IN_BLANK: valid name
                    ActivityItem(
                        id = "pvar-l2-a1",
                        lessonId = "pvar-l2",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank with the correct variable name.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "_____ = \"Mia\"\nprint(student_name)",
                        options = listOf(
                            "student_name",
                            "2student",
                            "student-name",
                            "student name"
                        ),
                        fillInAcceptedAnswers = listOf("student_name"),
                        correctFeedback = "Correct! Python variable names can use letters and underscores, and they cannot start with a number or contain spaces or hyphens.",
                        incorrectFeedback = "Not quite. A valid Python variable name cannot start with a number and cannot contain spaces or hyphens.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Valid characters",
                                explanation = "Letters, numbers, and underscores are allowed in variable names."),
                            ProcessStep(stepNumber = 2, title = "No spaces or hyphens",
                                explanation = "student name has a space, and student-name has a hyphen. Python does not allow those in variable names."),
                            ProcessStep(stepNumber = 3, title = "Cannot start with a digit",
                                explanation = "2student starts with a number. student-name has a hyphen. student name has a space.")
                        ),
                        finalResult = "student_name is a valid Python variable name.",
                        xpReward = 25
                    ),
                    // Q2 – FILL_IN_BLANK: ___ = "CodeQuest"
                    ActivityItem(
                        id = "pvar-l2-a2",
                        lessonId = "pvar-l2",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank with the best variable name.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "___ = \"CodeQuest\"\nprint(app_name)",
                        options = listOf("app_name", "app-name", "app name", "2app"),
                        fillInAcceptedAnswers = listOf("app_name"),
                        correctFeedback = "Correct! app_name matches print(app_name), and the underscore is allowed.",
                        incorrectFeedback = "Not quite. Choose a name that matches print(app_name) and follows Python naming rules.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Read the print line",
                                explanation = "print(app_name) tells you the variable is named app_name.",
                                codeBlock = "print(app_name)"),
                            ProcessStep(stepNumber = 2, title = "Eliminate invalid names",
                                explanation = "app-name has a hyphen, app name has a space, and 2app starts with a number."),
                            ProcessStep(stepNumber = 3, title = "Confirm",
                                explanation = "app_name uses only letters and underscore, so it is a valid Python name.")
                        ),
                        finalResult = "app_name = \"CodeQuest\" is valid Python.",
                        xpReward = 25
                    ),
                    // Q3 – FILL_IN_BLANK: most readable name
                    ActivityItem(
                        id = "pvar-l2-a3",
                        lessonId = "pvar-l2",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank with the most readable variable name.",
                        difficultyLabel = "Readability",
                        codeSnippet = "_____ = 95\nprint(_____)",
                        options = listOf("x", "student_score", "ss", "a1b2c3"),
                        fillInAcceptedAnswers = listOf("student_score"),
                        correctFeedback = "Correct! student_score is the most readable because it clearly describes what the value represents.",
                        incorrectFeedback = "Not quite. A readable variable name should clearly describe the data it stores.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Avoid single letters",
                                explanation = "x and ss give no clue about what they store."),
                            ProcessStep(stepNumber = 2, title = "Avoid random combinations",
                                explanation = "a1b2c3 follows the rules, but it does not explain the value."),
                            ProcessStep(stepNumber = 3, title = "Choose descriptive names",
                                explanation = "student_score makes it clear the variable holds a student's score.")
                        ),
                        finalResult = "student_score is the most readable name.",
                        xpReward = 25
                    ),
                    // Q4 – FILL_IN_BLANK: ___ = "Mia"
                    ActivityItem(
                        id = "pvar-l2-a4",
                        lessonId = "pvar-l2",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fix the variable name so the program works.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "___ = \"Mia\"\nprint(student_name)",
                        options = listOf("student_name", "student-name", "student name", "student.name"),
                        fillInAcceptedAnswers = listOf("student_name"),
                        correctFeedback = "Correct! student_name matches print(student_name), and the underscore is allowed.",
                        incorrectFeedback = "Not quite. Use the same name shown in print(student_name). Spaces, hyphens, and dots do not work here.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Check the print line",
                                explanation = "print(student_name) expects a variable named student_name.",
                                codeBlock = "print(student_name)"),
                            ProcessStep(stepNumber = 2, title = "Reject invalid separators",
                                explanation = "For this beginner pattern, use letters with an underscore: student_name."),
                            ProcessStep(stepNumber = 3, title = "Confirm",
                                explanation = "student_name uses underscore as a word separator, which is the Python convention.")
                        ),
                        finalResult = "student_name = \"Mia\" stores Mia, and print(student_name) displays Mia.",
                        xpReward = 25
                    ),
                    // Q5 – MC: why 2score is invalid
                    ActivityItem(
                        id = "pvar-l2-a5",
                        lessonId = "pvar-l2",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Why will this code cause an error in Python?",
                        difficultyLabel = "Naming rules",
                        codeSnippet = "2score = 90\nprint(2score)",
                        options = listOf(
                            "Variables cannot store numbers",
                            "Variable names cannot start with a number",
                            "Python does not use the equals sign",
                            "The value 90 is too high"
                        ),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! Python variable names can contain numbers, but they cannot start with a number.",
                        incorrectFeedback = "Not quite. The issue is the variable name. In Python, a variable name cannot begin with a number.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "The rule",
                                explanation = "A Python variable name must start with a letter or underscore, not a number."),
                            ProcessStep(stepNumber = 2, title = "What is valid",
                                explanation = "score2 = 90 is fine because the digit comes after the first letter.",
                                codeBlock = "score2 = 90"),
                            ProcessStep(stepNumber = 3, title = "Why Python enforces this",
                                explanation = "Python sees the starting 2 and cannot read 2score as a variable name.")
                        ),
                        finalResult = "Variable names cannot start with a number. score2 would work because the number comes later.",
                        xpReward = 25
                    )
                )
            ),

            // ── Lesson 3: Data Types ─────────────────────────────────────────────────
            Lesson(
                id = "pvar-l3",
                courseId = "programming-variables",
                title = "Data Types",
                description = "Learn the simple kinds of values Python can store.",
                content = "Every value has a type. Text in quotes is a string. Whole numbers are integers. Decimal numbers are floats. True and False are booleans.",
                order = 2,
                example = "\"Hello\" → str    25 → int    19.99 → float    True → bool",
                pathCardSubtitle = "Learn text, numbers, decimals, and true/false values.",
                activities = listOf(
                    // Q1 – MC: type of "Hello"
                    ActivityItem(
                        id = "pvar-l3-a1",
                        lessonId = "pvar-l3",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "What kind of value is \"Hello\"?",
                        difficultyLabel = "Type identification",
                        codeSnippet = "message = \"Hello\"\nprint(message)",
                        options = listOf("Integer", "String", "Boolean", "Float"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! Text inside quotation marks is called a string.",
                        incorrectFeedback = "Not quite. The quotation marks tell Python this is text, so it is a string.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Look for quotes",
                                explanation = "Any value wrapped in \" \" or ' ' is a string (text)."),
                            ProcessStep(stepNumber = 2, title = "Strings store text",
                                explanation = "\"Hello\" is text, not a number or a true/false value."),
                            ProcessStep(stepNumber = 3, title = "Confirm the type",
                                explanation = "type(\"Hello\") → <class 'str'>. str is short for string.",
                                codeBlock = "type(\"Hello\")  # <class 'str'>")
                        ),
                        finalResult = "\"Hello\" is a string data type.",
                        xpReward = 25
                    ),
                    // Q2 – MC: type of 25
                    ActivityItem(
                        id = "pvar-l3-a2",
                        lessonId = "pvar-l3",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "What kind of value is 25?",
                        difficultyLabel = "Type identification",
                        codeSnippet = "age = 25\nprint(age)",
                        options = listOf("String", "Boolean", "Integer", "List"),
                        correctAnswerIndex = 2,
                        correctFeedback = "Correct! 25 is a whole number, and Python calls whole numbers integers.",
                        incorrectFeedback = "Not quite. 25 has no quotes and no decimal point, so it is a whole number.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "No quotes, no decimal",
                                explanation = "25 has no quotes and no decimal point, so it is a whole number."),
                            ProcessStep(stepNumber = 2, title = "Integers in Python",
                                explanation = "Integers (int) are whole numbers: 0, 1, -5, 1000, etc."),
                            ProcessStep(stepNumber = 3, title = "Confirm the type",
                                explanation = "type(25) → <class 'int'>.",
                                codeBlock = "type(25)  # <class 'int'>")
                        ),
                        finalResult = "25 is a whole number. Python calls this an integer.",
                        xpReward = 25
                    ),
                    // Q3 – FILL_IN_BLANK: boolean value
                    ActivityItem(
                        id = "pvar-l3-a3",
                        lessonId = "pvar-l3",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank with a true/false value.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "is_student = ___\nprint(is_student)",
                        options = listOf("True", "\"True\"", "25", "student"),
                        fillInAcceptedAnswers = listOf("True"),
                        correctFeedback = "Correct! True is Python's word for a true value.",
                        incorrectFeedback = "Not quite. Use True or False without quotation marks for a true/false value.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "True or false",
                                explanation = "Booleans are true/false values. Python writes them as True and False."),
                            ProcessStep(stepNumber = 2, title = "True vs \"True\"",
                                explanation = "True without quotes is a true/false value. \"True\" with quotes is text."),
                            ProcessStep(stepNumber = 3, title = "Confirm",
                                explanation = "is_student = True stores a true/false value. print shows True.",
                                codeBlock = "is_student = True\nprint(is_student)  # True")
                        ),
                        finalResult = "is_student = True stores a true/false value.",
                        finalOutput = "True",
                        xpReward = 25
                    ),
                    // Q4 – OUTPUT_TRACING: float
                    ActivityItem(
                        id = "pvar-l3-a4",
                        lessonId = "pvar-l3",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What will this code print?",
                        difficultyLabel = "Tracing output",
                        codeSnippet = "price = 19.99\nprint(price)",
                        options = listOf("19.99", "price", "Integer", "Error"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! price stores 19.99, so print(price) displays 19.99.",
                        incorrectFeedback = "Not quite. print(price) shows the value stored inside price.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Store the float",
                                explanation = "price = 19.99 stores a decimal number (float).",
                                codeBlock = "price = 19.99"),
                            ProcessStep(stepNumber = 2, title = "Print the variable",
                                explanation = "print(price) shows the stored value, not the word price.",
                                highlightedCommand = "print(price)"),
                            ProcessStep(stepNumber = 3, title = "Output",
                                explanation = "The screen displays 19.99.",
                                miniVisualHint = "19.99")
                        ),
                        finalResult = "The output is 19.99.",
                        finalOutput = "19.99",
                        xpReward = 25
                    ),
                    // Q5 – FILL_IN_BLANK: type(age)
                    ActivityItem(
                        id = "pvar-l3-a5",
                        lessonId = "pvar-l3",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Which variable should go in the blank to check the number's type?",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "name = \"Ada\"\nage = 18\nprint(type(___))",
                        options = listOf("age", "name", "\"age\"", "type"),
                        fillInAcceptedAnswers = listOf("age"),
                        correctFeedback = "Correct! age stores 18, so type(age) checks the number.",
                        incorrectFeedback = "Not quite. Choose the variable that stores the number 18.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Which variable holds a number?",
                                explanation = "age = 18 stores a whole number. name = \"Ada\" stores text.",
                                codeBlock = "name = \"Ada\"\nage = 18"),
                            ProcessStep(stepNumber = 2, title = "Goal: check the number's type",
                                explanation = "type(age) returns the type of the value stored in age, which is int."),
                            ProcessStep(stepNumber = 3, title = "Confirm",
                                explanation = "print(type(age)) outputs <class 'int'>.",
                                miniVisualHint = "<class 'int'>")
                        ),
                        finalResult = "type(age) shows <class 'int'>.",
                        finalOutput = "<class 'int'>",
                        xpReward = 25
                    )
                )
            ),

            // ── Lesson 4: Updating Values ────────────────────────────────────────────
            Lesson(
                id = "pvar-l4",
                courseId = "programming-variables",
                title = "Updating Values",
                description = "Learn how variable values can change as your program runs.",
                content = "A variable can get a new value. Python reads the lines from top to bottom, so the latest value is the one print(...) shows.",
                order = 3,
                example = "score = 10  →  score = 20  →  print(score) shows 20.",
                pathCardSubtitle = "Track how variables change when a new value is stored.",
                activities = listOf(
                    // Q1 – OUTPUT_TRACING: overwrite score
                    ActivityItem(
                        id = "pvar-l4-a1",
                        lessonId = "pvar-l4",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What number will print at the end?",
                        difficultyLabel = "Tracing output",
                        codeSnippet = "score = 10\nscore = 20\nprint(score)",
                        options = listOf("10", "20", "30", "Error"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! score starts as 10, then changes to 20, so print(score) displays 20.",
                        incorrectFeedback = "Not quite. Read from top to bottom: the second line changes score to 20.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "First value",
                                explanation = "score = 10 stores 10.",
                                codeBlock = "score = 10"),
                            ProcessStep(stepNumber = 2, title = "New value",
                                explanation = "score = 20 changes score from 10 to 20. Python uses the latest value.",
                                codeBlock = "score = 20"),
                            ProcessStep(stepNumber = 3, title = "Print the final value",
                                explanation = "print(score) shows 20.",
                                miniVisualHint = "20")
                        ),
                        finalResult = "The output is 20.",
                        finalOutput = "20",
                        xpReward = 25
                    ),
                    // Q2 – FILL_IN_BLANK: points + ___ = 8
                    ActivityItem(
                        id = "pvar-l4-a2",
                        lessonId = "pvar-l4",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank so the output becomes 8.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "points = 5\npoints = points + ___\nprint(points)",
                        options = listOf("3", "5", "8", "\"3\""),
                        fillInAcceptedAnswers = listOf("3"),
                        correctFeedback = "Correct! points starts at 5, and 5 + 3 becomes 8.",
                        incorrectFeedback = "Not quite. Start with 5, then choose the number that makes the total 8.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Starting value",
                                explanation = "points = 5 — we start with 5.",
                                codeBlock = "points = 5"),
                            ProcessStep(stepNumber = 2, title = "What number reaches 8?",
                                explanation = "5 + ? = 8. Solving: ? = 3. So the blank is 3."),
                            ProcessStep(stepNumber = 3, title = "Confirm",
                                explanation = "points = points + 3 → 5 + 3 = 8. print(points) shows 8.",
                                miniVisualHint = "8")
                        ),
                        finalResult = "5 + 3 = 8.",
                        finalOutput = "8",
                        xpReward = 25
                    ),
                    // Q3 – MC: count = count + 1
                    ActivityItem(
                        id = "pvar-l4-a3",
                        lessonId = "pvar-l4",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "What does count = count + 1 do to count?",
                        difficultyLabel = "Understanding updates",
                        codeSnippet = "count = 4\ncount = count + 1\nprint(count)",
                        options = listOf(
                            "It decreases count",
                            "It increases count by 1",
                            "It deletes count",
                            "It prints count"
                        ),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! It takes the old count and adds 1.",
                        incorrectFeedback = "Not quite. count + 1 means take the old value and add one more.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Right side first",
                                explanation = "Python first reads the old value of count."),
                            ProcessStep(stepNumber = 2, title = "Then assign",
                                explanation = "Then Python stores the new value back in count."),
                            ProcessStep(stepNumber = 3, title = "Net effect",
                                explanation = "count goes up by exactly 1.",
                                codeBlock = "count = 0\ncount = count + 1  # now 1\ncount = count + 1  # now 2")
                        ),
                        finalResult = "count = count + 1 increments the variable by 1.",
                        xpReward = 25
                    ),
                    // Q4 – FILL_IN_BLANK: print(total)
                    ActivityItem(
                        id = "pvar-l4-a4",
                        lessonId = "pvar-l4",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank to print the updated total.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "total = 50\ntotal = total + 25\nprint(___)",
                        options = listOf("total", "Total", "\"total\"", "25"),
                        fillInAcceptedAnswers = listOf("total"),
                        correctFeedback = "Correct! total stores 75, so print(total) displays 75.",
                        incorrectFeedback = "Not quite. Use the variable name total to print the updated value.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Calculate the new total",
                                explanation = "total = 50, then total = 50 + 25 = 75.",
                                codeBlock = "total = 50\ntotal = total + 25"),
                            ProcessStep(stepNumber = 2, title = "Print the variable",
                                explanation = "print(total) shows the value currently stored in total."),
                            ProcessStep(stepNumber = 3, title = "Why not the others?",
                                explanation = "Total is a different name. \"total\" prints the text total, not 75. 25 is just a number.")
                        ),
                        finalResult = "print(total) outputs 75.",
                        finalOutput = "75",
                        xpReward = 25
                    ),
                    // Q5 – OUTPUT_TRACING: level incremented twice
                    ActivityItem(
                        id = "pvar-l4-a5",
                        lessonId = "pvar-l4",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What will be printed?",
                        difficultyLabel = "Tracing output",
                        codeSnippet = "level = 1\nlevel = level + 1\nlevel = level + 1\nprint(level)",
                        options = listOf("1", "2", "3", "Error"),
                        correctAnswerIndex = 2,
                        correctFeedback = "Correct! level starts at 1, then goes to 2, then goes to 3.",
                        incorrectFeedback = "Not quite. Follow each line: level increases by 1 two times.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Start",
                                explanation = "level = 1.",
                                codeBlock = "level = 1"),
                            ProcessStep(stepNumber = 2, title = "First increment",
                                explanation = "level = 1 + 1 = 2.",
                                miniVisualHint = "level → 2"),
                            ProcessStep(stepNumber = 3, title = "Second increment",
                                explanation = "level = 2 + 1 = 3. print(level) shows 3.",
                                miniVisualHint = "level → 3")
                        ),
                        finalResult = "The output is 3.",
                        finalOutput = "3",
                        xpReward = 25
                    )
                )
            )
        )
    )

    private fun coursePythonInputOutput(): Course = Course(
        id = "python-input-output",
        title = "Python Input and Output",
        description = "Practice receiving input, storing it in variables, displaying output, and fixing simple input/output code.",
        order = 2,
        icon = "⌨️",
        lessons = listOf(
            Lesson(
                id = "pio-l1",
                courseId = "python-input-output",
                title = "Input and Output Basics",
                description = "Learn what input and output mean using simple Python examples.",
                content = "Input is information given by the user. Output is information shown by the program.",
                order = 0,
                pathCardSubtitle = "Tell input and output apart.",
                activities = listOf(
                    pioActivity("pio-l1-a1", "pio-l1", ActivityType.MULTIPLE_CHOICE, "Core concept", "name = input(\"Enter your name: \")\nprint(name)", "Which line receives input from the user?", listOf("print(name)", "name = input(\"Enter your name: \")", "name", "\"Enter your name: \""), "Correct! input() asks the user to enter information.", "Not quite. The line with input() receives the user's answer.", "Input is information given by the user.", correctAnswerIndex = 1),
                    pioActivity("pio-l1-a2", "pio-l1", ActivityType.MULTIPLE_CHOICE, "Output concept", "message = \"Welcome\"\nprint(message)", "Which line shows output on the screen?", listOf("message = \"Welcome\"", "\"Welcome\"", "print(message)", "message"), "Correct! print(message) displays the value stored in message.", "Not quite. Output is shown using print().", "print() displays output.", correctAnswerIndex = 2),
                    pioActivity("pio-l1-a3", "pio-l1", ActivityType.FILL_IN_BLANK, "Fill in the blank", "____(\"Hello, Python\")", "Fill in the blank to display the message.", listOf("input", "print", "message", "text"), "Correct! print(\"Hello, Python\") displays the message.", "Error! Python uses print() to show output.", "The correct code is print(\"Hello, Python\").", acceptedAnswers = listOf("print"), finalOutput = "Hello, Python"),
                    pioActivity("pio-l1-a4", "pio-l1", ActivityType.OUTPUT_TRACING, "Predict output", "word = \"Python\"\nprint(word)", "What will this code display?", listOf("word", "Python", "print", "Error"), "Correct! print(word) displays the value stored in word.", "Not quite. The variable word stores \"Python\".", "The output is Python.", correctAnswerIndex = 1, finalOutput = "Python"),
                    pioActivity("pio-l1-a5", "pio-l1", ActivityType.MULTIPLE_CHOICE, "Input vs Output", "age = input(\"Age: \")\nprint(\"Your age is\", age)", "Which statement best describes this program?", listOf("It only creates a password", "It asks for age, then displays a message with the age", "It deletes the age variable", "It prints the word input only"), "Correct! The program gets input first, then shows output.", "Not quite. The first line uses input(), and the second line uses print().", "This program combines input and output.", correctAnswerIndex = 1)
                )
            ),
            Lesson(
                id = "pio-l2",
                courseId = "python-input-output",
                title = "Printing Values and Messages",
                description = "Practice printing text, variables, and combined messages.",
                content = "print() can display text, variable values, or both together.",
                order = 1,
                pathCardSubtitle = "Print text and variable values.",
                activities = listOf(
                    pioActivity("pio-l2-a1", "pio-l2", ActivityType.OUTPUT_TRACING, "Predict output", "name = \"Ada\"\nprint(\"Hello\", name)", "What will this code display?", listOf("Hello name", "Ada Hello", "Hello Ada", "Error"), "Correct! Python prints the text \"Hello\" and then the value of name.", "Not quite. name stores Ada, so the output includes Ada.", "The output is Hello Ada.", correctAnswerIndex = 2, finalOutput = "Hello Ada"),
                    pioActivity("pio-l2-a2", "pio-l2", ActivityType.FILL_IN_BLANK, "Fill in the blank", "name = \"Mia\"\nprint(\"Hi\", ____)", "Fill in the blank to display Hi Mia.", listOf("\"name\"", "name", "Mia", "input"), "Correct! name stores \"Mia\", so print(\"Hi\", name) displays Hi Mia.", "Not quite. Use the variable name without quotation marks to print its value.", "The correct answer is name.", acceptedAnswers = listOf("name"), finalOutput = "Hi Mia"),
                    pioActivity("pio-l2-a3", "pio-l2", ActivityType.FILL_IN_BLANK, "Complete the code", "____ = \"CodeQuest\"\nprint(____)", "Complete the code to display CodeQuest.", emptyList(), "Correct! The variable app stores \"CodeQuest\", and print(app) displays its value.", "Not quite. The same variable name should be used when storing and printing the value.", "Correct code:\napp = \"CodeQuest\"\nprint(app)", codeBlanks = listOf(CodeBlank("variable", "app", listOf("app", "\"app\"", "print", "input")), CodeBlank("printedVariable", "app", listOf("app", "\"CodeQuest\"", "print", "input"))), finalOutput = "CodeQuest"),
                    pioActivity("pio-l2-a4", "pio-l2", ActivityType.DEBUG_CODE, "Debug output", "name = \"Leo\"\nprint(\"Hello name\")", "Why does this not display Hello Leo?", listOf("name is inside the quotation marks", "print is not allowed", "Leo should be a number", "The code has no variable"), "Correct! Text inside quotation marks is printed exactly as written.", "Not quite. To print the value of name, it should be outside the quotation marks.", "Use print(\"Hello\", name) to display Hello Leo.", correctAnswerIndex = 0),
                    pioActivity("pio-l2-a5", "pio-l2", ActivityType.FILL_IN_BLANK, "Fix the code", "name = \"Leo\"\nprint(\"Hello\", ____)", "Fill in the blank to display Hello Leo.", listOf("name", "\"name\"", "Leo", "\"Leo\""), "Correct! name should be outside quotation marks to print its value.", "Not quite. Quotation marks print the word literally.", "Correct code:\nprint(\"Hello\", name)", acceptedAnswers = listOf("name"), finalOutput = "Hello Leo")
                )
            ),
            Lesson(
                id = "pio-l3",
                courseId = "python-input-output",
                title = "Getting User Input",
                description = "Practice using input() and storing user answers.",
                content = "input() asks the user for information. The variable on the left stores what the user types.",
                order = 2,
                pathCardSubtitle = "Use input() and variables together.",
                activities = listOf(
                    pioActivity("pio-l3-a1", "pio-l3", ActivityType.MULTIPLE_CHOICE, "Input function", "city = input(\"Enter city: \")", "Where will the user's answer be stored?", listOf("In input", "In \"Enter city: \"", "In city", "In print"), "Correct! The user's answer is stored in the variable city.", "Not quite. The variable on the left side stores the input.", "city stores what the user types.", correctAnswerIndex = 2),
                    pioActivity("pio-l3-a2", "pio-l3", ActivityType.FILL_IN_BLANK, "Fill in the blank", "name = ____(\"Enter name: \")", "Fill in the blank to ask the user for their name.", listOf("print", "input", "show", "text"), "Correct! input() receives information from the user.", "Error! Python uses input() for user input.", "Correct code:\nname = input(\"Enter name: \")", acceptedAnswers = listOf("input")),
                    pioActivity("pio-l3-a3", "pio-l3", ActivityType.FILL_IN_BLANK, "Complete input/output", "____ = input(\"Favorite color: \")\nprint(\"Color:\", ____)", "Complete the code to ask for a favorite color and display it.", emptyList(), "Correct! color stores the user's answer, then print displays it.", "Not quite. Use the same variable name to store and display the input.", "Correct code:\ncolor = input(\"Favorite color: \")\nprint(\"Color:\", color)", codeBlanks = listOf(CodeBlank("variable", "color", listOf("color", "\"color\"", "print", "input")), CodeBlank("printedVariable", "color", listOf("color", "\"Favorite color\"", "input", "print")))),
                    pioActivity("pio-l3-a4", "pio-l3", ActivityType.MULTIPLE_CHOICE, "Program order", "food = input(\"Favorite food: \")\nprint(\"You like\", food)", "What happens first?", listOf("The program prints You like", "The program asks for favorite food", "The program deletes food", "The program shows an error"), "Correct! Python runs the input line first.", "Not quite. Python reads the code from top to bottom.", "The program asks for input before printing the message.", correctAnswerIndex = 1),
                    pioActivity("pio-l3-a5", "pio-l3", ActivityType.DEBUG_CODE, "Debug input", "name = input(\"Name: \")\nprint(\"Hello\", user)", "Why can this code cause an error?", listOf("input() cannot be used", "user was never created as a variable", "print cannot show text", "name should be inside quotation marks"), "Correct! The program stores input in name, but tries to print user.", "Not quite. Check if the variable names match.", "Use print(\"Hello\", name).", correctAnswerIndex = 1)
                )
            ),
            Lesson(
                id = "pio-l4",
                courseId = "python-input-output",
                title = "Fixing Input and Output Code",
                description = "Debug common beginner mistakes in input and output programs.",
                content = "Input/output bugs often happen when variable names do not match or print() is missing.",
                order = 3,
                pathCardSubtitle = "Fix simple input/output programs.",
                activities = listOf(
                    pioActivity("pio-l4-a1", "pio-l4", ActivityType.DEBUG_CODE, "Debug variable", "name = input(\"Name: \")\nprint(\"Hello\", username)", "What is wrong with this code?", listOf("username does not match the variable name", "input should be deleted", "print should be first", "\"Hello\" cannot be printed"), "Correct! The input is stored in name, but the program tries to print username.", "Not quite. The variable names must match.", "Correct code:\nprint(\"Hello\", name)", correctAnswerIndex = 0),
                    pioActivity("pio-l4-a2", "pio-l4", ActivityType.FILL_IN_BLANK, "Fix the code", "name = input(\"Name: \")\nprint(\"Hello\", ____)", "Fill in the blank to fix the code.", listOf("name", "username", "\"Name\"", "input"), "Correct! name stores the user's input.", "Error! The printed variable should match the input variable.", "Correct code:\nprint(\"Hello\", name)", acceptedAnswers = listOf("name")),
                    pioActivity("pio-l4-a3", "pio-l4", ActivityType.FILL_IN_BLANK, "Complete the program", "____ = input(\"Enter username: \")\n____(\"Welcome\", username)", "Complete the program to ask for a username and display a welcome message.", emptyList(), "Correct! username stores the input, and print displays the welcome message.", "Not quite. First store input in a variable, then use print() to display output.", "Correct code:\nusername = input(\"Enter username: \")\nprint(\"Welcome\", username)", codeBlanks = listOf(CodeBlank("username", "username", listOf("username", "\"user\"", "print", "input")), CodeBlank("command", "print", listOf("print", "input", "show", "text")))),
                    pioActivity("pio-l4-a4", "pio-l4", ActivityType.FILL_IN_BLANK, "Input and Output Practice", "name = input(\"Enter your name: \")\ncourse = input(\"Enter your course: \")\n\nprint(\"Hello\", ____)\nprint(\"Welcome to\", ____)", "Complete the program so it displays the user's name and course.", emptyList(), "Correct! The program stores the user's input in variables, then prints the values using those variable names.", "Not quite. Use the variable name without quotation marks when you want to print the value stored inside it.", "Correct code:\nname = input(\"Enter your name: \")\ncourse = input(\"Enter your course: \")\n\nprint(\"Hello\", name)\nprint(\"Welcome to\", course)", codeBlanks = listOf(CodeBlank("name", "name", listOf("name", "\"name\"", "course", "input")), CodeBlank("course", "course", listOf("course", "\"course\"", "name", "print")))),
                    pioActivity("pio-l4-a5", "pio-l4", ActivityType.MULTIPLE_CHOICE, "Final debug review", "name = input(\"Name: \")\nprint(\"Hello\", name)", "Which statement best describes this program?", listOf("It stores input in name and prints a greeting", "It only prints the word name", "It has no output", "It deletes the user's answer"), "Correct! The program receives input and displays output.", "Not quite. input() receives information and print() displays it.", "This is a simple input/output program.", correctAnswerIndex = 0)
                )
            )
        )
    )

    private fun pioActivity(
        id: String,
        lessonId: String,
        type: ActivityType,
        category: String,
        codeSnippet: String?,
        prompt: String,
        options: List<String>,
        correctFeedback: String,
        incorrectFeedback: String,
        finalResult: String,
        correctAnswerIndex: Int = 0,
        acceptedAnswers: List<String> = emptyList(),
        codeBlanks: List<CodeBlank> = emptyList(),
        finalOutput: String? = null
    ): ActivityItem = ActivityItem(
        id = id,
        lessonId = lessonId,
        type = type,
        prompt = prompt,
        difficultyLabel = category,
        codeSnippet = codeSnippet,
        options = options,
        correctAnswerIndex = if (type == ActivityType.FILL_IN_BLANK) -1 else correctAnswerIndex,
        fillInAcceptedAnswers = acceptedAnswers,
        codeBlanks = codeBlanks,
        correctFeedback = correctFeedback,
        incorrectFeedback = incorrectFeedback,
        processSteps = listOf(
            ProcessStep(1, "Read the question", "Start with the code example if one is shown."),
            ProcessStep(2, "Pick the answer", "Choose the option that matches what Python does."),
            ProcessStep(3, "Review", finalResult)
        ),
        finalResult = finalResult,
        finalOutput = finalOutput,
        xpReward = 25
    )

    private fun coursePythonConditions(): Course = Course(
        id = "python-conditions",
        title = "Python Conditions",
        description = "Learn how Python makes decisions using if, else, and comparison operators.",
        order = 3,
        icon = "🔀",
        lessons = listOf(
            Lesson(
                id = "pc-l1",
                courseId = "python-conditions",
                title = "What is a Condition?",
                description = "Learn that conditions are true-or-false checks.",
                content = "A condition is a check that can be true or false.",
                order = 0,
                pathCardSubtitle = "Read true-or-false checks.",
                activities = listOf(
                    pioActivity("pc-l1-a1", "pc-l1", ActivityType.MULTIPLE_CHOICE, "Core concept", "score = 85\nscore >= 75", "What is a condition in Python?", listOf("A true-or-false check", "A picture in the app", "A password box", "A type of phone screen"), "Correct! A condition checks whether something is true or false.", "Not quite. A condition is a check that can be true or false.", "score >= 75 checks if the score is at least 75.", correctAnswerIndex = 0),
                    pioActivity("pc-l1-a2", "pc-l1", ActivityType.MULTIPLE_CHOICE, "True or false", "age = 18\nage >= 18", "Is the condition age >= 18 true or false?", listOf("False", "True", "Error", "It prints age"), "Correct! age is 18, so age >= 18 is true.", "Not quite. 18 is equal to 18, so the condition is true.", ">= means greater than or equal to.", correctAnswerIndex = 1),
                    pioActivity("pc-l1-a3", "pc-l1", ActivityType.FILL_IN_BLANK, "Fill in the blank", "score = 90\nscore ____ 75", "Fill in the blank to check if score is at least 75.", listOf(">=", "=", "<", "print"), "Correct! score >= 75 checks if score is at least 75.", "Not quite. Use >= when checking \"at least\" or \"greater than or equal.\"", "The correct condition is score >= 75.", acceptedAnswers = listOf(">=")),
                    pioActivity("pc-l1-a4", "pc-l1", ActivityType.MULTIPLE_CHOICE, "Read condition", "temperature = 30\ntemperature > 25", "What does this condition check?", listOf("If temperature is equal to 25", "If temperature is less than 25", "If temperature is greater than 25", "If temperature is printed"), "Correct! The > symbol checks if the left value is greater than the right value.", "Not quite. The > symbol means greater than.", "temperature > 25 checks if temperature is above 25.", correctAnswerIndex = 2),
                    pioActivity("pc-l1-a5", "pc-l1", ActivityType.MULTIPLE_CHOICE, "Beginner review", "is_raining = False", "What kind of value is False?", listOf("String", "Boolean", "Integer", "Comment"), "Correct! True and False are Boolean values.", "Not quite. True and False are called Boolean values.", "Conditions often result in Boolean values: True or False.", correctAnswerIndex = 1)
                )
            ),
            Lesson(
                id = "pc-l2",
                courseId = "python-conditions",
                title = "Using if Statements",
                description = "Learn how if runs code when a condition is true.",
                content = "An if statement runs its indented code only when the condition is true.",
                order = 1,
                pathCardSubtitle = "Use if to run code conditionally.",
                activities = listOf(
                    pioActivity("pc-l2-a1", "pc-l2", ActivityType.OUTPUT_TRACING, "Core concept", "score = 90\n\nif score >= 75:\n    print(\"Passed\")", "What will this program print?", listOf("Failed", "Passed", "score", "Nothing"), "Correct! Since 90 is at least 75, the condition is true.", "Not quite. The if block runs because score >= 75 is true.", "The program prints Passed.", correctAnswerIndex = 1, finalOutput = "Passed"),
                    pioActivity("pc-l2-a2", "pc-l2", ActivityType.FILL_IN_BLANK, "Fill in the blank", "score = 80\n\nif score ____ 75:\n    print(\"Passed\")", "Fill in the blank so the program prints Passed.", listOf(">=", "<", "=", "print"), "Correct! score >= 75 checks if the score is passing.", "Not quite. Use >= to check if score is at least 75.", "The correct condition is score >= 75.", acceptedAnswers = listOf(">=")),
                    pioActivity("pc-l2-a3", "pc-l2", ActivityType.MULTIPLE_CHOICE, "If syntax", "if score >= 75:\n    print(\"Passed\")", "Why does the if line end with a colon?", listOf("The colon starts the indented code block", "The colon prints the result", "The colon deletes the score", "The colon turns text into a number"), "Correct! In Python, the colon starts the block that belongs to the if statement.", "Not quite. The colon tells Python that an indented block follows.", "Python uses a colon before the indented if body.", correctAnswerIndex = 0),
                    pioActivity("pc-l2-a4", "pc-l2", ActivityType.FILL_IN_BLANK, "Complete the code", "score = 88\n\nif score ____ 75:\n    print(____)", "Complete the if statement to print Passed when score is passing.", emptyList(), "Correct! The condition is true, so Python prints Passed.", "Not quite. Use >= for passing score, and use quotation marks for text.", "Correct code:\nif score >= 75:\n    print(\"Passed\")", codeBlanks = listOf(CodeBlank("operator", ">=", listOf(">=", "<", "=", "print")), CodeBlank("message", "\"Passed\"", listOf("\"Passed\"", "Passed", "score", "75"))), finalOutput = "Passed"),
                    pioActivity("pc-l2-a5", "pc-l2", ActivityType.DEBUG_CODE, "Debug if", "score = 90\n\nif score >= 75\n    print(\"Passed\")", "What is missing in this code?", listOf("Quotation marks", "A closing parenthesis", "A colon after the if condition", "The variable score"), "Correct! Python needs a colon after the if condition.", "Not quite. Check the end of the if line.", "The fixed line is if score >= 75:", correctAnswerIndex = 2)
                )
            ),
            Lesson(
                id = "pc-l3",
                courseId = "python-conditions",
                title = "Using else",
                description = "Learn how else handles the other path.",
                content = "else runs when the if condition is false.",
                order = 2,
                pathCardSubtitle = "Use else for the other result.",
                activities = listOf(
                    pioActivity("pc-l3-a1", "pc-l3", ActivityType.OUTPUT_TRACING, "Core concept", "score = 60\n\nif score >= 75:\n    print(\"Passed\")\nelse:\n    print(\"Try again\")", "What will this program print?", listOf("Passed", "Try again", "score", "Nothing"), "Correct! Since 60 is below 75, the else block runs.", "Not quite. The if condition is false, so Python uses else.", "The output is Try again.", correctAnswerIndex = 1, finalOutput = "Try again"),
                    pioActivity("pc-l3-a2", "pc-l3", ActivityType.FILL_IN_BLANK, "Fill in the blank", "score = 60\n\nif score >= 75:\n    print(\"Passed\")\n____:\n    print(\"Try again\")", "Fill in the blank to handle the false condition.", listOf("else", "if", "print", "input"), "Correct! else runs when the if condition is false.", "Not quite. Use else for the other result.", "The correct keyword is else.", acceptedAnswers = listOf("else")),
                    pioActivity("pc-l3-a3", "pc-l3", ActivityType.OUTPUT_TRACING, "Read code", "is_open = False\n\nif is_open:\n    print(\"Enter\")\nelse:\n    print(\"Closed\")", "What will this code print?", listOf("Enter", "Closed", "is_open", "False Enter"), "Correct! is_open is False, so the else block runs.", "Not quite. The if block only runs when the condition is true.", "The output is Closed.", correctAnswerIndex = 1, finalOutput = "Closed"),
                    pioActivity("pc-l3-a4", "pc-l3", ActivityType.FILL_IN_BLANK, "Complete the code", "age = 15\n\nif age >= 18:\n    print(\"Adult\")\n____:\n    print(____)", "Complete the program so it prints Minor when age is below 18.", emptyList(), "Correct! Since age is 15, the else block prints Minor.", "Not quite. Use else for the other case, and use quotation marks for text.", "Correct code:\nelse:\n    print(\"Minor\")", codeBlanks = listOf(CodeBlank("keyword", "else", listOf("else", "if", "print", "input")), CodeBlank("message", "\"Minor\"", listOf("\"Minor\"", "Minor", "age", "\"Adult\""))), finalOutput = "Minor"),
                    pioActivity("pc-l3-a5", "pc-l3", ActivityType.MULTIPLE_CHOICE, "Purpose", null, "When does the else block run?", listOf("When the if condition is false", "When the if condition is true", "Before the program starts", "Only when there is no variable"), "Correct! else runs when the if condition is false.", "Not quite. else is the fallback when if is not true.", "if handles the true case, else handles the other case.", correctAnswerIndex = 0)
                )
            ),
            Lesson(
                id = "pc-l4",
                courseId = "python-conditions",
                title = "Comparing Values",
                description = "Learn simple comparison operators.",
                content = "Comparison operators like ==, <, and >= help Python make decisions.",
                order = 3,
                pathCardSubtitle = "Compare values with ==, <, and >=.",
                activities = listOf(
                    pioActivity("pc-l4-a1", "pc-l4", ActivityType.MULTIPLE_CHOICE, "Comparison symbols", "x = 10\nx == 10", "What does == check?", listOf("It assigns a value", "It checks if two values are equal", "It prints x", "It asks for input"), "Correct! == checks if two values are equal.", "Not quite. One equals sign assigns, but two equals signs compare.", "Use == for comparison.", correctAnswerIndex = 1),
                    pioActivity("pc-l4-a2", "pc-l4", ActivityType.FILL_IN_BLANK, "Fill in the blank", "password = \"code123\"\n\nif password ____ \"code123\":\n    print(\"Access granted\")", "Fill in the blank to check if the password matches.", listOf("==", "=", ">=", "input"), "Correct! == checks if the two values are equal.", "Not quite. Use == when comparing values.", "The condition is password == \"code123\".", acceptedAnswers = listOf("=="), finalOutput = "Access granted"),
                    pioActivity("pc-l4-a3", "pc-l4", ActivityType.MULTIPLE_CHOICE, "Greater or less", "points = 40\npoints < 50", "Is points < 50 true or false?", listOf("False", "True", "Error", "It prints 50"), "Correct! 40 is less than 50.", "Not quite. The < symbol checks if the left value is smaller.", "points < 50 is true.", correctAnswerIndex = 1),
                    pioActivity("pc-l4-a4", "pc-l4", ActivityType.FILL_IN_BLANK, "Complete the condition", "level = 3\n\nif level ____ 3:\n    print(____)", "Complete the condition so the program prints Level matched.", emptyList(), "Correct! == checks if level equals 3, and the text is printed.", "Not quite. Use == for comparison and quotation marks for text.", "Correct code:\nif level == 3:\n    print(\"Level matched\")", codeBlanks = listOf(CodeBlank("operator", "==", listOf("==", "=", "<", "input")), CodeBlank("message", "\"Level matched\"", listOf("\"Level matched\"", "Level matched", "level", "3"))), finalOutput = "Level matched"),
                    pioActivity("pc-l4-a5", "pc-l4", ActivityType.MULTIPLE_CHOICE, "Final review", "score = 75\n\nif score >= 75:\n    print(\"Passed\")\nelse:\n    print(\"Failed\")", "Which statement best describes this program?", listOf("It always prints Failed", "It checks the score and prints Passed if the score is at least 75", "It asks the user for input", "It has no condition"), "Correct! The program checks the score before choosing what to print.", "Not quite. The if statement checks whether score is at least 75.", "This is a simple condition program.", correctAnswerIndex = 1, finalOutput = "Passed")
                )
            )
        )
    )

    private fun placeholderPythonLesson(
        id: String,
        courseId: String,
        title: String,
        description: String,
        order: Int,
        prompt: String,
        codeSnippet: String,
        options: List<String>,
        correctFeedback: String,
        incorrectFeedback: String
    ): Lesson = Lesson(
        id = id,
        courseId = courseId,
        title = title,
        description = description,
        content = description,
        order = order,
        pathCardSubtitle = description,
        activities = listOf(
            ActivityItem(
                id = "$id-a1",
                lessonId = id,
                type = ActivityType.MULTIPLE_CHOICE,
                prompt = prompt,
                difficultyLabel = "Preview",
                codeSnippet = codeSnippet,
                options = options,
                correctAnswerIndex = 0,
                correctFeedback = correctFeedback,
                incorrectFeedback = incorrectFeedback,
                processSteps = listOf(
                    ProcessStep(1, "Read the code", "Look at the Python example first."),
                    ProcessStep(2, "Choose the best answer", "Use the code to understand the idea."),
                    ProcessStep(3, "Review", correctFeedback)
                ),
                finalResult = correctFeedback,
                xpReward = 25
            )
        )
    )

    private fun courseThinkingInPython(): Course = Course(
        id = "thinking-python",
        title = "Thinking in Python",
        description = "Learn how Python reads instructions, prints output, uses comments, and handles simple beginner errors.",
        order = 2,
        icon = "🐍",
        lessons = listOf(
            Lesson(
                id = "tp-l1",
                courseId = "thinking-python",
                title = "Python Prints Output",
                description = "Learn how Python displays messages using print().",
                content = "print() tells Python to display text or values on the screen.",
                order = 0,
                activities = listOf(
                    ActivityItem(
                        id = "tp-l1-a1",
                        lessonId = "tp-l1",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "What does print() do in Python?",
                        difficultyLabel = "Core concept",
                        codeSnippet = "print(\"Hello\")",
                        options = listOf("It displays output on the screen", "It deletes the program", "It creates a password", "It turns off the computer"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! print() displays text or values on the screen.",
                        incorrectFeedback = "Not quite. print() is used to show output.",
                        processSteps = listOf(
                            ProcessStep(1, "Read print()", "print() is a Python instruction."),
                            ProcessStep(2, "Look inside", "The text inside the parentheses is what Python displays."),
                            ProcessStep(3, "Result", "print() tells Python to display something.")
                        ),
                        finalResult = "print() tells Python to display something.",
                        finalOutput = "Hello",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l1-a2",
                        lessonId = "tp-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What will this code print?",
                        difficultyLabel = "Predict output",
                        codeSnippet = "print(\"CodeQuest\")",
                        options = listOf("CodeQuest", "print", "\"print CodeQuest\"", "Error"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Python prints the text inside the quotation marks.",
                        incorrectFeedback = "Not quite. The text inside the quotation marks is what appears as output.",
                        processSteps = listOf(
                            ProcessStep(1, "Find the text", "The text is CodeQuest."),
                            ProcessStep(2, "Use print()", "print() displays that text."),
                            ProcessStep(3, "Output", "The output is CodeQuest.")
                        ),
                        finalResult = "The output is CodeQuest.",
                        finalOutput = "CodeQuest",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l1-a3",
                        lessonId = "tp-l1",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank to display the message.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "____(\"Hello, Python\")",
                        options = listOf("print", "show", "display", "text"),
                        fillInAcceptedAnswers = listOf("print"),
                        correctFeedback = "Correct! print(\"Hello, Python\") displays the message.",
                        incorrectFeedback = "Error! Python uses print() to display output, not show or display.",
                        processSteps = listOf(
                            ProcessStep(1, "Choose the command", "Python uses print to display output."),
                            ProcessStep(2, "Complete the line", "print(\"Hello, Python\") is the complete instruction."),
                            ProcessStep(3, "Output", "The message appears on the screen.")
                        ),
                        finalResult = "The correct code is print(\"Hello, Python\").",
                        finalOutput = "Hello, Python",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l1-a4",
                        lessonId = "tp-l1",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Which line correctly prints the word Welcome?",
                        difficultyLabel = "Choose correct code",
                        options = listOf("print(\"Welcome\")", "print Welcome", "show(\"Welcome\")", "\"Welcome\" print"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Python uses print() with parentheses and quotation marks for text.",
                        incorrectFeedback = "Not quite. The correct format is print(\"message\").",
                        processSteps = listOf(
                            ProcessStep(1, "Use print", "print is the command for output."),
                            ProcessStep(2, "Use parentheses", "The message goes inside parentheses."),
                            ProcessStep(3, "Use quotes", "Text like Welcome needs quotation marks.")
                        ),
                        finalResult = "print(\"Welcome\") displays Welcome.",
                        finalOutput = "Welcome",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l1-a5",
                        lessonId = "tp-l1",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "Why can this code cause an error?",
                        difficultyLabel = "Debug output",
                        codeSnippet = "print(Hello)",
                        options = listOf("Hello should be inside quotation marks", "print cannot display text", "Parentheses are not allowed", "Python does not use words"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Text should be placed inside quotation marks.",
                        incorrectFeedback = "Not quite. Python treats Hello without quotation marks as a variable name.",
                        processSteps = listOf(
                            ProcessStep(1, "Find the text", "Hello is meant to be text."),
                            ProcessStep(2, "Add quotes", "Text should be inside quotation marks."),
                            ProcessStep(3, "Fix", "Write print(\"Hello\").")
                        ),
                        finalResult = "To print text, write print(\"Hello\").",
                        finalOutput = "Hello",
                        xpReward = 25
                    )
                )
            ),
            Lesson(
                id = "tp-l2",
                courseId = "thinking-python",
                title = "Reading Code in Order",
                description = "Learn that Python runs instructions from top to bottom.",
                content = "Python reads code one line at a time from top to bottom.",
                order = 1,
                activities = listOf(
                    ActivityItem(
                        id = "tp-l2-a1",
                        lessonId = "tp-l2",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What will be printed first?",
                        difficultyLabel = "Code order",
                        codeSnippet = "print(\"Start\")\nprint(\"End\")",
                        options = listOf("Start", "End", "Both at the same time", "Nothing"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Python reads the first line before the second line.",
                        incorrectFeedback = "Not quite. Python runs code from top to bottom.",
                        processSteps = listOf(
                            ProcessStep(1, "Line 1", "Python runs print(\"Start\") first."),
                            ProcessStep(2, "Line 2", "Then Python runs print(\"End\")."),
                            ProcessStep(3, "First output", "Start appears first.")
                        ),
                        finalResult = "Start is printed first because it is on the first line.",
                        finalOutput = "Start\nEnd",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l2-a2",
                        lessonId = "tp-l2",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What is the correct order of the output?",
                        difficultyLabel = "Predict output",
                        codeSnippet = "print(\"A\")\nprint(\"B\")\nprint(\"C\")",
                        options = listOf("A, B, C", "C, B, A", "A, C, B", "B, A, C"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Python prints the lines in order from top to bottom.",
                        incorrectFeedback = "Not quite. Follow the code line by line.",
                        processSteps = listOf(
                            ProcessStep(1, "First line", "A prints first."),
                            ProcessStep(2, "Second line", "B prints second."),
                            ProcessStep(3, "Third line", "C prints third.")
                        ),
                        finalResult = "The output order is A, then B, then C.",
                        finalOutput = "A\nB\nC",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l2-a3",
                        lessonId = "tp-l2",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank to complete the correct step order.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "print(\"Step 1\")\nprint(\"____\")\nprint(\"Step 3\")",
                        options = listOf("Step 2", "Step 4", "Start", "End"),
                        fillInAcceptedAnswers = listOf("Step 2"),
                        correctFeedback = "Correct! Step 2 belongs between Step 1 and Step 3.",
                        incorrectFeedback = "Not quite. The missing step should keep the sequence in order.",
                        processSteps = listOf(
                            ProcessStep(1, "Start", "The first line prints Step 1."),
                            ProcessStep(2, "Middle", "The middle should be Step 2."),
                            ProcessStep(3, "End", "The last line prints Step 3.")
                        ),
                        finalResult = "The complete order is Step 1, Step 2, Step 3.",
                        finalOutput = "Step 1\nStep 2\nStep 3",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l2-a4",
                        lessonId = "tp-l2",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Which program prints Morning first, then Afternoon?",
                        difficultyLabel = "Choose correct code",
                        options = listOf(
                            "print(\"Morning\")\nprint(\"Afternoon\")",
                            "print(\"Afternoon\")\nprint(\"Morning\")",
                            "print(\"Morning Afternoon\")",
                            "print(\"Evening\")\nprint(\"Morning\")"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! The first print line runs first, then the second print line.",
                        incorrectFeedback = "Not quite. Python follows the order of the lines.",
                        processSteps = listOf(
                            ProcessStep(1, "First output", "Put Morning on the first line."),
                            ProcessStep(2, "Second output", "Put Afternoon on the second line."),
                            ProcessStep(3, "Order matters", "Python prints them in that order.")
                        ),
                        finalResult = "To print Morning first, put print(\"Morning\") first.",
                        finalOutput = "Morning\nAfternoon",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l2-a5",
                        lessonId = "tp-l2",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Why does order matter in Python?",
                        difficultyLabel = "Sequence concept",
                        options = listOf("Python follows instructions from top to bottom", "Python ignores the first line", "Python always runs the last line first", "Python only reads comments"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Python follows instructions in sequence.",
                        incorrectFeedback = "Not quite. Python reads code one line at a time from top to bottom.",
                        processSteps = listOf(
                            ProcessStep(1, "Read line 1", "Python starts at the top."),
                            ProcessStep(2, "Move down", "Then it goes to the next line."),
                            ProcessStep(3, "Result", "Changing the order can change the result.")
                        ),
                        finalResult = "Changing the order of code can change the result.",
                        xpReward = 25
                    )
                )
            ),
            Lesson(
                id = "tp-l3",
                courseId = "thinking-python",
                title = "Comments and Clear Code",
                description = "Learn how comments help explain Python code.",
                content = "A comment is a note for humans. Python ignores comment lines when the program runs.",
                order = 2,
                activities = listOf(
                    ActivityItem(
                        id = "tp-l3-a1",
                        lessonId = "tp-l3",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "What is a comment in Python?",
                        difficultyLabel = "Core concept",
                        codeSnippet = "# This explains the code\nprint(\"Hello\")",
                        options = listOf("A note for humans reading the code", "A command that always prints text", "A required password", "A type of number"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Comments help explain code for people.",
                        incorrectFeedback = "Not quite. Comments are notes in the code.",
                        processSteps = listOf(
                            ProcessStep(1, "Find the comment", "The line starting with # is a comment."),
                            ProcessStep(2, "Human note", "It helps people understand the code."),
                            ProcessStep(3, "Python ignores it", "Python does not print the comment.")
                        ),
                        finalResult = "Python ignores comments when running the program.",
                        finalOutput = "Hello",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l3-a2",
                        lessonId = "tp-l3",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Which line is a comment?",
                        difficultyLabel = "Identify comment",
                        codeSnippet = "# print greeting\nprint(\"Hello\")",
                        options = listOf("# print greeting", "print(\"Hello\")", "\"Hello\"", "greeting"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! A Python comment starts with #.",
                        incorrectFeedback = "Not quite. Look for the line that starts with #.",
                        processSteps = listOf(
                            ProcessStep(1, "Look for #", "Python comments start with #."),
                            ProcessStep(2, "Read the note", "# print greeting explains the code."),
                            ProcessStep(3, "Output line", "print(\"Hello\") is the line that displays output.")
                        ),
                        finalResult = "# print greeting is a comment.",
                        finalOutput = "Hello",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l3-a3",
                        lessonId = "tp-l3",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank to make the first line a Python comment.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "____ This stores the user's name\nname = \"Ada\"",
                        options = listOf("#", "//", "--", "comment"),
                        fillInAcceptedAnswers = listOf("#"),
                        correctFeedback = "Correct! Python comments start with #.",
                        incorrectFeedback = "Error! Python uses # for comments, not // or --.",
                        processSteps = listOf(
                            ProcessStep(1, "Choose #", "# starts a Python comment."),
                            ProcessStep(2, "Read the note", "The comment explains the next line."),
                            ProcessStep(3, "Python ignores it", "The comment does not run as code.")
                        ),
                        finalResult = "The correct comment is # This stores the user's name.",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l3-a4",
                        lessonId = "tp-l3",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What will be printed?",
                        difficultyLabel = "Predict output",
                        codeSnippet = "# Hello\nprint(\"Python\")",
                        options = listOf("Python", "Hello", "Hello Python", "Nothing"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! The comment is ignored, and only Python is printed.",
                        incorrectFeedback = "Not quite. Python ignores the line that starts with #.",
                        processSteps = listOf(
                            ProcessStep(1, "Comment line", "# Hello is ignored by Python."),
                            ProcessStep(2, "Print line", "print(\"Python\") displays Python."),
                            ProcessStep(3, "Output", "Only Python appears.")
                        ),
                        finalResult = "Only print(\"Python\") creates output.",
                        finalOutput = "Python",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l3-a5",
                        lessonId = "tp-l3",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Why are comments useful in code?",
                        difficultyLabel = "Purpose",
                        options = listOf("They help explain what the code does", "They make the computer run faster", "They replace all variables", "They are printed automatically"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Comments make code easier to understand.",
                        incorrectFeedback = "Not quite. Comments are mainly for explanation.",
                        processSteps = listOf(
                            ProcessStep(1, "Write a note", "A comment explains the code."),
                            ProcessStep(2, "Help humans", "It helps someone read the program later."),
                            ProcessStep(3, "No output", "Comments are not printed automatically.")
                        ),
                        finalResult = "Good comments help humans understand the program.",
                        xpReward = 25
                    )
                )
            ),
            Lesson(
                id = "tp-l4",
                courseId = "thinking-python",
                title = "Simple Python Errors",
                description = "Learn how to notice and fix beginner Python mistakes.",
                content = "Beginner errors often come from missing parentheses, missing quotation marks, or incomplete print statements.",
                order = 3,
                activities = listOf(
                    ActivityItem(
                        id = "tp-l4-a1",
                        lessonId = "tp-l4",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "What is wrong with this code?",
                        difficultyLabel = "Debug syntax",
                        codeSnippet = "print(\"Hello\"",
                        options = listOf("It is missing a closing parenthesis", "Hello should not use quotation marks", "print is spelled wrong", "The code has no error"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! The closing parenthesis is missing.",
                        incorrectFeedback = "Not quite. Check the parentheses carefully.",
                        processSteps = listOf(
                            ProcessStep(1, "Open parenthesis", "print( starts the parentheses."),
                            ProcessStep(2, "Missing close", "The line needs a final )."),
                            ProcessStep(3, "Fix", "Write print(\"Hello\").")
                        ),
                        finalResult = "The fixed code is print(\"Hello\").",
                        finalOutput = "Hello",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l4-a2",
                        lessonId = "tp-l4",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "What is wrong with this code?",
                        difficultyLabel = "Debug string",
                        codeSnippet = "print(Hello)",
                        options = listOf("Hello should be inside quotation marks", "print should be deleted", "Parentheses are not allowed", "The code is already correct"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Text should be written inside quotation marks.",
                        incorrectFeedback = "Not quite. Without quotation marks, Python looks for a variable named Hello.",
                        processSteps = listOf(
                            ProcessStep(1, "Text needs quotes", "Hello is text here."),
                            ProcessStep(2, "Add quotes", "Use \"Hello\"."),
                            ProcessStep(3, "Fix", "Write print(\"Hello\").")
                        ),
                        finalResult = "The fixed code is print(\"Hello\").",
                        finalOutput = "Hello",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l4-a3",
                        lessonId = "tp-l4",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank to complete the code.",
                        difficultyLabel = "Fill in the blank",
                        codeSnippet = "print(\"CodeQuest\"____",
                        options = listOf(")", "(", "\"", ":"),
                        fillInAcceptedAnswers = listOf(")"),
                        correctFeedback = "Correct! The closing parenthesis completes the print statement.",
                        incorrectFeedback = "Error! The print statement needs a closing parenthesis.",
                        processSteps = listOf(
                            ProcessStep(1, "Start", "print( opens the parentheses."),
                            ProcessStep(2, "Finish", "A closing ) completes it."),
                            ProcessStep(3, "Fix", "Write print(\"CodeQuest\").")
                        ),
                        finalResult = "The correct code is print(\"CodeQuest\").",
                        finalOutput = "CodeQuest",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l4-a4",
                        lessonId = "tp-l4",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Which code has no error?",
                        difficultyLabel = "Choose correct code",
                        options = listOf("print(\"Hi\")", "print(\"Hi\"", "print(Hi)", "print Hi"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! print(\"Hi\") has the correct parentheses and quotation marks.",
                        incorrectFeedback = "Not quite. Correct Python print syntax uses print(\"text\").",
                        processSteps = listOf(
                            ProcessStep(1, "Use print", "Start with print."),
                            ProcessStep(2, "Use parentheses", "Put the text inside parentheses."),
                            ProcessStep(3, "Use quotes", "Text should be inside quotation marks.")
                        ),
                        finalResult = "print(\"Hi\") is the correct line.",
                        finalOutput = "Hi",
                        xpReward = 25
                    ),
                    ActivityItem(
                        id = "tp-l4-a5",
                        lessonId = "tp-l4",
                        type = ActivityType.FILL_IN_BLANK,
                        prompt = "Fill in the blank to print the word Python.",
                        difficultyLabel = "Debug fill in",
                        codeSnippet = "print(____)",
                        options = listOf("\"Python\"", "Python", "print", "#Python"),
                        fillInAcceptedAnswers = listOf("\"Python\""),
                        correctFeedback = "Correct! Text should be inside quotation marks.",
                        incorrectFeedback = "Error! To print text, Python needs quotation marks around the word.",
                        processSteps = listOf(
                            ProcessStep(1, "Goal", "We want to print text."),
                            ProcessStep(2, "Use quotes", "Text needs quotation marks."),
                            ProcessStep(3, "Fix", "Write print(\"Python\").")
                        ),
                        finalResult = "The correct code is print(\"Python\").",
                        finalOutput = "Python",
                        xpReward = 25
                    )
                )
            )
        )
    )

    private fun courseProgrammingWithFunctions(): Course = Course(
        id = "programming-functions",
        title = "Programming with Functions",
        description = "Learn how to organize reusable blocks of code.",
        order = 3,
        icon = "🔧",
        lessons = listOf(
            Lesson(
                id = "pf-l1",
                courseId = "programming-functions",
                title = "Why Functions?",
                description = "Pack logic you can call from many places.",
                content = "Functions reduce repetition and hide detail behind a clear name.",
                order = 0,
                activities = listOf(
                    ActivityItem(
                        id = "pf-l1-a1",
                        lessonId = "pf-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What gets printed?",
                        difficultyLabel = "Function basics",
                        codeSnippet = "def greet():\n    print(\"Hello\")\n\ngreet()",
                        options = listOf("Hello", "greet", "Nothing", "Error"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct. Calling greet() prints Hello.",
                        incorrectFeedback = "A function runs only when it is called.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Define function", explanation = "greet stores reusable steps."),
                            ProcessStep(stepNumber = 2, title = "Call function", explanation = "greet() executes the function body."),
                            ProcessStep(stepNumber = 3, title = "Observe output", explanation = "The printed output is Hello.")
                        ),
                        finalResult = "The output is Hello.",
                        finalOutput = "Hello"
                    )
                )
            ),
            Lesson(
                id = "pf-l2",
                courseId = "programming-functions",
                title = "Parameters and Return",
                description = "Send data in, get results out.",
                content = "Parameters act as inputs; return sends a result back to the caller.",
                order = 1,
                activities = listOf(
                    ActivityItem(
                        id = "pf-l2-a1",
                        lessonId = "pf-l2",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What does this print?",
                        difficultyLabel = "Inputs and return",
                        codeSnippet = "def double(x):\n    return x * 2\n\nprint(double(4))",
                        options = listOf("2", "4", "8", "44"),
                        correctAnswerIndex = 2,
                        correctFeedback = "Correct. double(4) returns 8.",
                        incorrectFeedback = "Trace parameter x and the return expression.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Pass input", explanation = "x receives 4."),
                            ProcessStep(stepNumber = 2, title = "Compute return", explanation = "x * 2 becomes 8."),
                            ProcessStep(stepNumber = 3, title = "Print result", explanation = "print(double(4)) shows 8.")
                        ),
                        finalResult = "The output is 8.",
                        finalOutput = "8"
                    )
                )
            )
        )
    )

    private fun courseAlgorithmicThinking(): Course = Course(
        id = "algorithmic-thinking",
        title = "Algorithmic Thinking",
        description = "Practice step-by-step problem solving.",
        order = 4,
        icon = "🧭",
        lessons = listOf(
            Lesson(
                id = "alg-l1",
                courseId = "algorithmic-thinking",
                title = "Breaking Down Problems",
                description = "Split big goals into smaller checks and steps.",
                content = "Algorithms are careful sequences of steps.",
                order = 0,
                activities = listOf(
                    ActivityItem(
                        id = "alg-l1-a1",
                        lessonId = "alg-l1",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "To count from 1 to 3, which step is missing?",
                        difficultyLabel = "Algorithm steps",
                        codeSnippet = "count = 1\nwhile count <= 3:\n    print(count)\n    # missing line",
                        options = listOf(
                            "count = count + 1",
                            "count = 1",
                            "print(\"done\")",
                            "while True:"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct. Incrementing count advances the loop.",
                        incorrectFeedback = "The loop needs a step that moves toward exit.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Start value", explanation = "count begins at 1."),
                            ProcessStep(stepNumber = 2, title = "Repeat condition", explanation = "Loop continues while count <= 3."),
                            ProcessStep(stepNumber = 3, title = "Progress step", explanation = "count = count + 1 prevents infinite loop.")
                        ),
                        finalResult = "Missing line: count = count + 1.",
                        finalOutput = null
                    )
                )
            ),
            Lesson(
                id = "alg-l2",
                courseId = "algorithmic-thinking",
                title = "Tracing Examples",
                description = "Walk through data by hand before coding.",
                content = "Tracing builds intuition and catches mistakes early.",
                order = 1,
                activities = listOf(
                    ActivityItem(
                        id = "alg-l2-a1",
                        lessonId = "alg-l2",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What is the final value of total?",
                        difficultyLabel = "Tracing practice",
                        codeSnippet = "total = 0\nfor n in [1, 2, 3]:\n    total = total + n\nprint(total)",
                        options = listOf("3", "5", "6", "123"),
                        correctAnswerIndex = 2,
                        correctFeedback = "Correct. total accumulates to 6.",
                        incorrectFeedback = "Trace total after each loop iteration.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Initialize", explanation = "total starts at 0."),
                            ProcessStep(stepNumber = 2, title = "Accumulate", explanation = "0+1=1, then 1+2=3, then 3+3=6."),
                            ProcessStep(stepNumber = 3, title = "Output", explanation = "The program prints 6.")
                        ),
                        finalResult = "The output is 6.",
                        finalOutput = "6"
                    )
                )
            )
        )
    )

    private fun courseCsFundamentals(): Course = Course(
        id = "cs-fundamentals",
        title = "Computer Science Fundamentals",
        description = "Explore basic concepts behind computers and programs.",
        order = 5,
        icon = "💻",
        lessons = listOf(
            Lesson(
                id = "cs-l1",
                courseId = "cs-fundamentals",
                title = "How Computers Store Data",
                description = "Bits, bytes, and memory in beginner terms.",
                content = "At the lowest level, information is stored as bits.",
                order = 0,
                activities = listOf(
                    ActivityItem(
                        id = "cs-l1-a1",
                        lessonId = "cs-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "A byte contains how many bits?",
                        difficultyLabel = "CS basics",
                        codeSnippet = "# quick fact check\nbits_in_byte = 8\nprint(bits_in_byte)",
                        options = listOf("2", "4", "8", "16"),
                        correctAnswerIndex = 2,
                        correctFeedback = "Correct. 1 byte = 8 bits.",
                        incorrectFeedback = "Remember the standard unit mapping.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Identify unit", explanation = "The question asks byte to bit conversion."),
                            ProcessStep(stepNumber = 2, title = "Recall definition", explanation = "By convention, one byte stores 8 bits."),
                            ProcessStep(stepNumber = 3, title = "Confirm output", explanation = "The snippet prints 8.")
                        ),
                        finalResult = "One byte contains 8 bits.",
                        finalOutput = "8"
                    )
                )
            ),
            Lesson(
                id = "cs-l2",
                courseId = "cs-fundamentals",
                title = "Programs and the CPU",
                description = "Fetch, decode, execute—simplified view.",
                content = "Processors execute instructions stored in memory.",
                order = 1,
                activities = listOf(
                    ActivityItem(
                        id = "cs-l2-a1",
                        lessonId = "cs-l2",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "Which stage comes after Fetch in the CPU cycle?",
                        difficultyLabel = "Execution cycle",
                        codeSnippet = "CPU cycle:\n1) Fetch\n2) ?\n3) Execute",
                        options = listOf("Shutdown", "Decode", "Sleep", "Compile"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct. The CPU decodes before execution.",
                        incorrectFeedback = "Think of the standard fetch-decode-execute sequence.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Fetch", explanation = "CPU reads instruction from memory."),
                            ProcessStep(stepNumber = 2, title = "Decode", explanation = "CPU interprets what the instruction means."),
                            ProcessStep(stepNumber = 3, title = "Execute", explanation = "CPU performs the operation.")
                        ),
                        finalResult = "Correct order: Fetch -> Decode -> Execute.",
                        finalOutput = null
                    )
                )
            )
        )
    )

    private fun courseNeuralNetworks(): Course = Course(
        id = "neural-intro",
        title = "Introduction to Neural Networks",
        description = "Understand the basic idea of how simple AI models learn.",
        order = 6,
        icon = "🧠",
        lessons = listOf(
            Lesson(
                id = "nn-l1",
                courseId = "neural-intro",
                title = "Neurons and Layers",
                description = "A cartoon view of stacked simple units.",
                content = "Neural networks combine many small computations.",
                order = 0,
                activities = listOf(
                    ActivityItem(
                        id = "nn-l1-a1",
                        lessonId = "nn-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "In a simple network, where does data enter first?",
                        difficultyLabel = "NN basics",
                        codeSnippet = "layers = [\"input\", \"hidden\", \"output\"]\nprint(layers[0])",
                        options = listOf("hidden", "output", "input", "loss"),
                        correctAnswerIndex = 2,
                        correctFeedback = "Correct. Data enters through the input layer.",
                        incorrectFeedback = "Look at the first layer in the sequence.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Read layer order", explanation = "The list starts with input."),
                            ProcessStep(stepNumber = 2, title = "Index access", explanation = "layers[0] selects the first item."),
                            ProcessStep(stepNumber = 3, title = "Output", explanation = "Printed value is input.")
                        ),
                        finalResult = "The first layer is input.",
                        finalOutput = "input"
                    )
                )
            ),
            Lesson(
                id = "nn-l2",
                courseId = "neural-intro",
                title = "Learning from Examples",
                description = "Adjust weights to reduce mistakes.",
                content = "Training tweaks parameters so predictions match labels.",
                order = 1,
                activities = listOf(
                    ActivityItem(
                        id = "nn-l2-a1",
                        lessonId = "nn-l2",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "What is the main goal during training?",
                        difficultyLabel = "Training intuition",
                        codeSnippet = "for each epoch:\n    predict\n    compare with label\n    update weights",
                        options = listOf(
                            "Increase error each epoch",
                            "Reduce prediction error over time",
                            "Randomize all labels",
                            "Delete the dataset"
                        ),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct. Training aims to reduce error.",
                        incorrectFeedback = "Focus on why weights are updated after comparison.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Predict", explanation = "Model outputs a guess."),
                            ProcessStep(stepNumber = 2, title = "Measure error", explanation = "Compare guess vs correct label."),
                            ProcessStep(stepNumber = 3, title = "Update weights", explanation = "Adjustments aim to lower future error.")
                        ),
                        finalResult = "Training goal: reduce prediction error.",
                        finalOutput = null
                    )
                )
            )
        )
    )
}
