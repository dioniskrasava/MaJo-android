package app.majo.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Добавляем импорт
import app.majo.data.local.datastore.SettingsDataStore // Добавляем импорт
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch // Добавляем импорт

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
class SettingsViewModel(
    // Изменяем конструктор, чтобы принимать наш DataStore Manager
    private val dataStore: SettingsDataStore
) : ViewModel() {

    // Внутренний, изменяемый источник состояния (MutableStateFlow)
    private val _state = MutableStateFlow(SettingsState())

    /**
     * Публичное, неизменяемое состояние экрана.
     * UI (Composable) подписывается на него, чтобы получать обновления.
     */
    val state: StateFlow<SettingsState> = _state


    init {
        // Запускаем сбор Flow из DataStore
        viewModelScope.launch {
            dataStore.settingsFlow.collect { storedSettings ->
                // Обновляем StateFlow нашей ViewModel новыми/сохраненными значениями
                _state.update { storedSettings }
            }
        }
    }


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
                viewModelScope.launch {
                    // 1. Сначала записываем в DataStore (асинхронно)
                    dataStore.setLanguageCode(newCode)
                }
                // 2. DataStore Flow обновит _state автоматически,
                //    поэтому ручное _state.update() здесь не нужно!
            }
            is SettingsEvent.DarkModeToggled -> {
                viewModelScope.launch {
                    // 1. Записываем в DataStore
                    dataStore.setDarkMode(event.isChecked)
                }
            }
            is SettingsEvent.AccentColorChanged -> {
                viewModelScope.launch {
                    // 1. Записываем в DataStore
                    dataStore.setAccentColor(event.newColor)
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