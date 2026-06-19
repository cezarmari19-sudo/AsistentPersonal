package com.asistent.di

import android.content.Context
import androidx.room.Room
import com.asistent.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "asistent.db")
            .fallbackToDestructiveMigration().build()

    @Provides fun provideSubDao(db: AppDatabase) = db.subscriptionDao()
    @Provides fun provideMedDao(db: AppDatabase) = db.medicationDao()
}
