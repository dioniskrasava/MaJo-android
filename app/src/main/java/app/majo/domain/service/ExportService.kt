package app.majo.domain.service

import app.majo.domain.model.ExportData
import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

object ExportService {
    private val json = Json { prettyPrint = true }

    fun exportToJson(actions: List<Action>, records: List<ActionRecord>): String {
        return json.encodeToString(ExportData(actions, records))
    }
}