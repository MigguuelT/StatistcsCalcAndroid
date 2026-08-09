package com.miguel.statscalculator.presentation.inferential

import androidx.lifecycle.ViewModel
import com.miguel.statscalculator.core.math.InferentialEngine
import com.miguel.statscalculator.core.util.DataParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InferentialViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InferentialUiState())
    val uiState: StateFlow<InferentialUiState> = _uiState.asStateFlow()

    fun selectTab(tab: InferentialTab) {
        _uiState.update { it.copy(selectedTab = tab, errorMessage = null) }
    }

    // IC Handlers
    fun onIcDataChanged(v: String) = _uiState.update { it.copy(icDataText = v, errorMessage = null) }
    fun onIcLevelChanged(v: String) = _uiState.update { it.copy(icConfidenceLevelText = v, errorMessage = null) }

    fun calculateIC() {
        val numbers = DataParser.parseSingleList(_uiState.value.icDataText)
        val level = _uiState.value.icConfidenceLevelText.replace(",", ".").toDoubleOrNull() ?: 0.95

        if (numbers.size < 2) {
            _uiState.update { it.copy(errorMessage = "Insira pelo menos 2 números na amostra.") }
            return
        }

        try {
            val res = InferentialEngine.calculateConfidenceInterval(numbers, level)
            _uiState.update { it.copy(icResult = res, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Erro no cálculo do IC.") }
        }
    }

    fun loadIcExample() {
        _uiState.update {
            it.copy(
                icDataText = "102.5, 101.8, 99.4, 105.1, 103.0, 98.9, 101.2, 104.5",
                icConfidenceLevelText = "0.95",
                errorMessage = null
            )
        }
        calculateIC()
    }

    // Teste de Hipóteses Handlers
    fun onTestDataChanged(v: String) = _uiState.update { it.copy(testDataText = v, errorMessage = null) }
    fun onHypoMeanChanged(v: String) = _uiState.update { it.copy(hypoMeanText = v, errorMessage = null) }
    fun onAlphaChanged(v: String) = _uiState.update { it.copy(alphaText = v, errorMessage = null) }

    fun calculateTest() {
        val numbers = DataParser.parseSingleList(_uiState.value.testDataText)
        val hypoM = _uiState.value.hypoMeanText.replace(",", ".").toDoubleOrNull() ?: 0.0
        val alpha = _uiState.value.alphaText.replace(",", ".").toDoubleOrNull() ?: 0.05

        if (numbers.size < 2) {
            _uiState.update { it.copy(errorMessage = "Insira pelo menos 2 números na amostra.") }
            return
        }

        try {
            val res = InferentialEngine.calculateOneSampleTest(numbers, hypoM, alpha)
            _uiState.update { it.copy(testResult = res, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Erro no Teste de Hipóteses.") }
        }
    }

    fun loadTestExample() {
        _uiState.update {
            it.copy(
                testDataText = "14.2, 15.1, 13.9, 15.8, 14.8, 15.3, 14.6",
                hypoMeanText = "15.0",
                alphaText = "0.05",
                errorMessage = null
            )
        }
        calculateTest()
    }

    // ANOVA Handlers
    fun onAnovaG1Changed(v: String) = _uiState.update { it.copy(anovaGroup1Text = v, errorMessage = null) }
    fun onAnovaG2Changed(v: String) = _uiState.update { it.copy(anovaGroup2Text = v, errorMessage = null) }
    fun onAnovaG3Changed(v: String) = _uiState.update { it.copy(anovaGroup3Text = v, errorMessage = null) }

    fun calculateAnova() {
        val g1 = DataParser.parseSingleList(_uiState.value.anovaGroup1Text)
        val g2 = DataParser.parseSingleList(_uiState.value.anovaGroup2Text)
        val g3 = DataParser.parseSingleList(_uiState.value.anovaGroup3Text)

        val groups = listOf(g1, g2, g3).filter { it.isNotEmpty() }

        if (groups.size < 2) {
            _uiState.update { it.copy(errorMessage = "Preencha pelo menos 2 grupos com dados para a ANOVA.") }
            return
        }

        try {
            val res = InferentialEngine.calculateAnovaOneWay(groups)
            _uiState.update { it.copy(anovaResult = res, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Erro na ANOVA.") }
        }
    }

    fun loadAnovaExample() {
        _uiState.update {
            it.copy(
                anovaGroup1Text = "85, 88, 90, 82, 87",
                anovaGroup2Text = "78, 80, 84, 82, 79",
                anovaGroup3Text = "92, 95, 91, 89, 94",
                errorMessage = null
            )
        }
        calculateAnova()
    }
}