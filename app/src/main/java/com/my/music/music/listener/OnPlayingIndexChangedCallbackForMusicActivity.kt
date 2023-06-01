package com.my.music.music.listener

import androidx.databinding.Observable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils


class OnPlayingIndexChangedCallbackForMusicActivity(private val rv: RecyclerView) : Observable.OnPropertyChangedCallback(), DefaultLifecycleObserver {

    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        LogUtils.d("sender = [${sender}], propertyId = [${propertyId}]")
        rv.scrollToPosition(PlayState.playingIndex.get())
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