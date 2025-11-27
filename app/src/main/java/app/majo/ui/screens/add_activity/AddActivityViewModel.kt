package app.majo.ui.screens.add_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.Activity
import app.majo.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddActivityViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    var state = MutableStateFlow(AddActivityState())
        private set

    fun onEvent(event: AddActivityEvent) {
        when (event) {

            is AddActivityEvent.OnNameChange ->
                state.update { it.copy(name = event.name) }

            is AddActivityEvent.OnTypeChange ->
                state.update { it.copy(type = event.type) }

            is AddActivityEvent.OnUnitChange ->
                state.update { it.copy(unit = event.unit) }

            is AddActivityEvent.OnCategoryChange ->
                state.update { it.copy(category = event.category) }

            is AddActivityEvent.OnPointsChange ->
                state.update { it.copy(pointsPerUnit = event.points) }

            AddActivityEvent.OnSaveClick ->
                save()

            AddActivityEvent.OnSavedHandled ->
                state.update { it.copy(isSaved = false) }
        }
    }

    private fun save() {
        val s = state.value

        val points = s.pointsPerUnit.toDoubleOrNull()
        if (points == null || points < 0) {
            state.update { it.copy(error = "Неверное значение очков") }
            return
        }

        viewModelScope.launch {
            state.update { it.copy(isSaving = true) }

            val newActivity = Activity(
                id = 0,
                name = s.name.trim(),
                type = s.type,
                unit = s.unit,
                pointsPerUnit = points,
                category = s.category
            )

            repository.insert(newActivity)

            state.update {
                it.copy(
                    isSaving = false,
                    isSaved = true
                )
            }
        }
    }
}
