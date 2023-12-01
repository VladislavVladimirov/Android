package com.netology.nmedia.repository

import com.netology.nmedia.repository.draft.event.DraftNewEventRepository
import com.netology.nmedia.repository.draft.event.DraftNewEventRepositoryImpl
import com.netology.nmedia.repository.draft.job.DraftNewJobRepository
import com.netology.nmedia.repository.draft.job.DraftNewJobRepositoryImpl
import com.netology.nmedia.repository.draft.post.DraftNewPostRepository
import com.netology.nmedia.repository.draft.post.DraftNewPostRepositoryImpl
import com.netology.nmedia.repository.event.EventRepository
import com.netology.nmedia.repository.event.EventRepositoryImpl
import com.netology.nmedia.repository.post.PostRepository
import com.netology.nmedia.repository.post.PostRepositoryImpl
import com.netology.nmedia.repository.user.UserRepository
import com.netology.nmedia.repository.user.UserRepositoryImpl
import com.netology.nmedia.repository.wall.WallRepository
import com.netology.nmedia.repository.wall.WallRepositoryImpl
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
    fun bindsDraftNewPostRepository(impl: DraftNewPostRepositoryImpl): DraftNewPostRepository
    @Singleton
    @Binds
    fun bindsDraftNewJobRepository(impl: DraftNewJobRepositoryImpl): DraftNewJobRepository
    @Singleton
    @Binds
    fun bindsDraftNewEventRepository(impl: DraftNewEventRepositoryImpl): DraftNewEventRepository
    @Singleton
    @Binds
    fun bindsWallRepository(impl: WallRepositoryImpl): WallRepository
    @Singleton
    @Binds
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository
    @Singleton
    @Binds
    fun bindsEventRepository(impl: EventRepositoryImpl): EventRepository
}