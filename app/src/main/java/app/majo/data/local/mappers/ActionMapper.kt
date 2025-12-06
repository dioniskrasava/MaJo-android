package app.majo.data.local.mappers

import app.majo.data.local.ActionEntity
import app.majo.domain.model.action.Action
import app.majo.domain.model.action.ActionCategory
import app.majo.domain.model.action.ActionType
import app.majo.domain.model.action.UnitType

/**
 * Файл содержит функции-расширения (Mappers) для преобразования
 * между сущностями базы данных [ActionEntity] и доменными моделями [Action].
 *
 * Mappers обеспечивают изоляцию слоя данных от слоя бизнес-логики (Domain Layer),
 * гарантируя, что изменения в схеме базы данных (ActionEntity)
 * не повлияют на основную логику приложения (Action).



 * Преобразует сущность базы данных [ActionEntity] в доменную модель [Action].
 *
 * Используется Репозиторием для извлечения данных из Room и их предоставления
 * слою бизнес-логики в виде, удобном для работы.
 *
 * @return Соответствующая доменная модель [Action], готовая к использованию в бизнес-логике.
 */
fun ActionEntity.toDomain(): Action = Action(
    id = id,
    name = name,
    // Преобразование строкового значения обратно в Enum-объект
    type = ActionType.valueOf(type),
    unit = UnitType.valueOf(unit),
    pointsPerUnit = pointsPerUnit,
    category = ActionCategory.valueOf(category),
    isActive = isActive,
    createdAt = createdAt
)

/**
 * Преобразует доменную модель [Action] в сущность базы данных [ActionEntity].
 *
 * Используется Репозиторием для подготовки данных к сохранению или обновлению в Room.
 *
 * @return Соответствующая сущность базы данных [ActionEntity], готовая к записи.
 */
fun Action.toEntity(): ActionEntity = ActionEntity(
    id = id,
    name = name,
    // Храним Enum как строку (через .name) для сохранения в Room (TEXT)
    type = type.name,
    unit = unit.name,
    pointsPerUnit = pointsPerUnit,
    category = category.name,
    isActive = isActive,
    createdAt = createdAt
)