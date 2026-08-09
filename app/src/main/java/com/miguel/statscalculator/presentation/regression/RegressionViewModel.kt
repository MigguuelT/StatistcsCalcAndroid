package com.miguel.statscalculator.presentation.regression

import androidx.lifecycle.ViewModel
import com.miguel.statscalculator.core.math.RegressionEngine
import com.miguel.statscalculator.core.util.DataParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegressionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegressionUiState())
    val uiState: StateFlow<RegressionUiState> = _uiState.asStateFlow()

    fun onInputXChanged(text: String) {
        _uiState.update { it.copy(inputTextX = text, errorMessage = null) }
    }

    fun onInputYChanged(text: String) {
        _uiState.update { it.copy(inputTextY = text, errorMessage = null) }
    }

    fun onPredictXChanged(text: String) {
        val xVal = text.toDoubleOrNull()
        val result = _uiState.value.result
        val predY = if (xVal != null && result != null) {
            result.intercept + result.slope * xVal
        } else null

        _uiState.update {
            it.copy(predictXInput = text, predictedY = predY)
        }
    }

    fun calculate() {
        val points = DataParser.parsePairedList(_uiState.value.inputTextX, _uiState.value.inputTextY)

        if (points.size < 2) {
            _uiState.update {
                it.copy(
                    errorMessage = "Insira pelo menos 2 pares de dados válidos em X e Y.",
                    result = null,
                    points = emptyList()
                )
            }
            return
        }

        try {
            val res = RegressionEngine.calculate(points)
            _uiState.update {
                it.copy(
                    result = res,
                    points = points,
                    errorMessage = null,
                    predictXInput = "",
                    predictedY = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    errorMessage = e.localizedMessage ?: "Erro ao calcular a regressão.",
                    result = null,
                    points = emptyList()
                )
            }
        }
    }

    fun loadSampleData() {
        val sampleX = "1, 2, 3, 4, 5, 6, 7, 8, 9, 10"
        val sampleY = "2.1, 3.8, 6.2, 7.5, 9.9, 12.1, 13.8, 16.2, 18.0, 20.5"
        _uiState.update {
            it.copy(
                inputTextX = sampleX,
                inputTextY = sampleY,
                errorMessage = null
            )
        }
        calculate()
    }

    fun clear() {
        _uiState.update { RegressionUiState() }
    }
}