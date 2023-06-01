package com.my.music.lrc

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.my.music.music.service.MusicBinder
import com.my.music.music.service.MusicService
import com.my.music.utils.LogUtils

class LrcViewModel : ViewModel(), DefaultLifecycleObserver {

    lateinit var musicBinder: MusicBinder

    private lateinit var musicServiceConnection: ServiceConnection

    fun bindService(activity: Activity, block: () -> Unit) {
        musicServiceConnection = MusicServiceConnection { service ->
            musicBinder = service as MusicBinder
            block()
        }
        activity.bindService(Intent(activity, MusicService::class.java), musicServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        LogUtils.d("onDestroy")
        (owner as Activity).unbindService(musicServiceConnection)
    }

    class MusicServiceConnection(val block: (IBinder) -> Unit) : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            LogUtils.d()
            block(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            LogUtils.d()
        }

    }
}