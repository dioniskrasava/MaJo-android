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

class LogsViewModel(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    val logsWithActions: StateFlow<Map<ActionRecord, Action?>> =
        combine(
            recordRepository.getAllRecords(),
            actionRepository.getActions()
        ) { records, actions ->
            records.associateWith { record ->
                actions.find { it.id == record.activityId }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
}