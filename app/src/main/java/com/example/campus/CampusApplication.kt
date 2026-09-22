package com.example.campus

import android.app.Application
import com.example.campus.sync.SyncManager

class CampusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start background sync
        SyncManager.startPeriodicSync(this)
        // Optionally trigger one immediate sync on startup
        SyncManager.triggerOneTimeSync(this)
    }
}