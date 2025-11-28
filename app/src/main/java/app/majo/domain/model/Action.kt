package app.majo.domain.model

/**
 * Модель отслеживаемой активности.
 *
 * Примеры:
 * - Бег
 * - Медитация
 * - Программирование
 * - Тренировка
 *
 * Каждая активность определяет:
 * - в каких единицах измеряется выполнение
 * - сколько очков начисляется за единицу
 */
data class Action(
    val id: Long,
    val name: String,
    val type: ActionType,
    val unit: UnitType,
    val pointsPerUnit: Double,
    val category: ActionCategory = ActionCategory.OTHER,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
