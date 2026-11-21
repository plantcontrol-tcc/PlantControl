package com.plantcontrol.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY name ASC")
    fun getAllPlants(): Flow<List<PlantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlantEntity>)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)
}

@Dao
interface EventLogDao {
    @Query("SELECT * FROM event_history ORDER BY id DESC")
    fun getAllLogs(): Flow<List<EventLogEntity>>

    @Insert
    suspend fun insertLog(log: EventLogEntity)

    @Query("DELETE FROM event_history")
    suspend fun clearLogs()
}