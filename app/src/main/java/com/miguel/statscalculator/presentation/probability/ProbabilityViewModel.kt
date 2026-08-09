package com.miguel.statscalculator.presentation.probability

import androidx.lifecycle.ViewModel
import com.miguel.statscalculator.core.math.CombinatoricsEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProbabilityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProbabilityUiState())
    val uiState: StateFlow<ProbabilityUiState> = _uiState.asStateFlow()

    fun onInputNChanged(value: String) {
        _uiState.update { it.copy(inputN = value, combinatoricsError = null) }
    }

    fun onInputKChanged(value: String) {
        _uiState.update { it.copy(inputK = value, combinatoricsError = null) }
    }

    fun calculateCombinatorics() {
        val n = _uiState.value.inputN.toIntOrNull()
        val k = _uiState.value.inputK.toIntOrNull()

        if (n == null) {
            _uiState.update { it.copy(combinatoricsError = "Informe um valor inteiro válido para n.") }
            return
        }

        try {
            val fact = CombinatoricsEngine.factorial(n).toString()
            val perm = CombinatoricsEngine.permutation(n).toString()

            val arr = if (k != null && k <= n) {
                CombinatoricsEngine.arrangement(n, k).toString()
            } else "--"

            val comb = if (k != null && k <= n) {
                CombinatoricsEngine.combination(n, k).toString()
            } else "--"

            _uiState.update {
                it.copy(
                    factorialResult = fact,
                    permutationResult = perm,
                    arrangementResult = arr,
                    combinationResult = comb,
                    combinatoricsError = null
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(combinatoricsError = e.localizedMessage ?: "Erro de cálculo.") }
        }
    }

    // Bayes Handlers
    fun onPriorAChanged(valStr: String) {
        _uiState.update { it.copy(inputPriorA = valStr, bayesError = null) }
    }

    fun onLikelihoodBGivenAChanged(valStr: String) {
        _uiState.update { it.copy(inputLikelihoodBGivenA = valStr, bayesError = null) }
    }

    fun onLikelihoodBGivenNotAChanged(valStr: String) {
        _uiState.update { it.copy(inputLikelihoodBGivenNotA = valStr, bayesError = null) }
    }

    fun calculateBayes() {
        val pA = _uiState.value.inputPriorA.replace(",", ".").toDoubleOrNull()
        val pBGivenA = _uiState.value.inputLikelihoodBGivenA.replace(",", ".").toDoubleOrNull()
        val pBGivenNotA = _uiState.value.inputLikelihoodBGivenNotA.replace(",", ".").toDoubleOrNull()

        if (pA == null || pBGivenA == null || pBGivenNotA == null) {
            _uiState.update { it.copy(bayesError = "Preencha todas as probabilidades com valores decimais válidos (0 a 1).") }
            return
        }

        try {
            val res = CombinatoricsEngine.bayesTheorem(pA, pBGivenA, pBGivenNotA)
            _uiState.update { it.copy(bayesResult = res, bayesError = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(bayesError = e.localizedMessage ?: "Erro no Teorema de Bayes.") }
        }
    }

    fun loadBayesExample() {
        _uiState.update {
            it.copy(
                inputPriorA = "0.01",           // P(Doença) = 1%
                inputLikelihoodBGivenA = "0.95",  // P(Teste+|Doença) = 95%
                inputLikelihoodBGivenNotA = "0.05",// P(Teste+|Saudável) = 5%
                bayesError = null
            )
        }
        calculateBayes()
    }
}