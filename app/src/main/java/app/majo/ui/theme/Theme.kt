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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import app.majo.ui.screens.settings.SettingsViewModel


// --- 1. ХЕЛПЕР ДЛЯ БЕЗОПАСНОГО ПОИСКА ACTIVITY ---
/**
 * Расширение Context для безопасного поиска корневой Activity в цепочке ContextWrapper.
 *
 * Требуется для выполнения системных операций, не входящих в Compose,
 * таких как изменение цвета системного статус-бара через Window.
 *
 * @return Корневой объект [Activity].
 * @throws IllegalStateException Если Composable не прикреплен к Activity.
 */
private fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Compose View is not attached to an Activity")
}


// --- 2. ЦВЕТОВЫЕ СХЕМЫ (ColorScheme) ---
/**
 * Цветовая схема для Темной темы (Dark Theme).
 * Использует цвета с суффиксом 80 (например, Purple80).
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
    // ... другие цвета
)

/**
 * Цветовая схема для Светлой темы (Light Theme).
 * Использует цвета с суффиксом 40 (например, Purple40).
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    // ... другие цвета
)


// --- 3. ГЛАВНАЯ ФУНКЦИЯ ТЕМЫ ---
/**
 * Применяет тему Material 3 к приложению (или части UI).
 *
 * Эта функция является реактивной и перерисовывается, если состояние [SettingsViewModel] меняется.
 *
 * @param settingsViewModel ViewModel, которая предоставляет текущее состояние [isDarkMode].
 * @param content Содержимое UI, к которому применяется тема.
 */
@Composable
fun MaJoTheme(
    settingsViewModel: SettingsViewModel,
    content: @Composable () -> Unit
) {
    // Считываем состояние isDarkMode из ViewModel реактивно
    val state by settingsViewModel.state.collectAsState()
    val useDarkMode = state.isDarkMode

    // Измененный код: генерируем схему динамически
    val colorScheme = createColorScheme(
        isDark = useDarkMode,
        accentColorName = state.currentAccentColor
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        /**
         * SideEffect (Побочный эффект):
         * Выполняет не-Compose операции (системные вызовы) внутри цикла композиции.
         * Здесь используется для синхронизации цвета системного статус-бара
         * с цветом основной темы Compose.
         */
        SideEffect {
            val window = view.context.findActivity().window
            // Устанавливаем цвет статус-бара в ARGB формате
            window.statusBarColor = colorScheme.primary.toArgb()

            // Если нужно управлять светлыми иконками в статус-баре:
            // WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkMode
        }
    }

    /**
     * Корневой Composable для применения темы.
     * Все вложенные Composable-функции получают доступ к этим значениям через [MaterialTheme].
     */
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Предполагается, что Typography определен
        content = content
    )
}



// Вспомогательная функция для генерации ColorScheme
@Composable
fun createColorScheme(
    isDark: Boolean,
    accentColorName: String
): ColorScheme {
    val primaryColor = getColorByName(accentColorName, isLight = !isDark)

    // В реальном приложении нужно вычислить или задать остальные цвета
    // на основе primary. Здесь для простоты используем существующие PurpleGrey.
    return if (isDark) {
        darkColorScheme(
            primary = primaryColor,
            secondary = PurpleGrey80,
            tertiary = Pink80
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            secondary = PurpleGrey40,
            tertiary = Pink40
        )
    }
}