package com.my.music.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.my.music.MusicApplication
import com.my.music.R
import com.my.music.music.MusicActivity
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils

class MusicNotificationManager(val context: Context) {
    companion object {
        const val pausePlayAction = "com.music.pause.play" // pause or play
        const val prevAction = "com.music.prev"
        const val nextAction = "com.music.next"
        const val musicNotificationId = 9001
        const val musicChannelId = "music channel id"
        const val musicChannelName = "music channel name"
        val notificationManager by lazy { MusicApplication.application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
        lateinit var notification: Notification
        val views = RemoteViews(MusicApplication.application.packageName, R.layout.layout_player_notification)

        fun updatePlayingStatus() {
            LogUtils.d()
            views.setTextViewText(R.id.tvTitle, PlayState.playingMusic().title)
            views.setImageViewResource(R.id.btnPlayOrPause, if (PlayState.isPlaying.get()) androidx.media3.ui.R.drawable.exo_styled_controls_pause else androidx.media3.ui.R.drawable.exo_styled_controls_play)
            notificationManager.notify(musicNotificationId, notification)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun createMusicNotification(): Notification {
        LogUtils.d()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(musicChannelId, musicChannelName, NotificationManager.IMPORTANCE_LOW)
            channel.setSound(null, null)
            notificationManager.createNotificationChannel(channel)
        }
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        views.setOnClickPendingIntent(R.id.btnPlayOrPause, PendingIntent.getBroadcast(context, 0, Intent(pausePlayAction), flag))
        views.setOnClickPendingIntent(R.id.btnPrev, PendingIntent.getBroadcast(context, 0, Intent(prevAction), flag))
        views.setOnClickPendingIntent(R.id.btnNext, PendingIntent.getBroadcast(context, 0, Intent(nextAction), flag))
        notification = NotificationCompat.Builder(context, musicChannelId)
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(Intent(context, MusicActivity::class.java))
                getPendingIntent(0, flag)
            })
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setCustomContentView(views)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCustomBigContentView(views)
            .setSound(null)
            .build()
        updatePlayingStatus()
        return notification
    }
}