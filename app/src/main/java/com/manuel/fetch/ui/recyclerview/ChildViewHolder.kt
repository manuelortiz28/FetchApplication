package com.manuel.fetch.ui.recyclerview

import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ChildViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
    fun bind(name: String) {
        textView.text = name

        itemView.setOnClickListener {
            Toast.makeText(textView.context, "Clicked hiring $name", Toast.LENGTH_SHORT).show()
        }
    }
}
