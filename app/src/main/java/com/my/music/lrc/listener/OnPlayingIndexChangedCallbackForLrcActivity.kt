package com.my.music.lrc.listener

import androidx.databinding.Observable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.my.music.lrc.view.LrcView
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class OnPlayingIndexChangedCallbackForLrcActivity(private val lifecycleScope: CoroutineScope, private val lrcView: LrcView) : Observable.OnPropertyChangedCallback(), DefaultLifecycleObserver {

    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        LogUtils.d("sender = [${sender}], propertyId = [${propertyId}]")
        lifecycleScope.launch {
            loadCurrentLrc()
        }
    }

    suspend fun loadCurrentLrc(time: Long = 0) = withContext(Dispatchers.Default) {
        val lrcFile = File(PlayState.playingMusic().path.replaceAfterLast(".", "lrc"))
        LogUtils.d("lrcFile: $lrcFile")
        lrcView.loadLrc(lrcFile, time)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        PlayState.playingIndex.addOnPropertyChangedCallback(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        PlayState.playingIndex.removeOnPropertyChangedCallback(this)
    }
}