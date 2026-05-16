package com.bmcs.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bmcs.app.ui.navigation.AppNavigation
import com.bmcs.app.ui.theme.BMCSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BMCSTheme {
                AppNavigation()
            }
        }
    }
}
