package app.majo.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.data.local.datastore.SettingsDataStore
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository

class SettingsViewModelFactory(
    private val dataStore: SettingsDataStore,
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(dataStore, actionRepository, recordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}