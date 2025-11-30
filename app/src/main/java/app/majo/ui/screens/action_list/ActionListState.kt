package app.majo.ui.screens.action_list

import app.majo.domain.model.action.Action

/**
 * Состояние экрана списка активностей.
 *
 * Состояние — это "истина" UI: он рисуется строго на основании state.
 * UI не хранит данные сам — всё хранит ViewModel.
 */
data class ActionListState(

    /**
     * Список всех активностей из репозитория.
     * Если пустой — может быть загрузка или пользователь ещё ничего не добавлял.
     */
    val actions: List<Action> = emptyList(),

    /**
     * Показывает, идёт ли загрузка данных.
     * Можно использовать для отображения индикатора прогресса.
     */
    val isLoading: Boolean = false,

    /**
     * Сообщение об ошибке, если что-то пошло не так.
     * Например, при проблемах с БД или сетью.
     */
    val error: String? = null
)
