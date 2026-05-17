package com.example.codequest.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberCodeQuestAppState(): CodeQuestAppState {
    val context = LocalContext.current
    return remember {
        CodeQuestPreferences.install(context)
        CodeQuestAppState()
    }
}
