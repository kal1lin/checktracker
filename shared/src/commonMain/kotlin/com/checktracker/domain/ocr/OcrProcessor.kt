package com.checktracker.domain.ocr

import com.checktracker.data.model.Expense

/**
 * Интерфейс для OCR обработки
 * Реализован отдельно для Android и iOS
 */
expect class OcrProcessor {
    suspend fun recognizeText(imageData: ByteArray): String
    fun release()
}

/**
 * Интерфейс для камеры
 */
expect class CameraProvider {
    fun requestCameraPermission(): Boolean
    fun hasCameraPermission(): Boolean
}
