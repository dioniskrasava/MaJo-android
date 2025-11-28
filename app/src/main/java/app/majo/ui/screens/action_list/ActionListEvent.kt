package app.majo.ui.screens.action_list


sealed interface ActionListEvent {
    data class OnActionClick(val id: Long) : ActionListEvent
    object OnAddClick : ActionListEvent
}
