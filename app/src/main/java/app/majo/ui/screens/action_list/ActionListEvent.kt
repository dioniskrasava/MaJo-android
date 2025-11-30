package app.majo.ui.screens.action_list

/**
 * События (intents) экрана списка активностей.
 *
 * Это часть архитектуры MVI/MVVM: UI отправляет события → ViewModel реагирует.
 *
 * Почему sealed interface?
 *  - позволяет чётко перечислить ВСЕ возможные события
 *  - when(event) в ViewModel становится строго исчерпывающим
 *  - нельзя создать “левых” событий
 *
 * По сути — это “команды”, которые UI посылает ViewModel.
 */
sealed interface ActionListEvent {

    /**
     * Пользователь нажал на карточку активности.
     * Передаём ID, чтобы ViewModel могла открыть экран деталей.
     */
    data class OnActionClick(val id: Long) : ActionListEvent

    /**
     * Пользователь нажал на кнопку добавления (+).
     * Обычно приводит к навигации на AddActivityScreen.
     */
    object OnAddClick : ActionListEvent
}
