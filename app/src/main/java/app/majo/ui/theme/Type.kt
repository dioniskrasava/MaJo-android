package app.majo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Объект, определяющий набор стилей типографики (шрифтов) для приложения.
 *
 * Этот объект передается в корневой Composable [MaterialTheme] и позволяет
 * всем элементам UI использовать консистентные, заранее определенные стили
 * (например, headlineLarge, bodyLarge, labelSmall) через [MaterialTheme.typography].
 *
 * Здесь определены стили по умолчанию, которые при необходимости могут быть переопределены.
 */
val Typography = Typography(
    /**
     * bodyLarge: Стандартный стиль текста для основного содержимого (body text)
     * в больших блоках.
     */
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Другие стили Material, которые можно переопределить:
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)