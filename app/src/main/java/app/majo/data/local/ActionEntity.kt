package app.majo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность базы данных (Entity) для таблицы 'actions'.
 *
 * Класс [ActionEntity] представляет собой схему (структуру) одной строки
 * в таблице 'actions' локальной базы данных Room.
 *
 * @property id Уникальный первичный ключ (Primary Key) записи. Генерируется автоматически.
 * @property name Название активности.
 * @property type Тип активности, хранится как строка (Enum.name).
 * @property unit Единица измерения для активности, хранится как строка (Enum.name).
 * @property pointsPerUnit Количество очков, начисляемых за одну единицу измерения.
 * @property category Категория активности, хранится как строка (Enum.name).
 * @property isActive Флаг, указывающий на доступность активности для использования.
 * @property createdAt Временная метка (timestamp) создания записи в миллисекундах.
 */
// Entity = “сущность”, “объект таблицы”, "шаблон одной строки таблицы"
@Entity(tableName = "actions")
data class ActionEntity(

    /**
     * Первичный ключ (Primary Key) таблицы.
     * [autoGenerate = true] позволяет Room автоматически присваивать уникальные,
     * последовательно возрастающие ID при вставке новых записей.
     * По умолчанию [id] установлен в 0, что является сигналом для Room о необходимости генерации.
     */
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