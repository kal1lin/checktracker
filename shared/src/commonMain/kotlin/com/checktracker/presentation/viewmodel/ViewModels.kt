package com.checktracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.checktracker.data.model.Expense
import com.checktracker.data.model.ExpenseCategory
import com.checktracker.data.repository.ExpenseRepository
import com.checktracker.domain.parser.ReceiptParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class DashboardState(
    val expenses: List<Expense> = emptyList(),
    val monthlySum: Double = 0.0,
    val dailySum: Double = 0.0,
    val categoryStats: Map<ExpenseCategory, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val selectedDate: LocalDate = LocalDate(2025, 12, 28)
)

class DashboardViewModel(private val repository: ExpenseRepository) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    init {
        loadDashboard()
    }
    
    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.getAllExpenses().collect { expenses ->
                    val monthlySum = expenses
                        .filter { !it.isIncome && it.date.monthNumber == _state.value.selectedDate.monthNumber }
                        .sumOf { it.amount }
                    
                    val dailySum = expenses
                        .filter { !it.isIncome && it.date == _state.value.selectedDate }
                        .sumOf { it.amount }
                    
                    val categoryStats = expenses
                        .filter { !it.isIncome }
                        .groupBy { it.category }
                        .mapValues { (_, list) -> list.sumOf { kotlin.math.abs(it.amount) } }
                        .mapKeys { (category, _) -> 
                            ExpenseCategory.values().find { it.displayName == category } ?: ExpenseCategory.OTHER
                        }
                    
                    _state.value = _state.value.copy(
                        expenses = expenses,
                        monthlySum = monthlySum,
                        dailySum = dailySum,
                        categoryStats = categoryStats,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
    
    fun selectDate(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
        loadDashboard()
    }
}

data class CameraState(
    val recognizedText: String = "",
    val isProcessing: Boolean = false,
    val error: String? = null
)

class CameraViewModel(private val repository: ExpenseRepository) : ViewModel() {
    
    private val _state = MutableStateFlow(CameraState())
    val state: StateFlow<CameraState> = _state.asStateFlow()
    
    fun processReceiptText(rawText: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                recognizedText = rawText,
                isProcessing = true
            )
        }
    }
    
    fun saveExpense(rawText: String) {
        viewModelScope.launch {
            try {
                val parsed = ReceiptParser.parseReceipt(rawText)
                val expense = Expense(
                    amount = -parsed.amount,
                    date = parsed.date,
                    merchant = parsed.merchant,
                    category = parsed.category.displayName,
                    rawText = parsed.rawText
                )
                repository.addExpense(expense)
                _state.value = _state.value.copy(isProcessing = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isProcessing = false
                )
            }
        }
    }
}
