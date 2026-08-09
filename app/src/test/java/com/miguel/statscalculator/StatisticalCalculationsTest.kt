package com.miguel.statscalculator

import com.miguel.statscalculator.util.AlternativeHypothesis
import com.miguel.statscalculator.util.StatisticalUtils.binomialCdf
import com.miguel.statscalculator.util.StatisticalUtils.binomialPmf
import com.miguel.statscalculator.util.StatisticalUtils.combination
import com.miguel.statscalculator.util.StatisticalUtils.confidenceIntervalMean
import com.miguel.statscalculator.util.StatisticalUtils.confidenceIntervalProportion
import com.miguel.statscalculator.util.StatisticalUtils.hypothesisTestMeanZ
import com.miguel.statscalculator.util.StatisticalUtils.hypothesisTestProportionZ
import com.miguel.statscalculator.util.StatisticalUtils.normalCdf
import com.miguel.statscalculator.util.StatisticalUtils.poissonCdf
import com.miguel.statscalculator.util.StatisticalUtils.poissonPmf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StatisticalCalculationsTest {

    private val EPSILON = 0.0001

    // ==========================================
    // 1. TESTES PADRÃO DE REFERÊNCIA
    // ==========================================

    @Test
    fun `normalCdf - deve retornar 0,9750 para Z = 1,96`() {
        val actual = normalCdf(1.96, 0.0, 1.0)
        assertEquals(0.9750, actual, EPSILON)
    }

    @Test
    fun `normalCdf - deve retornar 0,5000 no ponto da media`() {
        val actual = normalCdf(100.0, 100.0, 15.0)
        assertEquals(0.5000, actual, EPSILON)
    }

    @Test
    fun `normalCdf - deve tratar desvio padrao invalido sem crashar`() {
        val actual = normalCdf(x = 5.0, mean = 0.0, stdDev = 0.0)
        assertEquals(0.0, actual, EPSILON)
    }

    @Test
    fun `poissonPmf - deve calcular P exata para k igual a 2 com lambda 3,5`() {
        val actual = poissonPmf(k = 2, lambda = 3.5)
        assertEquals(0.1849, actual, EPSILON)
    }

    @Test
    fun `poissonCdf - deve somar probabilidades acumuladas para k menor ou igual a 2`() {
        val actual = poissonCdf(k = 2, lambda = 3.5)
        assertEquals(0.3208, actual, EPSILON)
    }

    @Test
    fun `binomialPmf - deve calcular 5 sucessos em 10 ensaios com p 0,5`() {
        val actual = binomialPmf(k = 5, n = 10, p = 0.5)
        assertEquals(0.2461, actual, EPSILON)
    }

    @Test
    fun `binomialPmf - deve retornar 0 quando k for maior que n`() {
        val actual = binomialPmf(k = 12, n = 10, p = 0.5)
        assertEquals(0.0, actual, EPSILON)
    }

    // ==========================================
    // 2. CASOS DE BORDA: DISTRIBUIÇÃO NORMAL
    // ==========================================

    @Test
    fun `normalCdf - Z extremamente alto (Z = 10) deve se aproximar de 1`() {
        val actual = normalCdf(x = 10.0, mean = 0.0, stdDev = 1.0)
        assertEquals(1.0, actual, EPSILON)
    }

    @Test
    fun `normalCdf - Z extremamente baixo (Z = -10) deve se aproximar de 0`() {
        val actual = normalCdf(x = -10.0, mean = 0.0, stdDev = 1.0)
        assertEquals(0.0, actual, EPSILON)
    }

    @Test
    fun `normalCdf - desvio padrao negativo deve retornar 0 sem excecao`() {
        val actual = normalCdf(x = 2.0, mean = 0.0, stdDev = -1.0)
        assertEquals(0.0, actual, EPSILON)
    }

    // ==========================================
    // 3. CASOS DE BORDA: DISTRIBUIÇÃO DE POISSON
    // ==========================================

    @Test
    fun `poissonPmf - k igual a zero deve calcular e0 (exp de minus lambda)`() {
        val actual = poissonPmf(k = 0, lambda = 2.0)
        assertEquals(0.1353, actual, EPSILON)
    }

    @Test
    fun `poissonPmf - k negativo deve retornar 0`() {
        val actual = poissonPmf(k = -1, lambda = 3.0)
        assertEquals(0.0, actual, EPSILON)
    }

    @Test
    fun `poissonPmf - lambda igual a zero deve retornar 0`() {
        val actual = poissonPmf(k = 2, lambda = 0.0)
        assertEquals(0.0, actual, EPSILON)
    }

    // ==========================================
    // 4. CASOS DE BORDA: DISTRIBUIÇÃO BINOMIAL
    // ==========================================

    @Test
    fun `binomialPmf - p igual a 0 deve ter 0 de probabilidade para k maior que 0`() {
        val actual = binomialPmf(k = 3, n = 10, p = 0.0)
        assertEquals(0.0, actual, EPSILON)
    }

    @Test
    fun `binomialPmf - p igual a 1 deve ter 100 porcento de probabilidade para k igual a n`() {
        val actual = binomialPmf(k = 10, n = 10, p = 1.0)
        assertEquals(1.0, actual, EPSILON)
    }

    @Test
    fun `binomialPmf - k igual a 0 deve calcular (1 - p) elevado a n`() {
        val actual = binomialPmf(k = 0, n = 5, p = 0.2)
        assertEquals(0.32768, actual, EPSILON)
    }

    @Test
    fun `binomialPmf - probabilidade p invalida (maior que 1 ou menor que 0) deve retornar 0`() {
        val actualUpper = binomialPmf(k = 2, n = 5, p = 1.5)
        val actualLower = binomialPmf(k = 2, n = 5, p = -0.5)
        assertEquals(0.0, actualUpper, EPSILON)
        assertEquals(0.0, actualLower, EPSILON)
    }

    // ==========================================
    // 5. TESTES DE PRECISÃO NUMÉRICA E COMBINATÓRIA
    // ==========================================

    @Test
    fun `combination - n grande (C(50, 5)) nao deve estourar precisao numerica`() {
        val actual = combination(n = 50, k = 5)
        val expected = 2118760.0
        assertEquals(expected, actual, EPSILON)
    }

    @Test
    fun `binomialCdf - soma acumulada para k igual a n deve ser exatamente 1`() {
        val actual = binomialCdf(k = 10, n = 10, p = 0.4)
        assertEquals(1.0, actual, EPSILON)
    }

    // ==========================================
    // 6. TESTES DE INTERVALO DE CONFIANÇA
    // ==========================================

    @Test
    fun `confidenceIntervalMean - deve calcular intervalo de 95 porcento corretamente`() {
        val result = confidenceIntervalMean(
            mean = 100.0,
            stdDev = 15.0,
            sampleSize = 100,
            confidenceLevelPercent = 95.0
        )

        assertEquals(1.5, result.standardError, EPSILON)
        assertEquals(2.9399, result.marginOfError, EPSILON)
        assertEquals(97.0600, result.lowerLimit, EPSILON)
        assertEquals(102.9399, result.upperLimit, EPSILON)
    }

    @Test
    fun `confidenceIntervalProportion - deve calcular intervalo de 90 porcento para proporcao`() {
        val result = confidenceIntervalProportion(
            proportion = 0.40,
            sampleSize = 400,
            confidenceLevelPercent = 90.0
        )

        assertEquals(0.02449, result.standardError, EPSILON)
        assertEquals(0.04029, result.marginOfError, EPSILON)
        assertEquals(0.35970, result.lowerLimit, EPSILON)
        assertEquals(0.44029, result.upperLimit, EPSILON)
    }

    // ==========================================
    // 7. TESTES DE HIPÓTESES (Z-TEST)
    // ==========================================

    @Test
    fun `hypothesisTestMeanZ - teste bilateral com rejeicao de H0`() {
        val result = hypothesisTestMeanZ(
            sampleMean = 104.0,
            nullMean = 100.0,
            stdDev = 15.0,
            sampleSize = 100,
            alpha = 0.05,
            alternative = AlternativeHypothesis.TWO_SIDED
        )

        assertEquals(2.6666, result.testStatistic, EPSILON)
        assertEquals(0.0076, result.pValue, EPSILON)
        assertTrue(result.rejectNull)
    }

    @Test
    fun `hypothesisTestMeanZ - teste unilateral a esquerda sem rejeicao de H0`() {
        val result = hypothesisTestMeanZ(
            sampleMean = 49.0,
            nullMean = 50.0,
            stdDev = 5.0,
            sampleSize = 36,
            alpha = 0.01,
            alternative = AlternativeHypothesis.LESS
        )

        assertEquals(-1.2000, result.testStatistic, EPSILON)
        assertEquals(0.1151, result.pValue, EPSILON)
        assertFalse(result.rejectNull)
    }

    @Test
    fun `hypothesisTestProportionZ - teste de proporcao com uso correto de p0 no SE`() {
        val result = hypothesisTestProportionZ(
            sampleProportion = 0.58,
            nullProportion = 0.50,
            sampleSize = 100,
            alpha = 0.05,
            alternative = AlternativeHypothesis.GREATER
        )

        assertEquals(1.6000, result.testStatistic, EPSILON)
        assertEquals(0.0547, result.pValue, EPSILON)
        assertFalse(result.rejectNull)
    }
}