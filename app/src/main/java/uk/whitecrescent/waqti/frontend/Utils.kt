@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class SimpleTextWatcher : TextWatcher {

    override fun afterTextChanged(editable: Editable?) {}

    override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {}
}

inline fun TextView.addAfterTextChangedListener(crossinline func: (Editable?) -> Unit) {
    this.addTextChangedListener(object : SimpleTextWatcher() {
        override fun afterTextChanged(editable: Editable?) {
            func(editable)
        }
    })
}

open class SimpleItemTouchHelperCallback : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
    }

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        /*This will never be called as we do not support swiping*/
    }

    override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int,
                                              viewSizeOutOfBounds: Int, totalSize: Int,
                                              msSinceStartScroll: Long): Int {
        return super.interpolateOutOfBoundsScroll(
                recyclerView, viewSize, viewSizeOutOfBounds, totalSize, 1500)
    }

}

enum class Orientation {
    HORIZONTAL, VERTICAL
}

class FABOnScrollListener(
        val fab: FloatingActionButton,
        val orientation: Orientation) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        when (orientation) {
            Orientation.HORIZONTAL -> {
                if (dx > 0 && fab.isVisible) fab.hide()
                else if (dx < 0 && !fab.isVisible) fab.show()
            }
            Orientation.VERTICAL -> {
                if (dy > 0 && fab.isVisible) fab.hide()
                else if (dy < 0 && !fab.isVisible) fab.show()
            }
        }

    }
}

/**
 * Just a utility to show us where there are Fragment changes happening
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.EXPRESSION)
annotation class GoToFragment