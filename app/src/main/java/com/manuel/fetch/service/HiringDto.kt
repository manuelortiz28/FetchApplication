package com.manuel.fetch.service

import com.google.gson.annotations.SerializedName

class HiringDto(
    @SerializedName("id") val id: Int,
    @SerializedName("listId") val groupId: Int,
    @SerializedName("name") val name: String?
)
