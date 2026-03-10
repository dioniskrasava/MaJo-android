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
                title = { Text(stringResource(R.string.settings_title)) },
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

            val languageCodes = listOf("ru", "en")

            //выбор языка
            DropdownField(
                label = stringResource(R.string.app_language),
                current = state.currentLanguageCode,
                items = languageCodes,
                getDisplayText = { code ->
                    when (code) {
                        "ru" -> stringResource(R.string.russian)
                        "en" -> stringResource(R.string.english)
                        else -> stringResource(R.string.russian)
                    }
                },
                onSelect = { code ->
                    viewModel.onEvent(SettingsEvent.LanguageChanged(code))
                }
            )

            Spacer(Modifier.height(32.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Улучшенный UX: клик по всей строке переключает тему
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.theme_label_on_settings_screen),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f) // Занимает всё доступное место слева
                )

            }

            Spacer(Modifier.height(16.dp))


            // Аналогично для цвета
            DropdownField(
                label = stringResource(R.string.accent_color),
                current = state.currentAccentColor,
                items = state.availableAccentColors,
                getDisplayText = { it },
                onSelect = { selectedColor ->
                    viewModel.onEvent(SettingsEvent.AccentColorChanged(selectedColor))
                }
            )

            Spacer(Modifier.height(16.dp))

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
                    text = stringResource(R.string.dark_theme),
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

            Spacer(Modifier.height(16.dp))

            // Переключатель пользовательских цветов
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onEvent(SettingsEvent.UseActionColorsToggled(!state.useActionColors)) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.use_action_colors), // нужно добавить строку в ресурсы
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.useActionColors,
                    onCheckedChange = { use ->
                        viewModel.onEvent(SettingsEvent.UseActionColorsToggled(use))
                    }
                )
            }
        }


    }
}