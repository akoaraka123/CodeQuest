package com.example.codequest.state

enum class LessonInteractionState {
    ACTIVITY,
    /** Thinking in Code Lesson 2: step-by-step robot puzzle guide before the first challenge. */
    ROBOT_DEMO_GUIDE,
    FEEDBACK,
    PROCESS_REVEAL,
    FINAL_RESULT,
    /** Snapshot of correct answer + explanation after exhausting attempts. */
    LESSON1_ANSWER_REVEAL
}
