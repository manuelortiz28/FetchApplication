package com.manuel.fetch.ui.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.manuel.fetch.R
import com.manuel.fetch.model.HiringGroup

private const val TYPE_HEADER = 0
private const val TYPE_CHILD = 1

class ExpandableAdapter(
    private val groups: List<HiringGroup>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ListItem>()

    init {
        updateItems()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateItems() {
        items.clear()
        groups.forEach { group ->
            items.add(ListItem.HeaderItem(group))
            if (group.expanded) {
                items.addAll(group.hirings.map { ListItem.ChildItem(it.name) })
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.HeaderItem -> TYPE_HEADER
            is ListItem.ChildItem -> TYPE_CHILD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.header_layout, parent, false) as TextView
                HeaderViewHolder(view) { updateItems() }
            }
            TYPE_CHILD -> {
                val view = inflater.inflate(R.layout.child_layout, parent, false) as TextView
                ChildViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item.group)
            is ListItem.ChildItem -> (holder as ChildViewHolder).bind(item.name)
        }
    }

    override fun getItemCount() = items.size
}
