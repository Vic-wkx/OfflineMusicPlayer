package com.my.music.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.my.music.repository.Music
import com.my.music.repository.MusicRepository
import kotlinx.coroutines.launch
import java.io.File

object MusicScanUtils {

    private const val SCAN_DIR_SP_KEY = "SCAN_DIR_SP_KEY"

    fun scanDirWhenCreated(context: Context) {
        val savedScanDir = SPUtils.INSTANCE.getString(SCAN_DIR_SP_KEY)
        LogUtils.d("savedScanDir = [${savedScanDir}]")
        if (savedScanDir.isNotEmpty()) {
            scanDir(context, savedScanDir)
        }
    }

    fun scanUri(context: Context, uri: Uri?) {
        if (uri == null) {
            LogUtils.d("The uri is null, maybe user closed the file picker.")
            return
        }
        val docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
        val treeUri = DocumentsContract.getTreeDocumentId(docUri)
        val dir = convertUriToDir(treeUri)
        LogUtils.d("dir: $dir")
        SPUtils.INSTANCE.put(SCAN_DIR_SP_KEY, dir)
        scanDir(context, dir)
    }

    private fun convertUriToDir(uri: String): String {
        val split = uri.split(File.pathSeparator, limit = 2)
        return "${if (split[0] == "primary") Environment.getExternalStorageDirectory().absolutePath else ""}/${split.getOrNull(1)}"
    }

    private fun scanDir(context: Context, dir: String) {
        PermissionUtils.requestPermission(context as AppCompatActivity) {
            LogUtils.d("Got permission.")
            context.lifecycleScope.launch {
                MusicRepository.init(dir)
            }
        }
    }

    fun scanMusicList(dir: String = ""): List<Music> {
        LogUtils.d("Scan $dir")
        return File(dir).walkTopDown().toList().filter { it.isFile && !it.path.endsWith(".lrc") }.map {
            Music(it.nameWithoutExtension, it.absolutePath)
        }
    }
}