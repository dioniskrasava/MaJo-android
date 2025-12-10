package app.majo.ui.screens.add_action

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.majo.domain.model.action.ActionCategory
import app.majo.domain.model.action.ActionType
import app.majo.domain.model.action.UnitType

/**
 * Стандартная высота в dp для вертикального отступа между элементами формы.
 */
const val HEIGHT_SPACER = 12 // высота между элементами

/**
 * Экран создания и редактирования активности.
 *
 * Этот Composable является "тупым" (dumb) экраном: он не содержит логики,
 * а только отображает данные из [AddActionViewModel.state] и
 * отправляет намерения пользователя ([AddActionEvent]) обратно в ViewModel.
 *
 * @param viewModel Экземпляр ViewModel, управляющий логикой формы.
 * @param actionId ID активности для режима редактирования. Если null — режим создания.
 * @param onSaved Колбэк, вызываемый после успешного сохранения или удаления активности,
 * используется для навигации назад или закрытия экрана.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    viewModel: AddActionViewModel,
    actionId: Long? = null,
    onSaved: () -> Unit
) {
    // 1. Потребление состояния
    val state by viewModel.state.collectAsState()

    // 2. Эффект для загрузки данных
    // Выполняется только один раз при первом actionId.
    LaunchedEffect(actionId) {
        if (actionId != null) {
            viewModel.loadAction(actionId)
        }
    }

    // 3. Обработка одноразового события (Навигация)
    // Если ViewModel сигнализирует об успешном сохранении, вызываем внешний колбэк
    if (state.isSaved) {
        onSaved()
        // Обязательно сообщаем ViewModel, что событие обработано, чтобы оно не сработало повторно.
        viewModel.onEvent(AddActionEvent.OnSavedHandled)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditMode) "Редактировать активность"
                        else "Добавить активность"
                    )
                }
            )
        }
    ) { padding ->

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                // Обеспечивает прокрутку формы, если экран слишком мал
                .verticalScroll(scrollState)
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            // Поле ввода Названия
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(AddActionEvent.OnNameChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Название") }
            )

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Селектор Типа активности
            DropdownField(
                label = "Тип активности",
                current = state.type.name,
                items = ActionType.values().map { it.name },
                onSelect = {
                    viewModel.onEvent(AddActionEvent.OnTypeChange(ActionType.valueOf(it)))
                }
            )

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Селектор Единицы измерения
            DropdownField(
                label = "Единица измерения",
                current = state.unit.name,
                items = state.availableUnits.map { it.name },
                onSelect = {
                    viewModel.onEvent(AddActionEvent.OnUnitChange(UnitType.valueOf(it)))
                }
            )

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Поле ввода Очков за единицу
            OutlinedTextField(
                value = state.pointsPerUnit,
                onValueChange = { viewModel.onEvent(AddActionEvent.OnPointsChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Очков за единицу") }
            )

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Селектор Категории
            DropdownField(
                label = "Категория",
                current = state.category.name,
                items = ActionCategory.values().map { it.name },
                onSelect = {
                    viewModel.onEvent(AddActionEvent.OnCategoryChange(ActionCategory.valueOf(it)))
                }
            )

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Кнопка сохранения
            Button(
                onClick = { viewModel.onEvent(AddActionEvent.OnSaveClick) },
                enabled = !state.isSaving, // Отключаем кнопку во время сохранения
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }

            // Отображение ошибки
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // КНОПКА УДАЛИТЬ (только в режиме редактирования)
            if (state.isEditMode) {
                Spacer(Modifier.height(HEIGHT_SPACER.dp))

                Button(
                    onClick = { /* TODO: viewModel.onEvent(AddActionEvent.OnDeleteClick) */ viewModel.delete() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить")
                }
            }


        }
    }
}

/**
 * Переиспользуемый Composable-компонент для выбора одного значения из списка
 * через выпадающее меню (Dropdown).
 *
 * Использует [ExposedDropdownMenuBox], что обеспечивает интеграцию с полем [OutlinedTextField].
 *
 * @param label Текстовая метка (Label) для поля.
 * @param current Текущее выбранное значение (строка).
 * @param items Список всех возможных строковых значений для выбора.
 * @param onSelect Колбэк, вызываемый при выборе нового элемента.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    current: String,
    items: List<String>,
    onSelect: (String) -> Unit
) {
    // Внутреннее состояние для управления открытием/закрытием меню
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = current,
            onValueChange = {}, // Невозможно изменить вручную, только через меню
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        expanded = false // Закрываем меню после выбора
                        onSelect(item)  // Передаем выбранное значение во внешний колбэк
                    }
                )
            }
        }
    }
}