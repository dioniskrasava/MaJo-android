package app.majo.ui.screens.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Определяет все возможные **события (Intents)**, которые могут быть отправлены
 * от пользовательского интерфейса (View/Composable) к [SettingsViewModel].
 *
 * Это часть однонаправленного потока данных (UDF), где UI отправляет "команды",
 * а ViewModel реагирует, обновляя состояние.
 */
sealed class SettingsEvent {
    /**
     * Событие: Пользователь выбрал новый язык в выпадающем списке.
     * @property newLanguage Строковое название выбранного языка (например, "Русский").
     */
    data class LanguageChanged(val newLanguage: String) : SettingsEvent()

    /**
     * Событие: Пользователь переключил состояние темной темы.
     * @property isChecked Текущее логическое значение переключателя.
     */
    data class DarkModeToggled(val isChecked: Boolean) : SettingsEvent()

    // Акцентный цвет для приложения
    data class AccentColorChanged(val newColor: String) : SettingsEvent()
}


/**
 * ViewModel для управления пользовательскими настройками приложения.
 *
 * Основная задача — хранить и управлять глобальным состоянием настроек,
 * обрабатывая пользовательские события ([SettingsEvent]) и, в будущем,
 * взаимодействуя с репозиторием ([UserSettingsRepository]) для персистентного хранения.
 */
class SettingsViewModel : ViewModel() {

    // Внутренний, изменяемый источник состояния (MutableStateFlow)
    private val _state = MutableStateFlow(SettingsState())

    /**
     * Публичное, неизменяемое состояние экрана.
     * UI (Composable) подписывается на него, чтобы получать обновления.
     */
    val state: StateFlow<SettingsState> = _state

    /**
     * Обработка пользовательских действий, поступающих от UI.
     *
     * @param event Конкретное событие [SettingsEvent], инициированное пользователем.
     */
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LanguageChanged -> {
                // Обновляем состояние: меняем код языка
                val newCode = mapLanguageToCode(event.newLanguage)
                _state.update {
                    it.copy(currentLanguageCode = newCode)
                }
                // ! Здесь в будущем будет вызов репозитория для сохранения (например: repository.saveLanguage(newCode))
            }
            is SettingsEvent.DarkModeToggled -> {
                _state.update {
                    it.copy(isDarkMode = event.isChecked)
                }
                // ! Здесь в будущем будет вызов репозитория для сохранения (например: repository.saveDarkMode(event.isChecked))
            }
            is SettingsEvent.AccentColorChanged -> {
                _state.update {
                    it.copy(currentAccentColor = event.newColor)
                }
            }

        }
    }

    /**
     * Простая функция-помощник для перевода отображаемого имени языка в его код.
     * @param languageName Имя языка (например, "Русский").
     * @return Код языка (например, "ru").
     */
    private fun mapLanguageToCode(languageName: String): String {
        return when (languageName) {
            "Русский" -> "ru"
            "English" -> "en"
            else -> "ru"
        }
    }
}