package com.mandadhi.voicecalc.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Insert
    suspend fun insert(record: CalculationRecord): Long

    @Query("SELECT * FROM calculations ORDER BY timestamp DESC")
    fun all(): Flow<List<CalculationRecord>>

    @Query("DELETE FROM calculations")
    suspend fun clearAll()
}
