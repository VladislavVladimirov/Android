package com.netology.nmedia.repository

import com.netology.nmedia.repository.auth.AuthRepository
import com.netology.nmedia.repository.auth.AuthRepositoryImpl
import com.netology.nmedia.repository.draft.DraftRepository
import com.netology.nmedia.repository.draft.DraftRepositorySharedPrefsImpl
import com.netology.nmedia.repository.post.PostRepository
import com.netology.nmedia.repository.post.PostRepositoryImpl
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