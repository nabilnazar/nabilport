package com.nabilnazar.offlinefirstapp.di

import android.content.Context
import androidx.work.WorkManager
import com.nabilnazar.offlinefirstapp.data.db.AppDatabase
import com.nabilnazar.offlinefirstapp.data.db.CalculationDao
import com.nabilnazar.offlinefirstapp.data.remote.SupabaseService
import com.nabilnazar.offlinefirstapp.data.repository.CalculationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideCalculationDao(database: AppDatabase): CalculationDao {
        return database.calculationDao()
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return SupabaseService.client
    }

    @Provides
    @Singleton
    fun provideCalculationRepository(
        dao: CalculationDao,
        supabaseClient: SupabaseClient
    ): CalculationRepository {
        return CalculationRepository(dao, supabaseClient)
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
        
    }
}
