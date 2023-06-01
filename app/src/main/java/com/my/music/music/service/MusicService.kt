package com.my.music.music.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.IBinder
import androidx.media3.common.MediaItem
import com.my.music.music.listener.OnMusicListChangedCallbackForPlayer
import com.my.music.notification.MusicNotificationManager
import com.my.music.notification.MusicNotificationManager.Companion.nextAction
import com.my.music.notification.MusicNotificationManager.Companion.pausePlayAction
import com.my.music.notification.MusicNotificationManager.Companion.prevAction
import com.my.music.notification.MusicNotificationReceiver
import com.my.music.repository.MusicRepository
import com.my.music.utils.LogUtils
import com.my.music.utils.SPUtils

class MusicService : Service() {

    val musicBinder by lazy { MusicBinder(this) }
    private val onMusicListChangedCallbackForPlayer by lazy { OnMusicListChangedCallbackForPlayer(musicBinder) }
    private val musicNotificationManager by lazy { MusicNotificationManager(this) }
    private val musicNotificationReceiver by lazy { MusicNotificationReceiver() }
    private val musicNotificationReceiverIntentFilter by lazy {
        IntentFilter().apply {
            addAction(pausePlayAction)
            addAction(prevAction)
            addAction(nextAction)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return musicBinder
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.d()
        musicBinder.player.setMediaItems(MusicRepository.musicList.map { MediaItem.fromUri(Uri.parse(it.path)) })
        musicBinder.player.seekToDefaultPosition(SPUtils.INSTANCE.getInt(MusicRepository.PLAYING_INDEX_SP_KEY, 0))
        MusicRepository.musicList.addOnListChangedCallback(onMusicListChangedCallbackForPlayer)
        val notification = musicNotificationManager.createMusicNotification()
        startForeground(MusicNotificationManager.musicNotificationId, notification)
        registerReceiver(musicNotificationReceiver, musicNotificationReceiverIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicRepository.musicList.removeOnListChangedCallback(onMusicListChangedCallbackForPlayer)
        unregisterReceiver(musicNotificationReceiver)
        musicBinder.release()
    }
}