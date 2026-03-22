package app.majo.di

import android.content.Context
import androidx.room.Room
import app.majo.data.local.database.AppDatabase
import app.majo.data.local.database.AppDatabaseInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabaseInstance.getDatabase(context)  // используем существующий синглтон
    }

    @Provides
    fun provideActionDao(database: AppDatabase) = database.actionDao()

    @Provides
    fun provideRecordDao(database: AppDatabase) = database.recordDao()
}