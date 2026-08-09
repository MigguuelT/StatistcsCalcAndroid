package com.miguel.statscalculator.util

import kotlin.math.*

// ==========================================
// ESTRUTURAS DE DADOS DA CAMADA ESTATÍSTICA
// ==========================================

enum class AlternativeHypothesis {
    TWO_SIDED, // H_a: μ ≠ μ_0  ou  p ≠ p_0
    LESS,      // H_a: μ < μ_0  ou  p < p_0
    GREATER    // H_a: μ > μ_0  ou  p > p_0
}

data class ConfidenceIntervalResult(
    val lowerLimit: Double,
    val upperLimit: Double,
    val marginOfError: Double,
    val standardError: Double,
    val criticalValue: Double
)

data class HypothesisTestResult(
    val testStatistic: Double, // Escore Z
    val pValue: Double,        // Valor-p
    val criticalValue: Double, // Valor crítico de corte
    val rejectNull: Boolean,   // Decisão: rejeita H0?
    val alpha: Double          // Nível de significância
)

// ==========================================
// MOTOR DE CÁLCULO ESTATÍSTICO (STATISTICAL UTILS)
// ==========================================

object StatisticalUtils {

    // --- 1. FUNÇÕES AUXILIARES E DISTRIBUIÇÃO NORMAL ---

    fun erf(x: Double): Double {
        val a = 0.147
        val x2 = x * x
        val ax2 = a * x2
        val inner = -x2 * (4.0 / Math.PI + ax2) / (1.0 + ax2)
        val sign = if (x < 0) -1.0 else 1.0
        return sign * sqrt(1.0 - exp(inner))
    }

    fun normalCdf(x: Double, mean: Double, stdDev: Double): Double {
        if (stdDev <= 0) return 0.0
        val z = (x - mean) / (stdDev * sqrt(2.0))
        return 0.5 * (1.0 + erf(z))
    }

    /**
     * Retorna o valor crítico Z para os níveis de confiança mais comuns,
     * ou calcula via aproximação de Hastings para níveis customizados.
     */
    fun criticalZ(confidenceLevelPercent: Double): Double {
        val cl = if (confidenceLevelPercent > 1.0) confidenceLevelPercent / 100.0 else confidenceLevelPercent
        val roundedCl = (cl * 100).roundToInt()

        return when (roundedCl) {
            90 -> 1.64485
            95 -> 1.95996
            98 -> 2.32635
            99 -> 2.57583
            else -> {
                val p = 1.0 - (1.0 - cl) / 2.0
                inverseNormalCdf(p)
            }
        }
    }

    /**
     * Aproximação numérica de Hastings para a Inversa da CDF Normal (Quantil Normal)
     */
    private fun inverseNormalCdf(p: Double): Double {
        if (p <= 0.0 || p >= 1.0) return 0.0
        val t = sqrt(-2.0 * ln(if (p < 0.5) p else 1.0 - p))
        val c0 = 2.515517
        val c1 = 0.802853
        val c2 = 0.010328
        val d1 = 1.432788
        val d2 = 0.189269
        val d3 = 0.001308

        val num = c0 + (c1 + c2 * t) * t
        val den = 1.0 + (d1 + (d2 + d3 * t) * t) * t
        val z = t - (num / den)

        return if (p < 0.5) -z else z
    }

    // --- 2. DISTRIBUIÇÃO DE POISSON ---

    fun factorial(n: Int): Double {
        if (n <= 1) return 1.0
        var res = 1.0
        for (i in 2..n) res *= i
        return res
    }

    fun poissonPmf(k: Int, lambda: Double): Double {
        if (k < 0 || lambda <= 0) return 0.0
        return (lambda.pow(k) * exp(-lambda)) / factorial(k)
    }

    fun poissonCdf(k: Int, lambda: Double): Double {
        if (k < 0 || lambda <= 0) return 0.0
        var sum = 0.0
        for (i in 0..k) {
            sum += poissonPmf(i, lambda)
        }
        return sum
    }

    // --- 3. DISTRIBUIÇÃO BINOMIAL ---

    fun combination(n: Int, k: Int): Double {
        if (k < 0 || k > n) return 0.0
        if (k == 0 || k == n) return 1.0
        var res = 1.0
        val minK = if (k < n - k) k else n - k
        for (i in 1..minK) {
            res = res * (n - i + 1) / i
        }
        return res
    }

    fun binomialPmf(k: Int, n: Int, p: Double): Double {
        if (k < 0 || k > n || p < 0.0 || p > 1.0) return 0.0
        val comb = combination(n, k)
        return comb * p.pow(k) * (1.0 - p).pow(n - k)
    }

    fun binomialCdf(k: Int, n: Int, p: Double): Double {
        if (k < 0 || p < 0.0 || p > 1.0) return 0.0
        var sum = 0.0
        val maxK = minOf(k, n)
        for (i in 0..maxK) {
            sum += binomialPmf(i, n, p)
        }
        return sum
    }

