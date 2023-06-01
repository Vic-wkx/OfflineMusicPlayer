package com.my.music.music.listener

import androidx.databinding.Observable
import com.my.music.notification.MusicNotificationManager
import com.my.music.repository.MusicRepository.PLAYING_INDEX_SP_KEY
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils
import com.my.music.utils.SPUtils


class OnPlayingIndexChangedCallbackForService : Observable.OnPropertyChangedCallback() {

    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        LogUtils.d("sender = [${sender}], propertyId = [${propertyId}]")
        MusicNotificationManager.updatePlayingStatus()
        SPUtils.INSTANCE.put(PLAYING_INDEX_SP_KEY, PlayState.playingIndex.get())
    }
}