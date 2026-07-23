package nhn.ntech.ndraw.helper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import nhn.ntech.ndraw.domain.prefs.UserPreferences

object PermissionManager {

    val photoPermission: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    val cameraPermission: String = Manifest.permission.CAMERA

    fun isCameraPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            cameraPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isMediaPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            photoPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkCameraPermission(
        activity: Activity,
        onGranted: () -> Unit,
        onLaunchLauncher: () -> Unit
    ) {
        if (isCameraPermissionGranted(activity)) {
            onGranted()
        } else {
            val userPrefs = UserPreferences(activity)
            val isAskedBefore = userPrefs.isCameraAskedBefore()
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, cameraPermission)

            if (isAskedBefore && !shouldShowRationale) {
                // Permanently denied by Android OS -> Direct user to Settings
                goToSettings(activity)
            } else {
                userPrefs.setCameraAskedBefore(true)
                onLaunchLauncher()
            }
        }
    }

    fun checkMediaPermission(
        activity: Activity,
        onGranted: () -> Unit,
        onLaunchLauncher: () -> Unit
    ) {
        if (isMediaPermissionGranted(activity)) {
            onGranted()
        } else {
            val userPrefs = UserPreferences(activity)
            val isAskedBefore = userPrefs.isMediaAskedBefore()
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, photoPermission)

            if (isAskedBefore && !shouldShowRationale) {
                // Permanently denied by Android OS -> Direct user to Settings
                goToSettings(activity)
            } else {
                userPrefs.setMediaAskedBefore(true)
                onLaunchLauncher()
            }
        }
    }

    fun goToSettings(context: Context) {
        Toast.makeText(
            context,
            "Please enable permissions in Settings",
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}