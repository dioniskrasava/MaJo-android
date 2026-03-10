package app.majo.ui.screens.action_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding 
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold 
import androidx.compose.material3.TopAppBar 
import androidx.compose.material3.Text 
import androidx.compose.material3.IconButton 
import androidx.compose.material3.Icon 
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.majo.R
import app.majo.ui.components.ActionCard
import app.majo.ui.screens.settings.SettingsViewModel


/**
 * Экран списка активностей.
 * ...
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionListScreen(
    viewModel: ActionListViewModel,
    settingsViewModel: SettingsViewModel,
    onItemClick: (Long) -> Unit,
    onNavigateToAddActivity: () -> Unit // <-- NEW: Добавляем обработчик
) {
    val state by viewModel.state.collectAsState()
    val settingsState by settingsViewModel.state.collectAsState()

    Scaffold(
        // NEW: Верхний бар с кнопкой "Добавить Активность"
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.activity_types)) },
                actions = {
                    // Кнопка: Создание новой активности (Добавить тип)
                    IconButton(onClick = onNavigateToAddActivity) {
                        Icon(
                            Icons.AutoMirrored.Filled.PlaylistAdd,
                            contentDescription = stringResource(R.string.add_activity)
                        )
                    }
                }
            )
        }
    ) { innerPadding -> // 2. Получаем отступы от TopAppBar и BottomAppBar

        // 3. Отображение списка
        LazyColumn(
            // Применяем отступы, чтобы контент не перекрывался барами
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Перебираем список доменных моделей Action из текущего состояния
            items(state.actions) { action ->
                ActionCard(
                    action = action,
                    useColors = settingsState.useActionColors,
                    cardAlpha = settingsState.cardAlpha,
                    onClick = { onItemClick(action.id) }
                )
            }
        }
    }
}