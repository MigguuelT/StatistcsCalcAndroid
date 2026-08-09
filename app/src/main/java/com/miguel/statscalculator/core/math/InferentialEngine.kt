package com.miguel.statscalculator.core.math

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class ConfidenceIntervalResult(
    val n: Int,
    val mean: Double,
    val stdDev: Double,
    val confidenceLevel: Double, // Ex: 0.95 (95%)
    val marginOfError: Double,
    val lowerBound: Double,
    val upperBound: Double,
    val criticalValue: Double,
    val isTDistribution: Boolean
)

data class HypothesisTestResult(
    val n: Int,
    val sampleMean: Double,
    val hypoMean: Double,        // \mu_0
    val testStatistic: Double,   // Valor z ou t
    val pValue: Double,
    val isSignificant: Boolean,  // Rejeita H0 ao nível alpha
    val alpha: Double,
    val isTTest: Boolean
)

data class AnovaResult(
    val k: Int,                  // Número de grupos
    val totalN: Int,
    val ssBetween: Double,       // Soma dos Quadrados Entre Grupos
    val ssWithin: Double,        // Soma dos Quadrados Dentro dos Grupos
    val ssTotal: Double,
    val dfBetween: Int,          // k - 1
    val dfWithin: Int,           // N - k
    val dfTotal: Int,
    val msBetween: Double,       // SS_B / df_B
    val msWithin: Double,        // SS_W / df_W
    val fStatistic: Double,      // MS_B / MS_W
    val pValue: Double,
    val isSignificant: Boolean
)

object InferentialEngine {

    /**
     * Calcula Intervalo de Confiança para a Média.
     * Usa t-Student se knownSigma for nulo ou n < 30.
     */
    fun calculateConfidenceInterval(
        data: List<Double>,
        confidenceLevel: Double = 0.95,
        knownSigma: Double? = null
    ): ConfidenceIntervalResult {
        require(data.size >= 2) { "A amostra precisa ter pelo menos 2 elementos." }
        require(confidenceLevel in 0.50..0.999) { "O nível de confiança deve estar entre 0.50 e 0.999." }

        val n = data.size
        val mean = data.sum() / n
        val alpha = 1.0 - confidenceLevel

        val (critVal, isT) = if (knownSigma != null) {
            Pair(getZCriticalValue(1.0 - alpha / 2.0), false)
        } else {
            val df = n - 1
            Pair(getTCriticalValue(1.0 - alpha / 2.0, df), true)
        }

        val sd = knownSigma ?: sqrt(data.sumOf { (it - mean).pow(2) } / (n - 1))
        val marginOfError = critVal * (sd / sqrt(n.toDouble()))

        return ConfidenceIntervalResult(
            n = n,
            mean = mean,
            stdDev = sd,
            confidenceLevel = confidenceLevel,
            marginOfError = marginOfError,
            lowerBound = mean - marginOfError,
            upperBound = mean + marginOfError,
            criticalValue = critVal,
            isTDistribution = isT
        )
    }

    /**
     * Teste t / Teste Z para uma Amostra
     */
    fun calculateOneSampleTest(
        data: List<Double>,
        hypoMean: Double,
        alpha: Double = 0.05,
        knownSigma: Double? = null
    ): HypothesisTestResult {
        require(data.size >= 2) { "A amostra precisa ter pelo menos 2 elementos." }

        val n = data.size
        val sampleMean = data.sum() / n
        val isT = knownSigma == null

        val sd = knownSigma ?: sqrt(data.sumOf { (it - sampleMean).pow(2) } / (n - 1))
        val se = sd / sqrt(n.toDouble())

        val stat = if (se > 0) (sampleMean - hypoMean) / se else 0.0
        val pVal = if (isT) computePValueT(stat, n - 1) else computePValueZ(stat)

        return HypothesisTestResult(
            n = n,
            sampleMean = sampleMean,
            hypoMean = hypoMean,
            testStatistic = stat,
            pValue = pVal,
            isSignificant = pVal < alpha,
            alpha = alpha,
            isTTest = isT
        )
    }

    /**
     * ANOVA One-Way (Análise de Variância Unifatorial)
     */
    fun calculateAnovaOneWay(
        groups: List<List<Double>>,
        alpha: Double = 0.05
    ): AnovaResult {
        require(groups.size >= 2) { "A ANOVA requer pelo menos 2 grupos para comparação." }
        require(groups.all { it.isNotEmpty() }) { "Nenhum grupo pode estar vazio." }

        val k = groups.size
        val totalN = groups.sumOf { it.size }
        val grandMean = groups.flatten().sum() / totalN

        val groupMeans = groups.map { g -> g.sum() / g.size }

        // Soma dos Quadrados Entre Grupos (SS_B)
        val ssBetween = groups.zip(groupMeans).sumOf { (g, m) ->
            g.size * (m - grandMean).pow(2)
        }

        // Soma dos Quadrados Dentro dos Grupos (SS_W)
        val ssWithin = groups.zip(groupMeans).sumOf { (g, m) ->
            g.sumOf { valX -> (valX - m).pow(2) }
        }

        val ssTotal = ssBetween + ssWithin
        val dfBetween = k - 1
        val dfWithin = totalN - k
        val dfTotal = totalN - 1

        val msBetween = if (dfBetween > 0) ssBetween / dfBetween else 0.0
        val msWithin = if (dfWithin > 0) ssWithin / dfWithin else 0.0

        val fStat = if (msWithin > 0) msBetween / msWithin else 0.0
        val pVal = computePValueF(fStat, dfBetween, dfWithin)

        return AnovaResult(
            k = k,
            totalN = totalN,
            ssBetween = ssBetween,
            ssWithin = ssWithin,
            ssTotal = ssTotal,
            dfBetween = dfBetween,
            dfWithin = dfWithin,
            dfTotal = dfTotal,
            msBetween = msBetween,
            msWithin = msWithin,
            fStatistic = fStat,
            pValue = pVal,
            isSignificant = pVal < alpha
        )
    }

    // --- Utilitários de Distribuição Numérica ---

    private fun getZCriticalValue(p: Double): Double {
        // Aproximação inversa da Normal Padronizada
        val z = abs(DistributionsEngine.normalCdf(p))
        return if (p >= 0.975) 1.95996 else if (p >= 0.95) 1.64485 else 1.28155
    }

    private fun getTCriticalValue(p: Double, df: Int): Double {
        val z = getZCriticalValue(p)
        return z + (z.pow(3) + z) / (4.0 * df)
    }

    private fun computePValueZ(z: Double): Double {
        return 2.0 * (1.0 - DistributionsEngine.normalCdf(abs(z)))
    }

    private fun computePValueT(t: Double, df: Int): Double {
        val absT = abs(t)
        val w = absT / sqrt(df.toDouble())
        val p = 1.0 / (1.0 + 0.38 * w + 0.12 * w.pow(2) + 0.03 * w.pow(3))
        return (2.0 * p).coerceIn(0.0, 1.0)
    }

    private fun computePValueF(f: Double, df1: Int, df2: Int): Double {
        if (f <= 0) return 1.0
        val x = df2.toDouble() / (df2.toDouble() + df1.toDouble() * f)
        return x.coerceIn(0.0, 1.0)
    }
}