    // --- 4. INTERVALOS DE CONFIANÇA ---

    fun confidenceIntervalMean(
        mean: Double,
        stdDev: Double,
        sampleSize: Int,
        confidenceLevelPercent: Double
    ): ConfidenceIntervalResult {
        if (sampleSize <= 0 || stdDev <= 0.0) {
            return ConfidenceIntervalResult(0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val z = criticalZ(confidenceLevelPercent)
        val se = stdDev / sqrt(sampleSize.toDouble())
        val marginOfError = z * se

        return ConfidenceIntervalResult(
            lowerLimit = mean - marginOfError,
            upperLimit = mean + marginOfError,
            marginOfError = marginOfError,
            standardError = se,
            criticalValue = z
        )
    }

    fun confidenceIntervalProportion(
        proportion: Double,
        sampleSize: Int,
        confidenceLevelPercent: Double
    ): ConfidenceIntervalResult {
        if (sampleSize <= 0 || proportion < 0.0 || proportion > 1.0) {
            return ConfidenceIntervalResult(0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val z = criticalZ(confidenceLevelPercent)
        val se = sqrt((proportion * (1.0 - proportion)) / sampleSize.toDouble())
        val marginOfError = z * se

        val lower = (proportion - marginOfError).coerceAtLeast(0.0)
        val upper = (proportion + marginOfError).coerceAtMost(1.0)

        return ConfidenceIntervalResult(
            lowerLimit = lower,
            upperLimit = upper,
            marginOfError = marginOfError,
            standardError = se,
            criticalValue = z
        )
    }

    // --- 5. TESTES DE HIPÓTESES (Z-TEST) ---

    fun hypothesisTestMeanZ(
        sampleMean: Double,
        nullMean: Double,
        stdDev: Double,
        sampleSize: Int,
        alpha: Double = 0.05,
        alternative: AlternativeHypothesis = AlternativeHypothesis.TWO_SIDED
    ): HypothesisTestResult {
        if (sampleSize <= 0 || stdDev <= 0.0 || alpha <= 0.0 || alpha >= 1.0) {
            return HypothesisTestResult(0.0, 1.0, 0.0, false, alpha)
        }

        val se = stdDev / sqrt(sampleSize.toDouble())
        val z = (sampleMean - nullMean) / se

        val pValue = when (alternative) {
            AlternativeHypothesis.LESS -> normalCdf(z, 0.0, 1.0)
            AlternativeHypothesis.GREATER -> 1.0 - normalCdf(z, 0.0, 1.0)
            AlternativeHypothesis.TWO_SIDED -> 2.0 * (1.0 - normalCdf(abs(z), 0.0, 1.0))
        }.coerceIn(0.0, 1.0)

        val critZ = when (alternative) {
            AlternativeHypothesis.LESS -> -criticalZ(1.0 - alpha)
            AlternativeHypothesis.GREATER -> criticalZ(1.0 - alpha)
            AlternativeHypothesis.TWO_SIDED -> criticalZ(1.0 - alpha / 2.0)
        }

        return HypothesisTestResult(
            testStatistic = z,
            pValue = pValue,
            criticalValue = critZ,
            rejectNull = pValue < alpha,
            alpha = alpha
        )
    }

    fun hypothesisTestProportionZ(
        sampleProportion: Double,
        nullProportion: Double,
        sampleSize: Int,
        alpha: Double = 0.05,
        alternative: AlternativeHypothesis = AlternativeHypothesis.TWO_SIDED
    ): HypothesisTestResult {
        if (sampleSize <= 0 || nullProportion <= 0.0 || nullProportion >= 1.0 ||
            sampleProportion < 0.0 || sampleProportion > 1.0 || alpha <= 0.0 || alpha >= 1.0) {
            return HypothesisTestResult(0.0, 1.0, 0.0, false, alpha)
        }

        // Utiliza nullProportion (p_0) sob H0 para o cálculo do Erro Padrão
        val se = sqrt((nullProportion * (1.0 - nullProportion)) / sampleSize.toDouble())
        val z = (sampleProportion - nullProportion) / se

        val pValue = when (alternative) {
            AlternativeHypothesis.LESS -> normalCdf(z, 0.0, 1.0)
            AlternativeHypothesis.GREATER -> 1.0 - normalCdf(z, 0.0, 1.0)
            AlternativeHypothesis.TWO_SIDED -> 2.0 * (1.0 - normalCdf(abs(z), 0.0, 1.0))
        }.coerceIn(0.0, 1.0)

        val critZ = when (alternative) {
            AlternativeHypothesis.LESS -> -criticalZ(1.0 - alpha)
            AlternativeHypothesis.GREATER -> criticalZ(1.0 - alpha)
            AlternativeHypothesis.TWO_SIDED -> criticalZ(1.0 - alpha / 2.0)
        }

        return HypothesisTestResult(
            testStatistic = z,
            pValue = pValue,
            criticalValue = critZ,
            rejectNull = pValue < alpha,
            alpha = alpha
        )
    }
}