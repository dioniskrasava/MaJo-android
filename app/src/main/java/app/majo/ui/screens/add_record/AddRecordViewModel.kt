package app.majo.ui.screens.add_record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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


    init {
        // Запускаем загрузку списка активностей
        viewModelScope.launch {
            actionRepository.getActions().collect { list ->
                _activities.value = list
                // Если активности появились, выбираем первую по умолчанию
                if (_selectedAction.value == null && list.isNotEmpty()) {
                    _selectedAction.value = list.first()
                    calculatePoints()
                }
            }
        }
    }

    fun selectAction(action: Action) {
        _selectedAction.value = action
        calculatePoints()
    }

    fun updateRecordValue(value: String) {
        // Простая валидация: только числа
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*\$"))) {
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


    fun saveRecord(onSuccess: () -> Unit) {
        val action = _selectedAction.value ?: return // Нечего сохранять без выбранной активности
        val value = _recordValue.value.toDoubleOrNull() ?: return // Нечего сохранять без значения

        // Проверяем, что очков больше нуля
        if (_calculatedPoints.value <= 0.0) return

        val newRecord = ActionRecord(
            id = 0, // Будет сгенерирован в базе данных
            activityId = action.id,
            value = value,
            timestamp = _timestamp.value,
            totalPoints = _calculatedPoints.value
        )

        viewModelScope.launch {
            recordRepository.insert(newRecord)
            onSuccess() // Успешное сохранение, можно вернуться назад
        }
    }
}