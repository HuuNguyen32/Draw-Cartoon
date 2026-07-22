package nhn.ntech.ndraw.utils

import android.content.Context

class TransferUtils {
    companion object {
        fun dpToPx(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density).toInt()
        }

        fun formatDuration(duration: Long): String {
            val minutes = (duration / 1000 / 60).toInt()
            val seconds = ((duration / 1000) % 60).toInt()
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}