package com.manuel.fetch.ui.recyclerview

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.manuel.fetch.R
import com.manuel.fetch.model.HiringGroup

class HeaderViewHolder(
    private val textView: TextView,
    private val toggleExpanded: () -> Unit
) : RecyclerView.ViewHolder(textView) {

    fun bind(group: HiringGroup) {
        textView.text = textView.context.getString(R.string.header_item, group.id.toString())
        textView.setCompoundDrawablesWithIntrinsicBounds(
            textView.context.createDrawableByExpandedStatus(group.expanded),
            null,
            null,
            null
        )
        itemView.setOnClickListener {
            group.expanded = !group.expanded
            toggleExpanded()
        }
    }
}