package app.majo.ui.screens.action_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository

/**
 * Фабрика (Factory) для создания экземпляров [ActionListViewModel].
 *
 * Этот класс является реализацией паттерна **Фабрика** и необходим для **внедрения зависимостей (Dependency Injection)**
 * в [ActionListViewModel], которая требует [app.majo.domain.repository.ActionRepository] в качестве аргумента конструктора.
 *
 * Поскольку Android Framework не может самостоятельно создать ViewModel с параметрами,
 * мы используем [androidx.lifecycle.ViewModelProvider.Factory] для ручного создания экземпляра и передачи ему репозитория.
 *
 * @property repository Реализация контракта [app.majo.domain.repository.ActionRepository], который будет внедрен в ViewModel.
 */
class ActionListViewModelFactory(
    private val actionRepository: ActionRepository,
    private val recordRepository: RecordRepository
) : ViewModelProvider.Factory {

    /**
     * Создает новый экземпляр ViewModel.
     *
     * Этот метод вызывается, когда Android требуется экземпляр ViewModel,
     * привязанный к жизненному циклу Activity или Fragment.
     *
     * @param modelClass Класс ViewModel, который требуется создать.
     * @return Экземпляр запрошенного ViewModel ([ActionListViewModel]), в который внедрена зависимость.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActionListViewModel::class.java)) {
            // NEW: Передаем оба репозитория в ActionListViewModel
            return ActionListViewModel(actionRepository, recordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}