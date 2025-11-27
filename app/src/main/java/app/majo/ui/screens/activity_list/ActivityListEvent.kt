package app.majo.ui.screens.activity_list


sealed interface ActivityListEvent {
    data class OnActivityClick(val id: Long) : ActivityListEvent
    object OnAddClick : ActivityListEvent
}
