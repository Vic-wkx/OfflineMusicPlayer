package com.my.music.repository

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.my.music.utils.LogUtils
import com.my.music.utils.MusicScanUtils
import com.my.music.utils.SPUtils

object MusicRepository {
    val musicList: ObservableList<Music> = ObservableArrayList()
    const val PLAYING_INDEX_SP_KEY = "PLAYING_INDEX_SP_KEY"

    fun init(dir: String = "") {
        LogUtils.d("init: $dir")
        musicList.clear()
        musicList.addAll(MusicScanUtils.scanMusicList(dir))
        if (musicList.isEmpty()) return
        PlayState.selectIndex(SPUtils.INSTANCE.getInt(PLAYING_INDEX_SP_KEY, 0))
    }
}