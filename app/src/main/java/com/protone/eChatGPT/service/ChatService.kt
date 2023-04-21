package com.protone.eChatGPT.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.R
import com.protone.eChatGPT.modes.chat.ChatActivity
import com.protone.eChatGPT.utils.getString
import com.protone.eChatGPT.utils.intent
import com.protone.eChatGPT.utils.tryWithCatch

class ChatService : Service() {

    companion object {
        const val DISMISS_ACTION = "NOTIFICATION_DISMISSED"
    }

    private val notificationBroadcastReceiver by lazy {
        LocalBroadcastManager.getInstance(EApplication.app)
    }

    private val notificationReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.takeIf { it.action == DISMISS_ACTION }?.let { notify() }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        tryWithCatch {
            notificationBroadcastReceiver.registerReceiver(
                notificationReceiver,
                IntentFilter(DISMISS_ACTION)
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tryWithCatch { startForeground(0x01, notify()) }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        tryWithCatch { notificationBroadcastReceiver.unregisterReceiver(notificationReceiver) }
    }

    private fun Context.notify(): Notification {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        return manager.initNotification(this).apply { manager.notify(0x01, this) }
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
                channel.setShowBadge(false)
                channel.enableLights(false)
                createNotificationChannel(channel)
                Notification.Builder(context, packageName).apply {
                    setOngoing(true)
                    setContentTitle(R.string.open_chat.getString())
                    setAutoCancel(true)
                    setTicker("")
                    setUsesChronometer(true)
                    setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    setSmallIcon(R.drawable.send_msg)
                    setDeleteIntent(
                        PendingIntent.getBroadcast(
                            this@ChatService,
                            1,
                            Intent(DISMISS_ACTION),
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    setCustomContentView(it)
                }.build()
            } else Notification().apply {
                icon = R.drawable.send_msg
                tickerText = ""
                contentView = it
            }
        }.also {
            it.flags =
                Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT or Notification.FLAG_FOREGROUND_SERVICE
            it.visibility = Notification.VISIBILITY_PUBLIC
            it.category = Notification.CATEGORY_TRANSPORT
        }
    }
}