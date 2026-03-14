package app.majo.ui.screens.ticker_setting


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActionRepository

class TickerSettingsViewModelFactory(
    private val actionRepository: ActionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TickerSettingsViewModel::class.java)) {
            return TickerSettingsViewModel(actionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}