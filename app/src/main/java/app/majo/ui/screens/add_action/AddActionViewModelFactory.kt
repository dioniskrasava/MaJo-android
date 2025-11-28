package app.majo.ui.screens.add_action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActionRepository

class AddActionViewModelFactory(
    private val repository: ActionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddActionViewModel(repository) as T
    }
}
