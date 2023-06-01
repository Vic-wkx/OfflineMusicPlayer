package com.my.music.music.listener

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList.OnListChangedCallback
import com.my.music.repository.Music
import com.my.music.music.MusicAdapter
import com.my.music.utils.LogUtils

class OnMusicListChangedCallbackForAdapter(private val musicAdapter: MusicAdapter) : OnListChangedCallback<ObservableArrayList<Music>>() {
    override fun onChanged(sender: ObservableArrayList<Music>) {
        LogUtils.d()
    }

    override fun onItemRangeChanged(sender: ObservableArrayList<Music>, positionStart: Int, itemCount: Int) {
        LogUtils.d("positionStart = [${positionStart}], itemCount = [${itemCount}]")
        musicAdapter.notifyItemRangeChanged(positionStart, itemCount)
    }

    override fun onItemRangeInserted(sender: ObservableArrayList<Music>, positionStart: Int, itemCount: Int) {
        LogUtils.d()
        musicAdapter.notifyItemRangeInserted(positionStart, itemCount)
    }

    override fun onItemRangeMoved(sender: ObservableArrayList<Music>, fromPosition: Int, toPosition: Int, itemCount: Int) {
        LogUtils.d()
    }

    override fun onItemRangeRemoved(sender: ObservableArrayList<Music>, positionStart: Int, itemCount: Int) {
        LogUtils.d()
    }
}