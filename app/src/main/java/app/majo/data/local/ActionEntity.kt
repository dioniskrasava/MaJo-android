package app.majo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Это таблица "actions" в базе данных Room.
 * Каждое поле — это колонка в таблице.
 */
@Entity(tableName = "actions")
data class ActionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,          // ID генерируется Room автоматически

    val name: String,          // Название активности
    val type: String,          // Тип (в домене у тебя ActionType)
    val unit: String,          // Единица измерения (UnitType)
    val pointsPerUnit: Double, // Очков за единицу
    val category: String,      // Категория (ActionCategory)
    val isActive: Boolean,     // Активна ли в списке
    val createdAt: Long        // Когда была создана
)
