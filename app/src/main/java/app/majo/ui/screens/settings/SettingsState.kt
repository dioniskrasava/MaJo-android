package app.majo.ui.screens.settings

/**
 * Состояние экрана настроек.
 *
 * Это единственный источник истины для Composable-функции [SettingsScreen].
 * Все элементы управления (переключатели, выпадающие списки) отображаются
 * строго на основании значений, содержащихся в этом объекте.
 *
 * @property currentLanguageCode Текущий выбранный код языка (например, "ru" или "en").
 * Используется для отображения выбранного значения и для логики смены локали.
 * @property isDarkMode Флаг, указывающий, включена ли в данный момент темная тема.
 * Напрямую привязан к состоянию свитча (Switch).
 * @property availableLanguages Список доступных языков, представленных в виде строк для отображения
 * в выпадающем меню.
 */
data class SettingsState(
    // Текущий выбранный язык (например, "ru" или "en")
    val currentLanguageCode: String = "ru",

    // Флаг для переключателя (например, "Темная тема")
    val isDarkMode: Boolean = false,

    // Список доступных языков для отображения в выпадающем списке
    val availableLanguages: List<String> = listOf("Русский", "English"),

    // Доступные акцентные цвета
    val availableAccentColors: List<String> = listOf("Purple", "Blue", "Green", "Red"),

    // Текущий (актуальный) акцентный цвет
    val currentAccentColor: String = "Purple"
)