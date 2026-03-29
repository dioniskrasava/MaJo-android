package app.majo.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import app.majo.domain.model.action.ActionCategory
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    val state: StateFlow<StatisticsState> = combine(
        recordRepository.getAllRecords(),
        actionRepository.getActions()
    ) { records, actions ->
        val actionMap = actions.associateBy { it.id }
        val now = System.currentTimeMillis()
        val startOfMonth = getStartOfMonth(now)
        val startOfWeek = now - TimeUnit.DAYS.toMillis(6) // последние 7 дней

        // Общая статистика
        val totalPointsAll = records.sumOf { it.totalPoints }
        val totalRecordsCount = records.size

        // За месяц
        val pointsThisMonth = records.filter { it.timestamp >= startOfMonth }
            .sumOf { it.totalPoints }

        // За неделю
        val pointsThisWeek = records.filter { it.timestamp >= startOfWeek }
            .sumOf { it.totalPoints }

        // Топ действий
        val pointsByActionId = records.groupBy { it.activityId }
            .mapValues { (_, list) -> list.sumOf { it.totalPoints } }
        val topActions = pointsByActionId.mapNotNull { (id, points) ->
            actionMap[id]?.let { action -> TopAction(action, points) }
        }.sortedByDescending { it.totalPoints }.take(5)

        // Очки по категориям (исправлено)
        val categoryPoints = mutableMapOf<ActionCategory, Double>()
        records.forEach { record ->
            actionMap[record.activityId]?.category?.let { category ->
                categoryPoints[category] = (categoryPoints[category] ?: 0.0) + record.totalPoints
            }
        }

        StatisticsState(
            isLoading = false,
            totalPointsAllTime = totalPointsAll,
            totalPointsThisMonth = pointsThisMonth,
            totalPointsThisWeek = pointsThisWeek,
            totalRecords = totalRecordsCount,
            topActions = topActions,
            pointsByCategory = categoryPoints
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsState(isLoading = true)
    )

    private fun getStartOfMonth(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}