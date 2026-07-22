package nhn.ntech.ndraw.presentation.custom

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Custom ImageView cho phép người dùng:
 *  - Kéo (drag) bằng 1 ngón tay
 *  - Xoay + phóng to/thu nhỏ (rotate + zoom) bằng 2 ngón tay
 *
 * Tất cả phép tính khoảng cách và góc đều được thực hiện trong hệ tọa độ
 * của parent (qua view.matrix) để tránh vòng lặp phản hồi gây rung lắc.
 *
 * Dùng translationX/translationY (thuộc tính chuẩn của View) thay vì
 * chỉnh trực tiếp LayoutParams, nên KHÔNG phụ thuộc vào loại ViewGroup cha
 * (hoạt động đúng trong RelativeLayout, ConstraintLayout, FrameLayout, v.v.)
 */
class RotateZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
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

    // Dùng cho ZOOM / ROTATE — khoảng cách và góc tính trong hệ tọa độ parent
    private var startDistance = 1f
    private var startAngle = 0f
    private var startScale = 1f
    private var startRotation = 0f

    // Lưu trạng thái flip riêng biệt, tránh ảnh hưởng đến zoom
    private var flipSignX = 1f
    private var flipSignY = 1f

    // Buffer dùng lại cho việc chuyển đổi tọa độ, tránh cấp phát bộ nhớ mỗi frame
    private val parentPts = FloatArray(4)

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
        flipSignX = -flipSignX
        scaleX = -scaleX
    }

    /** Lật ảnh theo chiều dọc */
    fun flipVertical() {
        flipSignY = -flipSignY
        scaleY = -scaleY
    }

    /**
     * Chuyển tọa độ 2 ngón tay từ hệ tọa độ local (view) sang hệ tọa độ parent.
     * Dùng view.matrix (bao gồm scale, rotation, pivot) để chuyển đổi.
     * Kết quả lưu trong [parentPts]: [x0, y0, x1, y1]
     */
    private fun mapToParent(event: MotionEvent, viewMatrix: Matrix) {
        parentPts[0] = event.getX(0)
        parentPts[1] = event.getY(0)
        parentPts[2] = event.getX(1)
        parentPts[3] = event.getY(1)
        viewMatrix.mapPoints(parentPts)
    }

    /** Khoảng cách giữa 2 ngón tay trong hệ tọa độ parent */
    private fun spacingInParent(event: MotionEvent, viewMatrix: Matrix): Float {
        if (event.pointerCount < 2) return 0f
        mapToParent(event, viewMatrix)
        val dx = parentPts[0] - parentPts[2]
        val dy = parentPts[1] - parentPts[3]
        return sqrt(dx * dx + dy * dy)
    }

    /** Góc tạo bởi 2 ngón tay trong hệ tọa độ parent (độ) */
    private fun rotationInParent(event: MotionEvent, viewMatrix: Matrix): Float {
        if (event.pointerCount < 2) return 0f
        mapToParent(event, viewMatrix)
        val dx = (parentPts[0] - parentPts[2]).toDouble()
        val dy = (parentPts[1] - parentPts[3]).toDouble()
        return Math.toDegrees(atan2(dy, dx)).toFloat()
    }

    /**
     * Tìm chỉ số pointer (pointerIndex) của ngón tay CÒN LẠI sau khi
     * 1 ngón đã nhấc lên.
     */
    private fun findRemainingPointerIndex(event: MotionEvent): Int {
        val actionIndex = event.actionIndex
        return if (actionIndex == 0) 1 else 0
    }

    /**
     * Tính tọa độ raw (screen) của một pointer từ tọa độ local.
     * Dùng view.matrix + vị trí view trên màn hình.
     */
    private fun getRawCoords(
        event: MotionEvent,
        pointerIndex: Int,
        view: View,
    ): FloatArray {
        val pts = floatArrayOf(event.getX(pointerIndex), event.getY(pointerIndex))
        // Chuyển từ local → parent bằng matrix
        view.matrix.mapPoints(pts)
        // Cộng thêm vị trí top-left của view (trước transform) trong parent
        pts[0] += view.left
        pts[1] += view.top

        // Chuyển tiếp lên screen coordinate thông qua parent
        val parent = view.parent
        if (parent is View) {
            val loc = IntArray(2)
            parent.getLocationOnScreen(loc)
            pts[0] += loc[0]
            pts[1] += loc[1]
        }
        return pts
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

        // Lấy matrix TRƯỚC khi thay đổi bất kỳ thuộc tính nào của view.
        // Quan trọng: event.getX(i) được Android tính dựa trên matrix tại thời điểm
        // dispatch, nên phải dùng cùng matrix đó để chuyển đổi ngược lại.
        val currentMatrix = Matrix(view.matrix)

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                dragOffsetX = event.rawX - view.translationX
                dragOffsetY = event.rawY - view.translationY
                mode = Mode.DRAG
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    // Tính distance và angle trong hệ tọa độ parent — ổn định, không bị
                    // ảnh hưởng bởi thay đổi scale/rotation của view
                    startDistance = spacingInParent(event, currentMatrix)
                    startAngle = rotationInParent(event, currentMatrix)
                    startScale = abs(view.scaleX)
                    startRotation = view.rotation
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
                            // Tính distance và angle trong hệ tọa độ parent
                            val newDistance = spacingInParent(event, currentMatrix)
                            val newAngle = rotationInParent(event, currentMatrix)

                            // --- Xoay ---
                            val angleDelta = newAngle - startAngle
                            view.rotation = startRotation + angleDelta

                            // --- Zoom ---
                            if (newDistance > MIN_TOUCH_DISTANCE && startDistance > MIN_TOUCH_DISTANCE) {
                                val ratio = newDistance / startDistance
                                var scale = ratio * startScale
                                scale = max(MIN_SCALE, min(MAX_SCALE, scale))
                                // Áp dụng scale với dấu flip giữ nguyên
                                view.scaleX = scale * flipSignX
                                view.scaleY = scale * flipSignY
                            }
                        }
                    }

                    Mode.NONE -> Unit
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                // Khi nhả bớt 1 ngón: nếu chỉ còn đúng 1 ngón, chuyển về DRAG.
                // Phải lấy tọa độ raw của ngón tay CÒN LẠI để tính drag offset chính xác.
                if (event.pointerCount - 1 == 1) {
                    val remainIdx = findRemainingPointerIndex(event)
                    val rawCoords = getRawCoords(event, remainIdx, view)

                    dragOffsetX = rawCoords[0] - view.translationX
                    dragOffsetY = rawCoords[1] - view.translationY
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