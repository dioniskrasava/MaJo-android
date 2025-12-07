package app.majo.ui.screens.add_action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.majo.domain.repository.ActionRepository

/**
 * Фабрика (Factory) для создания экземпляров [AddActionViewModel].
 *
 * Этот класс является реализацией паттерна **Фабрика** и необходим для **внедрения зависимостей (DI)**
 * в [AddActionViewModel], которая требует [ActionRepository] в качестве аргумента конструктора.
 *
 * Поскольку Android Framework не может самостоятельно создать ViewModel, требующую параметров
 * (зависимостей), мы используем [ViewModelProvider.Factory] для ручного создания экземпляра
 * и передачи ему репозитория.
 *
 * @property repository Реализация контракта [ActionRepository], который будет внедрен в ViewModel.
 */
class AddActionViewModelFactory(
    private val repository: ActionRepository
) : ViewModelProvider.Factory {

    /**
     * Создает новый экземпляр ViewModel.
     *
     * Этот метод вызывается, когда Android требуется экземпляр ViewModel.
     * Здесь мы вручную создаем экземпляр, передавая ему зависимость.
     *
     * @param modelClass Класс ViewModel, который требуется создать.
     * @return Экземпляр запрошенного ViewModel, в который внедрена зависимость.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Здесь обычно присутствует проверка if (modelClass.isAssignableFrom(AddActionViewModel::class.java))
        // Если проверка пройдена, создаем и возвращаем экземпляр.
        return AddActionViewModel(repository) as T
    }
}