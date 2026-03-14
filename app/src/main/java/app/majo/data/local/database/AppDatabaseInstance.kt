package app.majo.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Объект-синглтон, отвечающий за создание и предоставление единственного
 * экземпляра базы данных [AppDatabase] для всего приложения.
 *
 * Использование синглтона гарантирует, что база данных инициализируется
 * только один раз, что критически важно, так как создание базы данных
 * является ресурсоемкой операцией.
 */
object AppDatabaseInstance {

    /**
     * Поле для хранения единственного экземпляра [AppDatabase].
     *
     * Аннотация @Volatile гарантирует, что изменения, внесенные в INSTANCE
     * одним потоком, будут немедленно видны всем остальным потокам.
     */
    @Volatile
    private var INSTANCE: AppDatabase? = null


    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE actions ADD COLUMN color TEXT NOT NULL DEFAULT 'Purple'")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE actions ADD COLUMN ticker TEXT NOT NULL DEFAULT ''")
        }
    }

    /**
     * Предоставляет синглтон-экземпляр [AppDatabase].
     *
     * Если INSTANCE уже существует, он возвращается. В противном случае
     * база данных инициализируется внутри синхронизированного блока,
     * чтобы предотвратить создание нескольких экземпляров разными потоками
     * (Double-Checked Locking).
     *
     * @param context Контекст приложения, используемый для построения базы данных.
     * @return Единственный экземпляр [AppDatabase].
     */
    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "majo_database"
            )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4)   // добавлено
                .build().also { db ->
                    INSTANCE = db
                }
        }
    }
}