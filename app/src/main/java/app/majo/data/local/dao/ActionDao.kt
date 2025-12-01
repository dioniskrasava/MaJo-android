package app.majo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import app.majo.data.local.ActionEntity
import kotlinx.coroutines.flow.Flow


// объект доступа к базе данных?
@Dao
interface ActionDao {

    // Получить все активности (автоматически возвращает Flow)
    @Query("SELECT * FROM actions ORDER BY createdAt DESC")
    fun getActions(): Flow<List<ActionEntity>>

    // Получить одну по id
    @Query("SELECT * FROM actions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ActionEntity?

    // Вставить новую
    @Insert
    suspend fun insert(action: ActionEntity): Long

    // Обновить существующую
    @Update
    suspend fun update(action: ActionEntity)

    // Удалить по id
    @Query("DELETE FROM actions WHERE id = :id")
    suspend fun delete(id: Long)

}
