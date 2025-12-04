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
import app.majo.ui.screens.add_action.DropdownField
// Предполагается, что ты скопировал DropdownField из AddActivityScreen.kt в отдельный файл или скопируй его сюда.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    // ViewModel здесь создается с помощью factory = null, так как он простой
    viewModel: SettingsViewModel = viewModel()
) {
    // Подписываемся на состояние ViewModel
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
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
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            // --- 1. Выбор языка ---
            DropdownField(
                label = "Язык приложения",
                current = when (state.currentLanguageCode) {
                    "ru" -> "Русский"
                    "en" -> "English"
                    else -> "Русский"
                },
                items = state.availableLanguages,
                onSelect = { selectedLang ->
                    viewModel.onEvent(SettingsEvent.LanguageChanged(selectedLang))
                    // ! В будущем здесь будет перезагрузка Activity для смены языка
                }
            )

            Spacer(Modifier.height(32.dp))

            // --- 2. Переключатель Темной темы ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    onCheckedChange = { isChecked ->
                        viewModel.onEvent(SettingsEvent.DarkModeToggled(isChecked))
                    }
                )
            }
        }
    }
}