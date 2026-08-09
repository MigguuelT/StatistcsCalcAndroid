package com.miguel.statscalculator.presentation.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.miguel.statscalculator.data.local.AppDatabase
import com.miguel.statscalculator.data.repository.HistoryRepositoryImpl
import com.miguel.statscalculator.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HistoryRepositoryImpl(db.calculationDao())
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            repository.getAllHistory().collect { list ->
                _uiState.update { it.copy(items = list) }
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}