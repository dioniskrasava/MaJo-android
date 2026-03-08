// Файл: /home/ivan/AndroidStudioProjects/MaJo-android/app/src/main/java/app/majo/ui/screens/addrecord/AddRecordScreen.kt

package app.majo.ui.screens.add_record

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import app.majo.domain.model.action.Action
import app.majo.ui.common.NumberInput
import app.majo.ui.common.SimpleTopAppBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    viewModel: AddRecordViewModel,
    onNavigateBack: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val selectedAction by viewModel.selectedAction.collectAsState()
    val recordValue by viewModel.recordValue.collectAsState()
    val calculatedPoints by viewModel.calculatedPoints.collectAsState()

    val timestamp by viewModel.timestamp.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    // Форматтер для отображения даты
    val dateFormater = remember { SimpleDateFormat("dd MMMM yyyy", Locale("ru")) }

    // ----------------------*----- добавляем тайм-пикер
    val context = LocalContext.current

    // Форматировщик для отображения выбранного времени
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    // Подготовка данных для диалога
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            viewModel.updateTime(hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // 24-часовой формат
    )

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = if (isEditMode) "Редактировать запись" else "Добавить запись",
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

            Spacer(modifier = Modifier.height(16.dp))


            // Выбор даты
            OutlinedTextField(
                value = dateFormater.format(Date(timestamp)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Дата записи") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Поле отображения и выбора времени
            OutlinedTextField(
                value = timeFormatter.format(Date(timestamp)),
                onValueChange = { },
                label = { Text("Время записи") },
                readOnly = true, // Запрещаем ввод с клавиатуры
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { timePickerDialog.show() }, // Открываем диалог по клику
                enabled = false, // Чтобы поле выглядело кликабельным, но не фокусировалось
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Кнопка Сохранить/Обновить
            Button(
                onClick = {
                    viewModel.saveRecord(onSuccess = onNavigateBack)
                },
                enabled = selectedAction != null && calculatedPoints > 0.0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Обновить запись" else "Сохранить запись")
            }

            // Кнопка удаления (только в режиме редактирования)
            if (isEditMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.deleteRecord(onSuccess = onNavigateBack)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить запись")
                }
            }
        }
    }




    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = timestamp)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.updateTimestamp(it) }
                    showDatePicker = false
                }) { Text("ОК") }
            }
        ) {
            DatePicker(state = datePickerState)
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
