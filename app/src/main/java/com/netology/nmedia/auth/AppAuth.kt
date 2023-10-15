package com.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.netology.nmedia.di.DependencyContainer
import com.netology.nmedia.dto.PushToken
import com.netology.nmedia.model.AuthModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AppAuth(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"
    private val _authStateFlow: MutableStateFlow<AuthModel>

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0)

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
            putLong(idKey, user.id)
            putString(tokenKey, user.token)
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
                DependencyContainer.getInstance().apiService.uploadPushToken(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


