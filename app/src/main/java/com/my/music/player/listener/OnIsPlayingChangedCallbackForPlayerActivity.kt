package com.my.music.player.listener

import android.animation.AnimatorInflater
import android.widget.ImageView
import androidx.databinding.Observable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.my.music.R
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils

class OnIsPlayingChangedCallbackForPlayerActivity(private val ivCD: ImageView) : Observable.OnPropertyChangedCallback(), DefaultLifecycleObserver {

    private val rotateAnimator by lazy {
        AnimatorInflater.loadAnimator(ivCD.context, R.animator.anim_loading).apply {
            setTarget(ivCD)
        }
    }

    init {
        if (PlayState.isPlaying.get()) {
            LogUtils.d("Start to play.")
            rotateAnimator.start()
        }
    }

    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        LogUtils.d("sender = [${sender}], propertyId = [${propertyId}], isPlaying = [${PlayState.isPlaying}]")
        if (PlayState.isPlaying.get()) {
            LogUtils.d("Start to play.")
            if (rotateAnimator.isPaused) {
                rotateAnimator.resume()
            } else {
                rotateAnimator.start()
            }
        } else {
            LogUtils.d("Pause to play.")
            rotateAnimator.pause()
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        PlayState.isPlaying.addOnPropertyChangedCallback(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        PlayState.isPlaying.removeOnPropertyChangedCallback(this)
        ivCD.clearAnimation()
    }
}