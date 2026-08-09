package com.miguel.statscalculator.presentation.distributions

import androidx.lifecycle.ViewModel
import com.miguel.statscalculator.core.math.DistributionsEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DistributionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DistributionsUiState())
    val uiState: StateFlow<DistributionsUiState> = _uiState.asStateFlow()

    fun selectTab(tab: DistributionType) {
        _uiState.update { it.copy(selectedTab = tab, errorMessage = null) }
    }

    // Handlers Normal
    fun onNormalXChanged(v: String) = _uiState.update { it.copy(normalX = v, errorMessage = null) }
    fun onNormalMeanChanged(v: String) = _uiState.update { it.copy(normalMean = v, errorMessage = null) }
    fun onNormalStdDevChanged(v: String) = _uiState.update { it.copy(normalStdDev = v, errorMessage = null) }

    fun calculateNormal() {
        val x = _uiState.value.normalX.replace(",", ".").toDoubleOrNull()
        val mean = _uiState.value.normalMean.replace(",", ".").toDoubleOrNull()
        val sd = _uiState.value.normalStdDev.replace(",", ".").toDoubleOrNull()

        if (x == null || mean == null || sd == null) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos da Normal com números válidos.") }
            return
        }

        try {
            val res = DistributionsEngine.calculateNormal(x, mean, sd)
            _uiState.update { it.copy(normalResult = res, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Erro na distribuição Normal.") }
        }
    }

    fun loadNormalExample() {
        _uiState.update {
            it.copy(normalX = "115", normalMean = "100", normalStdDev = "15", errorMessage = null)
        }
        calculateNormal()
    }

    // Handlers Poisson
    fun onPoissonLambdaChanged(v: String) = _uiState.update { it.copy(poissonLambda = v, errorMessage = null) }
    fun onPoissonKChanged(v: String) = _uiState.update { it.copy(poissonK = v, errorMessage = null) }

    fun calculatePoisson() {
        val lambda = _uiState.value.poissonLambda.replace(",", ".").toDoubleOrNull()
        val k = _uiState.value.poissonK.toIntOrNull()

        if (lambda == null || k == null) {
            _uiState.update { it.copy(errorMessage = "Preencha Média (λ) e número de eventos (k) com valores válidos.") }
            return
        }

        try {
            val res = DistributionsEngine.calculatePoisson(lambda, k)
            _uiState.update { it.copy(poissonResult = res, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Erro na distribuição de Poisson.") }
        }
    }

    fun loadPoissonExample() {
        _uiState.update {
            it.copy(poissonLambda = "4.5", poissonK = "2", errorMessage = null)
        }
        calculatePoisson()
    }

    // Handlers Binomial
    fun onBinomialNChanged(v: String) = _uiState.update { it.copy(binomialN = v, errorMessage = null) }
    fun onBinomialKChanged(v: String) = _uiState.update { it.copy(binomialK = v, errorMessage = null) }
    fun onBinomialPChanged(v: String) = _uiState.update { it.copy(binomialP = v, errorMessage = null) }

    fun calculateBinomial() {
        val n = _uiState.value.binomialN.toIntOrNull()
        val k = _uiState.value.binomialK.toIntOrNull()
        val p = _uiState.value.binomialP.replace(",", ".").toDoubleOrNull()

        if (n == null || k == null || p == null) {
            _uiState.update { it.copy(errorMessage = "Preencha n, k e p com valores válidos.") }
            return
        }

        try {
            val res = DistributionsEngine.calculateBinomial(n, k, p)
            _uiState.update { it.copy(binomialResult = res, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Erro na distribuição Binomial.") }
        }
    }

    fun loadBinomialExample() {
        _uiState.update {
            it.copy(binomialN = "10", binomialK = "3", binomialP = "0.5", errorMessage = null)
        }
        calculateBinomial()
    }
}