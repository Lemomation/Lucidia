package com.lucidia.app.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Journal : Screen("journal", "Dream Journal")
    object Graph : Screen("graph", "Obsidian Graph")
    object Settings : Screen("settings", "Settings")
}
