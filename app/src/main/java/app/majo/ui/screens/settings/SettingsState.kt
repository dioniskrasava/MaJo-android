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
    val currentLanguageCode: String = "ru",
    val isDarkMode: Boolean = false,
    val availableAccentColors: List<String> = listOf("Purple", "Blue", "Green", "Red"), // Доступные акцентные цвета
    val currentAccentColor: String = "Purple", // Текущий (актуальный) акцентный цвет
    val useActionColors: Boolean = true,
    val cardAlpha: Float = 0.4f,
    val useTickersInMatrix: Boolean = false
)