package com.miguel.statscalculator.presentation.descriptive

import com.miguel.statscalculator.core.math.DescriptiveResult

data class DescriptiveUiState(
    val inputText: String = "",
    val result: DescriptiveResult? = null,
    val errorMessage: String? = null
)