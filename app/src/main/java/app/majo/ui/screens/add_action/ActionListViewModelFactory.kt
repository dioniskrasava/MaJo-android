package app.majo.ui.screens.add_action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActionRepository
import app.majo.ui.screens.action_list.ActionListViewModel

class ActionListViewModelFactory(
    private val repository: ActionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActionListViewModel(repository) as T
    }
}