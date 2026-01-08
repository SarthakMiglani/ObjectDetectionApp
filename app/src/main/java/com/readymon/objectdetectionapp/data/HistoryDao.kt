package com.readymon.objectdetectionapp.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(entity: HistoryEntity)

    @Query("SELECT * FROM HistoryEntity ORDER BY timestamp DESC")
    suspend fun getAll(): List<HistoryEntity>
}
