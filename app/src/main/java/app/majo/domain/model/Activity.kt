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
data class Activity(
    val id: Long,
    val name: String,
    val type: ActivityType,
    val unit: UnitType,
    val pointsPerUnit: Double,
    val category: ActivityCategory = ActivityCategory.OTHER,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
