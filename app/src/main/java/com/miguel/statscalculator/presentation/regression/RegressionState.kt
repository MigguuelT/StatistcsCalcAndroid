package com.miguel.statscalculator.presentation.regression

import com.miguel.statscalculator.core.math.LinearRegressionResult
import com.miguel.statscalculator.core.math.Point2D

data class RegressionUiState(
    val inputTextX: String = "",
    val inputTextY: String = "",
    val points: List<Point2D> = emptyList(),
    val result: LinearRegressionResult? = null,
    val errorMessage: String? = null,
    val predictXInput: String = "",
    val predictedY: Double? = null
)