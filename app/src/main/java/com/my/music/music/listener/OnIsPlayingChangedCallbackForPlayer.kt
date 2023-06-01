package com.my.music.music.listener

import androidx.databinding.Observable
import androidx.media3.common.Player
import com.my.music.notification.MusicNotificationManager
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils

class OnIsPlayingChangedCallbackForPlayer(val player: Player) : Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        LogUtils.d("sender = [${sender}], propertyId = [${propertyId}], isPlaying = [${PlayState.isPlaying}]")
        if (PlayState.isPlaying.get()) {
            LogUtils.d("Start to play.")
            player.prepare()
            player.play()
        } else {
            LogUtils.d("Pause to play.")
            player.pause()
        }
        MusicNotificationManager.updatePlayingStatus()
    }
}