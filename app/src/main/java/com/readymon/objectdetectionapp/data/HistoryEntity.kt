package com.readymon.objectdetectionapp.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imagePath: String,
    val detectedObjects: String,
    val timestamp: Long
)
