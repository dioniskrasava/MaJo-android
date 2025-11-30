package app.majo.domain.model.action

/**
 * Сводная статистика за период (день, неделю, месяц).
 *
 * Хранит:
 * - общее количество очков
 * - разбивку по активностям
 */
data class PeriodSummary(
    val startTimestamp: Long,
    val endTimestamp: Long,
    val totalPoints: Double,
    val pointsByActivity: Map<Long, Double>
)
