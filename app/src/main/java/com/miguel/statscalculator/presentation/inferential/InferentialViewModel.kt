package com.miguel.statscalculator.presentation.inferential

import androidx.lifecycle.ViewModel
import com.miguel.statscalculator.util.AlternativeHypothesis
import com.miguel.statscalculator.util.StatisticalUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InferentialViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InferentialState())
    val uiState: StateFlow<InferentialState> = _uiState.asStateFlow()

    fun onMainTabSelected(tabIndex: Int) {
        _uiState.update { it.copy(mainTab = tabIndex, errorMessage = null) }
    }

    // --- AÇÕES DO INTERVALO DE CONFIANÇA ---
    fun onCiParamTypeChanged(type: InferentialParamType) {
        _uiState.update { it.copy(ciParamType = type, ciResult = null, errorMessage = null) }
    }
    fun onCiEstimateChange(v: String) { _uiState.update { it.copy(ciEstimateText = v) } }
    fun onCiStdDevChange(v: String) { _uiState.update { it.copy(ciStdDevText = v) } }
    fun onCiSampleSizeChange(v: String) { _uiState.update { it.copy(ciSampleSizeText = v) } }
    fun onCiConfidenceLevelChange(v: String) { _uiState.update { it.copy(ciConfidenceLevelText = v) } }

    fun calculateCi() {
        val st = _uiState.value
        val n = st.ciSampleSizeText.toIntOrNull()
        val cl = st.ciConfidenceLevelText.toDoubleOrNull()
        val est = st.ciEstimateText.toDoubleOrNull()

        if (n == null || cl == null || est == null || n <= 0 || cl <= 0.0 || cl >= 100.0) {
            _uiState.update { it.copy(errorMessage = "Insira valores válidos para o Intervalo de Confiança.") }
            return
        }

        if (st.ciParamType == InferentialParamType.MEAN) {
            val s = st.ciStdDevText.toDoubleOrNull()
            if (s == null || s <= 0) {
                _uiState.update { it.copy(errorMessage = "Desvio padrão (s) deve ser > 0.") }
                return
            }
            val res = StatisticalUtils.confidenceIntervalMean(est, s, n, cl)
            _uiState.update { it.copy(ciResult = res, ciPointEstimateValue = est, errorMessage = null) }
        } else {
            if (est < 0.0 || est > 1.0) {
                _uiState.update { it.copy(errorMessage = "Proporção (p̂) deve estar entre 0.0 e 1.0.") }
                return
            }
            val res = StatisticalUtils.confidenceIntervalProportion(est, n, cl)
            _uiState.update { it.copy(ciResult = res, ciPointEstimateValue = est, errorMessage = null) }
        }
    }

    // --- AÇÕES DO TESTE DE HIPÓTESES ---
    fun onHtParamTypeChanged(type: InferentialParamType) {
        _uiState.update { it.copy(htParamType = type, htResult = null, errorMessage = null) }
    }
    fun onHtAlternativeChanged(alt: AlternativeHypothesis) {
        _uiState.update { it.copy(htAlternative = alt, htResult = null, errorMessage = null) }
    }
    fun onHtNullValueChange(v: String) { _uiState.update { it.copy(htNullValueText = v) } }
    fun onHtSampleEstimateChange(v: String) { _uiState.update { it.copy(htSampleEstimateText = v) } }
    fun onHtStdDevChange(v: String) { _uiState.update { it.copy(htStdDevText = v) } }
    fun onHtSampleSizeChange(v: String) { _uiState.update { it.copy(htSampleSizeText = v) } }
    fun onHtAlphaChange(v: String) { _uiState.update { it.copy(htAlphaText = v) } }

    fun calculateHt() {
        val st = _uiState.value
        val nullVal = st.htNullValueText.toDoubleOrNull()
        val estVal = st.htSampleEstimateText.toDoubleOrNull()
        val n = st.htSampleSizeText.toIntOrNull()
        val alpha = st.htAlphaText.toDoubleOrNull()

        if (nullVal == null || estVal == null || n == null || alpha == null || n <= 0 || alpha <= 0.0 || alpha >= 1.0) {
            _uiState.update { it.copy(errorMessage = "Insira dados numéricos válidos (0 < α < 1 e n > 0).") }
            return
        }

        if (st.htParamType == InferentialParamType.MEAN) {
            val s = st.htStdDevText.toDoubleOrNull()
            if (s == null || s <= 0) {
                _uiState.update { it.copy(errorMessage = "Desvio padrão (s) deve ser > 0.") }
                return
            }
            val res = StatisticalUtils.hypothesisTestMeanZ(
                sampleMean = estVal,
                nullMean = nullVal,
                stdDev = s,
                sampleSize = n,
                alpha = alpha,
                alternative = st.htAlternative
            )
            _uiState.update { it.copy(htResult = res, errorMessage = null) }
        } else {
            if (nullVal <= 0.0 || nullVal >= 1.0 || estVal < 0.0 || estVal > 1.0) {
                _uiState.update { it.copy(errorMessage = "A proporção deve estar entre 0.0 e 1.0.") }
                return
            }
            val res = StatisticalUtils.hypothesisTestProportionZ(
                sampleProportion = estVal,
                nullProportion = nullVal,
                sampleSize = n,
                alpha = alpha,
                alternative = st.htAlternative
            )
            _uiState.update { it.copy(htResult = res, errorMessage = null) }
        }
    }

    fun loadExample() {
        if (_uiState.value.mainTab == 0) {
            if (_uiState.value.ciParamType == InferentialParamType.MEAN) {
                _uiState.update { it.copy(ciEstimateText = "100.0", ciStdDevText = "15.0", ciSampleSizeText = "100", ciConfidenceLevelText = "95") }
            } else {
                _uiState.update { it.copy(ciEstimateText = "0.45", ciSampleSizeText = "400", ciConfidenceLevelText = "95") }
            }
            calculateCi()
        } else {
            if (_uiState.value.htParamType == InferentialParamType.MEAN) {
                _uiState.update {
                    it.copy(
                        htNullValueText = "100.0",
                        htSampleEstimateText = "104.0",
                        htStdDevText = "15.0",
                        htSampleSizeText = "100",
                        htAlphaText = "0.05",
                        htAlternative = AlternativeHypothesis.TWO_SIDED
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        htNullValueText = "0.50",
                        htSampleEstimateText = "0.58",
                        htSampleSizeText = "100",
                        htAlphaText = "0.05",
                        htAlternative = AlternativeHypothesis.GREATER
                    )
                }
            }
            calculateHt()
        }
    }

    fun clear() {
        _uiState.update { InferentialState(mainTab = it.mainTab) }
    }
}