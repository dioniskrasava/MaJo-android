package app.majo.ui.screens.action_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import app.majo.ui.components.ActionCard


@Composable
fun ActionListScreen(
    viewModel: ActionListViewModel,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(state.actions) { action ->
                ActionCard(
                    action = action,
                    onClick = { onItemClick(action.id) }
                )
            }
        }
    }
}

