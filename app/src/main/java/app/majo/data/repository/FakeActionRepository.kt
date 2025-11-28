package app.majo.data.repository

import app.majo.domain.model.Action
import app.majo.domain.repository.ActionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeActionRepository : ActionRepository {

    private val actions = MutableStateFlow<List<Action>>(emptyList())

    override fun getActions(): Flow<List<Action>> = actions

    override suspend fun getActionById(id: Long): Action? {
        return actions.value.firstOrNull { it.id == id }
    }

    override suspend fun insert(action: Action) {
        val newId = (actions.value.maxOfOrNull { it.id } ?: 0L) + 1
        val newAction = action.copy(id = newId)
        actions.value = actions.value + newAction
    }

    override suspend fun update(action: Action) {
        actions.value = actions.value.map {
            if (it.id == action.id) action else it
        }
    }

    override suspend fun delete(id: Long) {
        actions.value = actions.value.filterNot { it.id == id }
    }
}

