package com.netology.nmedia.repository.di

import com.netology.nmedia.repository.AuthRepository
import com.netology.nmedia.repository.AuthRepositoryImpl
import com.netology.nmedia.repository.DraftRepository
import com.netology.nmedia.repository.DraftRepositorySharedPrefsImpl
import com.netology.nmedia.repository.PostRepository
import com.netology.nmedia.repository.PostRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository
    @Singleton
    @Binds
    fun bindsDraftRepository(impl: DraftRepositorySharedPrefsImpl): DraftRepository

    @Singleton
    @Binds
    fun bindsAuthRepository(impl: AuthRepositoryImpl): AuthRepository

}