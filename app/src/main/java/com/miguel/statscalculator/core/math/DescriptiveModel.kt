package com.miguel.statscalculator.core.math

enum class ModeType {
    AMODAL, UNIMODAL, BIMODAL, MULTIMODAL
}

data class DescriptiveResult(
    val n: Int,
    val mean: Double,
    val median: Double,
    val modes: List<Double>,
    val modeType: ModeType,
    val min: Double,
    val max: Double,
    val range: Double,
    val sampleVariance: Double,
    val populationVariance: Double,
    val sampleStdDev: Double,
    val populationStdDev: Double,
    val meanAbsoluteDeviation: Double,
    val coefficientOfVariation: Double, // Em porcentagem (%)
    val q1: Double,
    val q2: Double, // Equivalente à Mediana
    val q3: Double,
    val iqr: Double,
    val skewness: Double, // Assimetria
    val kurtosis: Double  // Curtose
)