package app.majo.domain.model

import app.majo.domain.model.action.ActionType
import app.majo.domain.model.action.UnitType


/**
 * Определяет, какие единицы измерения (UnitType)
 * могут быть использованы с данным типом активности (ActionType).
 */
object ActionTypeUnitMapper {

    private val validUnitMapping = mapOf(
        // Для активностей, измеряемых временем:
        ActionType.TIME to listOf(
            UnitType.HOUR,
            UnitType.MINUTE
        ),
        // Для активностей, измеряемых расстоянием (хотя это может быть отдельный тип в будущем):
        ActionType.DISTANCE to listOf(
            UnitType.KM,
            UnitType.METER
        ),
        // Для активностей, измеряемых количеством:
        ActionType.COUNT to listOf(
            UnitType.REPETITION // Специальная единица для счета
        ),
        // Для бинарных (да/нет) активностей единицы измерения не нужны:
        ActionType.BINARY to emptyList()
    )

    /**
     * Возвращает список подходящих UnitType для заданного ActionType.
     * Если тип не найден, возвращает пустой список.
     */
    fun getValidUnitsForActionType(actionType: ActionType): List<UnitType> {
        return validUnitMapping[actionType] ?: emptyList()
    }
}