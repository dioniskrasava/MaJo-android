package app.majo.data.repository

import app.majo.domain.model.Activity
import app.majo.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeActivityRepository : ActivityRepository {

    private val activities = MutableStateFlow<List<Activity>>(emptyList())

    override fun getActivities(): Flow<List<Activity>> = activities

    override suspend fun getActivityById(id: Long): Activity? {
        return activities.value.firstOrNull { it.id == id }
    }

    override suspend fun insert(activity: Activity) {
        val newId = (activities.value.maxOfOrNull { it.id } ?: 0L) + 1
        val newActivity = activity.copy(id = newId)
        activities.value = activities.value + newActivity
    }

    override suspend fun update(activity: Activity) {
        activities.value = activities.value.map {
            if (it.id == activity.id) activity else it
        }
    }

    override suspend fun delete(id: Long) {
        activities.value = activities.value.filterNot { it.id == id }
    }
}
