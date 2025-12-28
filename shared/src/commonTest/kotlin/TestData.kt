/**
 * Тестовые данные для демонстрации и разработки
 */

package com.checktracker.domain.test

import com.checktracker.data.model.Expense
import com.checktracker.data.model.ExpenseCategory
import kotlinx.datetime.LocalDate

object TestReceiptData {
    
    /**
     * Реальный пример чека Магнита
     */
    val MAGNIT_RECEIPT = """
        МАГНИТ
        ул. Пушкина, д. 10
        магазин №1234
        
        Хлеб черный           89.90
        Молоко коровье 2.5% 120.50
        Яйца (10 шт)          95.00
        Масло сливочное     285.00
        Сыр голландский     450.00
        
        ИТОГО:              1040.40
        
        Дата: 28.12.2025 14:23:45
        Кассир: Иван
        Спасибо за покупку!
    """.trimIndent()
    
    /**
     * Чек заправки Лукойл
     */
    val LUKOIL_RECEIPT = """
        АЗС ЛУКОЙЛ
        Бензин АИ-92
        Объём: 45л × 53.50 ₽/л
        
        ОБЩАЯ СУММА:        2407.50 ₽
        
        28/12/2025  15:30
        Спасибо за покупку!
    """.trimIndent()
    
    /**
     * Чек ресторана
     */
    val RESTAURANT_RECEIPT = """
        КАФЕ "ЦЕНТРАЛЬНОЕ"
        
        Борщ украинский         420.00
        Стейк мраморный 350г   850.00
        Салат "Цезарь"         350.00
        Компот                 100.00
        Кофе эспрессо          150.00
        Хлеб белый              50.00
        
        ИТОГО:                1920.00 ₽
        
        28.12.2025 19:45
        Спасибо!
    """.trimIndent()
    
    /**
     * Сложный чек с ошибками OCR
     */
    val NOISY_RECEIPT = """
        [ЛОГОТИП МАГНИТА]
        
        Хлеб чёрный             ~90
        Молоко                  ~120
        Яйца домашние (10)      ~95
        
        ИТОГО: 590.40 ₽
        
        28.12.2025
        
        [РЕКЛАМА: Скидка 20% на всё!]
    """.trimIndent()
}

object TestExpenseData {
    
    val testExpenses = listOf(
        Expense(
            id = 1,
            amount = -1040.40,
            date = LocalDate(2025, 12, 28),
            merchant = "Магнит",
            category = ExpenseCategory.FOOD.displayName,
            rawText = TestReceiptData.MAGNIT_RECEIPT
        ),
        Expense(
            id = 2,
            amount = -2407.50,
            date = LocalDate(2025, 12, 28),
            merchant = "Лукойл",
            category = ExpenseCategory.TRANSPORT.displayName,
            rawText = TestReceiptData.LUKOIL_RECEIPT
        ),
        Expense(
            id = 3,
            amount = -1920.00,
            date = LocalDate(2025, 12, 28),
            merchant = "Кафе Центральное",
            category = ExpenseCategory.FOOD.displayName,
            rawText = TestReceiptData.RESTAURANT_RECEIPT
        ),
        Expense(
            id = 4,
            amount = -590.40,
            date = LocalDate(2025, 12, 27),
            merchant = "Пятёрочка",
            category = ExpenseCategory.FOOD.displayName,
            rawText = "Test"
        ),
        Expense(
            id = 5,
            amount = -3500.00,
            date = LocalDate(2025, 12, 25),
            merchant = "Икеа",
            category = ExpenseCategory.HOME.displayName,
            rawText = "Test"
        ),
        Expense(
            id = 6,
            amount = 50000.00,
            date = LocalDate(2025, 12, 24),
            merchant = "Работодатель",
            category = ExpenseCategory.INCOME.displayName,
            rawText = "Зарплата",
            isIncome = true
        )
    )
    
    fun getTotalExpenses(): Double {
        return testExpenses
            .filter { !it.isIncome }
            .sumOf { kotlin.math.abs(it.amount) }
    }
    
    fun getMonthlyExpenses(): Double {
        return testExpenses
            .filter { !it.isIncome && it.date.monthNumber == 12 }
            .sumOf { kotlin.math.abs(it.amount) }
    }
    
    fun getDailyExpenses(date: LocalDate): Double {
        return testExpenses
            .filter { !it.isIncome && it.date == date }
            .sumOf { kotlin.math.abs(it.amount) }
    }
}
