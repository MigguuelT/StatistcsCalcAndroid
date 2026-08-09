package com.miguel.statscalculator.core.math

import java.math.BigInteger

data class BayesResult(
    val priorA: Double,
    val likelihoodBGivenA: Double,
    val likelihoodBGivenNotA: Double,
    val posteriorA: Double,         // P(A|B)
    val totalProbabilityB: Double   // P(B)
)

object CombinatoricsEngine {

    /**
     * Calcula o Fatorial n! com BigInteger para evitar overflow em n > 20.
     */
    fun factorial(n: Int): BigInteger {
        require(n >= 0) { "O fatorial não é definido para números negativos." }
        var result = BigInteger.ONE
        for (i in 2..n) {
            result = result.multiply(BigInteger.valueOf(i.toLong()))
        }
        return result
    }

    /**
     * Permutação Simples P(n) = n!
     */
    fun permutation(n: Int): BigInteger = factorial(n)

    /**
     * Arranjo Simples A(n, k) = n! / (n - k)!
     */
    fun arrangement(n: Int, k: Int): BigInteger {
        require(n >= 0 && k >= 0) { "Os valores de n e k devem ser não-negativos." }
        require(n >= k) { "n deve ser maior ou igual a k." }

        var result = BigInteger.ONE
        for (i in (n - k + 1)..n) {
            result = result.multiply(BigInteger.valueOf(i.toLong()))
        }
        return result
    }

    /**
     * Combinação Simples C(n, k) = n! / (k! * (n - k)!)
     */
    fun combination(n: Int, k: Int): BigInteger {
        require(n >= 0 && k >= 0) { "Os valores de n e k devem ser não-negativos." }
        require(n >= k) { "n deve ser maior ou igual a k." }

        val kOpt = if (k > n - k) n - k else k
        var numerator = BigInteger.ONE
        var denominator = BigInteger.ONE

        for (i in 1..kOpt) {
            numerator = numerator.multiply(BigInteger.valueOf((n - i + 1).toLong()))
            denominator = denominator.multiply(BigInteger.valueOf(i.toLong()))
        }
        return numerator.divide(denominator)
    }

    /**
     * Teorema de Bayes: P(A|B) = [ P(B|A) * P(A) ] / P(B)
     * onde P(B) = P(B|A)*P(A) + P(B|¬A)*P(¬A)
     */
    fun bayesTheorem(
        priorA: Double,             // P(A)
        likelihoodBGivenA: Double,   // P(B|A)
        likelihoodBGivenNotA: Double // P(B|¬A)
    ): BayesResult {
        require(priorA in 0.0..1.0) { "A probabilidade a priori P(A) deve estar entre 0 e 1." }
        require(likelihoodBGivenA in 0.0..1.0) { "P(B|A) deve estar entre 0 e 1." }
        require(likelihoodBGivenNotA in 0.0..1.0) { "P(B|¬A) deve estar entre 0 e 1." }

        val priorNotA = 1.0 - priorA
        val totalProbB = (likelihoodBGivenA * priorA) + (likelihoodBGivenNotA * priorNotA)

        require(totalProbB > 0) { "A probabilidade total de B não pode ser zero." }

        val posteriorA = (likelihoodBGivenA * priorA) / totalProbB

        return BayesResult(
            priorA = priorA,
            likelihoodBGivenA = likelihoodBGivenA,
            likelihoodBGivenNotA = likelihoodBGivenNotA,
            posteriorA = posteriorA,
            totalProbabilityB = totalProbB
        )
    }
}