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
val Purple80 = Color(0xFF6C6481)
val PurpleGrey80 = Color(0xFFD2C5EF)
val Pink80 = Color(0xFFEFB8C8)

// --- Цвета для Светлой темы (Light Theme - 40) ---
val Purple40 = Color(0xFF606379)
val PurpleGrey40 = Color(0xFF28194B)
val Pink40 = Color(0xFF64527D)



// Новые цвета для примера:
val Blue500 = Color(0xFF2196F3)
val Blue200 = Color(0xFFA3F990)

val Green500 = Color(0xFF4CAF50)
val Green200 = Color(0xFFA5D6A7)

val Red500 = Color(0xFFAF4C4C)
val Red200 = Color(0xFFD6A5A5)




// NEW COLOR (OLD MAYBE DELETE)

// Цвета для фиолетовой темы
val PurpleLight = lightColorScheme(
    primary = Color(0xFF4E21F3),                                     //     ОСНОВНОЙ ЦВЕТ
    onPrimary = Color.White,                                //     (НА ОСНОВНОМ)    цвет текста/иконок
    primaryContainer = Color(0xFF2196F3),         //     КОНТЕЙНЕР ОСНОВНОГО ЦВЕТА
    onPrimaryContainer = Color(0xFF6021F3),                          //     На ____
    secondary = Color(0xFF351391),                                //     ДОПОЛНИТЕЛЬНЫЙ ЦВЕТ (ВТОРИЧНЫЙ)
    onSecondary = Color.White,                              //     НА дополнительном
    tertiary = Color(0xFF4E21C9),                                      //     ТРЕТИЧНЫЙ ЦВЕТ
    background = Color(0xFFFDFBFF),                  //     ФОНОВЫЙ ЦВЕТ
    surface = Color(0xFFFCFBFF),                     //     ЦВЕТ ПОВЕРХНОСТИ (карточки, меню)
    surfaceVariant = Color(0xFFCBC2E7),                          //     ВАРИАНТ ПОВЕРХНОСТИ
    error = Red500,                                         //     ЦВЕТ ОШИБКИ
                                                            //     onError  -  НА ошибке
    // ... остальные цвета
)

val PurpleDark = darkColorScheme(
    primary = Color(0xFF3819A9),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF4E21F3),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF251570),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF372683),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = PurpleGrey40,
    error = Red200,
    // ...
)

// Цвета для синей темы
val BlueLight = lightColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2196F3),
    onPrimaryContainer = Color(0xFF2196F3),
    secondary = Color(0xFF135891),
    onSecondary = Color.White,
    tertiary = Color(0xFF1169AF),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    surfaceVariant = Color(0xFFB3D5F3),
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
    primary = Color(0xFF28F321),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2196F3),
    onPrimaryContainer = Color(0xFF59F321),
    secondary = Color(0xFF269113),
    onSecondary = Color.White,
    tertiary = Color(0xFF48AF11),
    background = Color(0xFFFCFFFB),
    surface = Color(0xFFFDFFFB),
    surfaceVariant = Color(0xFFC4F3B3),
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
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2196F3),
    onPrimaryContainer = Color(0xFF2196F3),
    secondary = Color(0xFF135891),
    onSecondary = Color.White,
    tertiary = Color(0xFF1169AF),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    surfaceVariant = Color(0xFFB3D5F3),
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