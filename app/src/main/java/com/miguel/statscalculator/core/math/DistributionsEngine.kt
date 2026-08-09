package com.miguel.statscalculator.core.math

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

data class NormalResult(
    val x: Double,
    val mean: Double,
    val stdDev: Double,
    val zScore: Double,
    val probLessThanZ: Double,      // P(Z <= z)
    val probGreaterThanZ: Double,   // P(Z > z)
    val probTwoTailed: Double       // P(-|z| <= Z <= |z|)
)

data class PoissonResult(
    val lambda: Double,
    val k: Int,
    val probExact: Double,          // P(X = k)
    val probLessOrEqual: Double,    // P(X <= k)
    val probGreater: Double,        // P(X > k)
    val mean: Double,
    val variance: Double
)

data class BinomialResult(
    val n: Int,
    val k: Int,
    val p: Double,
    val probExact: Double,          // P(X = k)
    val probLessOrEqual: Double,    // P(X <= k)
    val probGreater: Double,        // P(X > k)
    val mean: Double,
    val variance: Double
)

object DistributionsEngine {

    /**
     * Calcula a Distribuição Normal Padronizada (Z-Score e Áreas)
     */
    fun calculateNormal(x: Double, mean: Double, stdDev: Double): NormalResult {
        require(stdDev > 0) { "O desvio padrão (σ) deve ser estritamente maior que zero." }

        val z = (x - mean) / stdDev
        val probLess = normalCdf(z)
        val probGreater = (1.0 - probLess).coerceIn(0.0, 1.0)
        val probTwoTailed = (2.0 * normalCdf(abs(z)) - 1.0).coerceIn(0.0, 1.0)

        return NormalResult(
            x = x,
            mean = mean,
            stdDev = stdDev,
            zScore = z,
            probLessThanZ = probLess,
            probGreaterThanZ = probGreater,
            probTwoTailed = probTwoTailed
        )
    }

    /**
     * Aproximação de Abramowitz & Stegun para a CDF Normal Padronizada \Phi(z)
     * Precisão de erro < 7.5e-8
     */
    fun normalCdf(z: Double): Double {
        if (z < -8.0) return 0.0
        if (z > 8.0) return 1.0

        val absZ = abs(z)
        val p = 0.2316419
        val b1 = 0.319381530
        val b2 = -0.356563782
        val b3 = 1.781477937
        val b4 = -1.821255978
        val b5 = 1.330274429

        val t = 1.0 / (1.0 + p * absZ)
        val pdf = (1.0 / sqrt(2.0 * Math.PI)) * exp(-0.5 * absZ * absZ)
        val poly = t * (b1 + t * (b2 + t * (b3 + t * (b4 + t * b5))))
        val cdf = 1.0 - pdf * poly

        return if (z >= 0) cdf else 1.0 - cdf
    }

    /**
     * Distribuição de Poisson: P(X = k) = (lambda^k * e^-lambda) / k!
     */
    fun calculatePoisson(lambda: Double, k: Int): PoissonResult {
        require(lambda > 0) { "O parâmetro Média (λ) deve ser maior que zero." }
        require(k >= 0) { "O número de ocorrências (k) não pode ser negativo." }

        var probExact = exp(-lambda)
        var term = probExact
        var probLessOrEqual = probExact

        for (i in 1..k) {
            term *= (lambda / i)
            probLessOrEqual += term
            if (i == k) {
                probExact = term
            }
        }
        val probGreater = (1.0 - probLessOrEqual).coerceAtLeast(0.0)

        return PoissonResult(
            lambda = lambda,
            k = k,
            probExact = probExact.coerceIn(0.0, 1.0),
            probLessOrEqual = probLessOrEqual.coerceIn(0.0, 1.0),
            probGreater = probGreater,
            mean = lambda,
            variance = lambda
        )
    }

    /**
     * Distribuição Binomial: P(X = k) = C(n, k) * p^k * (1-p)^(n-k)
     */
    fun calculateBinomial(n: Int, k: Int, p: Double): BinomialResult {
        require(n >= 0) { "O número de ensaios (n) deve ser não-negativo." }
        require(k in 0..n) { "O número de sucessos (k) deve estar entre 0 e n." }
        require(p in 0.0..1.0) { "A probabilidade (p) deve estar entre 0 e 1." }

        if (p == 1.0) {
            val exact = if (k == n) 1.0 else 0.0
            val le = if (k >= n) 1.0 else 0.0
            return BinomialResult(n, k, p, exact, le, 1.0 - le, n.toDouble(), 0.0)
        }
        if (p == 0.0) {
            val exact = if (k == 0) 1.0 else 0.0
            return BinomialResult(n, k, p, exact, 1.0, 0.0, 0.0, 0.0)
        }

        val q = 1.0 - p
        var probExact = 0.0
        var probLessOrEqual = 0.0

        var currentProb = q.pow(n.toDouble()) // i = 0
        if (k == 0) probExact = currentProb
        probLessOrEqual += currentProb

        for (i in 1..n) {
            currentProb *= (n - i + 1).toDouble() / i.toDouble() * (p / q)
            if (i <= k) {
                probLessOrEqual += currentProb
            }
            if (i == k) {
                probExact = currentProb
            }
        }

        val probGreater = (1.0 - probLessOrEqual).coerceAtLeast(0.0)
        val mean = n * p
        val variance = n * p * q

        return BinomialResult(
            n = n,
            k = k,
            p = p,
            probExact = probExact.coerceIn(0.0, 1.0),
            probLessOrEqual = probLessOrEqual.coerceIn(0.0, 1.0),
            probGreater = probGreater,
            mean = mean,
            variance = variance
        )
    }
}