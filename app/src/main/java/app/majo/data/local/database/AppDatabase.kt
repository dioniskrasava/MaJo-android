package app.majo.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.majo.data.local.ActionEntity
import app.majo.data.local.dao.ActionDao

/**
 * Главная база данных приложения.
 * Здесь перечисляются все таблицы и DAO.
 */
@Database(
    entities = [ActionEntity::class],  // ← пока только 1 таблица
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    // Подключаем DAO
    abstract fun actionDao(): ActionDao
}
