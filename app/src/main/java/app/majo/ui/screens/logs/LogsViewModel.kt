package app.majo.ui.screens.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import app.majo.ui.util.atStartOfDay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


sealed class LogListItem {
    data class Header(val dateText: String) : LogListItem()
    data class Item(val record: ActionRecord, val action: Action?) : LogListItem()
}


class LogsViewModel(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    val flatLogList: StateFlow<List<LogListItem>> =
        combine(
            recordRepository.getAllRecords(),
            actionRepository.getActions()
        ) { records, actions ->
            val actionMap = actions.associateBy { it.id }
            records
                .groupBy { record -> record.timestamp.atStartOfDay() }
                .toSortedMap(compareByDescending { it })  // дни от новых к старым
                .flatMap { (dayStart, recordsForDay) ->
                    listOf(LogListItem.Header(formatDateHeader(dayStart))) +
                            recordsForDay
                                .sortedByDescending { it.timestamp } // внутри дня от новых к старым
                                .map { record -> LogListItem.Item(record, actionMap[record.activityId]) }
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    // Вспомогательная функция форматирования заголовка
    private fun formatDateHeader(timestamp: Long): String {
        val formatter = SimpleDateFormat("d MMMM", Locale("ru"))
        return formatter.format(Date(timestamp))
    }
}