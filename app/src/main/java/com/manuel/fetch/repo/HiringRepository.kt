package com.manuel.fetch.repo

import com.manuel.fetch.model.Hiring
import com.manuel.fetch.model.HiringGroup
import com.manuel.fetch.service.HiringsService
import javax.inject.Inject

class HiringRepository @Inject constructor(private val hiringsService: HiringsService) {

    suspend fun fetchHirings(): List<HiringGroup> {
        val hiringsDtoList = hiringsService.fetchHirings().filter { !it.name.isNullOrEmpty() }
        val dtosGrouped = hiringsDtoList.groupBy { it.groupId }.toSortedMap()

        return dtosGrouped.map { (groupId, hiringDtos) ->
            HiringGroup(
                id = groupId,
                hirings = hiringDtos.map { dto ->
                    Hiring(
                        id = dto.id,
                        name = dto.name ?: ""
                    )
                }.sortedBy { it.name }
            )
        }
    }
}