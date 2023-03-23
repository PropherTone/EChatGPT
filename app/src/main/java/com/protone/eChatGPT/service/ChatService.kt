package com.protone.eChatGPT.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import com.protone.eChatGPT.R
import com.protone.eChatGPT.activity.ChatActivity
import com.protone.eChatGPT.utils.getString
import com.protone.eChatGPT.utils.intent

class ChatService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = manager.initNotification(this)
        startForeground(0x01, notification.apply { manager.notify(0x01, this) })
        return super.onStartCommand(intent, flags, startId)
    }

    @Suppress("DEPRECATION")
    private fun NotificationManager.initNotification(context: Context): Notification {
        return RemoteViews(packageName, R.layout.chat_remote_view).let {
            PendingIntent.getActivity(
                context,
                0,
                ChatActivity::class.intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            ).apply { it.setOnClickPendingIntent(R.id.open_chat, this) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    packageName,
                    "ChatNotification",
                    NotificationManager.IMPORTANCE_LOW
                )
                createNotificationChannel(channel)
                Notification.Builder(context, packageName).apply {
                    setOngoing(true)
                    setContentTitle(R.string.open_chat.getString())
                    setTicker("")
                    setSmallIcon(R.drawable.send_msg)
                    setCustomContentView(it)
                }.build()
            } else Notification().apply {
                icon = R.drawable.send_msg
                tickerText = ""
                contentView = it
            }
        }.also {
            it.flags = Notification.FLAG_NO_CLEAR
        }
    }
}