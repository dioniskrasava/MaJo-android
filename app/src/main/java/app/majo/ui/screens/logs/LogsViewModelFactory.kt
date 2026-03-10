package app.majo.ui.screens.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository

class LogsViewModelFactory(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogsViewModel::class.java)) {
            return LogsViewModel(actionRepository, recordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}