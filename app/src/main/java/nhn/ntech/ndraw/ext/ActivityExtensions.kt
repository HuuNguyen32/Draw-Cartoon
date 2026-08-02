package nhn.ntech.ndraw.ext

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewManagerFactory
import kotlin.system.exitProcess

import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager

// Share app
fun Activity.shareApp() {
    ShareCompat.IntentBuilder.from(this).setType("text/plain").setChooserTitle("Chooser title")
        .setText("http://play.google.com/store/apps/details?id=" + (this).packageName)
        .startChooser()
}

// Hide navigation bar & set status bar icon color (isBlack = true -> dark icons, false -> light icons)
fun Activity.hideSystemUI(isBlack: Boolean = false) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (isBlack) {
                controller.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        if (isBlack) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
                )

        window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}

// Privacy & policy
fun Activity.policy() {
    val url = "https://sites.google.com/view/diy-emoji-merge-emoji-maker/home"
    val i = Intent(Intent.ACTION_VIEW)
    i.data = url.toUri()
    startActivity(i)
}

// Rate
fun reviewApp(context: Activity, isBackPress: Boolean) {
    val manager = ReviewManagerFactory.create(context)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val flow = (context as Activity?)?.let { manager.launchReviewFlow(it, reviewInfo) }
            flow?.addOnCompleteListener { task2: Task<Void> ->
                if (isBackPress) {
                    exitProcess(0)
                }
            }
        } else {
            if (isBackPress) {
                exitProcess(0)
            }
        }
    }
}