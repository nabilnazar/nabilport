package com.nabilnazar.offlinefirstapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_table")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val input1: Double,
    val input2: Double,
    val result: Double,
    val isSynced: Boolean
)