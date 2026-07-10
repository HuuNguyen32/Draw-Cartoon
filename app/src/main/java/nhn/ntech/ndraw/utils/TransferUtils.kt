package nhn.ntech.ndraw.utils

import android.content.Context

class TransferUtils {
    companion object {
        fun dpToPx(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density).toInt()
        }
    }
}