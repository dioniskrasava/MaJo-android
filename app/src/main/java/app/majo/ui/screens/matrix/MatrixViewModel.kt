package app.majo.ui.screens.matrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import app.majo.ui.util.atStartOfDay
import kotlinx.coroutines.flow.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class MatrixState(
    val monthStart: Long = 0L,
    val actions: List<Action> = emptyList(),
    val days: List<Long> = emptyList(),
    val recordInfo: Map<Pair<Long, Long>, Pair<Long, Double>> = emptyMap(), // (activityId, dayStart) -> (recordId, totalPoints)
    val isLoading: Boolean = false
)

class MatrixViewModel(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _monthStart = MutableStateFlow(currentMonthStart())
    val monthStart: StateFlow<Long> = _monthStart.asStateFlow()

    private val actionsFlow = actionRepository.getActions()

    private val recordsMapFlow = _monthStart.flatMapLatest { start ->
        val end = getEndOfMonth(start)
        recordRepository.getRecordsForPeriod(start, end).map { records ->
            records.groupBy { record ->
                Pair(record.activityId, record.timestamp.atStartOfDay())
            }.mapValues { entry ->
                val first = entry.value.first()
                first.id to entry.value.sumOf { it.totalPoints }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val state: StateFlow<MatrixState> = combine(
        actionsFlow,
        _monthStart,
        recordsMapFlow
    ) { actions, monthStart, recordInfo ->
        val days = generateDaysInMonth(monthStart)
        MatrixState(
            monthStart = monthStart,
            actions = actions,
            days = days,
            recordInfo = recordInfo
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MatrixState(isLoading = true)
    )

    fun nextMonth() {
        _monthStart.value = addMonths(_monthStart.value, 1)
    }

    fun prevMonth() {
        _monthStart.value = addMonths(_monthStart.value, -1)
    }

    fun setCurrentMonth() {
        _monthStart.value = currentMonthStart()
    }

    private fun currentMonthStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun addMonths(time: Long, months: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        cal.add(Calendar.MONTH, months)
        return cal.timeInMillis
    }

    private fun getEndOfMonth(monthStart: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = monthStart
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    private fun generateDaysInMonth(monthStart: Long): List<Long> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = monthStart
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val days = mutableListOf<Long>()
        repeat(maxDay) { index ->
            cal.set(Calendar.DAY_OF_MONTH, index + 1)
            days.add(cal.timeInMillis.atStartOfDay())
        }
        return days
    }
}