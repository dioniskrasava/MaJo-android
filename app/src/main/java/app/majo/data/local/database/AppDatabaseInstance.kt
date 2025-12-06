package app.majo.data.local.database

import android.content.Context
import androidx.room.Room

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
        // Проверяем, существует ли экземпляр, и возвращаем его, если да.
        return INSTANCE ?: synchronized(this) {
            // Если экземпляра нет, создаем его (только внутри синхронизированного блока)
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "majo_database" // Имя файла базы данных.
            )
                // .fallbackToDestructiveMigration() // Полезная опция для разработки: при изменении версии сносит старую БД.
                .build().also { db ->
                    // Сохраняем созданный экземпляр
                    INSTANCE = db
                }
        }
    }
}