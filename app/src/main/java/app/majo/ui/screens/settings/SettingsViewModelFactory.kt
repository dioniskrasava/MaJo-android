package app.majo.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.data.local.datastore.SettingsDataStore

class SettingsViewModelFactory(
    private val dataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}