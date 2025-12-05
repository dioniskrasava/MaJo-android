package app.majo.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import app.majo.ui.screens.settings.SettingsViewModel


// --- 1. ХЕЛПЕР ДЛЯ БЕЗОПАСНОГО ПОИСКА ACTIVITY ---
/**
 * Расширение Context для безопасного поиска Activity в цепочке ContextWrapper.
 * Нужно для доступа к окну (Window) и статусу бара.
 */
private fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Compose View is not attached to an Activity")
}


// --- 2. ЦВЕТОВЫЕ СХЕМЫ (Здесь используются стандартные заглушки) ---
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
    // ... другие цвета
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    // ... другие цвета
)


// --- 3. ГЛАВНАЯ ФУНКЦИЯ ТЕМЫ ---
@Composable
fun MaJoTheme(
    // !!! ГЛАВНОЕ ИЗМЕНЕНИЕ: ViewModel передается как обязательный аргумент
    settingsViewModel: SettingsViewModel,
    content: @Composable () -> Unit
) {

    // Считываем состояние isDarkMode из ViewModel
    // Если ViewModel меняет isDarkMode, этот блок кода РЕАКТИВНО перезапустится
    val state by settingsViewModel.state.collectAsState()
    val useDarkMode = state.isDarkMode

    // Выбираем цветовую схему
    val colorScheme = if (useDarkMode) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity().window
            window.statusBarColor = colorScheme.primary.toArgb()
            // Если нужно управлять светлыми иконками в статус-баре:
            // WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkMode
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}