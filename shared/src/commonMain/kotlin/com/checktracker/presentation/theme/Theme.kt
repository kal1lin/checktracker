package com.checktracker.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Цветовая схема приложения
 */
object CheckTrackerColors {
    // Primary colors
    val primaryLight = Color(0xFF6366F1)
    val primaryDark = Color(0xFF4F46E5)
    val primaryVeryDark = Color(0xFF3B39C8)
    
    // Accent colors
    val accentLight = Color(0xFF8B5CF6)
    val accentDark = Color(0xFF7C3AED)
    
    // Semantic colors
    val success = Color(0xFF10B981)
    val error = Color(0xFFEF4444)
    val warning = Color(0xFFF59E0B)
    val info = Color(0xFF3B82F6)
    
    // Neutral colors
    val neutral50 = Color(0xFFFAFAFA)
    val neutral100 = Color(0xFFF5F5F5)
    val neutral200 = Color(0xFFEEEEEE)
    val neutral300 = Color(0xFFE0E0E0)
    val neutral400 = Color(0xFFBDBDBD)
    val neutral500 = Color(0xFF9E9E9E)
    val neutral600 = Color(0xFF757575)
    val neutral700 = Color(0xFF616161)
    val neutral800 = Color(0xFF424242)
    val neutral900 = Color(0xFF212121)
    
    val lightScheme = lightColorScheme(
        primary = primaryLight,
        secondary = accentLight,
        tertiary = info,
        background = neutral50,
        surface = Color.White,
        error = error
    )
    
    val darkScheme = darkColorScheme(
        primary = primaryLight,
        secondary = accentLight,
        tertiary = info,
        background = neutral900,
        surface = neutral800,
        error = error
    )
}

/**
 * Категория расходов с цветом
 */
object CategoryColors {
    val foodColor = Color(0xFFFF6B6B)
    val transportColor = Color(0xFF4ECDC4)
    val homeColor = Color(0xFFFFA500)
    val entertainmentColor = Color(0xFF9B59B6)
    val healthColor = Color(0xFF2ECC71)
    val utilitiesColor = Color(0xFF3498DB)
    val incomeColor = Color(0xFF27AE60)
    val otherColor = Color(0xFF95A5A6)
}
