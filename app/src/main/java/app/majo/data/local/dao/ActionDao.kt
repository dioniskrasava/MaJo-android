package app.majo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import app.majo.data.local.ActionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) для работы с сущностью [ActionEntity]
 * в локальной базе данных Room.
 *
 * Предоставляет основные методы для выполнения CRUD-операций
 * (Создание, Чтение, Обновление, Удаление) над таблицей 'actions'.
 * Room автоматически генерирует всю необходимую реализацию этого интерфейса.
 */
// объект доступа к базе данных?
@Dao
interface ActionDao {

    /**
     * Возвращает поток (Flow) всех записей активностей из базы данных.
     *
     * Это реактивный метод: он будет автоматически испускать новые списки,
     * как только данные в таблице 'actions' изменятся.
     * Записи отсортированы по времени создания (`createdAt`) в порядке убывания
     * (самые новые активности будут первыми).
     *
     * @return [Flow] списка сущностей [ActionEntity].
     */
    @Query("SELECT * FROM actions ORDER BY createdAt DESC")
    fun getActions(): Flow<List<ActionEntity>>

    /**
     * Получает одну конкретную запись активности по ее уникальному идентификатору.
     *
     * Является **suspend**-функцией, что указывает на выполнение в корутине
     * и блокирующую операцию ввода/вывода, которую нельзя вызывать из главного потока.
     *
     * @param id Уникальный Long-идентификатор записи.
     * @return Сущность [ActionEntity] или null, если запись не найдена.
     */
    @Query("SELECT * FROM actions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ActionEntity?

    /**
     * Вставляет новую запись активности в базу данных.
     *
     * Использует аннотацию [Insert] от Room.
     * Является suspend-функцией.
     *
     * @param action Сущность [ActionEntity] для вставки.
     * @return [Long] — Row ID новой вставленной записи.
     */
    @Insert
    suspend fun insert(action: ActionEntity): Long

    /**
     * Обновляет существующую запись активности в базе данных.
     *
     * Использует аннотацию [Update] от Room.
     * **Важно:** Room идентифицирует запись для обновления по ее первичному ключу (primary key),
     * который должен быть задан в [ActionEntity].
     * Является suspend-функцией.
     *
     * @param action Сущность [ActionEntity] с обновленными данными.
     */
    @Update
    suspend fun update(action: ActionEntity)

    /**
     * Удаляет запись активности по ее уникальному идентификатору.
     *
     * Использует прямой SQL-запрос. Является suspend-функцией.
     *
     * @param id Уникальный Long-идентификатор записи, которую нужно удалить.
     */
    @Query("DELETE FROM actions WHERE id = :id")
    suspend fun delete(id: Long)

}