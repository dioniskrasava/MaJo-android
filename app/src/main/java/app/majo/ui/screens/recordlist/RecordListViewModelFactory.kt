// Файл: /home/ivan/AndroidStudioProjects/MaJo-android/app/src/main/java/app/majo/ui/screens/recordlist/RecordListViewModelFactory.kt

package app.majo.ui.screens.recordlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository

class RecordListViewModelFactory(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordListViewModel::class.java)) {
            return RecordListViewModel(actionRepository, recordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}