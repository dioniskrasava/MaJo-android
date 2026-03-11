package app.majo.domain.model

import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val actions: List<Action>,
    val records: List<ActionRecord>
)