package com.miguel.statscalculator.core.math

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object DescriptiveEngine {

    fun calculate(data: List<Double>): DescriptiveResult {
        require(data.isNotEmpty()) { "A lista de dados não pode estar vazia." }

        val sorted = data.sorted()
        val n = sorted.size
        val mean = sorted.sum() / n
        val min = sorted.first()
        val max = sorted.last()
        val range = max - min

        // 1. Moda e Classificação
        val freqMap = sorted.groupingBy { it }.eachCount()
        val maxFreq = freqMap.values.maxOrNull() ?: 0

        val isAmodal = maxFreq <= 1 || freqMap.values.all { it == maxFreq }
        val modes = if (isAmodal) emptyList() else freqMap.filter { it.value == maxFreq }.keys.sorted()

        val modeType = when {
            modes.isEmpty() -> ModeType.AMODAL
            modes.size == 1 -> ModeType.UNIMODAL
            modes.size == 2 -> ModeType.BIMODAL
            else -> ModeType.MULTIMODAL
        }

        // 2. Mediana
        val median = calculatePercentile(sorted, 0.50)

        // 3. Variâncias e Desvios Padrão
        val sumSqDiff = sorted.sumOf { (it - mean).pow(2) }
        val populationVariance = sumSqDiff / n
        val sampleVariance = if (n > 1) sumSqDiff / (n - 1) else 0.0
        val populationStdDev = sqrt(populationVariance)
        val sampleStdDev = sqrt(sampleVariance)

        // 4. Desvio Médio Absoluto (DMA) e Coeficiente de Variação (CV)
        val meanAbsoluteDeviation = sorted.sumOf { abs(it - mean) } / n
        val coefficientOfVariation = if (mean != 0.0) (sampleStdDev / abs(mean)) * 100.0 else 0.0

        // 5. Quartis e Amplitudes
        val q1 = calculatePercentile(sorted, 0.25)
        val q2 = median
        val q3 = calculatePercentile(sorted, 0.75)
        val iqr = q3 - q1

        // 6. Assimetria (Skewness de Fisher-Pearson) e Curtose
        val m2 = populationVariance
        val m3 = sorted.sumOf { (it - mean).pow(3) } / n
        val m4 = sorted.sumOf { (it - mean).pow(4) } / n

        val skewness = if (m2 > 0) m3 / m2.pow(1.5) else 0.0
        val kurtosis = if (m2 > 0) (m4 / m2.pow(2.0)) - 3.0 else 0.0

        return DescriptiveResult(
            n = n,
            mean = mean,
            median = median,
            modes = modes,
            modeType = modeType,
            min = min,
            max = max,
            range = range,
            sampleVariance = sampleVariance,
            populationVariance = populationVariance,
            sampleStdDev = sampleStdDev,
            populationStdDev = populationStdDev,
            meanAbsoluteDeviation = meanAbsoluteDeviation,
            coefficientOfVariation = coefficientOfVariation,
            q1 = q1,
            q2 = q2,
            q3 = q3,
            iqr = iqr,
            skewness = skewness,
            kurtosis = kurtosis
        )
    }

    private fun calculatePercentile(sortedData: List<Double>, p: Double): Double {
        val n = sortedData.size
        if (n == 1) return sortedData[0]
        val pos = p * (n - 1)
        val index = pos.toInt()
        val fraction = pos - index
        if (index >= n - 1) return sortedData.last()
        return sortedData[index] + fraction * (sortedData[index + 1] - sortedData[index])
    }
}