package com.nabilnazar.offlinefirstapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabilnazar.offlinefirstapp.data.db.CalculationEntity
import com.nabilnazar.offlinefirstapp.data.repository.CalculationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CalculationRepository
) : ViewModel() {

    // Exposing calculation history as a StateFlow for UI to observe
    val calculationHistory: StateFlow<List<CalculationEntity>> =
        repository.allCalculations.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Function to add a new calculation
    fun addCalculation(input1: Double, input2: Double, result: Double) {
        viewModelScope.launch {
            repository.addCalculation(input1, input2, result)
        }
    }

    // Function to sync data with remote
    fun syncData() {
        viewModelScope.launch {
            repository.syncWithRemote()
        }
    }

    // Subscribe to realtime updates
    init {
        viewModelScope.launch {
            repository.subscribeToRealtimeUpdates()
        }
    }
}