package app.majo.domain.repository


import app.majo.domain.model.Activity
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для управления активностями.
 */
interface ActivityRepository {

    /**
     * Возвращает поток всех активных активностей.
     */
    fun getActivities(): Flow<List<Activity>>

    /**
     * Возвращает активность по ID.
     */
    suspend fun getActivityById(id: Long): Activity?

    /**
     * Добавляет новую активность.
     */
    suspend fun insert(activity: Activity)

    /**
     * Обновляет активность.
     */
    suspend fun update(activity: Activity)

    /**
     * Архивирует или удаляет активность.
     */
    suspend fun delete(id: Long)
}
