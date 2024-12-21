package com.nabilnazar.offlinefirstapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface CalculationDao {

    // Retrieve all calculations ordered by the latest first
    @Query("SELECT * FROM calculation_table ORDER BY id DESC")
    fun getAllCalculations(): Flow<List<CalculationEntity>>

    // Insert or update a calculation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calculation: CalculationEntity)

    // Mark a calculation as synced
    @Query("UPDATE calculation_table SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)
}