package app.majo.ui.screens.activity_list


import app.majo.domain.model.Activity

data class ActivityListState(
    val activities: List<Activity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
