package app.majo.domain.service

import app.majo.domain.model.action.Action
import app.majo.domain.model.action.ActionType

/**
 * Объект, инкапсулирующий **бизнес-логику начисления очков**.
 *
 * Это часть Сервисного Слоя Домена. Он полностью независим от
 * слоев данных и представления и выполняет ключевые расчеты.
 *
 * Используется Репозиторием или Use Case при создании новой записи [ActionRecord],
 * чтобы предварительно вычислить (кешировать) итоговое количество очков.
 */
object PointsCalculator {

    /**
     * Вычисляет общее количество очков за выполнение активности.
     * Расчет зависит от [ActionType] активности.
     *
     * @param action Доменная модель [Action], содержащая [Action.pointsPerUnit] и [Action.type].
     * @param value Фактическое значение выполнения (например, 3.5 км, 20.0 минут).
     * @return Общее количество очков, начисленное за данное выполнение.
     */
    fun calculatePoints(action: Action, value: Double): Double {
        // Используем выражение when для определения логики на основе типа активности
        return when (action.type) {
            ActionType.BINARY ->
                // Для бинарного типа: если value > 0 (активность сделана), начисляем pointsPerUnit, иначе 0.
                if (value > 0.0) action.pointsPerUnit else 0.0

            // Для всех остальных типов (TIME, DISTANCE, COUNT):
            // Просто умножаем значение выполнения на количество очков за единицу.
            else -> value * action.pointsPerUnit
        }
    }
}