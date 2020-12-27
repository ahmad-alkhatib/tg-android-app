package app.uicomponents

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GeneralSpaceItemDecoration(
    private val leftMargin: Int,
    private val rightMargin: Int,
    private val marginBetweenItems: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position: Int = parent.getChildAdapterPosition(view)

        val isLast: Boolean = position == state.itemCount - 1

        if (position == 0) {
            outRect.left = leftMargin
            outRect.right = marginBetweenItems
        } else if (isLast) {
            outRect.left = marginBetweenItems
            outRect.right = rightMargin
        } else {
            outRect.left = marginBetweenItems
            outRect.right = marginBetweenItems
        }
    }
}