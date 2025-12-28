package com.checktracker.domain.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.russian.RussianTextRecognizerOptions

actual class OcrProcessor {
    private val recognizer = TextRecognition.getClient(RussianTextRecognizerOptions.Builder().build())
    
    actual suspend fun recognizeText(imageData: ByteArray): String {
        return try {
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            val image = InputImage.fromBitmap(bitmap, 0)
            
            val task = recognizer.process(image)
            var result = ""
            
            task.addOnSuccessListener { visionText ->
                result = visionText.text
            }
            
            task.addOnFailureListener { e ->
                e.printStackTrace()
            }
            
            // Ждём завершения
            while (result.isEmpty() && !task.isComplete) {
                Thread.sleep(10)
            }
            
            result
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    
    actual fun release() {
        recognizer.close()
    }
}

actual class CameraProvider {
    actual fun requestCameraPermission(): Boolean {
        // Реализуется в Activity
        return true
    }
    
    actual fun hasCameraPermission(): Boolean {
        // Реализуется в Activity
        return true
    }
}
