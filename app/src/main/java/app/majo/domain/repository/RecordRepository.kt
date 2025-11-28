package app.majo.domain.repository


import app.majo.domain.model.ActionRecord
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для управления записями активностей.
 */
interface RecordRepository {

    /**
     * Возвращает все записи для конкретной активности.
     */
    fun getRecordsForActivity(activityId: Long): Flow<List<ActionRecord>>

    /**
     * Возвращает записи за период.
     */
    fun getRecordsForPeriod(start: Long, end: Long): Flow<List<ActionRecord>>

    /**
     * Добавляет новую запись.
     */
    suspend fun insert(record: ActionRecord)

    /**
     * Удаляет запись.
     */
    suspend fun delete(id: Long)
}
