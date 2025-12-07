package app.majo.ui.screens.add_action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActionRepository
import app.majo.ui.screens.action_list.ActionListViewModel

/**
 * Фабрика (Factory) для создания экземпляров [ActionListViewModel].
 *
 * Этот класс является реализацией паттерна **Фабрика** и необходим для **внедрения зависимостей (Dependency Injection)**
 * в [ActionListViewModel], которая требует [ActionRepository] в качестве аргумента конструктора.
 *
 * Поскольку Android Framework не может самостоятельно создать ViewModel с параметрами,
 * мы используем [ViewModelProvider.Factory] для ручного создания экземпляра и передачи ему репозитория.
 *
 * @property repository Реализация контракта [ActionRepository], который будет внедрен в ViewModel.
 */
class ActionListViewModelFactory(
    private val repository: ActionRepository
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
        // Здесь обычно проверяется, что modelClass == ActionListViewModel::class.java.
        // Если это так, создаем экземпляр и возвращаем его.
        // Оператор 'as T' необходим для соответствия сигнатуре метода,
        // а @Suppress("UNCHECKED_CAST") отключает предупреждение компилятора.
        return ActionListViewModel(repository) as T
    }
}