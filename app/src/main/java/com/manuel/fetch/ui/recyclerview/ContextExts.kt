package com.manuel.fetch.ui.recyclerview

import android.content.Context
import androidx.core.content.ContextCompat
import com.manuel.fetch.R

fun Context?.createDrawableByExpandedStatus(expanded: Boolean) = this?.let { context ->
    ContextCompat.getDrawable(
        context,
        if (expanded) {
            R.drawable.minus_fetch_28
        } else {
            R.drawable.plus_fetch_28
        }
    )
}