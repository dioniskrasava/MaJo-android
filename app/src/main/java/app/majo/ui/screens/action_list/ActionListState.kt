package app.majo.ui.screens.action_list


import app.majo.domain.model.Action

data class ActionListState(
    val actions: List<Action> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
