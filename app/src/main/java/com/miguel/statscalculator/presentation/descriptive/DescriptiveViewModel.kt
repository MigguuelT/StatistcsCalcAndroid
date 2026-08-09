package com.miguel.statscalculator.presentation.descriptive

import androidx.lifecycle.ViewModel
import com.miguel.statscalculator.core.math.DescriptiveEngine
import com.miguel.statscalculator.core.util.DataParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DescriptiveViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DescriptiveUiState())
    val uiState: StateFlow<DescriptiveUiState> = _uiState.asStateFlow()

    fun onInputTextChanged(newText: String) {
        _uiState.update { it.copy(inputText = newText, errorMessage = null) }
    }

    fun calculate() {
        val numbers = DataParser.parseSingleList(_uiState.value.inputText)
        if (numbers.isEmpty()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Insira pelo menos um número válido.",
                    result = null
                )
            }
            return
        }

        try {
            val result = DescriptiveEngine.calculate(numbers)
            _uiState.update { it.copy(result = result, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    errorMessage = e.localizedMessage ?: "Erro ao calcular.",
                    result = null
                )
            }
        }
    }

    fun loadSampleData() {
        val sample = "12, 15, 18, 22, 22, 25, 28, 30, 35, 40, 42, 50"
        _uiState.update { it.copy(inputText = sample, errorMessage = null) }
        calculate()
    }

    fun clear() {
        _uiState.update { DescriptiveUiState() }
    }
}