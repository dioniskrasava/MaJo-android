package app.majo.ui.screens.add_activity

import app.majo.domain.model.ActivityCategory
import app.majo.domain.model.ActivityType
import app.majo.domain.model.UnitType

sealed interface AddActivityEvent {
    data class OnNameChange(val name: String) : AddActivityEvent
    data class OnTypeChange(val type: ActivityType) : AddActivityEvent
    data class OnUnitChange(val unit: UnitType) : AddActivityEvent
    data class OnCategoryChange(val category: ActivityCategory) : AddActivityEvent
    data class OnPointsChange(val points: String) : AddActivityEvent

    object OnSaveClick : AddActivityEvent
    object OnSavedHandled : AddActivityEvent
}
