package com.example.campus.data.remote

import retrofit2.http.GET

interface CampusApi {
    @GET("announcements")
    suspend fun getAnnouncements(): List<AnnouncementDto>

    companion object {
        const val BASE_URL = "https://api.campus.example.com/"
    }
}