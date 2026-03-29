package app.majo.ui.util

import androidx.annotation.StringRes
import app.majo.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, @StringRes val titleRes: Int, val icon: ImageVector) {
    // Новый главный экран
    data object Records : Screen("records", R.string.records_title, Icons.Filled.List)
    data object Activities : Screen("activities", R.string.activities_title, Icons.Filled.Star)
    data object AddRecord : Screen("addRecord", R.string.add_record_title, Icons.Filled.List)
    data object AddActivity : Screen("addActivity", R.string.add_activity_title, Icons.Filled.Star)
    data object Settings : Screen("settings", R.string.settings_title, Icons.Filled.Settings)
    data object Logs : Screen("logs", R.string.logs_title, Icons.Filled.List)
    data object Statistics : Screen("statistics", R.string.statistics_title, Icons.Filled.BarChart)

    // Маршрут для редактирования (с аргументом)
    fun withArgs(vararg args: String): String = buildString {
        append(route)
        args.forEach { append("/$it") }
    }
}