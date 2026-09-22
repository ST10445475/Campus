package com.example.campus.data

import com.example.campus.data.local.AnnouncementDao
import com.example.campus.data.local.AnnouncementEntity
import com.example.campus.data.remote.CampusApi
import com.example.campus.data.remote.toEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AnnouncementRepository(private val announcementDao: AnnouncementDao) {

    private val api = Retrofit.Builder()
        .baseUrl(CampusApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CampusApi::class.java)

    val announcements: Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncements()

    suspend fun refreshAnnouncements() {
        try {
            val remoteData = api.getAnnouncements()
            val entities = remoteData.map { it.toEntity() }
            
            // Simple sync strategy: replace all local data with remote data
            announcementDao.deleteAll()
            announcementDao.insertAll(entities)
        } catch (e: Exception) {
            // Log error or handle failure (e.g., no internet)
            e.printStackTrace()
        }
    }
}