// Файл: /home/ivan/AndroidStudioProjects/MaJo-android/app/src/main/java/app/majo/ui/screens/action_list/ActionListScreen.kt

package app.majo.ui.screens.action_list

// ... (Оставь существующие импорты)
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding // <-- NEW: Нужен для отступов
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold // <-- NEW: Импорт
import androidx.compose.material3.TopAppBar // <-- NEW: Импорт
import androidx.compose.material3.Text // <-- NEW: Импорт
import androidx.compose.material3.IconButton // <-- NEW: Импорт
import androidx.compose.material3.Icon // <-- NEW: Импорт
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.majo.ui.components.ActionCard

// ... (Оставь остальные импорты)

/**
 * Экран списка активностей.
 * ...
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionListScreen(
    viewModel: ActionListViewModel,
    onItemClick: (Long) -> Unit,
    onNavigateToAddActivity: () -> Unit // <-- NEW: Добавляем обработчик
) {
    val state by viewModel.state.collectAsState()

    // 1. Обертываем экран в Scaffold
    Scaffold(
        // NEW: Верхний бар с кнопкой "Добавить Активность"
        topBar = {
            TopAppBar(
                title = { Text(text = "Типы активностей") },
                actions = {
                    // Кнопка: Создание новой активности (Добавить тип)
                    IconButton(onClick = onNavigateToAddActivity) {
                        Icon(
                            Icons.AutoMirrored.Filled.PlaylistAdd,
                            contentDescription = "Добавить новый тип активности"
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
                    onClick = { onItemClick(action.id) }
                )
            }
        }
    }
}