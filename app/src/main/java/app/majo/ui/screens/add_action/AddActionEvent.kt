package app.majo.ui.screens.add_action

import app.majo.domain.model.ActionCategory
import app.majo.domain.model.ActionType
import app.majo.domain.model.UnitType

sealed interface AddActionEvent {
    data class OnNameChange(val name: String) : AddActionEvent
    data class OnTypeChange(val type: ActionType) : AddActionEvent
    data class OnUnitChange(val unit: UnitType) : AddActionEvent
    data class OnCategoryChange(val category: ActionCategory) : AddActionEvent
    data class OnPointsChange(val points: String) : AddActionEvent

    object OnSaveClick : AddActionEvent
    object OnSavedHandled : AddActionEvent
}
