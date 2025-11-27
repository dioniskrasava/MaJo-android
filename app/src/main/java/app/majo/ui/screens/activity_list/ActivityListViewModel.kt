package app.majo.ui.screens.activity_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActivityListViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    var state = kotlinx.coroutines.flow.MutableStateFlow(ActivityListState())
        private set

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            repository.getActivities().collectLatest { activities ->
                state.update { it.copy(activities = activities, isLoading = false) }
            }
        }
    }

    fun onEvent(event: ActivityListEvent) {
        when (event) {
            is ActivityListEvent.OnActivityClick -> {
                // TODO: добавить навигацию
            }
            ActivityListEvent.OnAddClick -> {
                // TODO: добавить навигацию
            }
        }
    }
}
