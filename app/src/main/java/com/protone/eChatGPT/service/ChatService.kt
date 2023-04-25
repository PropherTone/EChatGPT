package com.protone.eChatGPT.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.R
import com.protone.eChatGPT.modes.chat.ChatActivity
import com.protone.eChatGPT.modes.history.HistoryActivity
import com.protone.eChatGPT.modes.image.ImageActivity
import com.protone.eChatGPT.modes.menu.MenuActivity
import com.protone.eChatGPT.repository.UserConfig
import com.protone.eChatGPT.repository.UserConfig.Shortcut.Companion.getResource
import com.protone.eChatGPT.repository.userConfig
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
        val shortcuts = userConfig.notificationShortcuts
        val size = shortcuts.size
        return RemoteViews(
            packageName, when (size) {
                2 -> R.layout.chat_remote_view_2icon
                3 -> R.layout.chat_remote_view_3icon
                4 -> R.layout.chat_remote_view_4icon
                else -> R.layout.chat_remote_view_1icon
            }
        ).let {
            fun setPendingIntent(shortcut: UserConfig.Shortcut, @IdRes id: Int) {
                it.setImageViewResource(id, shortcut.getResource())
                PendingIntent.getActivity(
                    context,
                    0,
                    shortcut.getActivityIntent(),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                ).apply { it.setOnClickPendingIntent(id, this) }
            }
            if (size > 0) setPendingIntent(shortcuts[0], R.id.icon1)
            if (size >= 2) setPendingIntent(shortcuts[1], R.id.icon2)
            if (size >= 3) setPendingIntent(shortcuts[2], R.id.icon3)
            if (size >= 4) setPendingIntent(shortcuts[3], R.id.icon4)
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

    private fun UserConfig.Shortcut.getActivityIntent() = when (this) {
        UserConfig.Shortcut.Config -> MenuActivity::class.intent.putExtra(
            MenuActivity.OPEN_CONFIG,
            true
        )

        UserConfig.Shortcut.Image -> ImageActivity::class.intent
        UserConfig.Shortcut.History -> HistoryActivity::class.intent
//        UserConfig.Shortcut.Audio -> R.drawable.audio
//        UserConfig.Shortcut.Completions -> R.drawable.completions
//        UserConfig.Shortcut.Edits -> R.drawable.edit
//        UserConfig.Shortcut.Embeddings -> R.drawable.embeddings
//        UserConfig.Shortcut.Files -> R.drawable.file
//        UserConfig.Shortcut.FineTunes -> R.drawable.tune
//        UserConfig.Shortcut.Models -> R.drawable.model
//        UserConfig.Shortcut.Moderations -> R.drawable.moderation
        else -> ChatActivity::class.intent
    }


}