package app.majo.data.repository

import app.majo.data.local.dao.RecordDao
import app.majo.data.local.mappers.toDomain
import app.majo.data.local.mappers.toEntity
import app.majo.domain.model.record.ActionRecord
import app.majo.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecordRepositoryImpl(
    private val dao: RecordDao
) : RecordRepository {

    override fun getRecordsForActivity(activityId: Long): Flow<List<ActionRecord>> =
        dao.getByActivityId(activityId).map { list ->
            list.map { it.toDomain() }
        }

    override fun getRecordsForPeriod(start: Long, end: Long): Flow<List<ActionRecord>> =
        dao.getForPeriod(start, end).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun insert(record: ActionRecord) {
        dao.insert(record.toEntity())
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }
}