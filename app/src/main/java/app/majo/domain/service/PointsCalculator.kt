package app.majo.domain.service

import app.majo.domain.model.action.Action
import app.majo.domain.model.action.ActionType

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
    fun calculatePoints(action: Action, value: Double): Double {
        return when (action.type) {
            ActionType.BINARY ->
                if (value > 0.0) action.pointsPerUnit else 0.0

            else -> value * action.pointsPerUnit
        }
    }
}
