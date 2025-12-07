package app.majo.ui.screens.action_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.majo.ui.components.ActionCard

/**
 * Экран списка активностей.
 *
 * Этот Composable является корневым элементом экрана, ответственным за:
 * 1. Получение (потребление) состояния ([ActionListState]) из ViewModel.
 * 2. Отображение списка активностей, используя компонент [ActionCard].
 * 3. Передачу событий навигации через колбэки.
 *
 * Теперь он занимает всё доступное место внутри MainScreen.
 *
 * @param viewModel Экземпляр [ActionListViewModel], который управляет состоянием экрана.
 * @param onItemClick Колбэк, вызываемый при нажатии на карточку активности.
 * Он используется для навигации.
 */
@Composable
fun ActionListScreen(
    viewModel: ActionListViewModel,
    onItemClick: (Long) -> Unit
) {
    // 1. Потребление состояния
    // collectAsState преобразует Flow<ActionListState> из ViewModel
    // в реактивное состояние Compose (State), за которым следит Composable.
    // При изменении данных в Flow, Composable автоматически перерисовывается.
    val state by viewModel.state.collectAsState()

    // 2. Отображение списка
    // LazyColumn используется для эффективного рендеринга больших списков,
    // создавая элементы только тогда, когда они видны пользователю.
    LazyColumn(modifier = Modifier.fillMaxSize()) {

        // Перебираем список доменных моделей Action из текущего состояния
        items(state.actions) { action ->
            ActionCard(
                action = action,
                // При нажатии ActionCard, вызываем внешний колбэк onItemClick
                onClick = { onItemClick(action.id) }
            )
        }
    }
}