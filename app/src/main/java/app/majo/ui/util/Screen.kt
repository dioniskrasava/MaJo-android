package app.majo.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    // Новый главный экран
    data object Records : Screen("records", "Записи", Icons.Filled.List)

    // Экран управления активностями
    data object Activities : Screen("activities", "Активности", Icons.Filled.Star)

    // Экран добавления записи (отдельный маршрут, не в нижнем баре)
    data object AddRecord : Screen("addRecord", "Добавить запись", Icons.Filled.List)

    // Экран добавления/редактирования типа активности
    data object AddActivity : Screen("addActivity", "Новый тип", Icons.Filled.Star)

    // Экран настроек (если он есть)
    data object Settings : Screen("settings", "Настройки", Icons.Filled.Star)

    // Маршрут для редактирования (с аргументом)
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}