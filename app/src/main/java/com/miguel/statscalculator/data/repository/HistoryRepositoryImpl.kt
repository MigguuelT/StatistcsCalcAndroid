package com.miguel.statscalculator.data.repository

import com.miguel.statscalculator.data.local.CalculationDao
import com.miguel.statscalculator.data.local.CalculationEntity
import com.miguel.statscalculator.domain.model.CalculationHistory
import com.miguel.statscalculator.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(
    private val dao: CalculationDao
) : HistoryRepository {

    override fun getAllHistory(): Flow<List<CalculationHistory>> {
        return dao.getAllHistory().map { list ->
            list.map { entity ->
                CalculationHistory(
                    id = entity.id,
                    moduleName = entity.moduleName,
                    summary = entity.summary,
                    detailText = entity.detailText,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun insertHistory(item: CalculationHistory) {
        dao.insert(
            CalculationEntity(
                moduleName = item.moduleName,
                summary = item.summary,
                detailText = item.detailText,
                timestamp = item.timestamp
            )
        )
    }

    override suspend fun clearHistory() {
        dao.clearAll()
    }
}