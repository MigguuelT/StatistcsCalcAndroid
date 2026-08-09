package com.miguel.statscalculator.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moduleName: String,
    val summary: String,
    val detailText: String,
    val timestamp: Long
)