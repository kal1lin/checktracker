package com.checktracker.data.repository

import com.checktracker.data.model.Expense
import com.checktracker.data.model.ExpenseCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ExpenseRepositoryTest {
    
    private val repository = InMemoryExpenseRepository()
    
    @Test
    fun testAddExpense() = runBlocking {
        val expense = Expense(
            amount = -500.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Магнит",
            category = "Еда",
            rawText = "Test"
        )
        
        val id = repository.addExpense(expense)
        assertTrue(id > 0)
        
        val retrieved = repository.getExpenseById(id).first()
        assertNotNull(retrieved)
        assertEquals("Магнит", retrieved.merchant)
    }
    
    @Test
    fun testGetAllExpenses() = runBlocking {
        val expense1 = Expense(
            amount = -300.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Магнит",
            category = "Еда",
            rawText = "Test 1"
        )
        val expense2 = Expense(
            amount = -500.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Лукойл",
            category = "Транспорт",
            rawText = "Test 2"
        )
        
        repository.addExpense(expense1)
        repository.addExpense(expense2)
        
        val all = repository.getAllExpenses().first()
        assertEquals(2, all.size)
    }
    
    @Test
    fun testUpdateExpense() = runBlocking {
        val expense = Expense(
            amount = -100.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Магнит",
            category = "Еда",
            rawText = "Test"
        )
        
        val id = repository.addExpense(expense)
        val updated = expense.copy(id = id, amount = -200.0)
        repository.updateExpense(updated)
        
        val retrieved = repository.getExpenseById(id).first()
        assertEquals(-200.0, retrieved?.amount)
    }
    
    @Test
    fun testDeleteExpense() = runBlocking {
        val expense = Expense(
            amount = -100.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Магнит",
            category = "Еда",
            rawText = "Test"
        )
        
        val id = repository.addExpense(expense)
        repository.deleteExpense(id)
        
        val retrieved = repository.getExpenseById(id).first()
        assertEquals(null, retrieved)
    }
    
    @Test
    fun testGetExpensesByDateRange() = runBlocking {
        val expense1 = Expense(
            amount = -100.0,
            date = LocalDate(2025, 12, 27),
            merchant = "Магнит",
            category = "Еда",
            rawText = "Test 1"
        )
        val expense2 = Expense(
            amount = -200.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Лукойл",
            category = "Транспорт",
            rawText = "Test 2"
        )
        val expense3 = Expense(
            amount = -300.0,
            date = LocalDate(2025, 12, 29),
            merchant = "Икеа",
            category = "Дом",
            rawText = "Test 3"
        )
        
        repository.addExpense(expense1)
        repository.addExpense(expense2)
        repository.addExpense(expense3)
        
        val range = repository.getExpensesByDateRange(
            LocalDate(2025, 12, 28),
            LocalDate(2025, 12, 29)
        ).first()
        
        assertEquals(2, range.size)
    }
    
    @Test
    fun testGetMonthlySum() = runBlocking {
        val expense1 = Expense(
            amount = -500.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Магнит",
            category = "Еда",
            rawText = "Test 1"
        )
        val expense2 = Expense(
            amount = -1000.0,
            date = LocalDate(2025, 12, 25),
            merchant = "Лукойл",
            category = "Транспорт",
            rawText = "Test 2"
        )
        
        repository.addExpense(expense1)
        repository.addExpense(expense2)
        
        val sum = repository.getMonthlySum(2025, 12).first()
        assertEquals(-1500.0, sum)
    }
    
    @Test
    fun testGetDailySum() = runBlocking {
        val expense1 = Expense(
            amount = -300.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Магнит",
            category = "Еда",
            rawText = "Test 1"
        )
        val expense2 = Expense(
            amount = -200.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Лукойл",
            category = "Транспорт",
            rawText = "Test 2"
        )
        
        repository.addExpense(expense1)
        repository.addExpense(expense2)
        
        val sum = repository.getDailySum(LocalDate(2025, 12, 28)).first()
        assertEquals(-500.0, sum)
    }
    
    @Test
    fun testIncomeNotCounted() = runBlocking {
        val income = Expense(
            amount = 50000.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Работодатель",
            category = "Доход",
            rawText = "Зарплата",
            isIncome = true
        )
        val expense = Expense(
            amount = -500.0,
            date = LocalDate(2025, 12, 28),
            merchant = "Магнит",
            category = "Еда",
            rawText = "Test"
        )
        
        repository.addExpense(income)
        repository.addExpense(expense)
        
        val dailySum = repository.getDailySum(LocalDate(2025, 12, 28)).first()
        assertEquals(-500.0, dailySum)
    }
}
