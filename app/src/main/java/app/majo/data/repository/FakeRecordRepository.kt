package app.majo.data.repository

import app.majo.domain.model.action.ActionRecord
import app.majo.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRecordRepository : RecordRepository {

    private val records = MutableStateFlow<List<ActionRecord>>(emptyList())

    override fun getRecordsForActivity(activityId: Long): Flow<List<ActionRecord>> {
        return MutableStateFlow(records.value.filter { it.activityId == activityId })
    }

    override fun getRecordsForPeriod(start: Long, end: Long): Flow<List<ActionRecord>> {
        return MutableStateFlow(records.value.filter {
            it.timestamp in start..end
        })
    }

    override suspend fun insert(record: ActionRecord) {
        val newId = (records.value.maxOfOrNull { it.id } ?: 0L) + 1
        val newRecord = record.copy(id = newId)
        records.value = records.value + newRecord
    }

    override suspend fun delete(id: Long) {
        records.value = records.value.filterNot { it.id == id }
    }
}
