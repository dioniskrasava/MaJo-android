package app.majo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import app.majo.data.local.RecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Query("SELECT * FROM records WHERE activityId = :activityId ORDER BY timestamp DESC")
    fun getByActivityId(activityId: Long): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getForPeriod(start: Long, end: Long): Flow<List<RecordEntity>>

    @Insert
    suspend fun insert(record: RecordEntity): Long

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun delete(id: Long)
}