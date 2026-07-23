package nhn.ntech.ndraw.ext

import android.app.Activity
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewManagerFactory
import kotlin.system.exitProcess

// Share app
fun Activity.shareApp() {
    ShareCompat.IntentBuilder.from(this).setType("text/plain").setChooserTitle("Chooser title")
        .setText("http://play.google.com/store/apps/details?id=" + (this).packageName)
        .startChooser()
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