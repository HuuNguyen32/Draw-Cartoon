package nhn.ntech.ndraw.ext

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt

fun TextView.setTextGradientColor(
    colors: IntArray = intArrayOf(
        "#B7ADF4".toColorInt(),
        "#DFA1F6".toColorInt()
    ),
    positions: FloatArray = floatArrayOf(
        0f,
        1f
    ),
    shaderTile: Shader.TileMode = Shader.TileMode.CLAMP,
) {
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

fun TextView.setTextColor(
    context: Context,
    fullText: String,
    subText: String,
    color: Int,
    typeFace: Int = Typeface.NORMAL,
    flags: Int = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
) {
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

fun TextView.clearTextShader() {
    paint.shader = null
}

private var lastClick: Long = 0L

fun View.tap(delay: Long = 200, onClick: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastClick > delay) {
        this.setOnClickListener { onClick() }
        lastClick = currentTime
    }
}