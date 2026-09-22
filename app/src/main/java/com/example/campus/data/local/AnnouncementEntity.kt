package com.example.campus.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long
)