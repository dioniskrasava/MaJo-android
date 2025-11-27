package app.majo.ui.screens.add_activity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.majo.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    viewModel: AddActivityViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.isSaved) {
        onSaved()
        viewModel.onEvent(AddActivityEvent.OnSavedHandled)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Добавить активность") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(AddActivityEvent.OnNameChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Название") }
            )

            Spacer(Modifier.height(16.dp))

            // Type selector
            DropdownField(
                label = "Тип активности",
                current = state.type.name,
                items = ActivityType.values().map { it.name },
                onSelect = {
                    viewModel.onEvent(AddActivityEvent.OnTypeChange(ActivityType.valueOf(it)))
                }
            )

            Spacer(Modifier.height(16.dp))

            // Unit selector
            DropdownField(
                label = "Единица измерения",
                current = state.unit.name,
                items = UnitType.values().map { it.name },
                onSelect = {
                    viewModel.onEvent(AddActivityEvent.OnUnitChange(UnitType.valueOf(it)))
                }
            )

            Spacer(Modifier.height(16.dp))

            // Points
            OutlinedTextField(
                value = state.pointsPerUnit,
                onValueChange = { viewModel.onEvent(AddActivityEvent.OnPointsChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Очков за единицу") }
            )

            Spacer(Modifier.height(16.dp))

            // Category selector
            DropdownField(
                label = "Категория",
                current = state.category.name,
                items = ActivityCategory.values().map { it.name },
                onSelect = {
                    viewModel.onEvent(AddActivityEvent.OnCategoryChange(ActivityCategory.valueOf(it)))
                }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onEvent(AddActivityEvent.OnSaveClick) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun DropdownField(
    label: String,
    current: String,
    items: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Box {
            OutlinedTextField(
                value = current,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            expanded = false
                            onSelect(item)
                        }
                    )
                }
            }
        }
    }
}
