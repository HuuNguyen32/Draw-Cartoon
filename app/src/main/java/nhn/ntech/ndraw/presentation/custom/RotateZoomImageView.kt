package nhn.ntech.ndraw.presentation.custom

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Custom ImageView cho phép người dùng:
 *  - Kéo (drag) bằng 1 ngón tay
 *  - Xoay + phóng to/thu nhỏ (rotate + zoom) bằng 2 ngón tay
 *
 * Dùng translationX/translationY (thuộc tính chuẩn của View) thay vì
 * chỉnh trực tiếp LayoutParams, nên KHÔNG phụ thuộc vào loại ViewGroup cha
 * (hoạt động đúng trong RelativeLayout, ConstraintLayout, FrameLayout, v.v.)
 */
class RotateZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), View.OnTouchListener {

    private enum class Mode { NONE, DRAG, ZOOM }

    companion object {
        private const val MIN_SCALE = 0.3f
        private const val MAX_SCALE = 5.0f
        private const val MIN_TOUCH_DISTANCE = 10f
    }

    private var mode = Mode.NONE

    // Dùng cho DRAG: lưu offset giữa điểm chạm và translationX/Y hiện tại của view
    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    // Dùng cho ZOOM / ROTATE
    private var startDistance = 1f
    private var startAngle = 0f
    private var startScale = 1f

    /**
     * Khi true: vô hiệu hóa kéo/xoay/zoom qua chạm tay.
     * Có thể vẫn gọi flipHorizontal() bằng code dù đang khóa, vì đó là
     * hành động chủ động từ nút bấm, không phải từ cử chỉ chạm.
     */
    var isLocked: Boolean = false

    init {
        setOnTouchListener(this)
    }

    /** Lật ảnh theo chiều ngang, giữ nguyên vị trí, kích thước và góc xoay hiện tại */
    fun flipHorizontal() {
        scaleX = -scaleX
    }

    /** Lật ảnh theo chiều dọc */
    fun flipVertical() {
        scaleY = -scaleY
    }

    /** Khoảng cách giữa 2 ngón tay */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    /** Góc tạo bởi 2 ngón tay (độ) */
    private fun rotation(event: MotionEvent): Float {
        val deltaX = (event.getX(0) - event.getX(1)).toDouble()
        val deltaY = (event.getY(0) - event.getY(1)).toDouble()
        return Math.toDegrees(atan2(deltaY, deltaX)).toFloat()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val view = v as ImageView

        // Đang khóa: không cho kéo/xoay/zoom, nhưng vẫn "nuốt" sự kiện
        // để tránh việc chạm xuyên xuống view/layout phía dưới.
        if (isLocked) {
            return true
        }

        // Bật anti-alias an toàn: chỉ cast nếu đúng loại Drawable
        (view.drawable as? BitmapDrawable)?.setAntiAlias(true)

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                dragOffsetX = event.rawX - view.translationX
                dragOffsetY = event.rawY - view.translationY
                mode = Mode.DRAG
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    startDistance = spacing(event)
                    startAngle = rotation(event) - view.rotation
                    startScale = view.scaleX
                    if (startDistance > MIN_TOUCH_DISTANCE) {
                        mode = Mode.ZOOM
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                when (mode) {
                    Mode.DRAG -> {
                        view.translationX = event.rawX - dragOffsetX
                        view.translationY = event.rawY - dragOffsetY
                    }

                    Mode.ZOOM -> {
                        if (event.pointerCount == 2) {
                            // --- Xoay ---
                            val newRotation = rotation(event) - startAngle
                            view.rotation = newRotation

                            // --- Zoom ---
                            val newDistance = spacing(event)
                            if (newDistance > MIN_TOUCH_DISTANCE) {
                                var scale = (newDistance / startDistance) * startScale
                                scale = max(MIN_SCALE, min(MAX_SCALE, scale))
                                view.scaleX = scale
                                view.scaleY = scale
                            }
                            // setScaleX/Y và setRotation tự xoay/scale quanh pivot
                            // (mặc định là tâm view), không cần tính lại vị trí thủ công.
                        }
                    }

                    Mode.NONE -> Unit
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                // Khi nhả bớt 1 ngón: nếu chỉ còn đúng 1 ngón, chuyển về DRAG
                // và tính lại offset dựa trên translationX/Y hiện tại để không bị giật hình.
                if (event.pointerCount - 1 == 1) {
                    dragOffsetX = event.rawX - view.translationX
                    dragOffsetY = event.rawY - view.translationY
                    mode = Mode.DRAG
                } else {
                    mode = Mode.NONE
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mode = Mode.NONE
            }
        }

        return true
    }
}