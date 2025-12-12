// Файл: /home/ivan/AndroidStudioProjects/MaJo-android/app/src/main/java/app/majo/ui/screens/recordlist/RecordListViewModel.kt

package app.majo.ui.screens.recordlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.concurrent.TimeUnit

// Вспомогательная функция, чтобы обнулить время (начало дня)
fun Long.atStartOfDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

class RecordListViewModel(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    // Текущий день, который мы отображаем (в миллисекундах начала дня)
    private val _currentDayStartMs = MutableStateFlow(System.currentTimeMillis().atStartOfDay())
    val currentDayStartMs: StateFlow<Long> = _currentDayStartMs

    // Flow, который загружает записи в зависимости от выбранного дня
    private val recordsFlow: Flow<List<ActionRecord>> = _currentDayStartMs
        .flatMapLatest { dayStart ->
            val dayEnd = dayStart + TimeUnit.DAYS.toMillis(1) - 1 // Конец дня
            recordRepository.getRecordsForPeriod(dayStart, dayEnd)
        }

    // Все активности (для сопоставления ID и имени)
    private val activitiesFlow = actionRepository.getActions()

    // Объединенный Flow для UI: записи + их соответствующие имена активностей
    val recordsWithActivities: StateFlow<Map<ActionRecord, Action?>> =
        combine(recordsFlow, activitiesFlow) { records, activities ->
            records.associateWith { record ->
                activities.find { it.id == record.activityId }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    /**
     * Переключает отображаемый день на предыдущий (например, со Сегодня на Вчера).
     */
    fun goToPreviousDay() {
        _currentDayStartMs.value -= TimeUnit.DAYS.toMillis(1)
    }

    /**
     * Переключает отображаемый день на следующий (например, со Вчера на Сегодня).
     */
    fun goToNextDay() {
        if (_currentDayStartMs.value < System.currentTimeMillis().atStartOfDay()) {
            _currentDayStartMs.value += TimeUnit.DAYS.toMillis(1)
        }
    }
}