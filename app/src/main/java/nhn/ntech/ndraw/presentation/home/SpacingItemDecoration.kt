package nhn.ntech.ndraw.presentation.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    private val spacing: Int,
    private val spanCount: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (column == 0) {
            outRect.right = spacing
            outRect.left = 0
            outRect.top = 0
            outRect.bottom = spacing * 2
        } else {
            outRect.right = 0
            outRect.left = spacing
            outRect.top = 0
            outRect.bottom = spacing * 2
        }
    }
}