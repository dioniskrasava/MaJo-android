package app.majo.ui.screens.add_action

import app.majo.domain.model.action.ActionCategory
import app.majo.domain.model.action.ActionType
import app.majo.domain.model.action.UnitType

/**
 * Определяет все возможные **события (Intents)**, которые могут быть отправлены
 * от пользовательского интерфейса (формы) к ViewModel экрана создания активности.
 *
 * Поскольку это sealed interface, он гарантирует, что мы можем чётко
 * обработать все возможные взаимодействия пользователя с формой.
 */
sealed interface AddActionEvent {

    /**
     * Событие, инициируемое при изменении названия активности в поле ввода.
     * @property name Новое строковое значение названия.
     */
    data class OnNameChange(val name: String) : AddActionEvent

    /**
     * Событие, инициируемое при выборе нового типа активности.
     * @property type Новый тип [ActionType].
     */
    data class OnTypeChange(val type: ActionType) : AddActionEvent

    /**
     * Событие, инициируемое при выборе новой единицы измерения.
     * @property unit Новая единица измерения [UnitType].
     */
    data class OnUnitChange(val unit: UnitType) : AddActionEvent

    /**
     * Событие, инициируемое при выборе новой категории активности.
     * @property category Новая категория [ActionCategory].
     */
    data class OnCategoryChange(val category: ActionCategory) : AddActionEvent

    /**
     * Событие, инициируемое при изменении значения очков за единицу.
     * Хранится как строка для упрощения обработки ввода (валидация на число).
     * @property points Новое строковое значение очков.
     */
    data class OnPointsChange(val points: String) : AddActionEvent

    /**
     * Событие: Пользователь нажал на кнопку "Сохранить".
     *
     * Это событие запускает логику валидации формы и, в случае успеха, сохранение
     * новой активности через репозиторий.
     */
    object OnSaveClick : AddActionEvent

    /**
     * Событие: Сигнал о том, что процесс сохранения завершился успешно и был обработан UI.
     *
     * Это событие-заглушка используется для "одноразовых событий" (One-time events)
     * в ViewModel, например, для закрытия экрана после успешного сохранения
     * и очистки флага успешности.
     */
    object OnSavedHandled : AddActionEvent
}