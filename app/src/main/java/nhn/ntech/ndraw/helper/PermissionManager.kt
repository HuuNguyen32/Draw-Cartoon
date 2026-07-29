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
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.utils.DialogUtils

import android.app.Dialog

object PermissionManager {

    private var activePermissionDialog: Dialog? = null

    fun dismissPermissionDialog() {
        activePermissionDialog?.let {
            if (it.isShowing) {
                try {
                    it.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        activePermissionDialog = null
    }

    val photoPermission: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        onLaunchLauncher: () -> Unit,
        isSketching: Boolean = false,
        title: String = activity.getString(R.string.permission_settings_title),
        description: String = activity.getString(R.string.permission_settings_des),
    ) {
        if (isCameraPermissionGranted(activity)) {
            dismissPermissionDialog()
            onGranted()
        } else {
            if (activePermissionDialog?.isShowing == true) {
                return
            }
            val userPrefs = UserPreferences(activity)
            val isAskedBefore = userPrefs.isCameraAskedBefore()
            val shouldShowRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(activity, cameraPermission)

            if (isAskedBefore && !shouldShowRationale) {
                activePermissionDialog = DialogUtils.createConfirmDialog(
                    activity,
                    title,
                    description,
                    activity.getString(R.string.open_settings_title),
                    activity.getString(R.string.cancel_title),
                    onConfirm = {
                        activePermissionDialog = null
                        goToSettings(activity)
                    },
                    onDeny = {
                        activePermissionDialog = null
                        activity.finish()
                    },
                    isSketching = isSketching
                )
            } else {
                userPrefs.setCameraAskedBefore(true)
                onLaunchLauncher()
            }
        }
    }

    fun checkMediaPermission(
        activity: Activity,
        onGranted: () -> Unit,
        onLaunchLauncher: () -> Unit,
    ) {
        if (isMediaPermissionGranted(activity)) {
            dismissPermissionDialog()
            onGranted()
        } else {
            if (activePermissionDialog?.isShowing == true) {
                return
            }
            val userPrefs = UserPreferences(activity)
            val isAskedBefore = userPrefs.isMediaAskedBefore()
            val shouldShowRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(activity, photoPermission)

            if (isAskedBefore && !shouldShowRationale) {
                // Permanently denied by Android OS -> Direct user to Settings
                activePermissionDialog = DialogUtils.createConfirmDialog(
                    activity,
                    activity.getString(R.string.permission_settings_title),
                    activity.getString(R.string.permission_settings_des),
                    activity.getString(R.string.open_settings_title),
                    activity.getString(R.string.cancel_title),
                    onConfirm = {
                        activePermissionDialog = null
                        goToSettings(activity)
                    }
                )
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