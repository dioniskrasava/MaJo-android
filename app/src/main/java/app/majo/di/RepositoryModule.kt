package app.majo.di

import app.majo.data.repository.ActionRepositoryImpl
import app.majo.data.repository.RecordRepositoryImpl
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindActionRepository(impl: ActionRepositoryImpl): ActionRepository

    @Binds
    @Singleton
    abstract fun bindRecordRepository(impl: RecordRepositoryImpl): RecordRepository
}