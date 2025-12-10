package app.majo.ui.screens.add_action

import app.majo.domain.model.action.ActionCategory
import app.majo.domain.model.action.ActionType
import app.majo.domain.model.action.UnitType
import app.majo.domain.model.ActionTypeUnitMapper

/**
 * Состояние экрана создания и редактирования активности.
 *
 * Этот класс содержит все изменяемые данные, необходимые для корректного
 * отображения формы, ее валидации и обработки навигации.
 *
 * @property name Текущее значение поля ввода "Название".
 * @property type Текущий выбранный тип активности [ActionType].
 * @property unit Текущая выбранная единица измерения [UnitType].
 * @property pointsPerUnit Значение очков за единицу, хранится как [String]
 * для упрощения обработки ввода пользователя и валидации (преобразование в Double).
 * @property category Текущая выбранная категория [ActionCategory].
 * @property isSaving Флаг, указывающий, выполняется ли в данный момент асинхронная операция сохранения.
 * Используется для блокировки кнопки "Сохранить".
 * @property isSaved Флаг-сигнал об успешном сохранении. Используется для одноразовой навигации.
 * @property error Сообщение об ошибке (например, ошибка валидации). Если null, ошибки нет.
 * @property isEditMode Флаг, указывающий, находится ли экран в режиме редактирования (true)
 * или создания новой активности (false).
 * @property editId ID активности, если экран находится в режиме редактирования.
 */
data class AddActionState(
    val name: String = "",
    val type: ActionType = ActionType.COUNT,
    val unit: UnitType = UnitType.REPETITION,
    val pointsPerUnit: String = "1",
    val category: ActionCategory = ActionCategory.OTHER,
    // НОВОЕ ПОЛЕ: Список доступных UnitType
    val availableUnits: List<UnitType> = ActionTypeUnitMapper.getValidUnitsForActionType(ActionType.COUNT), // <-- Инициализация
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val editId: Long? = null
)