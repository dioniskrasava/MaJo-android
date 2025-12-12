package app.majo.ui.screens.addrecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository

class AddRecordViewModelFactory(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddRecordViewModel::class.java)) {
            return AddRecordViewModel(actionRepository, recordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}