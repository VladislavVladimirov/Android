package com.netology.nmedia.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import android.Manifest
import com.netology.nmedia.R
import com.netology.nmedia.di.DependencyContainer
import org.json.JSONObject
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()
    private val pushStub by lazy {
      getString(R.string.new_notification)
    }
    override fun onCreate() {
        super.onCreate()
        val name = getString(R.string.channel_remote_name)
        val descriptionText = getString(R.string.channel_remote_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val dependencyContainer = DependencyContainer.getInstance()
        val authId = dependencyContainer.appAuth.authStateFlow.value.id
        try {
            if (message.data["action"] == null) {
                val pushJson = message.data.values.firstOrNull()?.let { JSONObject(it) }
                val recipientId: String? = pushJson?.optString("recipientId")
                val content: String = pushJson?.optString("content") ?: pushStub
                println("..........................................")
                when(recipientId){
                    "null", authId.toString() -> handlePush(content)
                    "0" -> dependencyContainer.appAuth.uploadPushToken()
                    else -> dependencyContainer.appAuth.uploadPushToken()
                }
            } else {
                message.data["action"]?.let {
                    when (Action.valueOf(it)) {
                        Action.LIKE -> handleLike(
                            gson.fromJson(message.data["content"], Like::class.java)
                        )
                        Action.NEW_POST -> handleNewPost(
                            gson.fromJson(message.data["content"], NewPost::class.java)
                        )
                    }
                }
            }
        } catch (e: java.lang.IllegalArgumentException) {
            handleNotUpdated()
        }
    }
    private fun handlePush(content: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notify(notification)

    }
    private fun handleUnknownAction() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.error
                )
            )
            .setContentText(getString(R.string.notification_unknown_action))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notify(notification)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor
                )
            )
            .setStyle(NotificationCompat.BigTextStyle().bigText( getString(
                R.string.notification_user_liked,
                content.userName,
                content.postAuthor
            )))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notify(notification)
    }

    private fun handleNewPost(content: NewPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_new_post,
                    content.authorName
                )
            )
            .setContentText(content.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notify(notification)
    }
    private fun handleNotUpdated() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.new_notification,
                )
            )
            .setContentText(
                getString(
                    R.string.notification_not_updated,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notify(notification)
    }

    private fun notify(notification: Notification) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }

    override fun onNewToken(token: String) {
        DependencyContainer.getInstance().appAuth.uploadPushToken(token)
    }

}

enum class Action {
    LIKE, NEW_POST
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class NewPost(
    val authorName: String,
    val text: String
)

