package app.majo.ui.screens.recordlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen(
    viewModel: RecordListViewModel,
    onNavigateToSettings: () -> Unit // <-- NEW: Добавляем обработчик навигации
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Журнал действий") },
                actions = {
                    // Кнопка Настройки
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Настройки")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Здесь будет контент (LazyColumn или Pager)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Список записей за сегодняшний день (Скоро свайпы и логика!)")
        }
    }
}