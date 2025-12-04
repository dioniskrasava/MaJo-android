package app.majo.ui.screens.settings

/**
 * Состояние экрана настроек
 */
data class SettingsState(
    // Текущий выбранный язык (например, "ru" или "en")
    val currentLanguageCode: String = "ru",

    // Флаг для переключателя (например, "Темная тема")
    val isDarkMode: Boolean = false,

    // Список доступных языков для отображения в выпадающем списке
    val availableLanguages: List<String> = listOf("Русский", "English")
)