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
 * Теперь он занимает всё доступное место внутри MainScreen.
 */
@Composable
fun ActionListScreen(
    viewModel: ActionListViewModel,
    onItemClick: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Просто список, без Scaffold, так как Scaffold теперь снаружи
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.actions) { action ->
            ActionCard(
                action = action,
                onClick = { onItemClick(action.id) }
            )
        }
    }
}
