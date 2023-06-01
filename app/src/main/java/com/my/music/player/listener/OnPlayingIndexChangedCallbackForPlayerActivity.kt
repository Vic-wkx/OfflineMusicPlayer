package com.my.music.player.listener

import android.widget.TextView
import androidx.databinding.Observable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils

class OnPlayingIndexChangedCallbackForPlayerActivity(private val tvTitle: TextView) : Observable.OnPropertyChangedCallback(), DefaultLifecycleObserver {

    init {
        tvTitle.text = PlayState.playingMusic().title
    }

    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        LogUtils.d("sender = [${sender}], propertyId = [${propertyId}]")
        tvTitle.text = PlayState.playingMusic().title
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        PlayState.playingIndex.addOnPropertyChangedCallback(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        PlayState.playingIndex.removeOnPropertyChangedCallback(this)
    }
}