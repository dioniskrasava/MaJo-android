package app.majo.ui.screens.activity_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.majo.ui.components.ActivityCard

@Composable
fun ActivityListScreen(
    viewModel: ActivityListViewModel
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(ActivityListEvent.OnAddClick)
            }) {
                Text("+")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(state.activities) { activity ->
                    ActivityCard(
                        activity = activity,
                        onClick = {
                            viewModel.onEvent(ActivityListEvent.OnActivityClick(activity.id))
                        }
                    )
                }
            }
        }
    }
}
