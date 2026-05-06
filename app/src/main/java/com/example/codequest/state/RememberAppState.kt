package com.example.codequest.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberCodeQuestAppState(): CodeQuestAppState = remember { CodeQuestAppState() }
