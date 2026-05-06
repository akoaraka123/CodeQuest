package com.example.codequest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.codequest.ui.screens.CodeQuestApp
import com.example.codequest.ui.theme.CodeQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeQuestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    CodeQuestApp()
                }
            }
        }
    }
}