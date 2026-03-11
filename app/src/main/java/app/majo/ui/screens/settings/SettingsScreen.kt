package app.majo.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var pendingJson by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            pendingJson?.let { json ->
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(json.toByteArray())
                }
                pendingJson = null
            }
        }
    }

    // Отслеживаем изменение языка, чтобы пересоздать Activity
    LaunchedEffect(state.currentLanguageCode) {
        // Просто вызываем колбэк при любом изменении языка после инициализации
        // Запоминаем начальное значение при первом запуске
        val initial = state.currentLanguageCode
        snapshotFlow { state.currentLanguageCode }
            .collect { newCode ->
                if (newCode != initial) {
                    onLanguageChange()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Секция: Язык и регион
            SettingsSection(title = stringResource(R.string.language_region)) {
                DropdownField(
                    label = stringResource(R.string.app_language),
                    current = state.currentLanguageCode,
                    items = listOf("ru", "en"),
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
            }

            // Секция: Внешний вид
            SettingsSection(title = stringResource(R.string.appearance)) {
                // Акцентный цвет
                DropdownField(
                    label = stringResource(R.string.accent_color),
                    current = state.currentAccentColor,
                    items = state.availableAccentColors,
                    getDisplayText = { it },
                    onSelect = { color ->
                        viewModel.onEvent(SettingsEvent.AccentColorChanged(color))
                    }
                )

                HorizontalDivider()

                // Тёмная тема
                SettingsSwitchItem(
                    title = stringResource(R.string.dark_theme),
                    checked = state.isDarkMode,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.DarkModeToggled(it)) }
                )

                HorizontalDivider()

                // Использовать цвета действий
                SettingsSwitchItem(
                    title = stringResource(R.string.use_action_colors),
                    checked = state.useActionColors,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.UseActionColorsToggled(it)) }
                )

                HorizontalDivider()

                // Прозрачность карточек
                SettingsSliderItem(
                    title = stringResource(R.string.card_transparency),
                    value = state.cardAlpha,
                    valueRange = 0.1f..1f,
                    onValueChange = { viewModel.setCardAlpha(it) },
                    valueFormatter = { "${(it * 100).toInt()}%" }
                )
            }

            // Секция: Данные
            SettingsSection(title = stringResource(R.string.data_management)) {
                SettingsButtonItem(
                    title = stringResource(R.string.export_to_json),
                    onClick = {
                        viewModel.exportData { json ->
                            pendingJson = json
                            exportLauncher.launch("majo_export.json")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = null // управление через клик по строке
        )
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String = { "%.1f".format(it) }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = valueFormatter(value),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SettingsButtonItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        // Можно добавить иконку перехода, например стрелку
    }
}