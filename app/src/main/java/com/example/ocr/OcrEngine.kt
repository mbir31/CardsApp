package com.example.ocr

import android.graphics.Bitmap
import com.example.model.OcrResult

/**
 * Abstraction layer for hybrid OCR processing engines in CardsApp.
 */
interface OcrEngine {
    val engineName: String
    suspend fun process(image: Bitmap): OcrResult
}
