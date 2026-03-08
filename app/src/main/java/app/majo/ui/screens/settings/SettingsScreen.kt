package app.majo.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.majo.ui.screens.add_action.DropdownField // Вспомогательный компонент для выбора
import app.majo.R

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
    viewModel: SettingsViewModel,
    onLanguageChange: () -> Unit
) {
    // Подписываемся на реактивный поток состояния ViewModel
    val state by viewModel.state.collectAsState()


    // Запоминаем язык при первом входе на экран
    val initialLanguageCode = remember { state.currentLanguageCode }
    val isFirstRender = remember { mutableStateOf(true) }


    // Следим за изменением языка
    LaunchedEffect(state.currentLanguageCode) {
        if (!isFirstRender.value && state.currentLanguageCode != initialLanguageCode) {
            onLanguageChange()  // пересоздаём Activity только после реального изменения
        }
        isFirstRender.value = false
    }


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

            val russian = stringResource(R.string.russian)
            val english = stringResource(R.string.english)

            //выбор языка
            DropdownField(
                label = stringResource(R.string.app_language),
                current = when (state.currentLanguageCode) {
                    "ru" -> russian
                    "en" -> english
                    else -> russian
                },
                items = listOf(
                    stringResource(R.string.russian),
                    stringResource(R.string.english)
                ),
                onSelect = { selectedLang ->
                    val code = if (selectedLang == russian) "ru" else "en"
                    viewModel.onEvent(SettingsEvent.LanguageChanged(selectedLang))
                    //onLanguageChange(code)
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

            Spacer(Modifier.height(32.dp))

            DropdownField(
                label = "Акцентный цвет",
                current = state.currentAccentColor,
                items = state.availableAccentColors,
                onSelect = { selectedColor ->
                    viewModel.onEvent(SettingsEvent.AccentColorChanged(selectedColor))
                }
            )
        }
    }
}