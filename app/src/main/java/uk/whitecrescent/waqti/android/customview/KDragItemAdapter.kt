package uk.whitecrescent.waqti.android.customview

import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import java.util.Collections

abstract class KDragItemAdapter<T, VH : KDragItemAdapter.ViewHolder> : RecyclerView.Adapter<VH>() {

    var dragStartCallback: DragStartCallback? = null
    var dragItemId = RecyclerView.NO_ID
    var dropTargetId = RecyclerView.NO_ID
    var itemList: MutableList<T>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        this.setHasStableIds(true)
    }

    fun getPosition(item: T): Int {
        (0..itemCount).forEach {
            if (itemList!![it] == item) return it
        }
        return RecyclerView.NO_POSITION
    }

    fun removeItem(position: Int): T? {
        if (itemList != null && itemList!!.size > position && position >= 0) {
            val item = itemList!!.removeAt(position)
            notifyItemRemoved(position)
            return item
        }
        return null
    }

    fun addItem(pos: Int, item: T) {
        if (itemList != null && itemList!!.size >= pos) {
            itemList!!.add(pos, item)
            notifyItemInserted(pos)
        }
    }

    fun changeItemPosition(fromPos: Int, toPos: Int) {
        if (itemList != null && itemList!!.size > fromPos && itemList!!.size > toPos) {
            val item = itemList!!.removeAt(fromPos)
            itemList!!.add(toPos, item)
            notifyItemMoved(fromPos, toPos)
        }
    }

    fun swapItems(pos1: Int, pos2: Int) {
        if (itemList != null && itemList!!.size > pos1 && itemList!!.size > pos2) {
            Collections.swap(itemList!!, pos1, pos2)
            notifyDataSetChanged()
        }
    }

    fun getPositionForItemId(id: Long): Int {
        val count = itemCount
        for (i in 0 until count) {
            if (id == getItemId(i)) {
                return i
            }
        }
        return RecyclerView.NO_POSITION
    }

    override fun getItemCount(): Int {
        return if (itemList == null) 0 else itemList!!.size
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        val itemId = getItemId(position)
        holder.itemId0 = itemId
        holder.itemView.visibility = if (dragItemId == itemId) View.INVISIBLE else View.VISIBLE
        holder.dragStartCallback = dragStartCallback
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.dragStartCallback = null
    }

    override fun getItemId(position: Int): Long {
        return getUniqueItemId(position)
    }

    abstract fun getUniqueItemId(position: Int): Long

    interface DragStartCallback {

        val isDragging: Boolean
        fun startDrag(itemView: View, itemId: Long): Boolean
    }

    abstract class ViewHolder(itemView: View, handleResID: Int, dragOnLongPress: Boolean)
        : RecyclerView.ViewHolder(itemView) {
        var grabView: View = itemView.findViewById(handleResID)
        var itemId0: Long = 0L
        var dragStartCallback: DragStartCallback? = null

        init {
            if (dragOnLongPress) {
                grabView.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        if (dragStartCallback == null) return false
                        if (dragStartCallback!!.startDrag(itemView, itemId0)) return true
                        if (itemView == grabView) return onItemLongClicked(v!!)
                        return false
                    }
                })
            } else {
                grabView.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        if (dragStartCallback == null) {
                            return false
                        }

                        if (event!!.getAction() == MotionEvent.ACTION_DOWN &&
                                dragStartCallback!!.startDrag(itemView, itemId0)) {
                            return true
                        }

                        return if (!dragStartCallback!!.isDragging && itemView === grabView) {
                            onItemTouch(v!!, event!!)
                        } else false
                    }
                })
            }

            itemView.setOnClickListener { onItemClicked(it) }

            if (itemView != grabView) {
                itemView.apply {
                    setOnLongClickListener { onItemLongClicked(it) }
                    setOnTouchListener { v, event -> onItemTouch(v, event) }
                }
            }
        }

        fun onItemClicked(view: View) {}

        fun onItemTouch(view: View, event: MotionEvent): Boolean {
            return false
        }

        fun onItemLongClicked(view: View): Boolean {
            return false
        }
    }

}