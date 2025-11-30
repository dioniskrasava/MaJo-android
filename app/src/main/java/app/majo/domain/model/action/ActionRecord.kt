package app.majo.domain.model.action

/**
 * Запись о выполнении активности.
 *
 * Примеры:
 * - "Пробежал 3 км"
 * - "Медитировал 20 минут"
 * - "Сделал 50 отжиманий"
 *
 * totalPoints — кешированное значение,
 * чтобы быстро показывать статистику.
 */
data class ActionRecord(
    val id: Long,
    val activityId: Long,
    val value: Double,
    val timestamp: Long,
    val totalPoints: Double
)
