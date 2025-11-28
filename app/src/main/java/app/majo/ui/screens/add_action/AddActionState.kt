package app.majo.ui.screens.add_action

import app.majo.domain.model.ActionCategory
import app.majo.domain.model.ActionType
import app.majo.domain.model.UnitType

data class AddActionState(
    val name: String = "",
    val type: ActionType = ActionType.COUNT,
    val unit: UnitType = UnitType.REPETITION,
    val pointsPerUnit: String = "1",
    val category: ActionCategory = ActionCategory.OTHER,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
