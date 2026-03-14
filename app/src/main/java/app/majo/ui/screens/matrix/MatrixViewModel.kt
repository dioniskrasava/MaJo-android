package app.majo.ui.screens.matrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import app.majo.ui.screens.settings.MatrixPeriodType
import app.majo.ui.util.atStartOfDay
import kotlinx.coroutines.flow.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

// Обновляем data class MatrixState, добавляем startDate и periodType (опционально)
data class MatrixState(
    val startDate: Long = 0L,
    val actions: List<Action> = emptyList(),
    val days: List<Long> = emptyList(),
    val recordInfo: Map<Pair<Long, Long>, Pair<Long, Double>> = emptyMap(),
    val isLoading: Boolean = false,
    val periodType: MatrixPeriodType = MatrixPeriodType.MONTH
)

class MatrixViewModel(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _periodType = MutableStateFlow(MatrixPeriodType.MONTH)
    private val _currentStartDate = MutableStateFlow(getStartOfCurrentPeriod(MatrixPeriodType.MONTH))

    private val actionsFlow = actionRepository.getActions()

    private val recordsMapFlow = combine(_currentStartDate, _periodType) { start, type ->
        val end = getEndOfPeriod(start, type)
        start to end
    }.flatMapLatest { (start, end) ->
        recordRepository.getRecordsForPeriod(start, end).map { records ->
            records.groupBy { record ->
                Pair(record.activityId, record.timestamp.atStartOfDay())
            }.mapValues { entry ->
                entry.value.first().id to entry.value.sumOf { it.totalPoints }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val state: StateFlow<MatrixState> = combine(
        actionsFlow,
        _currentStartDate,
        _periodType,
        recordsMapFlow
    ) { actions, start, type, recordInfo ->
        val days = generateDays(start, type)
        MatrixState(
            startDate = start,
            actions = actions,
            days = days,
            recordInfo = recordInfo,
            periodType = type
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MatrixState(isLoading = true)
    )

    // Публичный метод для обновления типа периода извне (вызывается из UI при изменении настроек)
    fun updatePeriodType(type: MatrixPeriodType) {
        _periodType.value = type
        // При смене типа переходим на текущий период этого типа
        _currentStartDate.value = getStartOfCurrentPeriod(type)
    }

    fun nextPeriod() {
        _currentStartDate.value = addPeriod(1)
    }

    fun prevPeriod() {
        _currentStartDate.value = addPeriod(-1)
    }

    fun setCurrentPeriod() {
        _currentStartDate.value = getStartOfCurrentPeriod(_periodType.value)
    }

    // Вспомогательные функции
    private fun getStartOfCurrentPeriod(type: MatrixPeriodType): Long {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        return when (type) {
            MatrixPeriodType.MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            MatrixPeriodType.WEEK_CALENDAR -> {
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            MatrixPeriodType.WEEK_ROLLING -> {
                cal.add(Calendar.DAY_OF_MONTH, -6)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
        }
    }

    private fun addPeriod(amount: Int): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = _currentStartDate.value }
        when (_periodType.value) {
            MatrixPeriodType.MONTH -> cal.add(Calendar.MONTH, amount)
            MatrixPeriodType.WEEK_CALENDAR, MatrixPeriodType.WEEK_ROLLING -> cal.add(Calendar.DAY_OF_MONTH, amount * 7)
        }
        return cal.timeInMillis
    }

    private fun getEndOfPeriod(start: Long, type: MatrixPeriodType): Long {
        return when (type) {
            MatrixPeriodType.MONTH -> {
                val cal = Calendar.getInstance().apply { timeInMillis = start }
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                cal.timeInMillis
            }
            MatrixPeriodType.WEEK_CALENDAR, MatrixPeriodType.WEEK_ROLLING -> {
                start + TimeUnit.DAYS.toMillis(7) - 1
            }
        }
    }

    private fun generateDays(start: Long, type: MatrixPeriodType): List<Long> {
        return when (type) {
            MatrixPeriodType.MONTH -> {
                val cal = Calendar.getInstance().apply { timeInMillis = start }
                val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                (1..maxDay).map { day ->
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    cal.timeInMillis.atStartOfDay()
                }
            }
            MatrixPeriodType.WEEK_CALENDAR, MatrixPeriodType.WEEK_ROLLING -> {
                (0..6).map { offset ->
                    val cal = Calendar.getInstance().apply { timeInMillis = start }
                    cal.add(Calendar.DAY_OF_MONTH, offset)
                    cal.timeInMillis.atStartOfDay()
                }
            }
        }
    }
}

