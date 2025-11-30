package app.majo.ui.screens.action_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.repository.ActionRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel экрана списка активностей.
 *
 * Основные задачи:
 *  1. Загружать данные из репозитория
 *  2. Хранить состояние экрана (state)
 *  3. Реагировать на события UI (ActionListEvent)
 *  4. Отделять UI от бизнес-логики
 *
 * Важно: UI = "тупой", вся логика — здесь.
 */
class ActionListViewModel(
    private val repository: ActionRepository
) : ViewModel() {

    /**
     * MutableStateFlow — источник состояния.
     * UI подписывается на state через collectAsState().
     *
     * private set → UI не может менять state напрямую.
     */
    var state = kotlinx.coroutines.flow.MutableStateFlow(ActionListState())
        private set

    /**
     * init{} вызывается сразу после создания ViewModel.
     * Используем его для первичной загрузки данных.
     */
    init {
        loadActivities()
    }

    /**
     * Загрузка списка активностей из репозитория.
     * Репозиторий возвращает Flow<List<Action>>, поэтому мы подписываемся на него.
     */
    private fun loadActivities() {
        viewModelScope.launch {

            /**
             * collectLatest:
             *  - подписывается на поток репозитория
             *  - получает новое значение при любом обновлении БД
             *  - автоматически обновляет UI
             */
            repository.getActions().collectLatest { activities ->

                // Обновляем состояние (копируем, потому что data class immutable)
                state.update {
                    it.copy(
                        actions = activities,
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Обработка событий от UI.
     * UI посылает ActionListEvent → ViewModel реагирует.
     */
    fun onEvent(event: ActionListEvent) {
        when (event) {

            is ActionListEvent.OnActionClick -> {
                // Здесь будет навигация на экран деталей (позже)
            }

            ActionListEvent.OnAddClick -> {
                // Навигация на AddActivityScreen (позже)
            }
        }
    }
}
