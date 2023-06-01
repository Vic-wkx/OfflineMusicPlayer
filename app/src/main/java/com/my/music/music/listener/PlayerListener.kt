package com.my.music.music.listener

import androidx.media3.common.Player
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils

class PlayerListener : Player.Listener {
    override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        LogUtils.d("onPositionDiscontinuity, oldPosition =  ${oldPosition.mediaItemIndex}, newPosition = ${newPosition.mediaItemIndex}, reason = $reason")
        PlayState.selectIndex(newPosition.mediaItemIndex)
    }
}