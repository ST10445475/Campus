package com.example.campus.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.campus.data.AnnouncementRepository
import com.example.campus.data.local.AppDatabase

class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AnnouncementRepository(database.announcementDao())

        return try {
            repository.refreshAnnouncements()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}