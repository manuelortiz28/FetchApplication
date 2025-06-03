package com.manuel.fetch.ui.recyclerview

import com.manuel.fetch.model.HiringGroup

sealed class ListItem {
    data class HeaderItem(val group: HiringGroup) : ListItem()
    data class ChildItem(val name: String) : ListItem()
}
