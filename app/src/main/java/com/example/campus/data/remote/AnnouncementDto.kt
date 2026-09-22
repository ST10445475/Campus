package com.example.campus.data.remote

import com.example.campus.data.local.AnnouncementEntity

data class AnnouncementDto(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long
)

fun AnnouncementDto.toEntity(): AnnouncementEntity {
    return AnnouncementEntity(
        id = id,
        title = title,
        content = content,
        timestamp = timestamp
    )
}