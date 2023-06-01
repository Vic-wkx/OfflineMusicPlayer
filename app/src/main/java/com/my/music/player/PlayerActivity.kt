package com.my.music.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.media3.common.util.UnstableApi
import com.my.music.R
import com.my.music.databinding.ActivityPlayerBinding
import com.my.music.internal.BaseActivity
import com.my.music.lrc.LrcActivity
import com.my.music.player.listener.OnIsPlayingChangedCallbackForPlayerActivity
import com.my.music.player.listener.OnPlayingIndexChangedCallbackForPlayerActivity
import com.my.music.utils.LogUtils


class PlayerActivity : BaseActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val playerViewModel by viewModels<PlayerViewModel>()
    private val ivCD by lazy { binding.playerView.findViewById<ImageView>(R.id.ivCD) }

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)
        lifecycle.addObserver(playerViewModel)
        lifecycle.addObserver(OnIsPlayingChangedCallbackForPlayerActivity(ivCD))
        lifecycle.addObserver(OnPlayingIndexChangedCallbackForPlayerActivity(binding.playerView.findViewById(R.id.tvTitle)))
        playerViewModel.bindService(this) {
            binding.playerView.player = playerViewModel.musicBinder.player
            binding.playerView.showController()
            binding.playerView.controllerShowTimeoutMs = -1
            binding.playerView.controllerHideOnTouch = false
            binding.playerView.findViewById<ImageButton>(R.id.exo_prev).setOnClickListener {
                playerViewModel.musicBinder.player.seekToPreviousMediaItem()
            }
            binding.playerView.findViewById<ImageButton>(R.id.exo_play_pause).setOnClickListener {
                playerViewModel.musicBinder.playOrPause()
            }
            ivCD.setOnClickListener {
                LrcActivity.start(this)
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, PlayerActivity::class.java)
            context.startActivity(starter)
        }
    }
}