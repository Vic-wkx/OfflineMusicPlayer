package com.my.music.music

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.my.music.music.listener.OnMusicListChangedCallbackForAdapter
import com.my.music.music.service.MusicBinder
import com.my.music.music.service.MusicService
import com.my.music.repository.MusicRepository
import com.my.music.utils.LogUtils
import com.my.music.utils.MusicScanUtils

class MusicViewModel : ViewModel(), DefaultLifecycleObserver {
    val musicAdapter by lazy { MusicAdapter(this) }
    private val onMusicListChangedCallbackForAdapter by lazy { OnMusicListChangedCallbackForAdapter(musicAdapter) }
    var musicBinder: MusicBinder? = null
    private val musicServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                LogUtils.d()
                musicBinder = service as MusicBinder
            }

            override fun onServiceDisconnected(name: ComponentName) {
                LogUtils.d()
            }
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        LogUtils.d("onCreate")
        (owner as Activity).bindService(Intent(owner, MusicService::class.java), musicServiceConnection, Context.BIND_AUTO_CREATE)
        MusicRepository.musicList.addOnListChangedCallback(onMusicListChangedCallbackForAdapter)
        MusicScanUtils.scanDirWhenCreated(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        LogUtils.d("onDestroy")
        (owner as Activity).unbindService(musicServiceConnection)
        MusicRepository.musicList.removeOnListChangedCallback(onMusicListChangedCallbackForAdapter)
    }
}