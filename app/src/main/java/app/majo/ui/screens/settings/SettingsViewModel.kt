package app.majo.ui.screens.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// События, которые приходят от пользователя
sealed class SettingsEvent {
    data class LanguageChanged(val newLanguage: String) : SettingsEvent()
    data class DarkModeToggled(val isChecked: Boolean) : SettingsEvent()
}

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    /**
     * Обработка пользовательских действий
     */
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LanguageChanged -> {
                // Обновляем состояние: меняем код языка
                val newCode = mapLanguageToCode(event.newLanguage)
                _state.update {
                    it.copy(currentLanguageCode = newCode)
                }
                // ! Здесь в будущем будет вызов репозитория для сохранения
            }
            is SettingsEvent.DarkModeToggled -> {
                _state.update {
                    it.copy(isDarkMode = event.isChecked)
                }
                // ! Здесь в будущем будет вызов репозитория для сохранения
            }
        }
    }

    // Простая функция-помощник для перевода имени языка в его код
    private fun mapLanguageToCode(languageName: String): String {
        return when (languageName) {
            "Русский" -> "ru"
            "English" -> "en"
            else -> "ru"
        }
    }
}