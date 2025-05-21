package com.example.aichatapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aichatapp.screens.ChatScreen
import com.example.aichatapp.screens.HomeScreen
import com.example.aichatapp.ui.theme.AIChatAppTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIChatAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = HomeScreen) {
                    composable<HomeScreen> {
                        HomeScreen(navController = navController)
                    }
                    composable<ChatScreen> {
                        ChatScreen(navController = navController)
                    }
                }
            }
        }
    }
}