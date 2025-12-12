package app.majo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


/**Сущность записи активности
 *
 * @property id Уникальный первичный ключ (Primary Key) записи. Генерируется автоматически.
 * @property activityId id активности
 * @property value значение (количество)
 * @property timestamp Временная метка
 * @property totalPoints количество очков
 * */
@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: Long,
    val value: Double,
    val timestamp: Long,
    val totalPoints: Double
)