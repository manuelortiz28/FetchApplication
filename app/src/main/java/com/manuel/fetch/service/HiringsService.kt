package com.manuel.fetch.service

import retrofit2.http.GET

interface HiringsService {
    @GET("/hiring.json")
    suspend fun fetchHirings(): List<HiringDto>
}