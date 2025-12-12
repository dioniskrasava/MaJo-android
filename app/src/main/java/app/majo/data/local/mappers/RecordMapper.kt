package app.majo.data.local.mappers

import app.majo.data.local.RecordEntity
import app.majo.domain.model.record.ActionRecord

fun RecordEntity.toDomain(): ActionRecord = ActionRecord(
    id = id,
    activityId = activityId,
    value = value,
    timestamp = timestamp,
    totalPoints = totalPoints
)

fun ActionRecord.toEntity(): RecordEntity = RecordEntity(
    id = id,
    activityId = activityId,
    value = value,
    timestamp = timestamp,
    totalPoints = totalPoints
)