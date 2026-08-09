package com.miguel.statscalculator.domain.model

data class CalculationHistory(
    val id: Long = 0,
    val moduleName: String,
    val summary: String,
    val detailText: String,
    val timestamp: Long = System.currentTimeMillis()
)