package com.manuel.fetch.model

enum class SortOrder {
    ASC,
    DESC
}

fun SortOrder?.toggle(): SortOrder {
    return when (this) {
        SortOrder.ASC -> SortOrder.DESC
        SortOrder.DESC -> SortOrder.ASC
        null -> SortOrder.ASC // Default to ASC if null
    }
}
