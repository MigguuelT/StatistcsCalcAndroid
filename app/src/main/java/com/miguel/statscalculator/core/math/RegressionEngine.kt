package com.miguel.statscalculator.core.math

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object RegressionEngine {

    fun calculate(points: List<Point2D>): LinearRegressionResult {
        require(points.size >= 2) { "É necessário ter pelo menos 2 pontos para regressão linear." }

        val n = points.size
        val meanX = points.sumOf { it.x } / n
        val meanY = points.sumOf { it.y } / n

        val ssXX = points.sumOf { (it.x - meanX).pow(2) }
        val ssYY = points.sumOf { (it.y - meanY).pow(2) }
        val ssXY = points.sumOf { (it.x - meanX) * (it.y - meanY) }

        require(ssXX > 0) { "A variância de X não pode ser zero (todos os valores de X são iguais)." }

        val slope = ssXY / ssXX
        val intercept = meanY - slope * meanX

        val pearsonR = if (ssYY > 0) ssXY / sqrt(ssXX * ssYY) else 0.0
        val rSquared = pearsonR.pow(2)

        val adjustedRSquared = if (n > 2) {
            1.0 - (1.0 - rSquared) * (n - 1.0) / (n - 2.0)
        } else {
            rSquared
        }

        // Resíduos e Erro Padrão
        val ssr = points.sumOf { p ->
            val yPred = intercept + slope * p.x
            (p.y - yPred).pow(2)
        }

        val stdErrorEstimate = if (n > 2) sqrt(ssr / (n - 2)) else 0.0
        val stdErrorSlope = if (ssXX > 0 && n > 2) stdErrorEstimate / sqrt(ssXX) else 0.0

        val tStatistic = if (stdErrorSlope > 0) slope / stdErrorSlope else 0.0
        val degreesOfFreedom = n - 2
        val pValue = if (degreesOfFreedom > 0) computePValue(tStatistic, degreesOfFreedom) else 1.0

        val sign = if (slope >= 0) "+" else "-"
        val equationStr = String.format("y = %.4f %s %.4fx", intercept, sign, abs(slope))

        return LinearRegressionResult(
            n = n,
            slope = slope,
            intercept = intercept,
            pearsonR = pearsonR,
            rSquared = rSquared,
            adjustedRSquared = adjustedRSquared,
            stdErrorEstimate = stdErrorEstimate,
            stdErrorSlope = stdErrorSlope,
            tStatistic = tStatistic,
            pValue = pValue,
            equationString = equationStr
        )
    }

    // Aproximação numérica segura do valor-p para a distribuição t de Student
    private fun computePValue(t: Double, df: Int): Double {
        if (df <= 0 || t.isNaN()) return 1.0
        val absT = abs(t)
        val w = absT / sqrt(df.toDouble())
        val p = 1.0 / (1.0 + 0.38 * w + 0.12 * w.pow(2) + 0.03 * w.pow(3))
        return p.coerceIn(0.0, 1.0)
    }
}