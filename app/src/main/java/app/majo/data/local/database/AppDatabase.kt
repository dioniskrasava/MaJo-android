package app.majo.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.majo.data.local.ActionEntity
import app.majo.data.local.RecordEntity
import app.majo.data.local.dao.ActionDao
import app.majo.data.local.dao.RecordDao
/**
 * Главный класс базы данных приложения, который расширяет [RoomDatabase].
 *
 * Этот класс является точкой входа для доступа к постоянным данным приложения,
 * определенным с помощью библиотеки Room.
 *
 * Аннотация @Database используется для:
 * 1. Перечисления всех сущностей ([entities]), которые будут храниться в базе данных.
 * 2. Указания версии базы данных ([version]). Это критично для миграций.
 * 3. Подключения всех Data Access Objects (DAO).
 */
@Database(
    // entities: Список всех классов-сущностей (таблиц) в базе данных.
    entities = [ActionEntity::class, RecordEntity::class],  // ← пока только 1 таблица
    // version: Версия базы данных. Должна увеличиваться при изменении схемы (например, добавлении столбца).
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Абстрактная функция, предоставляющая доступ к DAO для сущности Action.
     *
     * Room автоматически генерирует реализацию этого метода.
     * Через этот DAO Репозиторий получает доступ к операциям чтения/записи.
     *
     * @return Экземпляр [ActionDao].
     */
    // Подключаем DAO
    abstract fun actionDao(): ActionDao


    abstract fun recordDao(): RecordDao
}