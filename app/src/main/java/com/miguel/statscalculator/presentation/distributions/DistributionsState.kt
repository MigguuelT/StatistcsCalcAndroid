package com.miguel.statscalculator.presentation.distributions

import com.miguel.statscalculator.core.math.BinomialResult
import com.miguel.statscalculator.core.math.NormalResult
import com.miguel.statscalculator.core.math.PoissonResult

enum class DistributionType {
    NORMAL, POISSON, BINOMIAL
}

data class DistributionsUiState(
    val selectedTab: DistributionType = DistributionType.NORMAL,

    // Inputs Normal
    val normalX: String = "",
    val normalMean: String = "",
    val normalStdDev: String = "",
    val normalResult: NormalResult? = null,

    // Inputs Poisson
    val poissonLambda: String = "",
    val poissonK: String = "",
    val poissonResult: PoissonResult? = null,

    // Inputs Binomial
    val binomialN: String = "",
    val binomialK: String = "",
    val binomialP: String = "",
    val binomialResult: BinomialResult? = null,

    val errorMessage: String? = null
)