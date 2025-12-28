package com.checktracker.domain.parser

import com.checktracker.data.model.ExpenseCategory
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate

/**
 * Парсер данных чека с использованием Regex
 */
object ReceiptParser {
    
    // Regex для извлечения суммы
    private val amountRegex = Regex(
        """(?i)(?:итого|общая сумма|ко платежу)[:\s]+([0-9]{1,}[.,][0-9]{2})\s*₽""",
        RegexOption.IGNORE_CASE
    )
    
    // Regex для дат (DD.MM.YYYY или DD/MM/YYYY)
    private val dateRegex = Regex("""(\d{1,2})[-./](\d{1,2})[-./](\d{4})""")
    
    // Словарь магазинов → категория
    private val merchantCategoryMap = mapOf(
        "магнит" to ExpenseCategory.FOOD,
        "пятёрочка" to ExpenseCategory.FOOD,
        "лента" to ExpenseCategory.FOOD,
        "metro" to ExpenseCategory.FOOD,
        "окей" to ExpenseCategory.FOOD,
        "лукойл" to ExpenseCategory.TRANSPORT,
        "газпромнефть" to ExpenseCategory.TRANSPORT,
        "роснефть" to ExpenseCategory.TRANSPORT,
        "икеа" to ExpenseCategory.HOME,
        "leroy merlin" to ExpenseCategory.HOME,
        "baumarkt" to ExpenseCategory.HOME,
        "кино" to ExpenseCategory.ENTERTAINMENT,
        "театр" to ExpenseCategory.ENTERTAINMENT,
        "билет" to ExpenseCategory.ENTERTAINMENT
    )
    
    /**
     * Парсит текст чека и извлекает структурированные данные
     */
    fun parseReceipt(rawText: String): ParsedReceipt {
        val cleanText = rawText.toLowerCase()
        
        val amount = extractAmount(rawText)
        val date = extractDate(rawText)
        val merchant = extractMerchant(rawText)
        val category = determineCategory(merchant, cleanText)
        
        return ParsedReceipt(
            amount = amount,
            date = date,
            merchant = merchant,
            category = category,
            rawText = rawText,
            confidence = calculateConfidence(amount, date, merchant)
        )
    }
    
    private fun extractAmount(text: String): Double {
        val match = amountRegex.find(text)
        return if (match != null) {
            val amountStr = match.groupValues[1].replace(",", ".")
            amountStr.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }
    }
    
    private fun extractDate(text: String): LocalDate {
        val match = dateRegex.find(text)
        return if (match != null) {
            try {
                val day = match.groupValues[1].toInt()
                val month = match.groupValues[2].toInt()
                val year = match.groupValues[3].toInt()
                
                if (day in 1..31 && month in 1..12) {
                    "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}".toLocalDate()
                } else {
                    LocalDate(java.time.Year.now().value, 12, 28)
                }
            } catch (e: Exception) {
                LocalDate(java.time.Year.now().value, 12, 28)
            }
        } else {
            LocalDate(java.time.Year.now().value, 12, 28)
        }
    }
    
    private fun extractMerchant(text: String): String {
        // Ищем название известного магазина
        for ((merchant, _) in merchantCategoryMap) {
            if (text.contains(merchant, ignoreCase = true)) {
                return merchant.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }
        
        // Если не найдено, берём первую строку с капиталей (обычно название)
        val lines = text.split("\n")
        return lines.firstOrNull { it.isNotEmpty() } ?: "Неизвестный магазин"
    }
    
    private fun determineCategory(merchant: String, text: String): ExpenseCategory {
        val lowerMerchant = merchant.toLowerCase()
        
        // Проверяем по известным магазинам
        for ((key, category) in merchantCategoryMap) {
            if (lowerMerchant.contains(key)) {
                return category
            }
        }
        
        // Проверяем по ключевым словам в тексте
        for (category in ExpenseCategory.values()) {
            for (keyword in category.keywords) {
                if (text.contains(keyword)) {
                    return category
                }
            }
        }
        
        return ExpenseCategory.OTHER
    }
    
    private fun calculateConfidence(amount: Double, date: LocalDate, merchant: String): Float {
        var confidence = 0.5f
        
        if (amount > 0) confidence += 0.3f
        if (merchant != "Неизвестный магазин") confidence += 0.2f
        
        return minOf(confidence, 1f)
    }
}

data class ParsedReceipt(
    val amount: Double,
    val date: LocalDate,
    val merchant: String,
    val category: ExpenseCategory,
    val rawText: String,
    val confidence: Float
)
