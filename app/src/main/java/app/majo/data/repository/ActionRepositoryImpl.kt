package app.majo.data.repository

import app.majo.data.local.ActionEntity
import app.majo.data.local.dao.ActionDao
import app.majo.data.local.mappers.toDomain
import app.majo.data.local.mappers.toEntity
import app.majo.domain.model.Action
import app.majo.domain.repository.ActionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActionRepositoryImpl(
    private val dao: ActionDao
) : ActionRepository {

    override fun getActions(): Flow<List<Action>> =
        dao.getActions().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getActionById(id: Long): Action? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(action: Action) {
        dao.insert(action.toEntity())
    }

    override suspend fun update(action: Action) {
        dao.update(action.toEntity())
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }
}
