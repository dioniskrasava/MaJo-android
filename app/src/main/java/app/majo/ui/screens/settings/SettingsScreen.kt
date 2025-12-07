package app.majo.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.majo.ui.screens.add_action.DropdownField // Вспомогательный компонент для выбора

/**
 * Экран настроек приложения.
 *
 * Этот Composable отвечает за:
 * 1. Потребление состояния ([SettingsState]) из [SettingsViewModel].
 * 2. Отображение элементов управления (язык, тема).
 * 3. Отправку событий ([SettingsEvent]) в ViewModel при изменении настроек пользователем.
 *
 * @param onBack Колбэк для навигации назад, вызывается при нажатии кнопки "Назад".
 * @param viewModel Экземпляр ViewModel, управляющий логикой настроек.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    // Подписываемся на реактивный поток состояния ViewModel
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Используем ArrowBack с поддержкой AutoMirrored для RTL/LTR
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            // --- 1. Выбор языка ---
            DropdownField(
                label = "Язык приложения",
                // Логика отображения названия языка на основе кода
                current = when (state.currentLanguageCode) {
                    "ru" -> "Русский"
                    "en" -> "English"
                    else -> "Русский"
                },
                items = state.availableLanguages,
                onSelect = { selectedLang ->
                    // Отправляем событие в ViewModel для изменения настройки
                    viewModel.onEvent(SettingsEvent.LanguageChanged(selectedLang))
                    // ! В будущем здесь будет перезагрузка Activity для смены языка
                }
            )

            Spacer(Modifier.height(32.dp))

            // --- 2. Переключатель Темной темы ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Улучшенный UX: клик по всей строке переключает тему
                    .clickable { viewModel.onEvent(SettingsEvent.DarkModeToggled(!state.isDarkMode)) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Использовать темную тему",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f) // Занимает всё доступное место слева
                )
                Switch(
                    checked = state.isDarkMode,
                    // Переключатель также отправляет событие
                    onCheckedChange = { isChecked ->
                        viewModel.onEvent(SettingsEvent.DarkModeToggled(isChecked))
                    }
                )
            }
        }
    }
}