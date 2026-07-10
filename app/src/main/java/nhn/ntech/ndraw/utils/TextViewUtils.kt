package nhn.ntech.ndraw.utils

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.ContextCompat

fun TextView.setTextGradientColor(colors: IntArray, positions: FloatArray, shaderTile: Shader.TileMode = Shader.TileMode.CLAMP) {
    this.post {
        val width = this.width.toFloat()
        if (width > 0) {
            val shader = LinearGradient(
                0f, 0f, width, 0f,
                colors,
                positions,
                shaderTile
            )

            this.paint.shader = shader
            this.invalidate()
        }
    }
}

fun TextView.setTextColor(context: Context, fullText: String, subText: String, color: Int, typeFace: Int = Typeface.NORMAL, flags: Int = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) {
    val spannable = SpannableString(fullText)
    val startStr = spannable.indexOf(subText)
    val endStr = startStr + subText.length

    spannable.setSpan(
        ForegroundColorSpan(
            ContextCompat.getColor(context, color)
        ),
        startStr,
        endStr,
        flags
    )

    spannable.setSpan(
        StyleSpan(typeFace),
        startStr,
        endStr,
        flags
    )
    this.text = spannable
}