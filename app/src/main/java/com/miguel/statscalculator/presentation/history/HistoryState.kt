package com.miguel.statscalculator.presentation.history

import com.miguel.statscalculator.domain.model.CalculationHistory

data class HistoryUiState(
    val items: List<CalculationHistory> = emptyList(),
    val isLoading: Boolean = false
)