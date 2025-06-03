package com.manuel.fetch.ui.matcher

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * A matcher that checks if a RecyclerView has an item at a specific position that matches the provided itemMatcher.
 *
 * @param position The position of the item in the RecyclerView.
 * @param itemMatcher The matcher to apply to the item at the specified position.
 * @return A Matcher that can be used with Espresso to assert the presence of an item at the specified position.
 */
fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("Has item at position: $position")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: View): Boolean {
            if (view !is RecyclerView) return false

            val viewHolder = view.findViewHolderForAdapterPosition(position) ?: return false
            return itemMatcher.matches(viewHolder.itemView)
        }
    }
}
