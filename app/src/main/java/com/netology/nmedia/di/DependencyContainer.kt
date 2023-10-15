package com.netology.nmedia.di

import android.content.Context
import androidx.room.Room
import com.netology.nmedia.BuildConfig
import com.netology.nmedia.api.ApiService
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.db.AppDb
import com.netology.nmedia.repository.DraftRepository
import com.netology.nmedia.repository.DraftRepositorySharedPrefsImpl
import com.netology.nmedia.repository.PostRepository
import com.netology.nmedia.repository.PostRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

class DependencyContainer(
    private val context: Context
) {
    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"

        @Volatile
        private var instance: DependencyContainer? = null
        fun initApp(context: Context) {
            instance = DependencyContainer(context)
        }
        fun getInstance(): DependencyContainer {
            return instance!!
        }

    }

    private val appDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()
    private val postDao = appDb.postDao()
    val appAuth = AppAuth(context)
    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .addInterceptor { chain ->
            appAuth.authStateFlow.value.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }
        .build()
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .baseUrl(BASE_URL)
        .build()
    val apiService = retrofit.create<ApiService>()

    val repository: PostRepository = PostRepositoryImpl(postDao, apiService)
    val draftRepository: DraftRepository = DraftRepositorySharedPrefsImpl(context)


}

