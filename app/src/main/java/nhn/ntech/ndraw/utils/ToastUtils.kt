package nhn.ntech.ndraw.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {
    private var toast: Toast? = null
    private var lastClickTime: Long = 0

    fun showToast(
        delayTime: Long = 200,
        context: Context,
        message: String,
        isLong: Boolean = false,
    ) {
        val nowClickTime = System.currentTimeMillis()
        if (nowClickTime - lastClickTime >= delayTime) {
            toast?.cancel()
            toast = Toast.makeText(
                context,
                message,
                if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            )
            toast?.show()
            lastClickTime = nowClickTime
        }
    }

    fun cancelAllToast() {
        toast?.cancel()
        toast = null
    }
}