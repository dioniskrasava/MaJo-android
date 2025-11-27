package app.majo.domain.service

import app.majo.domain.model.Activity
import app.majo.domain.model.ActivityType

/**
 * Бизнес-логика начисления очков.
 *
 * Используется при создании новой записи.
 */
object PointsCalculator {

    /**
     * Вычисляет количество очков,
     * исходя из типа активности.
     */
    fun calculatePoints(activity: Activity, value: Double): Double {
        return when (activity.type) {
            ActivityType.BINARY ->
                if (value > 0.0) activity.pointsPerUnit else 0.0

            else -> value * activity.pointsPerUnit
        }
    }
}
