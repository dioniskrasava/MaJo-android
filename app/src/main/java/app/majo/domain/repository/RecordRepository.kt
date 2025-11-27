package app.majo.domain.repository


import app.majo.domain.model.ActivityRecord
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для управления записями активностей.
 */
interface RecordRepository {

    /**
     * Возвращает все записи для конкретной активности.
     */
    fun getRecordsForActivity(activityId: Long): Flow<List<ActivityRecord>>

    /**
     * Возвращает записи за период.
     */
    fun getRecordsForPeriod(start: Long, end: Long): Flow<List<ActivityRecord>>

    /**
     * Добавляет новую запись.
     */
    suspend fun insert(record: ActivityRecord)

    /**
     * Удаляет запись.
     */
    suspend fun delete(id: Long)
}
