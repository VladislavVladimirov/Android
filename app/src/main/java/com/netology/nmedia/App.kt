package com.netology.nmedia

import android.app.Application
import com.netology.nmedia.auth.AppAuth

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initAppAuth(applicationContext)
    }
}