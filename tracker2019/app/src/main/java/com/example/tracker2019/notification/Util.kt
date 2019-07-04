package com.example.tracker2019.notification

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/14/19
 * @desc
 */
object Util {
    fun create(context: Context, channelId: String, init: NotificationCompat.Builder.() -> Unit): Notification {
        val builder = NotificationCompat.Builder(context, channelId)
        builder.apply(init)
        return builder.build()
    }

    fun show(context: Context, notificationId: Int, notification: Notification) {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
    }

    fun dismiss(context: Context, notificationId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }
    }
}
