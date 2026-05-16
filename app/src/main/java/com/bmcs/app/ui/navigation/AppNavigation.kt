package com.bmcs.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bmcs.app.ui.components.BottomNavBar
import com.bmcs.app.ui.screens.ProfileScreen
import com.bmcs.app.ui.screens.StoreScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var currentRoute by rememberSaveable { mutableStateOf(BottomNavItem.Perfil.route) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onItemSelected = { item ->
                    currentRoute = item.route
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Perfil.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Inicio.route) {
                // Placeholder - redirects to Profile for now
                ProfileScreen()
            }
            composable(BottomNavItem.Album.route) {
                // Placeholder - redirects to Profile for now
                ProfileScreen()
            }
            composable(BottomNavItem.Tienda.route) {
                StoreScreen()
            }
            composable(BottomNavItem.Perfil.route) {
                ProfileScreen()
            }
        }
    }
}
