package com.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.netology.nmedia.model.AuthModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"
    private val _authStateFlow: MutableStateFlow<AuthModel>

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getInt(idKey, 0)

        _authStateFlow = if (id == 0 || token == null) {
            prefs.edit {
                clear()
            }
            MutableStateFlow(AuthModel())
        } else {
            MutableStateFlow(AuthModel(id, token))
        }

    }

    val authStateFlow = _authStateFlow.asStateFlow()

    fun setUser(user: AuthModel) {
        _authStateFlow.value = user
       with (prefs.edit()) {
            putInt(idKey, user.id)
            putString(tokenKey, user.token)
            apply()
        }

    }

    fun removeUser() {
        _authStateFlow.value = AuthModel()
        with (prefs.edit()) {
            clear()
            apply()
        }

    }

}


