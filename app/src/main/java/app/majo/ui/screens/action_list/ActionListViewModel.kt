package app.majo.ui.screens.action_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.repository.ActionRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActionListViewModel(
    private val repository: ActionRepository
) : ViewModel() {

    var state = kotlinx.coroutines.flow.MutableStateFlow(ActionListState())
        private set

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            repository.getActions().collectLatest { activities ->
                state.update { it.copy(actions = activities, isLoading = false) }
            }
        }
    }

    fun onEvent(event: ActionListEvent) {
        when (event) {
            is ActionListEvent.OnActionClick -> {
                // TODO: добавить навигацию
            }
            ActionListEvent.OnAddClick -> {
                // TODO: добавить навигацию
            }
        }
    }
}
