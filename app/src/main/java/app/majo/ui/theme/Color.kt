package app.majo.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Файл, содержащий базовую палитру цветов для темы приложения.
 *
 * Цвета названы по конвенции Material 3 (M3):
 * - Цвета с суффиксом '80' используются в **Темной теме (Dark Theme)**.
 * - Цвета с суффиксом '40' используются в **Светлой теме (Light Theme)**.
 *
 * Они являются основой для создания полных цветовых схем (ColorScheme).
 */

// --- Цвета для Темной темы (Dark Theme - 80) ---
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

// --- Цвета для Светлой темы (Light Theme - 40) ---
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)



// Новые цвета для примера:
val Blue500 = Color(0xFF2196F3)
val Blue200 = Color(0xFF90CAF9)

val Green500 = Color(0xFF4CAF50)
val Green200 = Color(0xFFA5D6A7)

val Red500 = Color(0xFFAF4C4C)
val Red200 = Color(0xFFD6A5A5)


fun getColorByName(name: String, isLight: Boolean): Color {
    return when (name) {
        "Blue" -> if (isLight) Blue500 else Blue200
        "Green" -> if (isLight) Green500 else Green200
        "Purple" -> if (isLight) Purple40 else Purple80
        "Red" -> if (isLight) Red500 else Red200
        else -> if (isLight) Purple40 else Purple80 // По умолчанию
    }
}