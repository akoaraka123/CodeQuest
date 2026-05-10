package com.example.codequest.data

import com.example.codequest.model.ActivityItem
import com.example.codequest.model.ActivityType
import com.example.codequest.model.Badge
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
        courseThinkingInCode(),
        courseProgrammingWithVariables(),
        courseThinkingInPython(),
        courseProgrammingWithFunctions(),
        courseAlgorithmicThinking(),
        courseCsFundamentals(),
        courseNeuralNetworks()
    )

    val badges: List<Badge> = listOf(
        Badge("first-steps", "First Steps", "Complete your first lesson.", "👣", 1),
        Badge("thinking-coder", "Thinking Coder", "Finish the Thinking in Code course.", "💡", 1),
        Badge("variable-starter", "Variable Starter", "Finish Programming with Variables.", "📦", 1),
        Badge("function-builder", "Function Builder", "Finish Programming with Functions.", "🔧", 1),
        Badge("algorithm-explorer", "Algorithm Explorer", "Finish Algorithmic Thinking.", "🧭", 1),
        Badge("cs-rookie", "CS Rookie", "Finish Computer Science Fundamentals.", "💻", 1),
        Badge("neural-beginner", "Neural Beginner", "Finish Introduction to Neural Networks.", "🧠", 1)
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
        val ordered = courses.sortedBy { it.order }
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
        description = "Learn how to break problems into clear instructions.",
        order = 0,
        icon = "🧠",
        lessons = listOf(
            Lesson(
                id = "tic-l1",
                courseId = "thinking-in-code",
                title = "What is a Program?",
                description = "Understand what a program is and how simple instructions can control a computer.",
                content = "A computer program is a set of step-by-step instructions that a machine can follow. " +
                    "Programs turn a goal into ordered actions—just like directions that must happen in sequence.",
                order = 0,
                example = "Question → answer → guided steps → recap before moving on.",
                activities = listOf(
                    ActivityItem(
                        id = "tic-l1-a1",
                        lessonId = "tic-l1",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Which statement best describes a computer program?",
                        difficultyLabel = "Foundational skill",
                        options = listOf(
                            "A list of physical computer parts",
                            "Step-by-step instructions a computer can follow",
                            "A social media post about technology",
                            "A cable that connects two screens"
                        ),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! A program is a set of instructions that tells a computer what to do.",
                        incorrectFeedback = "Not quite. A program is not a physical part or a cable. Let's break it down.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Think about what computers need",
                                explanation = "Computers do not decide actions by themselves. They need clear instructions."
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Understand what instructions do",
                                explanation = "Instructions tell the computer what action to perform and in what order."
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Connect it to a program",
                                explanation = "A program is a group of instructions written to solve a task."
                            )
                        ),
                        finalResult = "A computer program is a set of step-by-step instructions that a computer can follow.",
                        finalOutput = null,
                        requiresProcessRevealBeforeFinal = false
                    ),
                    ActivityItem(
                        id = "tic-l1-a2",
                        lessonId = "tic-l1",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Which example is most like a simple program?",
                        difficultyLabel = "Foundational skill",
                        options = listOf(
                            "A recipe that lists steps for cooking rice",
                            "A random picture saved on a phone",
                            "A broken keyboard",
                            "A phone charger"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! A recipe is similar to a program because it follows ordered steps.",
                        incorrectFeedback = "Not quite. A simple program works like a set of ordered steps.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Look for ordered instructions",
                                explanation = "A program has steps that must be followed in a clear order."
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Compare the choices",
                                explanation = "A recipe gives instructions like wash rice, add water, cook, and serve."
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Match the idea",
                                explanation = "That is similar to how a program tells a computer what to do step by step."
                            )
                        ),
                        finalResult = "A recipe is most like a simple program because it follows ordered instructions.",
                        finalOutput = null,
                        requiresProcessRevealBeforeFinal = false
                    ),
                    ActivityItem(
                        id = "tic-l1-a3",
                        lessonId = "tic-l1",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "What does a program tell a computer to do?",
                        difficultyLabel = "Foundational skill",
                        options = listOf(
                            "Ignore all commands",
                            "Follow instructions to complete a task",
                            "Turn into a human",
                            "Stop using electricity"
                        ),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! A program gives instructions so the computer can complete a task.",
                        incorrectFeedback = "Not quite. A program gives the computer actions to follow.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Identify the purpose of a program",
                                explanation = "A program exists to make the computer do something useful."
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Understand the role of commands",
                                explanation = "Commands are instructions that tell the computer what action to perform."
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Complete the idea",
                                explanation = "When the instructions run together, they help complete a task."
                            )
                        ),
                        finalResult = "A program tells a computer to follow instructions to complete a task.",
                        finalOutput = null,
                        requiresProcessRevealBeforeFinal = false
                    ),
                    ActivityItem(
                        id = "tic-l1-a4",
                        lessonId = "tic-l1",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Why does the order of instructions matter in a program?",
                        difficultyLabel = "Foundational skill",
                        options = listOf(
                            "Because computers follow instructions in sequence",
                            "Because computers only read colors",
                            "Because programs do not use logic",
                            "Because order never matters"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! Computers usually follow instructions in the order they are written.",
                        incorrectFeedback = "Not quite. The order matters because the computer follows steps one after another.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Think about sequence",
                                explanation = "A sequence means one step happens after another."
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Apply it to programs",
                                explanation = "If instructions are in the wrong order, the result may also be wrong."
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Simple example",
                                explanation = "You cannot print a result before calculating it. The calculation must happen first."
                            )
                        ),
                        finalResult = "The order matters because computers follow instructions in sequence.",
                        finalOutput = null,
                        requiresProcessRevealBeforeFinal = false
                    ),
                    ActivityItem(
                        id = "tic-l1-a5",
                        lessonId = "tic-l1",
                        type = ActivityType.MULTIPLE_CHOICE,
                        prompt = "Which set of instructions is most complete for making the robot find the red target?",
                        difficultyLabel = "Foundational skill",
                        options = listOf(
                            "Turn right, move forward, turn right, move forward, select red",
                            "Select red, move forward, turn right",
                            "Turn left, select red, move backward",
                            "Move forward only"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct! The steps are complete and ordered properly.",
                        incorrectFeedback = "Not quite. The robot needs a complete and properly ordered sequence.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Start with movement",
                                explanation = "The robot must first move from its starting position."
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Change direction",
                                explanation = "The robot turns right to face the path toward the red target."
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Move to the target",
                                explanation = "The robot moves forward again to reach the red target."
                            ),
                            ProcessStep(
                                stepNumber = 4,
                                title = "Select the target",
                                explanation = "The robot can only select red after reaching the red tile."
                            )
                        ),
                        finalResult = "A complete path turns toward the target, moves forward along that facing, then selects red only on the red tile.",
                        finalOutput = null,
                        requiresProcessRevealBeforeFinal = false
                    )
                ),
                pathCardSubtitle = "Understand what a program is and how instructions control a computer."
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
                    )
                )
            ),
            Lesson(
                id = "tic-l3",
                courseId = "thinking-in-code",
                title = "Input, Process, Output",
                description = "Most programs follow: receive data, work on it, show a result.",
                content = "Most programs follow a simple pattern. They receive input, process it, then produce output.",
                order = 2,
                example = "A login form: input (typed password), process (compare hash), output (success or error message).",
                activities = listOf(
                    ActivityItem(
                        id = "tic-l3-a1",
                        lessonId = "tic-l3",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What is the output?",
                        difficultyLabel = "Trace the program",
                        codeSnippet = "x = 5\ny = 2\nprint(x + y)",
                        options = listOf("52", "7", "x + y", "Error"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct! The program adds the two numeric values.",
                        incorrectFeedback = "Not quite. Let's walk through the process.",
                        processSteps = listOf(
                            ProcessStep(
                                stepNumber = 1,
                                title = "Read the variables",
                                explanation = "x stores the value 5 and y stores the value 2.",
                                codeBlock = "x = 5\ny = 2"
                            ),
                            ProcessStep(
                                stepNumber = 2,
                                title = "Understand the operation",
                                explanation = "The plus sign adds the values stored in x and y.",
                                highlightedCommand = "+",
                                miniVisualHint = "arithmetic add"
                            ),
                            ProcessStep(
                                stepNumber = 3,
                                title = "Calculate the result",
                                explanation = "5 + 2 equals 7.",
                                miniVisualHint = "= 7"
                            )
                        ),
                        finalResult = "The correct answer is 7.",
                        finalOutput = "7"
                    )
                )
            ),
            Lesson(
                id = "tic-l4",
                courseId = "thinking-in-code",
                title = "Debugging Simple Logic",
                description = "Find mismatches between what you meant and what you wrote.",
                content = "Debugging starts by comparing expected behavior to actual behavior.",
                order = 3,
                example = "If login always fails, check whether you compare the right fields and types.",
                activities = listOf(
                    ActivityItem(
                        id = "tic-l4-a1",
                        lessonId = "tic-l4",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "The program should print PASS for score = 60. What should be fixed?",
                        difficultyLabel = "Debug reasoning",
                        codeSnippet = "score = 60\nif score > 60:\n    print(\"PASS\")\nelse:\n    print(\"REVIEW\")",
                        options = listOf(
                            "Change score > 60 to score >= 60",
                            "Change print(\"PASS\") to print(\"REVIEW\")",
                            "Delete the else block",
                            "Set score = 0"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Nice catch. The boundary value 60 must be included.",
                        incorrectFeedback = "Check the boundary condition again.",
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
                                explanation = "Use >= 60 so the logic matches the requirement."
                            )
                        ),
                        finalResult = "Boundary bug fixed: score >= 60.",
                        finalOutput = null
                    )
                )
            )
        )
    )

    private fun courseProgrammingWithVariables(): Course = Course(
        id = "programming-variables",
        title = "Programming with Variables",
        description = "Understand how data is stored, named, and reused.",
        order = 1,
        icon = "📦",
        lessons = listOf(
            Lesson(
                id = "pvar-l1",
                courseId = "programming-variables",
                title = "What is a Variable?",
                description = "A named box that holds a value you can reuse.",
                content = "A variable stores a value under a name so you can read or update it later.",
                order = 0,
                example = "score = 0 then score = score + 10 after a correct answer.",
                activities = listOf(
                    ActivityItem(
                        id = "pvar-l1-a1",
                        lessonId = "pvar-l1",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What will be printed?",
                        difficultyLabel = "Core concept",
                        codeSnippet = "coins = 3\ncoins = coins + 2\nprint(coins)",
                        options = listOf("3", "2", "5", "32"),
                        correctAnswerIndex = 2,
                        correctFeedback = "Correct. The variable updates to 5.",
                        incorrectFeedback = "Track how coins changes line by line.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Initial value", explanation = "coins starts at 3."),
                            ProcessStep(stepNumber = 2, title = "Update", explanation = "coins + 2 gives 5, then coins becomes 5."),
                            ProcessStep(stepNumber = 3, title = "Print", explanation = "print(coins) shows 5.")
                        ),
                        finalResult = "The output is 5.",
                        finalOutput = "5"
                    )
                )
            ),
            Lesson(
                id = "pvar-l2",
                courseId = "programming-variables",
                title = "Naming Variables",
                description = "Pick clear names so logic is easy to follow.",
                content = "Names should describe the meaning of the data—like userScore, not just x—unless the context is tiny.",
                order = 1,
                activities = listOf(
                    ActivityItem(
                        id = "pvar-l2-a1",
                        lessonId = "pvar-l2",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "Which variable name best improves readability for a user's points?",
                        difficultyLabel = "Naming practice",
                        codeSnippet = "p = 120\nprint(p)",
                        options = listOf("p", "u", "userPoints", "z1"),
                        correctAnswerIndex = 2,
                        correctFeedback = "Yes. Clear names make code easier to understand.",
                        incorrectFeedback = "Choose the name that explains the data meaning.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Look at the value meaning", explanation = "120 refers to points, not a random number."),
                            ProcessStep(stepNumber = 2, title = "Prefer descriptive names", explanation = "userPoints communicates purpose directly."),
                            ProcessStep(stepNumber = 3, title = "Avoid vague letters", explanation = "Single letters hide intent outside math contexts.")
                        ),
                        finalResult = "Best variable name: userPoints.",
                        finalOutput = null
                    )
                )
            ),
            Lesson(
                id = "pvar-l3",
                courseId = "programming-variables",
                title = "Data Types",
                description = "Different kinds of data behave differently in operations.",
                content = "Numbers, text, and true/false behave differently. Types help the computer apply the right rules.",
                order = 2,
                activities = listOf(
                    ActivityItem(
                        id = "pvar-l3-a1",
                        lessonId = "pvar-l3",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What does this print?",
                        difficultyLabel = "Type awareness",
                        codeSnippet = "a = \"5\"\nb = \"2\"\nprint(a + b)",
                        options = listOf("7", "52", "Error", "5 2"),
                        correctAnswerIndex = 1,
                        correctFeedback = "Correct. Strings concatenate into \"52\".",
                        incorrectFeedback = "Both values are text, so + joins them.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Check the quotes", explanation = "Quoted values are strings."),
                            ProcessStep(stepNumber = 2, title = "Apply string +", explanation = "For strings, + means join, not arithmetic."),
                            ProcessStep(stepNumber = 3, title = "Compute result", explanation = "\"5\" + \"2\" becomes \"52\".")
                        ),
                        finalResult = "The output is 52.",
                        finalOutput = "52"
                    )
                )
            ),
            Lesson(
                id = "pvar-l4",
                courseId = "programming-variables",
                title = "Updating Values",
                description = "Variables can change as your program runs.",
                content = "Assignment updates what a name points to.",
                order = 3,
                activities = listOf(
                    ActivityItem(
                        id = "pvar-l4-a1",
                        lessonId = "pvar-l4",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What is the final output?",
                        difficultyLabel = "State tracking",
                        codeSnippet = "lives = 3\nlives = lives - 1\nlives = lives - 1\nprint(lives)",
                        options = listOf("3", "2", "1", "0"),
                        correctAnswerIndex = 2,
                        correctFeedback = "Correct. Lives decreases from 3 to 1.",
                        incorrectFeedback = "Trace each assignment update in order.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Start", explanation = "lives = 3"),
                            ProcessStep(stepNumber = 2, title = "First update", explanation = "After -1, lives = 2"),
                            ProcessStep(stepNumber = 3, title = "Second update", explanation = "After another -1, lives = 1")
                        ),
                        finalResult = "The output is 1.",
                        finalOutput = "1"
                    )
                )
            )
        )
    )

    private fun courseThinkingInPython(): Course = Course(
        id = "thinking-python",
        title = "Thinking in Python",
        description = "Start reading and writing simple Python-style logic.",
        order = 2,
        icon = "🐍",
        lessons = listOf(
            Lesson(
                id = "tp-l1",
                courseId = "thinking-python",
                title = "Python Syntax Basics",
                description = "Indentation and colons shape how blocks run.",
                content = "Python uses indentation to group statements.",
                order = 0,
                activities = listOf(
                    ActivityItem(
                        id = "tp-l1-a1",
                        lessonId = "tp-l1",
                        type = ActivityType.DEBUG_CODE,
                        prompt = "Which line is required to make this Python if-block valid?",
                        difficultyLabel = "Syntax basics",
                        codeSnippet = "score = 10\nif score > 5\n    print(\"ok\")",
                        options = listOf(
                            "Add : after if score > 5",
                            "Remove indentation from print",
                            "Change score to a string",
                            "Delete print"
                        ),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct. Python if statements need a colon.",
                        incorrectFeedback = "Look at Python block syntax after conditions.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Find syntax marker", explanation = "Python uses : to start a block."),
                            ProcessStep(stepNumber = 2, title = "Keep indentation", explanation = "Indented lines belong to the if block."),
                            ProcessStep(stepNumber = 3, title = "Apply fix", explanation = "if score > 5: is the valid header.")
                        ),
                        finalResult = "Fixed syntax: add colon after condition.",
                        finalOutput = null
                    )
                )
            ),
            Lesson(
                id = "tp-l2",
                courseId = "thinking-python",
                title = "Simple Control Flow",
                description = "if / else routes your program based on conditions.",
                content = "Control flow chooses which lines run next.",
                order = 1,
                activities = listOf(
                    ActivityItem(
                        id = "tp-l2-a1",
                        lessonId = "tp-l2",
                        type = ActivityType.OUTPUT_TRACING,
                        prompt = "What will be printed?",
                        difficultyLabel = "Control flow trace",
                        codeSnippet = "temp = 31\nif temp > 30:\n    print(\"hot\")\nelse:\n    print(\"cool\")",
                        options = listOf("hot", "cool", "31", "error"),
                        correctAnswerIndex = 0,
                        correctFeedback = "Correct. Condition is true, so \"hot\" prints.",
                        incorrectFeedback = "Check whether temp > 30 is true or false.",
                        processSteps = listOf(
                            ProcessStep(stepNumber = 1, title = "Evaluate condition", explanation = "31 > 30 is true."),
                            ProcessStep(stepNumber = 2, title = "Choose branch", explanation = "True means run the if branch."),
                            ProcessStep(stepNumber = 3, title = "Output", explanation = "The program prints hot.")
                        ),
                        finalResult = "The output is hot.",
                        finalOutput = "hot"
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
