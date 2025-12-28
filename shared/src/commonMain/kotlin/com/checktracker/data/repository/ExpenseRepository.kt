package com.checktracker.data.repository

import com.checktracker.data.model.Expense
import com.checktracker.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Repository для управления расходами
 */
interface ExpenseRepository {
    suspend fun addExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(id: Long)
    fun getExpenseById(id: Long): Flow<Expense?>
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>
    fun getMonthlySum(year: Int, month: Int): Flow<Double>
    fun getDailySum(date: LocalDate): Flow<Double>
}

/**
 * Реализация в памяти для тестирования
 */
class InMemoryExpenseRepository : ExpenseRepository {
    private val expenses = mutableMapOf<Long, Expense>()
    private var nextId = 1L
    
    override suspend fun addExpense(expense: Expense): Long {
        val id = nextId++
        val newExpense = expense.copy(id = id)
        expenses[id] = newExpense
        return id
    }
    
    override suspend fun updateExpense(expense: Expense) {
        expenses[expense.id] = expense
    }
    
    override suspend fun deleteExpense(id: Long) {
        expenses.remove(id)
    }
    
    override fun getExpenseById(id: Long): Flow<Expense?> {
        return kotlinx.coroutines.flow.flowOf(expenses[id])
    }
    
    override fun getAllExpenses(): Flow<List<Expense>> {
        return kotlinx.coroutines.flow.flowOf(expenses.values.toList())
    }
    
    override fun getExpensesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        return kotlinx.coroutines.flow.flowOf(
            expenses.values.filter { it.date in startDate..endDate }
        )
    }
    
    override fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>> {
        return kotlinx.coroutines.flow.flowOf(
            expenses.values.filter { it.category == category.displayName }
        )
    }
    
    override fun getMonthlySum(year: Int, month: Int): Flow<Double> {
        return kotlinx.coroutines.flow.flowOf(
            expenses.values
                .filter { it.date.year == year && it.date.monthNumber == month && !it.isIncome }
                .sumOf { it.amount }
        )
    }
    
    override fun getDailySum(date: LocalDate): Flow<Double> {
        return kotlinx.coroutines.flow.flowOf(
            expenses.values
                .filter { it.date == date && !it.isIncome }
                .sumOf { it.amount }
        )
    }
}
