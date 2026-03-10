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



// Цвета для активностей

val Amber500 = Color(0xFFFFC107)
val Amber200 = Color(0xFFFFE082)
val Cyan500 = Color(0xFF00BCD4)
val Cyan200 = Color(0xFF80DEEA)
val DeepOrange500 = Color(0xFFFF5722)
val DeepOrange200 = Color(0xFFFFAB91)
val Pink500 = Color(0xFFE91E63)
val Pink200 = Color(0xFFF48FB1)
val Indigo500 = Color(0xFF3F51B5)
val Indigo200 = Color(0xFF9FA8DA)
val Teal500 = Color(0xFF009688)
val Teal200 = Color(0xFF80CBC4)
val Lime500 = Color(0xFFCDDC39)
val Lime200 = Color(0xFFE6EE9C)
val Brown500 = Color(0xFF795548)
val Brown200 = Color(0xFFBCAAA4)



// NEW COLOR (OLD MAYBE DELETE)

// Цвета для фиолетовой темы
val PurpleLight = lightColorScheme(
    primary = Color(0xFF8D80B9),                                     //     ОСНОВНОЙ ЦВЕТ
    onPrimary = Color.White,                                //     (НА ОСНОВНОМ)    цвет текста/иконок
    primaryContainer = Color(0xFF2196F3),         //     КОНТЕЙНЕР ОСНОВНОГО ЦВЕТА
    onPrimaryContainer = Color(0xFF6021F3),                          //     На ____
    secondary = Color(0xFF8C80BB),                                //     ДОПОЛНИТЕЛЬНЫЙ ЦВЕТ (ВТОРИЧНЫЙ)
    onSecondary = Color(0xFF000000),                              //     НА дополнительном
    tertiary = Color(0xFF7E6CAF),                                      //     ТРЕТИЧНЫЙ ЦВЕТ
    background = Color(0xFFFDFBFF),                  //     ФОНОВЫЙ ЦВЕТ
    surface = Color(0xFFFCFBFF),                     //     ЦВЕТ ПОВЕРХНОСТИ (карточки, меню)
    surfaceVariant = Color(0xFFCBC2E7),                          //     ВАРИАНТ ПОВЕРХНОСТИ
    error = Red500,                                         //     ЦВЕТ ОШИБКИ
                                                            //     onError  -  НА ошибке
    // ... остальные цвета
)

val PurpleDark = darkColorScheme(
    primary = Color(0xFF5C5575),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF4E21F3),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF574E81),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF3B3652),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF383542),
    error = Red200,
    // ...
)

// Цвета для синей темы
val BlueLight = lightColorScheme(
    primary = Color(0xFF51A9EF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2196F3),
    onPrimaryContainer = Color(0xFF2196F3),
    secondary = Color(0xFF3D77A4),
    onSecondary = Color.White,
    tertiary = Color(0xFF1169AF),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    surfaceVariant = Color(0xFFB3D5F3),
    error = Red500,
    // ...
)

val BlueDark = darkColorScheme(
    primary = Color(0xFF4E6281),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF2155F3),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF384C6B),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF4E6281),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF353B42),
    error = Red200,
    // ...
)

val GreenLight = lightColorScheme(
    primary = Color(0xFF7FA970),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E0CA),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFF7FA970),
    onSecondary = Color.White,
    tertiary = Color(0xFF98BE8A),
    background = Color(0xFFFCFFFB),
    surface = Color(0xFFFDFFFB),
    surfaceVariant = Color(0xFFD0E0CA),
    error = Red500,
    // ... остальные цвета
)

val GreenDark = darkColorScheme(
    primary = Color(0xFF5C7555),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF72F321),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF66814E),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF405236),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF3B4235),
    error = Red200,
    // ...
)

val RedLight = lightColorScheme(
    primary = Color(0xFFBE8A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0CACA),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFFBD6D6D),
    onSecondary = Color.White,
    tertiary = Color(0xFFC07373),
    background = Color(0xFFFCFFFB),
    surface = Color(0xFFFFFBFB),
    surfaceVariant = Color(0xFFE0CACA),
    error = Red500,
    // ... остальные цвета
)

val RedDark = darkColorScheme(
    primary = Color(0xFF755555),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF32121),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF814E4E),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF523636),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF423535),
    error = Red200,
    // ...
)


fun getColorByName(name: String, isLight: Boolean): Color {
    return when (name) {
        "Blue" -> if (isLight) Blue500 else Blue200
        "Green" -> if (isLight) Green500 else Green200
        "Purple" -> if (isLight) Purple40 else Purple80
        "Red" -> if (isLight) Red500 else Red200
        "Amber" -> if (isLight) Amber500 else Amber200
        "Cyan" -> if (isLight) Cyan500 else Cyan200
        "DeepOrange" -> if (isLight) DeepOrange500 else DeepOrange200
        "Pink" -> if (isLight) Pink500 else Pink200
        "Indigo" -> if (isLight) Indigo500 else Indigo200
        "Teal" -> if (isLight) Teal500 else Teal200
        "Lime" -> if (isLight) Lime500 else Lime200
        "Brown" -> if (isLight) Brown500 else Brown200
        // добавьте другие по аналогии
        else -> if (isLight) Purple40 else Purple80
    }
}