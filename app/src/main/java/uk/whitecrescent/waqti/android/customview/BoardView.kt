package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.whitecrescent.waqti.android.scrollToEnd
import uk.whitecrescent.waqti.android.snackBar
import uk.whitecrescent.waqti.model.collections.TaskList
import java.util.Collections

class BoardView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) :
        RecyclerView(context, attributeSet, defStyle) {

    val boardAdapter: BoardAdapter
        get() = this.adapter as BoardAdapter

    // contains all the LinearLayouts, should this be here or in Adapter?
    val views = ArrayList<TaskListView>()

    init {
        layoutManager = LinearLayoutManager(this.context, HORIZONTAL, false)
        adapter = BoardAdapter() // TODO: 15-Dec-18 Adapter needs to be provided by caller (Activity/Fragment)
        assert(this.adapter != null)
        assert(this.adapter is BoardAdapter)
        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, 0)
            }

            override fun isLongPressDragEnabled() = true

            override fun isItemViewSwipeEnabled() = false

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                // TODO: 15-Dec-18 check what this does?
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                /*This will never be called as we do not support swiping*/
            }

            override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int,
                                 target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

                if (adapter != null && adapter is BoardAdapter) {

                    if (fromPos < toPos) {
                        (fromPos until toPos).forEach {
                            Collections.swap((adapter as BoardAdapter).itemList, it, it + 1)
                        }
                    } else {
                        (fromPos downTo toPos + 1).forEach {
                            Collections.swap((adapter as BoardAdapter).itemList, it, it - 1)
                        }
                    }
                    adapter!!.notifyItemMoved(fromPos, toPos)
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                recyclerView.snackBar("DONE!")
            }

            override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                if (viewHolder.itemView.x <
                        recyclerView.x - viewHolder.itemView.width / 4) {
                }
                if (viewHolder.itemView.x + viewHolder.itemView.width >
                        recyclerView.x + recyclerView.width + viewHolder.itemView.width / 4) {
                }
            }

            override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int, viewSizeOutOfBounds: Int, totalSize: Int, msSinceStartScroll: Long): Int {
                return super.interpolateOutOfBoundsScroll(
                        recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll)
                // TODO: 13-Dec-18 Override this to make better when we drag outside the bounds
            }

        }).attachToRecyclerView(this)

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // TODO: 13-Dec-18 Here we put AutoScrolling horizontally
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

    }

    // We need to make EVERYTHING exist in a single class/ file, the best way to emulate the
    // problem is by trying to add a new Empty List, in Board we may need to have a list of all
    // the lists (DragRecyclerViews) that way we have access to their adapters as well and so we
    // can modify them directly easily and completely

    fun addNewEmptyList() {
        boardAdapter.add(TaskList("NEW BOIIIII"))
        scrollToEnd()
    }

}

class BoardViewHolder(view: View) : RecyclerView.ViewHolder(view)