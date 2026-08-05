package com.example.model

/**
 * Data representation of structured OCR scan results from physical business cards.
 * Supports multilingual fields (English + Bangla).
 */
data class OcrResult(
    val name: String? = null,
    val title: String? = null,
    val company: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val address: String? = null,
    val rawText: String = "",
    val detectedLanguage: String = "English", // "English", "Bangla", "Bilingual"
    val confidenceScore: Float = 0.9f,
    val engineUsed: String = "Google Vision LLM", // "Google Vision LLM", "Cloud Vision", "PaddleOCR", "Tesseract"
    val logoDetected: Boolean = false,
    val isFallback: Boolean = false,
    val statusMessage: String? = null
)

/**
 * OCR Engine selection mode options.
 */
enum class OcrEngineMode {
    AUTO,
    FORCE_CLOUD,
    FORCE_OFFLINE
}

/**
 * Application Settings state model.
 */
data class OcrSettings(
    val onlineAiMode: Boolean = true,
    val engineMode: OcrEngineMode = OcrEngineMode.AUTO,
    val driveBackupEnabled: Boolean = false,
    val dailyDriveBackupEnabled: Boolean = true,
    val lastBackupTimestamp: Long = 0L,
    val isDriveConnected: Boolean = false,
    val driveAccountEmail: String? = null,
    val autoLocalBackupEnabled: Boolean = true,
    val lastLocalBackupTimestamp: Long = 0L
)
