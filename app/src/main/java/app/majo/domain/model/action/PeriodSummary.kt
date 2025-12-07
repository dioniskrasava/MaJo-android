package app.majo.domain.model.action

/**
 * Модель сводной статистики за определенный период времени (день, неделя, месяц).
 *
 * Эта доменная модель используется в слое бизнес-логики (например, в Use Case)
 * для представления агрегированных данных, необходимых для построения графиков
 * и отображения результатов пользователя.
 *
 * Хранит:
 * - общее количество очков
 * - разбивку по активностям
 *
 * @property startTimestamp Временная метка начала периода (в миллисекундах).
 * @property endTimestamp Временная метка окончания периода (в миллисекундах).
 * @property totalPoints Общее количество очков, заработанных за весь период.
 * @property pointsByActivity Карта, содержащая разбивку общего количества очков по активности.
 * Ключ: ID активности (Long). Значение: Количество очков (Double).
 */
data class PeriodSummary(
    val startTimestamp: Long,
    val endTimestamp: Long,
    val totalPoints: Double,
    val pointsByActivity: Map<Long, Double>
)