package lk.nibm.kandy.hdse252ft.smartrestaurantpos.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {
    
    val CAMERA_PERMISSION = Manifest.permission.CAMERA
    
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
