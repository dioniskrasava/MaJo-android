package app.majo.data.local.mappers

import app.majo.data.local.ActionEntity
import app.majo.domain.model.action.Action
import app.majo.domain.model.action.ActionCategory
import app.majo.domain.model.action.ActionType
import app.majo.domain.model.action.UnitType

fun ActionEntity.toDomain(): Action = Action(
    id = id,
    name = name,
    type = ActionType.valueOf(type),
    unit = UnitType.valueOf(unit),
    pointsPerUnit = pointsPerUnit,
    category = ActionCategory.valueOf(category),
    isActive = isActive,
    createdAt = createdAt
)

fun Action.toEntity(): ActionEntity = ActionEntity(
    id = id,
    name = name,
    type = type.name,
    unit = unit.name,
    pointsPerUnit = pointsPerUnit,
    category = category.name,
    isActive = isActive,
    createdAt = createdAt
)
