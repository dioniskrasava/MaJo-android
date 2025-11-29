package app.majo.data.local.database

import android.content.Context
import androidx.room.Room

object AppDatabaseInstance {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "majo_database"
            ).build().also { db ->
                INSTANCE = db
            }
        }
    }
}
