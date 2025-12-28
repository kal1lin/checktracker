package com.checktracker.domain.parser

import com.checktracker.data.model.ExpenseCategory
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ReceiptParserTest {
    
    @Test
    fun testExtractAmount_ValidFormat() {
        val receipt = """
            МАГНИТ
            Яйца — 120.00 ₽
            Молоко — 85.50 ₽
            ИТОГО: 1250.00 ₽
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        assertEquals(1250.0, parsed.amount)
    }
    
    @Test
    fun testExtractAmount_WithComma() {
        val receipt = """
            КО ПЛАТЕЖУ: 850,75 ₽
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        assertEquals(850.75, parsed.amount)
    }
    
    @Test
    fun testExtractDate_StandardFormat() {
        val receipt = """
            28.12.2025
            МАГНИТ
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        assertEquals(28, parsed.date.dayOfMonth)
        assertEquals(12, parsed.date.monthNumber)
        assertEquals(2025, parsed.date.year)
    }
    
    @Test
    fun testExtractDate_WithSlashes() {
        val receipt = """
            28/12/2025
            ЛЕНТА
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        assertEquals(28, parsed.date.dayOfMonth)
        assertEquals(12, parsed.date.monthNumber)
        assertEquals(2025, parsed.date.year)
    }
    
    @Test
    fun testRecognizeMerchant_Magnit() {
        val receipt = """
            МАГНИТ
            Яйца — 120.00 ₽
            ИТОГО: 500.00 ₽
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        assertEquals("Магнит", parsed.merchant)
        assertEquals(ExpenseCategory.FOOD, parsed.category)
    }
    
    @Test
    fun testRecognizeMerchant_Paterochka() {
        val receipt = """
            ПЯТЁРОЧКА
            Хлеб — 50.00 ₽
            ИТОГО: 300.00 ₽
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        assertEquals("Пятёрочка", parsed.merchant)
        assertEquals(ExpenseCategory.FOOD, parsed.category)
    }
    
    @Test
    fun testCategoryDetection_Transport() {
        val receipt = """
            ЛУКОЙЛ
            28.12.2025
            Бензин 92 — 2000.00 ₽
            ИТОГО: 2000.00 ₽
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        assertEquals(ExpenseCategory.TRANSPORT, parsed.category)
    }
    
    @Test
    fun testCategoryDetection_Home() {
        val receipt = """
            LEROY MERLIN
            28.12.2025
            Краска — 1500.00 ₽
            ИТОГО: 1500.00 ₽
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        assertEquals(ExpenseCategory.HOME, parsed.category)
    }
    
    @Test
    fun testConfidenceCalculation() {
        val receipt = """
            МАГНИТ
            28.12.2025
            ИТОГО: 500.00 ₽
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        assertNotEquals(0f, parsed.confidence)
        assertEquals(true, parsed.confidence > 0)
    }
    
    @Test
    fun testFullReceiptParsing_RealExample() {
        val receipt = """
            МАГНИТ
            ул. Пушкина, 10
            
            Хлеб черный          89.90 ₽
            Молоко 2.5%         120.50 ₽
            Яйца (10 шт)         95.00 ₽
            Масло сливочное     285.00 ₽
            
            ИТОГО:              590.40 ₽
            
            28.12.2025  14:23
            Спасибо за покупку!
        """.trimIndent()
        
        val parsed = ReceiptParser.parseReceipt(receipt)
        
        assertEquals(590.40, parsed.amount)
        assertEquals("Магнит", parsed.merchant)
        assertEquals(ExpenseCategory.FOOD, parsed.category)
        assertEquals(28, parsed.date.dayOfMonth)
    }
    
    @Test
    fun testEmptyReceipt() {
        val receipt = ""
        val parsed = ReceiptParser.parseReceipt(receipt)
        
        assertEquals(0.0, parsed.amount)
        assertEquals("Неизвестный магазин", parsed.merchant)
        assertEquals(ExpenseCategory.OTHER, parsed.category)
    }
    
    @Test
    fun testCaseInsensitivity() {
        val receipt1 = "ИТОГО: 1000.00 ₽"
        val receipt2 = "итого: 1000.00 ₽"
        
        val parsed1 = ReceiptParser.parseReceipt(receipt1)
        val parsed2 = ReceiptParser.parseReceipt(receipt2)
        
        assertEquals(parsed1.amount, parsed2.amount)
    }
}
