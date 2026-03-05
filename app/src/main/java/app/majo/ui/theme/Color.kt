package app.majo.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme


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
val PurpleGrey80 = Color(0xFFD2C5EF)
val Pink80 = Color(0xFFEFB8C8)

// --- Цвета для Светлой темы (Light Theme - 40) ---
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF9886C9)
val Pink40 = Color(0xFF64527D)



// Новые цвета для примера:
val Blue500 = Color(0xFF2196F3)
val Blue200 = Color(0xFF90CAF9)

val Green500 = Color(0xFF4CAF50)
val Green200 = Color(0xFFA5D6A7)

val Red500 = Color(0xFFAF4C4C)
val Red200 = Color(0xFFD6A5A5)




// NEW COLOR (OLD MAYBE DELETE)

// Цвета для фиолетовой темы
val PurpleLight = lightColorScheme(
    primary = Purple40,                                     //     ОСНОВНОЙ ЦВЕТ
    onPrimary = Color.White,                                //     (НА ОСНОВНОМ)    цвет текста/иконок
    primaryContainer = Purple80.copy(alpha = 0.5f),         //     КОНТЕЙНЕР ОСНОВНОГО ЦВЕТА
    onPrimaryContainer = Purple40,                          //     На ____
    secondary = PurpleGrey40,                               //     ДОПОЛНИТЕЛЬНЫЙ ЦВЕТ (ВТОРИЧНЫЙ)
    onSecondary = Color.White,                              //     НА дополнительном
    tertiary = Pink40,                                      //     ТРЕТИЧНЫЙ ЦВЕТ
    background = Color(0xFFF8F1FD),                  //     ФОНОВЫЙ ЦВЕТ
    surface = Color(0xFFFFFBFE),                     //     ЦВЕТ ПОВЕРХНОСТИ (карточки, меню)
    surfaceVariant = PurpleGrey80,                          //     ВАРИАНТ ПОВЕРХНОСТИ
    error = Red500,                                         //     ЦВЕТ ОШИБКИ
                                                            //     onError  -  НА ошибке
    // ... остальные цвета
)

val PurpleDark = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple40,
    primaryContainer = Purple40.copy(alpha = 0.5f),
    onPrimaryContainer = Purple80,
    secondary = PurpleGrey80,
    onSecondary = PurpleGrey40,
    tertiary = Pink80,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = PurpleGrey40,
    error = Red200,
    // ...
)

// Цвета для синей темы
val BlueLight = lightColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    primaryContainer = Blue200.copy(alpha = 0.5f),
    onPrimaryContainer = Blue500,
    secondary = Color(0xFF03A9F4), // какой-то синий оттенок
    onSecondary = Color.White,                              //     НА дополнительном
    tertiary = Pink40,                                      //     ТРЕТИЧНЫЙ ЦВЕТ
    background = Color(0xFFFFFBFE),                  //     ФОНОВЫЙ ЦВЕТ
    surface = Color(0xFFFFFBFE),                     //     ЦВЕТ ПОВЕРХНОСТИ (карточки, меню)
    surfaceVariant = PurpleGrey80,                          //     ВАРИАНТ ПОВЕРХНОСТИ
    error = Red500,
    // ...
)

val BlueDark = darkColorScheme(
    primary = Blue200,
    onPrimary = Blue500,
    primaryContainer = Blue500.copy(alpha = 0.5f),
    onPrimaryContainer = Blue200,
    // ...
)

val GreenLight = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Purple80.copy(alpha = 0.5f),
    onPrimaryContainer = Purple40,
    secondary = PurpleGrey40,
    onSecondary = Color.White,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    surfaceVariant = PurpleGrey80.copy(alpha = 0.3f),
    error = Red500,
    // ... остальные цвета
)

val GreenDark = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple40,
    primaryContainer = Purple40.copy(alpha = 0.5f),
    onPrimaryContainer = Purple80,
    secondary = PurpleGrey80,
    onSecondary = PurpleGrey40,
    tertiary = Pink80,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = PurpleGrey40.copy(alpha = 0.3f),
    error = Red200,
    // ...
)

val RedLight = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Purple80.copy(alpha = 0.5f),
    onPrimaryContainer = Purple40,
    secondary = PurpleGrey40,
    onSecondary = Color.White,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    surfaceVariant = PurpleGrey80.copy(alpha = 0.3f),
    error = Red500,
    // ... остальные цвета
)

val RedDark = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple40,
    primaryContainer = Purple40.copy(alpha = 0.5f),
    onPrimaryContainer = Purple80,
    secondary = PurpleGrey80,
    onSecondary = PurpleGrey40,
    tertiary = Pink80,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = PurpleGrey40.copy(alpha = 0.3f),
    error = Red200,
    // ...
)


fun getColorByName(name: String, isLight: Boolean): Color {
    return when (name) {
        "Blue" -> if (isLight) Blue500 else Blue200
        "Green" -> if (isLight) Green500 else Green200
        "Purple" -> if (isLight) Purple40 else Purple80
        "Red" -> if (isLight) Red500 else Red200
        else -> if (isLight) Purple40 else Purple80 // По умолчанию
    }
}