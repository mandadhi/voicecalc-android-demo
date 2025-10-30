package com.mandadhi.voicecalc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mandadhi.voicecalc.db.AppDatabase
import com.mandadhi.voicecalc.db.CalculationRecord
import com.mandadhi.voicecalc.repo.CalculationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val repo: CalculationRepository
    val history = MutableStateFlow<List<CalculationRecord>>(emptyList())

    init {
        val db = AppDatabase.get(application)
        repo = CalculationRepository(db.calculationDao())
        viewModelScope.launch {
            repo.all().collect { list -> history.value = list }
        }
    }

    fun saveCalculation(input: String, parsed: String, result: String) {
        viewModelScope.launch { repo.insert(CalculationRecord(inputText = input, parsedExpression = parsed, resultText = result)) }
    }

    fun clearHistory() {
        viewModelScope.launch { repo.clear() }
    }
}
