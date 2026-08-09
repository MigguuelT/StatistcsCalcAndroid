package com.miguel.statscalculator.presentation.inferential

import com.miguel.statscalculator.util.AlternativeHypothesis
import com.miguel.statscalculator.util.ConfidenceIntervalResult
import com.miguel.statscalculator.util.HypothesisTestResult

enum class InferentialParamType {
    MEAN,       // Média Populacional (μ)
    PROPORTION  // Proporção Populacional (p)
}

data class InferentialState(
    // 0: Intervalo de Confiança, 1: Teste de Hipóteses
    val mainTab: Int = 0,

    // --- ESTADO DO INTERVALO DE CONFIANÇA ---
    val ciParamType: InferentialParamType = InferentialParamType.MEAN,
    val ciEstimateText: String = "",          // x̅ ou p̂
    val ciStdDevText: String = "",            // s
    val ciSampleSizeText: String = "",        // n
    val ciConfidenceLevelText: String = "95", // %
    val ciResult: ConfidenceIntervalResult? = null,
    val ciPointEstimateValue: Double = 0.0,

    // --- ESTADO DO TESTE DE HIPÓTESES ---
    val htParamType: InferentialParamType = InferentialParamType.MEAN,
    val htAlternative: AlternativeHypothesis = AlternativeHypothesis.TWO_SIDED,
    val htNullValueText: String = "",         // μ0 ou p0 (Valor sob H0)
    val htSampleEstimateText: String = "",   // x̅ ou p̂
    val htStdDevText: String = "",           // s (apenas para Média)
    val htSampleSizeText: String = "",       // n
    val htAlphaText: String = "0.05",        // Nível de significância α
    val htResult: HypothesisTestResult? = null,

    // --- MENSAGENS E ERROS ---
    val errorMessage: String? = null
)