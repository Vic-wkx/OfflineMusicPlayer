package com.my.music.music

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.my.music.R
import com.my.music.databinding.ActivityMusicBinding
import com.my.music.internal.BaseActivity
import com.my.music.music.listener.OnPlayingIndexChangedCallbackForMusicActivity
import com.my.music.player.PlayerActivity
import com.my.music.repository.MusicRepository
import com.my.music.repository.PlayState
import com.my.music.utils.LogUtils
import com.my.music.utils.MusicScanUtils


class MusicActivity : BaseActivity() {
    private lateinit var binding: ActivityMusicBinding
    private val musicViewModel by viewModels<MusicViewModel>()
    private var openDocumentTreeLauncher: ActivityResultLauncher<Uri?> = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
        MusicScanUtils.scanUri(this, it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_music)
        binding.musicRepository = MusicRepository
        binding.musicViewModel = musicViewModel
        binding.playState = PlayState
        setSupportActionBar(binding.toolbar)
        lifecycle.addObserver(musicViewModel)
        lifecycle.addObserver(OnPlayingIndexChangedCallbackForMusicActivity(binding.rvMusic))
    }

    fun scanMusic(view: View? = null) {
        LogUtils.d("btnScan clicked. $view")
        openDocumentTreeLauncher.launch(null)
    }

    fun playOrPause(view: View) {
        LogUtils.d("view = [${view}]")
        musicViewModel.musicBinder?.playOrPause()
    }

    fun navigateToPlayerActivity(view: View) {
        LogUtils.d("view = [${view}]")
        PlayerActivity.start(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reload -> {
                scanMusic()
            }
        }
        // TODO: Add search logic
        return true
    }

}