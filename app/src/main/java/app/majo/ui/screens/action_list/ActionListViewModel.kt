package app.majo.ui.screens.action_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel экрана списка активностей.
 *
 * Основные задачи:
 * 1. Получать данные об активностях из репозитория [ActionRepository].
 * 2. Хранить и управлять состоянием экрана ([ActionListState]) с помощью [MutableStateFlow].
 * 3. Реагировать на события UI ([ActionListEvent]), инициируя бизнес-логику.
 * 4. Предоставлять чистый API для UI, отделяя его от деталей данных.
 *
 * @property repository Контракт, через который ViewModel получает доступ к данным.
 */
class ActionListViewModel(
    private val repository: ActionRepository,
    recordRepository: RecordRepository
) : ViewModel() {

    /**
     * MutableStateFlow — реактивный источник состояния экрана.
     *
     * UI (Composable) подписывается на этот Flow через collectAsState().
     * Приватный сеттер (`private set`) гарантирует, что состояние может быть изменено
     * только внутри ViewModel (принцип **однонаправленного потока данных**).
     */
    var state = MutableStateFlow(ActionListState())
        private set

    /**
     * Блок инициализации, вызываемый сразу после создания ViewModel.
     * Используется для запуска асинхронной загрузки данных.
     */
    init {
        loadActivities()
    }

    /**
     * Асинхронная загрузка списка активностей из репозитория.
     *
     * Подписывается на Flow, возвращаемый репозиторием, чтобы UI автоматически
     * обновлялся при любых изменениях в базе данных.
     */
    private fun loadActivities() {
        // viewModelScope привязывает корутину к жизненному циклу ViewModel.
        // При уничтожении ViewModel корутина автоматически отменяется.
        viewModelScope.launch {

            // Устанавливаем флаг загрузки в true (для первого запуска)
            state.update { it.copy(isLoading = true, error = null) }

            /**
             * collectLatest:
             * - Подписывается на поток данных из репозитория.
             * - Ждет новое значение (список activities) при любом обновлении БД.
             * - Предыдущий обработчик (если есть) автоматически отменяется.
             */
            repository.getActions().collectLatest { activities ->

                // Обновляем состояние, передавая новый список и устанавливая isLoading = false
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
     * Обработка событий, поступающих от пользовательского интерфейса ([ActionListEvent]).
     *
     * Это метод-диспетчер, который направляет UI-интенты к соответствующей логике.
     *
     * @param event Конкретное событие, инициированное пользователем.
     */
    fun onEvent(event: ActionListEvent) {
        when (event) {

            is ActionListEvent.OnActionClick -> {
                // TODO: Здесь будет логика для навигации на экран деталей активности.
                // Например: navigator.navigateToDetails(event.id)
            }

            ActionListEvent.OnAddClick -> {
                // TODO: Здесь будет логика для навигации на экран создания новой активности.
                // Например: navigator.navigateToAdd()
            }
        }
    }
}