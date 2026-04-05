package app.majo.ui.screens.add_record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.action.Action
import app.majo.domain.model.action.ActionType
import app.majo.domain.model.record.ActionRecord
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class AddRecordViewModel(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    // 1. Список всех доступных активностей
    private val _activities = MutableStateFlow<List<Action>>(emptyList())
    val activities: StateFlow<List<Action>> = _activities.asStateFlow()

    // 2. Выбранная активность и введенное значение
    private val _selectedAction = MutableStateFlow<Action?>(null)
    val selectedAction: StateFlow<Action?> = _selectedAction.asStateFlow()

    private val _recordValue = MutableStateFlow("")
    val recordValue: StateFlow<String> = _recordValue.asStateFlow()

    // Рассчитанное количество очков (отображается на UI)
    private val _calculatedPoints = MutableStateFlow(0.0)
    val calculatedPoints: StateFlow<Double> = _calculatedPoints.asStateFlow()

    // Поле состояния для времени
    private val _timestamp = MutableStateFlow(System.currentTimeMillis())
    val timestamp: StateFlow<Long> = _timestamp

    // Режим редактирования
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _editId = MutableStateFlow<Long?>(null)
    val editId: StateFlow<Long?> = _editId.asStateFlow()

    // Добавление бинарной активности (Да/Нет)
    private val _showNumberInput = MutableStateFlow(false)
    val showNumberInput: StateFlow<Boolean> = _showNumberInput.asStateFlow()

    init {
        viewModelScope.launch {
            actionRepository.getActions().collect { list ->
                _activities.value = list
                // Выбираем первую активность только если нет выбранной и не в режиме редактирования
                if (!_isEditMode.value && _selectedAction.value == null && list.isNotEmpty()) {
                    _selectedAction.value = list.first()
                    _showNumberInput.value = (list.first().type != ActionType.BINARY)
                    calculatePoints()
                }
            }
        }
    }

    fun updateTimestamp(selectedDateMs: Long) {
        val calendar = Calendar.getInstance()
        val now = Calendar.getInstance()

        // Устанавливаем календарь на выбранную дату
        calendar.timeInMillis = selectedDateMs

        // Проверяем, совпадает ли выбранный день с сегодняшним
        val isToday = calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

        if (isToday) {
            // Если сегодня — ставим текущие часы и минуты
            calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE))
        } else {
            // Если другой день — по умолчанию 00:00
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
        }

        // Сбрасываем секунды для чистоты данных
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        _timestamp.value = calendar.timeInMillis
    }

    // Метод для ручного изменения времени пользователем
    fun updateTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = _timestamp.value
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        _timestamp.value = calendar.timeInMillis
    }


    fun selectAction(action: Action) {
        _selectedAction.value = action
        _showNumberInput.value = (action.type != ActionType.BINARY)
        if (action.type == ActionType.BINARY) {
            _recordValue.value = ""   // очищаем значение
        }
        calculatePoints()
    }

    fun updateRecordValue(value: String) {
        // Простая валидация: только числа
        if (value.isEmpty() || value.matches(Regex("^-?\\d*\\.?\\d*\$")))  {
            _recordValue.value = value
            calculatePoints()
        }
    }

    private fun calculatePoints() {
        val action = _selectedAction.value
        val value = _recordValue.value.toDoubleOrNull() ?: 0.0

        // Логика расчета очков: Value * PointsPerUnit
        val points = if (action != null) {
            value * action.pointsPerUnit
        } else {
            0.0
        }
        _calculatedPoints.value = points
    }

    // Загрузка записи для редактирования
    fun loadRecord(recordId: Long) {
        viewModelScope.launch {
            val record = recordRepository.getRecordById(recordId) ?: return@launch
            _isEditMode.value = true
            _editId.value = recordId
            _timestamp.value = record.timestamp
            val action = activities.value.find { it.id == record.activityId }
            if (action != null) {
                _selectedAction.value = action
                if (action.type == ActionType.BINARY) {
                    val value = record.value
                    if (value == 1.0 || value == -1.0) {
                        _showNumberInput.value = false
                        _recordValue.value = value.toString()
                    } else {
                        _showNumberInput.value = true
                        _recordValue.value = value.toString()
                    }
                } else {
                    _showNumberInput.value = true
                    _recordValue.value = record.value.toString()
                }
            } else {
                _recordValue.value = record.value.toString()
            }
            calculatePoints()
        }
    }


    fun saveRecord(onSuccess: () -> Unit) {
        val action = _selectedAction.value ?: return // Нечего сохранять без выбранной активности
        val value = _recordValue.value.toDoubleOrNull() ?: return // Нечего сохранять без значения



        if (_isEditMode.value) {
            // Редактирование
            val updatedRecord = ActionRecord(
                id = _editId.value ?: return,
                activityId = action.id,
                value = value,
                timestamp = _timestamp.value,
                totalPoints = _calculatedPoints.value
            )
            viewModelScope.launch {
                recordRepository.update(updatedRecord)
                onSuccess()
            }
        } else {
            // Новая запись
            val newRecord = ActionRecord(
                id = 0,
                activityId = action.id,
                value = value,
                timestamp = _timestamp.value,
                totalPoints = _calculatedPoints.value
            )
            viewModelScope.launch {
                recordRepository.insert(newRecord)
                onSuccess()
            }
        }
    }


    // Удаление записи (доступно только в режиме редактирования)
    fun deleteRecord(onSuccess: () -> Unit) {
        val id = _editId.value ?: return
        viewModelScope.launch {
            recordRepository.delete(id)
            onSuccess()
        }
    }


    fun selectActionById(activityId: Long) {
        viewModelScope.launch {
            // Дождёмся загрузки списка активностей
            actionRepository.getActions().first()
            val action = activities.value.find { it.id == activityId }
            action?.let { selectAction(it) }
        }
    }


    fun setBinaryValue(value: Double) {
        _recordValue.value = value.toString()
        calculatePoints()
    }

    // Добавим метод для переключения в режим ручного ввода
    fun enableNumberInput() {
        if (selectedAction.value?.type == ActionType.BINARY) {
            _showNumberInput.value = true
            _recordValue.value = "" // очищаем, чтобы пользователь ввёл новое значение
            calculatePoints()
        }
    }





}