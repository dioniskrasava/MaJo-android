package app.majo.ui.screens.add_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActivityRepository

class AddActivityViewModelFactory(
    private val repository: ActivityRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddActivityViewModel(repository) as T
    }
}
