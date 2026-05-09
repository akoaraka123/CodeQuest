package com.example.codequest.state

enum class LessonInteractionState {
    ACTIVITY,
    FEEDBACK,
    PROCESS_REVEAL,
    FINAL_RESULT,
    /** Lesson 1 only: snapshot of correct answer + explanation after exhausting attempts. */
    LESSON1_ANSWER_REVEAL
}
