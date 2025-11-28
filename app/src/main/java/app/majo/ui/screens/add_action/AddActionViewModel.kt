package app.majo.ui.screens.add_action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.Action
import app.majo.domain.repository.ActionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddActionViewModel(
    private val repository: ActionRepository
) : ViewModel() {

    var state = MutableStateFlow(AddActionState())
        private set

    fun onEvent(event: AddActionEvent) {
        when (event) {

            is AddActionEvent.OnNameChange ->
                state.update { it.copy(name = event.name) }

            is AddActionEvent.OnTypeChange ->
                state.update { it.copy(type = event.type) }

            is AddActionEvent.OnUnitChange ->
                state.update { it.copy(unit = event.unit) }

            is AddActionEvent.OnCategoryChange ->
                state.update { it.copy(category = event.category) }

            is AddActionEvent.OnPointsChange ->
                state.update { it.copy(pointsPerUnit = event.points) }

            AddActionEvent.OnSaveClick ->
                save()

            AddActionEvent.OnSavedHandled ->
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

            val newActivity = Action(
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
