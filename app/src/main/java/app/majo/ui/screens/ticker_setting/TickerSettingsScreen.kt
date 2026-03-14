package app.majo.ui.screens.ticker_setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.majo.ui.common.SimpleTopAppBar

@Composable
fun TickerSettingsScreen(
    viewModel: TickerSettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val actions by viewModel.actions.collectAsState()
    val tickerMap by viewModel.tickerMap.collectAsState()

    Scaffold(
        topBar = { SimpleTopAppBar("Настройка тикеров", onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(actions) { action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = action.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = tickerMap[action.id] ?: action.ticker,
                            onValueChange = { viewModel.updateTicker(action.id, it) },
                            label = { Text("Тикер") },
                            singleLine = true,
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
            Button(
                onClick = { viewModel.saveTickers(onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Сохранить")
            }
        }
    }
}