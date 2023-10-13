package com.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.netology.nmedia.api.Api
import com.netology.nmedia.dto.PushToken
import com.netology.nmedia.model.AuthModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authStateFlow: MutableStateFlow<AuthModel>

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0)

        if (id == 0L || token == null) {
            prefs.edit {
                clear()
            }
            _authStateFlow = MutableStateFlow(AuthModel())
        } else {
            _authStateFlow = MutableStateFlow(AuthModel(id, token))
        }
        uploadPushToken()
    }

    val authStateFlow = _authStateFlow.asStateFlow()



    fun setUser(user: AuthModel) {
        _authStateFlow.value = user
       with (prefs.edit()) {
            putLong(ID_KEY, user.id)
            putString(TOKEN_KEY, user.token)
            apply()
        }
        uploadPushToken()
    }

    fun removeUser() {
        _authStateFlow.value = AuthModel()
        with (prefs.edit()) {
            clear()
            apply()
        }
        uploadPushToken()
    }
    fun uploadPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                Api.retrofitService.uploadPushToken(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"

        @Volatile
        private var instance: AppAuth? = null

        @Synchronized
        fun initAppAuth(context: Context): AppAuth {
            return instance ?: AppAuth(context).apply { instance = this }
        }

        fun getInstance(): AppAuth = requireNotNull(instance) { "initAppAuth was not invoked" }
    }
}


