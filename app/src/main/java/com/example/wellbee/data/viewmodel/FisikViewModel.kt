package com.example.wellbee.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wellbee.data.FisikRepository
import com.example.wellbee.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FisikViewModel(private val repository: FisikRepository) : ViewModel() {

    private val _sportList = MutableStateFlow<List<SportHistory>>(emptyList())
    val sportList: StateFlow<List<SportHistory>> = _sportList

    private val _sportChart = MutableStateFlow<FisikRepository.WeeklyChartData?>(null)
    val sportChart: StateFlow<FisikRepository.WeeklyChartData?> = _sportChart

    private val _sleepList = MutableStateFlow<List<SleepData>>(emptyList())
    val sleepList: StateFlow<List<SleepData>> = _sleepList

    private val _sleepChart = MutableStateFlow<FisikRepository.WeeklyChartData?>(null)
    val sleepChart: StateFlow<FisikRepository.WeeklyChartData?> = _sleepChart

    private val _weightList = MutableStateFlow<List<WeightData>>(emptyList())
    val weightList: StateFlow<List<WeightData>> = _weightList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadAllData() {
        loadSportData()
        loadSleepData()
        loadWeightData()
    }

    fun loadSportData() {
        viewModelScope.launch {
            _isLoading.value = true

            val resList = repository.getSportHistory()
            if (resList.isSuccess) {
                _sportList.value = resList.getOrDefault(emptyList())
            }

            val resChart = repository.getWeeklySportChartData()
            _sportChart.value = resChart

            _isLoading.value = false
        }
    }

    fun loadSleepData() {
        viewModelScope.launch {
            _isLoading.value = true

            val resList = repository.getSleepHistory()
            if (resList.isSuccess) {
                _sleepList.value = resList.getOrDefault(emptyList())
            }
            val resChart = repository.getWeeklySleepChartData()
            _sleepChart.value = resChart

            _isLoading.value = false
        }
    }

    fun loadWeightData() {
        viewModelScope.launch {
            _isLoading.value = true
            val resList = repository.getWeightHistory()
            if (resList.isSuccess) {
                _weightList.value = resList.getOrDefault(emptyList())
            }
            _isLoading.value = false
        }
    }

    fun catatOlahraga(req: SportRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.catatOlahraga(req)
            _isLoading.value = false

            if (result.isSuccess) {
                loadSportData() // Refresh data otomatis biar grafik naik
                onSuccess()
            } else {
                _errorMessage.value = "Gagal: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun catatTidur(req: SleepRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.catatTidur(req)
            _isLoading.value = false

            if (result.isSuccess) {
                loadSleepData()
                onSuccess()
            } else {
                _errorMessage.value = "Gagal: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun catatBerat(req: WeightRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.catatWeight(req)
            _isLoading.value = false

            if (result.isSuccess) {
                loadWeightData()
                onSuccess()
            } else {
                _errorMessage.value = "Gagal: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}

class FisikViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FisikViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FisikViewModel(FisikRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}