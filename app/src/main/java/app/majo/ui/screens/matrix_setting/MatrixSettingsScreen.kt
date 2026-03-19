package app.majo.ui.screens.matrix_setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.majo.ui.common.SimpleTopAppBar
import app.majo.ui.screens.add_action.DropdownField
import app.majo.ui.screens.settings.MatrixPeriodType
import app.majo.ui.screens.settings.SettingsEvent
import app.majo.ui.screens.settings.SettingsViewModel
import kotlin.math.roundToInt

@Composable
fun MatrixSettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onConfigureTickers: () -> Unit
) {
    val state by settingsViewModel.state.collectAsState()
    Scaffold(
        topBar = { SimpleTopAppBar("Настройки матрицы", onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Существующий переключатель тикеров
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { settingsViewModel.onEvent(SettingsEvent.ToggleUseTickersInMatrix(!state.useTickersInMatrix)) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Использовать тикеры вместо полных названий",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Switch(
                            checked = state.useTickersInMatrix,
                            onCheckedChange = { settingsViewModel.onEvent(SettingsEvent.ToggleUseTickersInMatrix(it)) }
                        )
                    }
                    HorizontalDivider()


                    TextButton(
                        onClick = onConfigureTickers,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Настроить тикеры")
                    }


                    // НОВОЕ: выбор типа периода
                    DropdownField(
                        label = "Тип периода",
                        current = state.matrixPeriodType,
                        items = MatrixPeriodType.values().toList(),
                        getDisplayText = { type ->
                            when (type) {
                                MatrixPeriodType.MONTH -> "Месяц"
                                MatrixPeriodType.WEEK_CALENDAR -> "Неделя (календарная)"
                                MatrixPeriodType.WEEK_ROLLING -> "Неделя (последние 7 дней)"
                            }
                        },
                        onSelect = { settingsViewModel.onEvent(SettingsEvent.MatrixPeriodTypeChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider()

                    // НОВОЕ: слайдер размера ячеек
                    SettingsSliderItemInt(
                        title = "Размер ячеек",
                        value = state.matrixCellSize,
                        valueRange = 30..60,
                        onValueChange = { settingsViewModel.onEvent(SettingsEvent.MatrixCellSizeChanged(it)) },
                        valueFormatter = { "$it dp" }
                    )

                    HorizontalDivider()

                    TextButton(
                        onClick = onConfigureTickers,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Настроить тикеры")
                    }

                }
            }
        }
    }
}


// Вспомогательный компонент для слайдера с целыми числами
@Composable
fun SettingsSliderItemInt(
    title: String,
    value: Int,
    valueRange: IntRange,
    onValueChange: (Int) -> Unit,
    valueFormatter: (Int) -> String = { "$it dp" }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = valueFormatter(value), style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
            steps = (valueRange.last - valueRange.first) - 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}