package com.netology.nmedia.repository.wall

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.netology.nmedia.dto.Job
import com.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow

interface WallRepository {
    suspend fun removeJobById(id: Int)
    suspend fun saveJob(job: Job)
    suspend fun getPosts(authorId: Int): Flow<PagingData<Post>>
    suspend fun getLatestPostsById(id: Int)
     fun getJobs(): LiveData<List<Job>>
    suspend fun loadJobsById(id: Int)
}