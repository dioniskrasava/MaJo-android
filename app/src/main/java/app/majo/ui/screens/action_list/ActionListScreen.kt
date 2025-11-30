package app.majo.ui.screens.action_list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.majo.ui.components.ActionCard

/**
 * Основной экран, который показывает список всех активностей.
 *
 * Отвечает только за UI и взаимодействие пользователя.
 * Никакой логики получения данных здесь быть НЕ должно.
 *
 * Архитектурный принцип:
 *  - UI → события → ViewModel
 *  - ViewModel → state → UI
 *
 * @param viewModel экранная ViewModel
 * @param onAddClick вызывается при нажатии "+"
 * @param onItemClick вызывается при выборе активности
 */
@Composable
fun ActionListScreen(
    viewModel: ActionListViewModel,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    // Подписываемся на StateFlow из ViewModel
    // UI перерисуется автоматически при изменении стейта
    val state by viewModel.state.collectAsState()

    Scaffold(
        // Кнопка добавления активности
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->

        /**
         * LazyColumn — оптимизированный вертикальный список.
         * - рендерит только видимые элементы
         * - идеально подходит для списков с карточками
         */
        LazyColumn(modifier = Modifier.padding(padding)) {

            /**
             * items(...) автоматически делает key = index.
             * Ты передаёшь весь список state.actions.
             */
            items(state.actions) { action ->

                // Компонент карточки активности
                ActionCard(
                    action = action,
                    onClick = { onItemClick(action.id) }
                )
            }
        }
    }
}
