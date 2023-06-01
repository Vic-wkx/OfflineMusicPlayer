package com.my.music.music.listener

import android.net.Uri
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.media3.common.MediaItem
import com.my.music.music.service.MusicBinder
import com.my.music.repository.Music
import com.my.music.repository.MusicRepository
import com.my.music.utils.LogUtils
import com.my.music.utils.SPUtils

class OnMusicListChangedCallbackForPlayer(private val musicBinder: MusicBinder) : OnListChangedCallback<ObservableArrayList<Music>>() {
    override fun onChanged(sender: ObservableArrayList<Music>) {
        LogUtils.d("sender.size = [${sender.size}]")
    }

    override fun onItemRangeChanged(sender: ObservableArrayList<Music>, positionStart: Int, itemCount: Int) {
        LogUtils.d("sender.size = [${sender.size}], positionStart = [${positionStart}], itemCount = [${itemCount}]")
    }

    override fun onItemRangeInserted(sender: ObservableArrayList<Music>, positionStart: Int, itemCount: Int) {
        LogUtils.d("sender.size = [${sender.size}], positionStart = [${positionStart}], itemCount = [${itemCount}]")
        musicBinder.player.setMediaItems(sender.map { MediaItem.fromUri(Uri.parse(it.path)) })
        musicBinder.player.seekToDefaultPosition(SPUtils.INSTANCE.getInt(MusicRepository.PLAYING_INDEX_SP_KEY, 0))
    }

    override fun onItemRangeMoved(sender: ObservableArrayList<Music>, fromPosition: Int, toPosition: Int, itemCount: Int) {
        LogUtils.d("sender.size = [${sender.size}], fromPosition = [${fromPosition}], toPosition = [${toPosition}], itemCount = [${itemCount}]")
    }

    override fun onItemRangeRemoved(sender: ObservableArrayList<Music>, positionStart: Int, itemCount: Int) {
        LogUtils.d("sender.size = [${sender.size}], positionStart = [${positionStart}], itemCount = [${itemCount}]")
    }
}