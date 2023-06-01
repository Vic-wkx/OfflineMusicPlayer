package com.my.music.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.my.music.music.service.MusicService
import com.my.music.notification.MusicNotificationManager.Companion.nextAction
import com.my.music.notification.MusicNotificationManager.Companion.pausePlayAction
import com.my.music.notification.MusicNotificationManager.Companion.prevAction
import com.my.music.utils.LogUtils

class MusicNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        LogUtils.d("context = [${context}], intent = [${intent}]")
        if (context !is MusicService) return
        when (intent.action) {
            pausePlayAction -> {
                context.musicBinder.playOrPause()
            }

            prevAction -> {
                context.musicBinder.playPrev()
            }

            nextAction -> {
                context.musicBinder.playNext()
            }
        }
    }
}