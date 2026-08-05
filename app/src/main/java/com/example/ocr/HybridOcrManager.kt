package com.example.ocr

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.model.OcrEngineMode
import com.example.model.OcrResult
import com.example.security.ApiKeyManager
import com.example.settings.NetworkMonitor
import com.example.settings.SettingsManager
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Intelligent Hybrid OCR Router for CardsApp.
 * Routes OCR tasks based on user preferences, network status, API key availability,
 * and automatically falls back to on-device OCR when cloud engines timeout or fail.
 */
class HybridOcrManager(
    private val context: Context,
    private val settingsManager: SettingsManager,
    private val networkMonitor: NetworkMonitor
) {
    private val googleVisionLlmEngine = GoogleVisionLlmEngine()
    private val cloudVisionOcrEngine = CloudVisionOcrEngine()
    private val mlKitOcrEngine = MlKitOcrEngine()
    private val paddleOcrEngine = PaddleOcrEngine()
    private val tesseractOcrEngine = TesseractOcrEngine()

    /**
     * Executes OCR processing on a captured card image.
     */
    suspend fun processCardImage(image: Bitmap): OcrResult {
        val settings = settingsManager.settingsState.value
        val isOnline = networkMonitor.isOnline.value
        val isStrongInternet = networkMonitor.isStrongConnection.value
        val hasCloudKey = ApiKeyManager.hasValidCloudKey()

        val shouldAttemptCloud = when (settings.engineMode) {
            OcrEngineMode.FORCE_CLOUD -> true
            OcrEngineMode.FORCE_OFFLINE -> false
            OcrEngineMode.AUTO -> settings.onlineAiMode && isOnline && hasCloudKey
        }

        if (shouldAttemptCloud) {
            Log.d(TAG, "Attempting Priority 1 Cloud OCR (Google Vision LLM)...")
            try {
                // Set 6.5s timeout for cloud response to keep UX snappy
                val cloudResult = withTimeoutOrNull(6500) {
                    googleVisionLlmEngine.process(image)
                }

                if (cloudResult != null) {
                    Log.d(TAG, "Cloud OCR successful with engine: ${cloudResult.engineUsed}")
                    return cloudResult
                } else {
                    Log.w(TAG, "Cloud OCR timed out (>6.5s). Falling back to on-device OCR.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cloud OCR failed: ${e.message}. Falling back to on-device OCR.", e)
            }
        }

        // --- FALLBACK TO ON-DEVICE OCR ---
        Log.d(TAG, "Executing On-Device Offline OCR (Google ML Kit)...")
        val fallbackToastMsg = if (!settings.onlineAiMode) {
            "Processed in Offline Mode"
        } else if (!hasCloudKey) {
            "Switched to Offline OCR"
        } else if (!isOnline) {
            "Switched to Offline OCR (No Internet)"
        } else {
            "Switched to Offline OCR"
        }

        return try {
            val mlKitResult = mlKitOcrEngine.process(image)
            mlKitResult.copy(
                isFallback = true,
                statusMessage = mlKitResult.statusMessage ?: fallbackToastMsg
            )
        } catch (e: Exception) {
            Log.e(TAG, "ML Kit OCR failed, using PaddleOCR fallback", e)
            val paddleResult = paddleOcrEngine.process(image)
            paddleResult.copy(
                isFallback = true,
                statusMessage = fallbackToastMsg
            )
        }
    }

    /**
     * Executes ML Kit OCR directly on actual gallery image Uri.
     */
    suspend fun processCardImageUri(uri: Uri, fallbackBitmap: Bitmap): OcrResult {
        val settings = settingsManager.settingsState.value
        val isOnline = networkMonitor.isOnline.value
        val hasCloudKey = ApiKeyManager.hasValidCloudKey()

        val shouldAttemptCloud = when (settings.engineMode) {
            OcrEngineMode.FORCE_CLOUD -> true
            OcrEngineMode.FORCE_OFFLINE -> false
            OcrEngineMode.AUTO -> settings.onlineAiMode && isOnline && hasCloudKey
        }

        if (shouldAttemptCloud) {
            Log.d(TAG, "Attempting Priority 1 Cloud OCR on gallery image...")
            try {
                val cloudResult = withTimeoutOrNull(6500) {
                    googleVisionLlmEngine.process(fallbackBitmap)
                }
                if (cloudResult != null && !cloudResult.rawText.isNullOrBlank()) {
                    return cloudResult
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cloud OCR on gallery image failed: ${e.message}", e)
            }
        }

        Log.d(TAG, "Executing ML Kit OCR on actual gallery Uri: $uri")
        return try {
            val mlKitUriResult = mlKitOcrEngine.processUri(context, uri)
            if (mlKitUriResult.rawText.isNotBlank()) {
                mlKitUriResult
            } else {
                mlKitOcrEngine.process(fallbackBitmap)
            }
        } catch (e: Exception) {
            Log.e(TAG, "ML Kit Uri processing failed, falling back to bitmap OCR", e)
            mlKitOcrEngine.process(fallbackBitmap)
        }
    }

    /**
     * Merges results if multi-pass or verification layer is run.
     */
    fun mergeOcrResults(primary: OcrResult, secondary: OcrResult): OcrResult {
        return primary.copy(
            name = primary.name ?: secondary.name,
            title = primary.title ?: secondary.title,
            company = primary.company ?: secondary.company,
            phone = primary.phone ?: secondary.phone,
            email = primary.email ?: secondary.email,
            website = primary.website ?: secondary.website,
            address = primary.address ?: secondary.address
        )
    }

    companion object {
        private const val TAG = "HybridOcrManager"
    }
}
