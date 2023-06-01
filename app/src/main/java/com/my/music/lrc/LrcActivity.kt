package com.my.music.lrc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.my.music.R
import com.my.music.databinding.ActivityLrcBinding
import com.my.music.internal.BaseActivity
import com.my.music.lrc.listener.OnPlayingIndexChangedCallbackForLrcActivity
import com.my.music.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LrcActivity : BaseActivity() {
    companion object {
        const val UPDATE_INTERVAL = 300L
        fun start(context: Context) {
            val starter = Intent(context, LrcActivity::class.java)
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityLrcBinding
    private val lrcViewModel by viewModels<LrcViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lrc)
        val onPlayingIndexChangedCallbackForLrcActivity = OnPlayingIndexChangedCallbackForLrcActivity(lifecycleScope, binding.lrcView)
        lifecycle.addObserver(onPlayingIndexChangedCallbackForLrcActivity)
        lrcViewModel.bindService(this) {
            binding.lrcView.onPlayClickListener = { view, time ->
                LogUtils.d("view = [${view}], time = [${time}]")
                lrcViewModel.musicBinder.player.seekTo(time)
            }
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    LogUtils.d("Load lrc with current position.")
                    onPlayingIndexChangedCallbackForLrcActivity.loadCurrentLrc(lrcViewModel.musicBinder.player.contentPosition)
                }
                while (true) {
                    delay(UPDATE_INTERVAL)
                    binding.lrcView.updateTime(lrcViewModel.musicBinder.player.contentPosition)
                }
            }
        }
    }
}