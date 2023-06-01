package com.my.music.utils

import android.Manifest
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.my.music.R
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope

object PermissionUtils {
    fun requestPermission(context: AppCompatActivity, block: () -> Unit) {
        PermissionX.init(context).permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope: ExplainScope, deniedList, beforeRequest ->
                scope.showRequestReasonDialog(deniedList, "扫描音乐需要读取媒体库权限", "好的")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "需要去设置中手动开启权限", "Ok")
            }
            .setDialogTintColor(R.color.white, R.color.purple_200)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    block.invoke()
                } else {
                    Toast.makeText(context, "请求权限被拒绝", Toast.LENGTH_SHORT).show()
                }
            }
    }
}