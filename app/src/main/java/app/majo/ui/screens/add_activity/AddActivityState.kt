package app.majo.ui.screens.add_activity

import app.majo.domain.model.ActivityCategory
import app.majo.domain.model.ActivityType
import app.majo.domain.model.UnitType

data class AddActivityState(
    val name: String = "",
    val type: ActivityType = ActivityType.COUNT,
    val unit: UnitType = UnitType.REPETITION,
    val pointsPerUnit: String = "1",
    val category: ActivityCategory = ActivityCategory.OTHER,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
