package com.plantcontrol.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.plantcontrol.app.ui.MainViewModel
import com.plantcontrol.app.ui.screens.catalog.PlantCatalogScreen
import com.plantcontrol.app.ui.screens.dashboard.AnalyticsDashboardScreen
import com.plantcontrol.app.ui.screens.history.HistoryScreen
import com.plantcontrol.app.ui.screens.home.HomeScreen
import com.plantcontrol.app.ui.screens.settings.SettingsScreen
import com.plantcontrol.app.ui.theme.PlantControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            PlantControlTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "home"

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == "home",
                                onClick = { navController.navigate("home") },
                                label = { Text("Início") },
                                icon = { Text("🏠") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "plants",
                                onClick = { navController.navigate("plants") },
                                label = { Text("Plantas") },
                                icon = { Text("🌿") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "history",
                                onClick = { navController.navigate("history") },
                                label = { Text("Histórico") },
                                icon = { Text("📋") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "dashboard",
                                onClick = { navController.navigate("dashboard") },
                                label = { Text("Dashboard") },
                                icon = { Text("📊") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "settings",
                                onClick = { navController.navigate("settings") },
                                label = { Text("Ajustes") },
                                icon = { Text("⚙") }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(viewModel, onNavigateToPlants = { navController.navigate("plants") })
                        }
                        composable("plants") {
                            PlantCatalogScreen(
                                viewModel = viewModel,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("history") { HistoryScreen(viewModel) }
                        composable("dashboard") { AnalyticsDashboardScreen(viewModel) }
                        composable("settings") { SettingsScreen(viewModel) }
                    }
                }
            }
        }
    }
}