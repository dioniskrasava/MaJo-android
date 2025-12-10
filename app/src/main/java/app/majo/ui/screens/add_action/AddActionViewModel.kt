package app.majo.ui.screens.add_action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.action.Action
import app.majo.domain.repository.ActionRepository
import app.majo.domain.model.ActionTypeUnitMapper // <-- ИМПОРТ
import app.majo.domain.model.action.UnitType // <-- ИМПОРТ
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel экрана создания и редактирования активности.
 *
 * ViewModel выполняет роль **процессора намерений (Intent Processor)** и **менеджера состояния (State Manager)**:
 * 1. Реагирует на события UI ([AddActionEvent]) и обновляет состояние [state].
 * 2. Выполняет валидацию, конвертацию типов и бизнес-логику (сохранение/обновление/удаление)
 * через [ActionRepository].
 *
 * @property repository Контракт репозитория, используемый для выполнения CRUD-операций.
 */
class AddActionViewModel(
    private val repository: ActionRepository
) : ViewModel() {

    /**
     * MutableStateFlow — единственный источник истины для UI.
     * Содержит текущее состояние формы ([AddActionState]).
     * Приватный сеттер гарантирует, что состояние можно изменить только
     * внутри ViewModel.
     */
    var state = MutableStateFlow(AddActionState())
        private set

    /**
     * Обработка событий, поступающих от UI ([AddActionEvent]).
     *
     * Это центральный диспетчер, который направляет события:
     * - Простые изменения полей -> прямое обновление состояния.
     * - Сложные действия (Сохранить) -> вызов приватной логики.
     */
    fun onEvent(event: AddActionEvent) {
        when (event) {
            is AddActionEvent.OnNameChange ->
                state.update { it.copy(name = event.name, error = null) } // Сбрасываем ошибку при вводе

            is AddActionEvent.OnTypeChange -> { // <-- ИЗМЕНЕНИЕ ЗДЕСЬ
                val newType = event.type
                // 1. Получаем корректный список единиц для нового типа
                val newAvailableUnits = ActionTypeUnitMapper.getValidUnitsForActionType(newType)

                // 2. Определяем новую выбранную единицу
                val currentUnit = state.value.unit
                val newUnit = when {
                    // Если текущая единица (currentUnit) все еще валидна, оставляем ее
                    newAvailableUnits.contains(currentUnit) -> currentUnit
                    // Если единицы вообще доступны, выбираем первую из нового списка
                    newAvailableUnits.isNotEmpty() -> newAvailableUnits.first()
                    // Если единицы не нужны (например, для BINARY), ставим NONE или любую заглушку
                    else -> UnitType.NONE
                }

                // 3. Обновляем состояние
                state.update {
                    it.copy(
                        type = newType,
                        // Обновляем список доступных единиц
                        availableUnits = newAvailableUnits,
                        // Обновляем выбранную единицу на корректную
                        unit = newUnit
                    )
                }
            }

            is AddActionEvent.OnUnitChange ->
                state.update { it.copy(unit = event.unit) }

            is AddActionEvent.OnCategoryChange ->
                state.update { it.copy(category = event.category) }

            is AddActionEvent.OnPointsChange ->
                state.update { it.copy(pointsPerUnit = event.points, error = null) } // Сбрасываем ошибку

            AddActionEvent.OnSaveClick ->
                save() // Запускает валидацию и сохранение/обновление

            AddActionEvent.OnSavedHandled ->
                // Очистка флага после того, как UI использовал его для навигации
                state.update { it.copy(isSaved = false) }
        }
    }

    /**
     * Основная функция сохранения, которая переключается между
     * созданием новой активности и обновлением существующей.
     */
    private fun save() {
        val s = state.value

        // 1. Проверка режима: Если EditMode, вызываем update()
        if (s.isEditMode) {
            update()
            return
        }

        // 2. Валидация для режима создания
        val points = s.pointsPerUnit.toDoubleOrNull()
        if (points == null || points < 0 || s.name.trim().isBlank()) {
            state.update { it.copy(error = "Необходимо указать название и верное значение очков") }
            return
        }

        // 3. Сохранение (Insert)
        viewModelScope.launch {
            state.update { it.copy(isSaving = true) }

            val newActivity = Action(
                // id = 0, так как Room (или репозиторий) сгенерирует ID
                id = 0,
                name = s.name.trim(),
                type = s.type,
                unit = s.unit,
                pointsPerUnit = points,
                category = s.category
            )

            repository.insert(newActivity)

            // Устанавливаем isSaved = true для навигации UI
            state.update {
                it.copy(
                    isSaving = false,
                    isSaved = true
                )
            }
        }
    }

    /**
     * Загружает существующую активность по ID для режима редактирования.
     * Вызывается из Composable с помощью LaunchedEffect.
     *
     * @param id ID активности для загрузки.
     */
    fun loadAction(id: Long) {
        viewModelScope.launch {
            val action = repository.getActionById(id)
            action?.let {
                // 1. Сначала рассчитываем корректный список единиц для загруженного типа
                val availableUnitsForLoadedAction = ActionTypeUnitMapper.getValidUnitsForActionType(it.type) // <-- ИЗМЕНЕНИЕ

                state.update { s ->
                    s.copy(
                        name = it.name,
                        type = it.type,
                        unit = it.unit,
                        // Конвертируем обратно в String для поля ввода
                        pointsPerUnit = it.pointsPerUnit.toString(),
                        category = it.category,

                        // ОБНОВЛЯЕМ availableUnits здесь
                        availableUnits = availableUnitsForLoadedAction, // <-- ИЗМЕНЕНИЕ

                        // Настраиваем режим редактирования
                        isEditMode = true,
                        editId = id
                    )
                }
            }
        }
    }

    /**
     * Логика обновления существующей активности.
     * Вызывается из save(), если isEditMode == true.
     */
    private fun update() {
        val s = state.value

        val points = s.pointsPerUnit.toDoubleOrNull()
        if (points == null || points < 0 || s.name.trim().isBlank() || s.editId == null) {
            state.update { it.copy(error = "Ошибка валидации или отсутствует ID редактирования") }
            return
        }

        viewModelScope.launch {
            state.update { it.copy(isSaving = true) }

            val updated = Action(
                // Передаем существующий ID для обновления
                id = s.editId,
                name = s.name.trim(),
                type = s.type,
                unit = s.unit,
                pointsPerUnit = points,
                category = s.category
            )

            repository.update(updated)

            state.update {
                it.copy(
                    isSaving = false,
                    isSaved = true // Триггер навигации
                )
            }
        }
    }

    /**
     * Удаляет активность из репозитория.
     *
     * Запускает навигацию (закрытие экрана) после успешного удаления.
     */
    fun delete() {
        val id = state.value.editId ?: return

        viewModelScope.launch {
            repository.delete(id)

            // Устанавливаем isSaved = true, чтобы вызвать onSaved() в Composable
            state.update {
                it.copy(
                    isSaved = true
                )
            }
        }
    }
}