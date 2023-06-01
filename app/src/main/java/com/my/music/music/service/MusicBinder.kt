package com.my.music.music.service

import android.content.Context
import android.os.Binder
import android.widget.Toast
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.my.music.MusicApplication
import com.my.music.music.listener.OnIsPlayingChangedCallbackForPlayer
import com.my.music.music.listener.OnPlayingIndexChangedCallbackForService
import com.my.music.music.listener.PlayerListener
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils

class MusicBinder(private val context: Context) : Binder() {

    val player: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                addListener(playerListener)
            }
    }
    private val playerListener by lazy { PlayerListener() }
    private val onIsPlayingChangedCallbackForPlayer by lazy { OnIsPlayingChangedCallbackForPlayer(player) }
    private val onPlayingIndexChangedCallbackForService by lazy { OnPlayingIndexChangedCallbackForService() }

    init {
        PlayState.isPlaying.addOnPropertyChangedCallback(onIsPlayingChangedCallbackForPlayer)
        PlayState.playingIndex.addOnPropertyChangedCallback(onPlayingIndexChangedCallbackForService)
    }

    fun startPlay(index: Int = player.currentMediaItemIndex) {
        LogUtils.d("startPlay. isPlaying: ${PlayState.isPlaying.get()}, index: $index")
        if (index == player.currentMediaItemIndex && PlayState.isPlaying.get()) {
            LogUtils.d("It's already playing.")
            return
        }
        player.seekToDefaultPosition(index)
        PlayState.playStarted(index)
    }

    fun playOrPause() {
        LogUtils.d("playOrPause. isPlaying: ${PlayState.isPlaying.get()}")
        if (player.mediaItemCount == 0) {
            LogUtils.d("There's no media item.")
            Toast.makeText(MusicApplication.application, "There's no media item.", Toast.LENGTH_SHORT).show()
            return
        }
        PlayState.isPlaying.set(!PlayState.isPlaying.get())
    }

    fun playPrev() {
        LogUtils.d("playPrev")
        if (player.mediaItemCount == 0) {
            LogUtils.d("There's no media item.")
            Toast.makeText(MusicApplication.application, "There's no media item.", Toast.LENGTH_SHORT).show()
            return
        }
        player.seekToPreviousMediaItem()
    }

    fun playNext() {
        LogUtils.d("playNext")
        if (player.mediaItemCount == 0) {
            LogUtils.d("There's no media item.")
            Toast.makeText(MusicApplication.application, "There's no media item.", Toast.LENGTH_SHORT).show()
            return
        }
        player.seekToNextMediaItem()
    }

    private fun stopPlay() {
        LogUtils.d("stopPlay, isPlaying: ${PlayState.isPlaying.get()}, PlayState.isPlaying: ${PlayState.isPlaying}")
        if (PlayState.isPlaying.get()) {
            PlayState.isPlaying.set(false)
        }
    }

    fun release() {
        stopPlay()
        player.removeListener(playerListener)
        PlayState.isPlaying.removeOnPropertyChangedCallback(onIsPlayingChangedCallbackForPlayer)
        PlayState.playingIndex.removeOnPropertyChangedCallback(onPlayingIndexChangedCallbackForService)
        player.release()
    }
}