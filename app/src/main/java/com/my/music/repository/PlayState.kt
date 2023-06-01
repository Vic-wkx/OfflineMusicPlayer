package com.my.music.repository

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.my.music.utils.LogUtils

object PlayState {
    var isPlaying: ObservableBoolean = ObservableBoolean(false)
    var playingIndex: ObservableInt = ObservableInt(0)

    fun playStarted(index: Int) {
        if (!isPlaying.get()) {
            isPlaying.set(true)
        }
        selectIndex(index)
    }

    fun playingMusic(): Music = if (playingIndex.get() < MusicRepository.musicList.size) MusicRepository.musicList[playingIndex.get()] else Music()

    fun selectIndex(index: Int) {
        if (MusicRepository.musicList.isEmpty()) return
        if (index >= MusicRepository.musicList.size) {
            LogUtils.e("Index is out of musicList.size. index: $index, size: ${MusicRepository.musicList.size}")
            selectIndex(0)
            return
        }
        LogUtils.d("index = [${index}]")
        MusicRepository.apply {
            if (playingIndex.get() != index) {
                musicList[playingIndex.get()] = musicList[playingIndex.get()].apply { isSelected = false }
            }
            musicList[index] = musicList[index].apply { isSelected = true }
        }
        playingIndex.set(index)
    }
}