package com.miguel.statscalculator.presentation.inferential

import com.miguel.statscalculator.core.math.AnovaResult
import com.miguel.statscalculator.core.math.ConfidenceIntervalResult
import com.miguel.statscalculator.core.math.HypothesisTestResult

enum class InferentialTab {
    CONFIDENCE_INTERVAL, HYPOTHESIS_TEST, ANOVA
}

data class InferentialUiState(
    val selectedTab: InferentialTab = InferentialTab.CONFIDENCE_INTERVAL,

    // Inputs IC
    val icDataText: String = "",
    val icConfidenceLevelText: String = "0.95",
    val icResult: ConfidenceIntervalResult? = null,

    // Inputs Teste de Hipóteses
    val testDataText: String = "",
    val hypoMeanText: String = "0.0",
    val alphaText: String = "0.05",
    val testResult: HypothesisTestResult? = null,

    // Inputs ANOVA
    val anovaGroup1Text: String = "",
    val anovaGroup2Text: String = "",
    val anovaGroup3Text: String = "",
    val anovaResult: AnovaResult? = null,

    val errorMessage: String? = null
)