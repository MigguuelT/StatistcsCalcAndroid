package com.miguel.statscalculator.presentation.probability

import com.miguel.statscalculator.core.math.BayesResult

data class ProbabilityUiState(
    // Inputs de Combinatória
    val inputN: String = "",
    val inputK: String = "",
    val factorialResult: String? = null,
    val permutationResult: String? = null,
    val arrangementResult: String? = null,
    val combinationResult: String? = null,
    val combinatoricsError: String? = null,

    // Inputs de Bayes
    val inputPriorA: String = "",
    val inputLikelihoodBGivenA: String = "",
    val inputLikelihoodBGivenNotA: String = "",
    val bayesResult: BayesResult? = null,
    val bayesError: String? = null
)