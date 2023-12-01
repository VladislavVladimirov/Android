package com.netology.nmedia.db

import android.content.Context
import androidx.room.Room
import com.netology.nmedia.dao.EventDao
import com.netology.nmedia.dao.EventKeyDao
import com.netology.nmedia.dao.JobDao
import com.netology.nmedia.dao.PostDao
import com.netology.nmedia.dao.PostKeyDao
import com.netology.nmedia.dao.UserDao
import com.netology.nmedia.dao.WallPostDao
import com.netology.nmedia.dao.WallPostKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()
    @Provides
    fun providePostDao(
        appDb: AppDb
    ): PostDao = appDb.postDao()
    @Provides
    fun provideJobDao(
        appDb: AppDb
    ): JobDao = appDb.jobDao()
    @Provides
    fun provideUserDao(
        appDb: AppDb
    ): UserDao = appDb.userDao()
    @Provides
    fun provideWallPostDao(
        appDb: AppDb
    ): WallPostDao = appDb.wallPostDao()
    @Provides
    fun providePostKeyDao(
        appDb: AppDb
    ): PostKeyDao = appDb.postKeyDao()
    @Provides
    fun provideWallPostKeyDao(
        appDb: AppDb
    ): WallPostKeyDao = appDb.wallPostKeyDao()
    @Provides
    fun provideEventDao(appDb: AppDb): EventDao = appDb.eventDao()
    @Provides
    fun provideEventKeyDao(appDb: AppDb): EventKeyDao = appDb.eventKeyDao()
}



