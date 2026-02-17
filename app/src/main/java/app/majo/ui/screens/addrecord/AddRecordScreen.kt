// Файл: /home/ivan/AndroidStudioProjects/MaJo-android/app/src/main/java/app/majo/ui/screens/addrecord/AddRecordScreen.kt

package app.majo.ui.screens.addrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.majo.domain.model.action.Action
import app.majo.ui.common.NumberInput
import app.majo.ui.common.SimpleTopAppBar


@Composable
fun AddRecordScreen(
    viewModel: AddRecordViewModel,
    onNavigateBack: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val selectedAction by viewModel.selectedAction.collectAsState()
    val recordValue by viewModel.recordValue.collectAsState()
    val calculatedPoints by viewModel.calculatedPoints.collectAsState()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Добавить запись",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Выбор Активности
            ActivityDropdown(
                activities = activities,
                selectedAction = selectedAction,
                onSelectAction = viewModel::selectAction
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Поле ввода Значения
            NumberInput( // NumberInput должен существовать или быть реализован
                value = recordValue,
                onValueChange = viewModel::updateRecordValue,
                label = "Значение (${selectedAction?.unit ?: "ед.изм."})",
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Отображение Рассчитанных очков
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Очки за запись:",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = String.format("%.2f", calculatedPoints),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Кнопка Сохранить
            Button(
                onClick = {
                    viewModel.saveRecord(onSuccess = onNavigateBack)
                },
                enabled = selectedAction != null && calculatedPoints > 0.0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить запись")
            }
        }
    }
}

// Компонент для выпадающего списка Активностей
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityDropdown(
    activities: List<Action>,
    selectedAction: Action?,
    onSelectAction: (Action) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedAction?.name ?: "Выберите активность",
            onValueChange = { },
            label = { Text("Активность") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            activities.forEach { action ->
                DropdownMenuItem(
                    text = { Text(action.name) },
                    onClick = {
                        onSelectAction(action)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

// ПРИМЕЧАНИЕ: Предполагаем, что NumberInput и SimpleTopAppBar уже есть в проекте.
// Если нет, их нужно будет реализовать в app.majo.ui.common.