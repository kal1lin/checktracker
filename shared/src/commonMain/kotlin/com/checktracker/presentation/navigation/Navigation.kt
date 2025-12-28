package com.checktracker.presentation.navigation

/**
 * Навигация между экранами приложения
 */
sealed class Screen {
    object Dashboard : Screen()
    object Camera : Screen()
    object Analytics : Screen()
    object History : Screen()
    object Settings : Screen()
    
    fun route(): String = this::class.simpleName ?: "dashboard"
}

/**
 * Bottom Navigation items
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: String
) {
    companion object {
        val items = listOf(
            BottomNavItem(Screen.Dashboard, "Главная", "📊"),
            BottomNavItem(Screen.Camera, "Сканер", "📷"),
            BottomNavItem(Screen.Analytics, "Аналитика", "📈"),
            BottomNavItem(Screen.History, "История", "📋"),
            BottomNavItem(Screen.Settings, "Параметры", "⚙️")
        )
    }
}
