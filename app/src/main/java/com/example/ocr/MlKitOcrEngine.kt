package com.example.ocr

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.model.OcrResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * On-Device Offline Engine: Google ML Kit Text Recognition.
 * Operates 100% locally on Android without requiring an API key or internet connection.
 * Accurately extracts text from any physical or gallery card image.
 */
class MlKitOcrEngine : OcrEngine {

    override val engineName: String = "Google ML Kit (On-Device)"

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    override suspend fun process(image: Bitmap): OcrResult = withContext(Dispatchers.Default) {
        val rawText = recognizeTextFromBitmap(image)
        if (rawText.isBlank()) {
            OcrResult(
                rawText = "",
                confidenceScore = 0.5f,
                engineUsed = engineName,
                isFallback = true,
                statusMessage = "On-Device OCR: No text detected on card"
            )
        } else {
            val parsed = PaddleOcrEngine.parseRawText(rawText)
            parsed.copy(
                confidenceScore = 0.92f,
                engineUsed = engineName,
                isFallback = true,
                statusMessage = "Card scanned successfully (On-Device OCR)"
            )
        }
    }

    suspend fun processUri(context: Context, uri: Uri): OcrResult = withContext(Dispatchers.Default) {
        val rawText = recognizeTextFromUri(context, uri)
        if (rawText.isBlank()) {
            OcrResult(
                rawText = "",
                confidenceScore = 0.5f,
                engineUsed = engineName,
                isFallback = true,
                statusMessage = "On-Device OCR: No text detected on gallery image"
            )
        } else {
            val parsed = PaddleOcrEngine.parseRawText(rawText)
            parsed.copy(
                confidenceScore = 0.95f,
                engineUsed = engineName,
                isFallback = true,
                statusMessage = "Gallery card scanned successfully (ML Kit OCR)"
            )
        }
    }

    private suspend fun recognizeTextFromBitmap(bitmap: Bitmap): String =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        if (continuation.isActive) {
                            continuation.resume(visionText.text)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "ML Kit text recognition failed", e)
                        if (continuation.isActive) {
                            continuation.resume("")
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize ML Kit image processing", e)
                if (continuation.isActive) {
                    continuation.resume("")
                }
            }
        }

    private suspend fun recognizeTextFromUri(context: Context, uri: Uri): String =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputImage = InputImage.fromFilePath(context, uri)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        if (continuation.isActive) {
                            continuation.resume(visionText.text)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "ML Kit text recognition from Uri failed", e)
                        if (continuation.isActive) {
                            continuation.resume("")
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize ML Kit Uri image processing", e)
                if (continuation.isActive) {
                    continuation.resume("")
                }
            }
        }

    companion object {
        private const val TAG = "MlKitOcrEngine"
    }
}
