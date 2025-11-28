package app.majo.domain.repository


import app.majo.domain.model.Action
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для управления активностями.
 */
interface ActionRepository {

    /**
     * Возвращает поток всех активных активностей.
     */
    fun getActions(): Flow<List<Action>>

    /**
     * Возвращает активность по ID.
     */
    suspend fun getActionById(id: Long): Action?

    /**
     * Добавляет новую активность.
     */
    suspend fun insert(action: Action)

    /**
     * Обновляет активность.
     */
    suspend fun update(action: Action)

    /**
     * Архивирует или удаляет активность.
     */
    suspend fun delete(id: Long)
}
