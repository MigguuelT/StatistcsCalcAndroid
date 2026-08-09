package com.miguel.statscalculator.domain.repository

import com.miguel.statscalculator.domain.model.CalculationHistory
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<CalculationHistory>>
    suspend fun insertHistory(item: CalculationHistory)
    suspend fun clearHistory()
}