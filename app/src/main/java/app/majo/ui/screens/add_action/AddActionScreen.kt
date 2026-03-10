package app.majo.ui.screens.add_action

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.majo.domain.model.action.ActionCategory
import app.majo.domain.model.action.ActionType
import app.majo.domain.model.action.UnitType
import app.majo.ui.common.SimpleTopAppBar
import app.majo.R
import app.majo.ui.theme.getColorByName
import app.majo.ui.util.toLocalizedString
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration

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
    onNavigateBack: () -> Unit,
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
            SimpleTopAppBar(
                title = if (state.isEditMode) stringResource(R.string.edit_activity_title) else stringResource(R.string.add_activity_title),
                onNavigateBack = onNavigateBack
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
                label = { Text(stringResource(R.string.name)) }
            )

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Тип активности
            DropdownField(
                label = stringResource(R.string.activity_type),
                current = state.type,
                items = ActionType.values().toList(),
                getDisplayText = { it.toLocalizedString() },
                onSelect = { viewModel.onEvent(AddActionEvent.OnTypeChange(it)) }
            )


            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Единица измерения
            DropdownField(
                label = stringResource(R.string.unit),
                current = state.unit,
                items = state.availableUnits,
                getDisplayText = { it.toLocalizedString() },
                onSelect = { viewModel.onEvent(AddActionEvent.OnUnitChange(it)) }
            )

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Поле ввода Очков за единицу
            OutlinedTextField(
                value = state.pointsPerUnit,
                onValueChange = { viewModel.onEvent(AddActionEvent.OnPointsChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.points_per_unit_label)) }
            )

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Категория
            DropdownField(
                label = stringResource(R.string.category),
                current = state.category,
                items = ActionCategory.values().toList(),
                getDisplayText = { it.toLocalizedString() },
                onSelect = { viewModel.onEvent(AddActionEvent.OnCategoryChange(it)) }
            )

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Выбор цвета
            Text(stringResource(R.string.select_color), style = MaterialTheme.typography.titleSmall)
            ColorPicker(
                colors = state.availableColors,
                selectedColor = state.color,
                onColorSelected = { viewModel.onEvent(AddActionEvent.OnColorChange(it)) }
            )
            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            Spacer(Modifier.height(HEIGHT_SPACER.dp))

            // Кнопка сохранения
            Button(
                onClick = { viewModel.onEvent(AddActionEvent.OnSaveClick) },
                enabled = !state.isSaving, // Отключаем кнопку во время сохранения
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(	stringResource(R.string.save))
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
                    Text(	stringResource(R.string.delete))
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
fun <T> DropdownField(
    label: String,
    current: T,
    items: List<T>,
    getDisplayText: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = getDisplayText(current),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                    text = { Text(getDisplayText(item)) },
                    onClick = {
                        expanded = false
                        onSelect(item)
                    }
                )
            }
        }
    }
}

// КАСТОМНЫЙ ВЫБОР ЦВЕТА
@Composable
fun ColorPicker(
    colors: List<String>,
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLight = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO // нужно получить текущую тему

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(colors) { colorName ->
            val color = getColorByName(colorName, isLight)
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onColorSelected(colorName) },
                color = color,
                border = BorderStroke(
                    width = if (colorName == selectedColor) 3.dp else 1.dp,
                    color = if (colorName == selectedColor) MaterialTheme.colorScheme.primary else Color.Transparent
                )
            ) {}
        }
    }
}