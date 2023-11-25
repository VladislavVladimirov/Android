package com.netology.nmedia.repository.wall

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.netology.nmedia.api.JobApiService
import com.netology.nmedia.api.PostApiService
import com.netology.nmedia.dao.JobDao
import com.netology.nmedia.dao.WallPostDao
import com.netology.nmedia.dao.WallPostKeyDao
import com.netology.nmedia.db.AppDb
import com.netology.nmedia.dto.Job
import com.netology.nmedia.dto.Post
import com.netology.nmedia.entity.JobEntity
import com.netology.nmedia.entity.toJobEntity
import com.netology.nmedia.entity.toWallPostEntity
import com.netology.nmedia.error.ApiError
import com.netology.nmedia.error.NetworkError
import com.netology.nmedia.error.UnknownError
import com.netology.nmedia.paging.WallRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class WallRepositoryImpl @Inject constructor(
    private val wallDao: WallPostDao,
    private val jobDao: JobDao,
    private val wallPostKeyDao: WallPostKeyDao,
    private val appDb: AppDb,
    private val jobApiService: JobApiService,
    private val postApiService: PostApiService,
) : WallRepository {

    @ExperimentalPagingApi
    override suspend fun getPosts(authorId: Int): Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(
            postApiService,
            wallDao,
            wallPostKeyDao,
            appDb,
            authorId
        ),
        pagingSourceFactory = { wallDao.getPagingSource() }
    ).flow.map { postList ->
        postList.map { it.toDto() }
    }

    override fun getJobs(): LiveData<List<Job>> = jobDao.getAll().map { jobs ->
        jobs.map { it.toDto() }
    }



    override suspend fun loadJobsById(id: Int) {
        try {
            jobDao.removeAll()
            val response = jobApiService.getJobs(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(body.toJobEntity())
        } catch (e: IOException) {
            throw NetworkError
        }  catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveJob(job: Job) {
        try {
            val response = jobApiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeJobById(id: Int) {
        try {
            jobDao.removeById(id)
            val response = jobApiService.removeJobById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getLatestPostsById(id: Int) {
        try {
            wallDao.removeAll()
            val response = postApiService.getLatestWallPosts(id, 10)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            wallDao.insert(body.toWallPostEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


}






