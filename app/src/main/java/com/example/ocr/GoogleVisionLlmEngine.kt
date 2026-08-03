package com.example.ocr

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.model.OcrResult
import com.example.security.ApiKeyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Priority 1 Cloud Engine: Uses Google Vision LLM (Gemini multimodal) to parse bilingual
 * (English + Bangla) physical business cards into structured JSON data.
 */
class GoogleVisionLlmEngine : OcrEngine {

    override val engineName: String = "Google Vision LLM"

    override suspend fun process(image: Bitmap): OcrResult = withContext(Dispatchers.IO) {
        val apiKey = ApiKeyManager.getGeminiApiKey()
        if (apiKey.isEmpty() || !ApiKeyManager.hasValidCloudKey()) {
            throw IllegalStateException("Cloud API Key missing or invalid placeholder.")
        }

        // 1. Scale image down if necessary (max 1600px width/height for fast upload)
        val scaledBitmap = scaleBitmapIfNeeded(image, 1200)
        val base64Image = bitmapToBase64(scaledBitmap)

        // 2. Prepare model endpoint (using gemini-3.1-pro-preview as specified)
        val endpointUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=$apiKey"

        val prompt = """
            You are an expert OCR and document structure extraction AI. Analyze this image of a business card (which may contain English, Bangla, or both).
            Extract the following fields accurately. If name or company name is in Bangla or non-English script, provide the English phonetic transliteration/translation (e.g., 'Ariful Islam' for 'আরিফুল ইসলাম'). Every name and company field must be returned in clear, readable English script.
            Return STRICT JSON with exact keys:
            {
              "name": "Full name of the person in English (transliterate if Bangla)",
              "title": "Designation/Job Title in English",
              "company": "Company / Organization Name in English",
              "phone": "Phone number(s) formatted cleanly",
              "email": "Email address",
              "website": "Website URL",
              "address": "Physical or office address",
              "rawText": "Complete transcription of all readable text on the card",
              "detectedLanguage": "English", "Bangla", or "Bilingual",
              "logoDetected": true or false
            }
            Do not include Markdown backticks around JSON if possible, or return raw JSON directly.
        """.trimIndent()

        // Build JSON request payload
        val requestJson = JSONObject().apply {
            val contentsArr = org.json.JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArr = org.json.JSONArray().apply {
                        // Text prompt part
                        put(JSONObject().apply { put("text", prompt) })
                        // Image part
                        put(JSONObject().apply {
                            put("inlineData", JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64Image)
                            })
                        })
                    }
                    put("parts", partsArr)
                }
                put(contentObj)
            }
            put("contents", contentsArr)
            
            // System instructions & generation config
            val genConfig = JSONObject().apply {
                put("temperature", 0.1)
                put("responseMimeType", "application/json")
            }
            put("generationConfig", genConfig)
        }

        val url = URL(endpointUrl)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 7000
            readTimeout = 7000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }

        try {
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(requestJson.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                parseGeminiResponse(responseText)
            } else {
                val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e(TAG, "Gemini API HTTP Error $responseCode: $errorStream")
                throw IllegalStateException("HTTP $responseCode from Cloud Vision LLM API")
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseGeminiResponse(jsonString: String): OcrResult {
        val root = JSONObject(jsonString)
        val candidates = root.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val textPart = parts?.optJSONObject(0)?.optString("text") ?: ""

        val cleanedJson = textPart.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val parsedObj = JSONObject(cleanedJson)

        return OcrResult(
            name = parsedObj.optString("name").takeIf { it.isNotBlank() && it != "null" },
            title = parsedObj.optString("title").takeIf { it.isNotBlank() && it != "null" },
            company = parsedObj.optString("company").takeIf { it.isNotBlank() && it != "null" },
            phone = parsedObj.optString("phone").takeIf { it.isNotBlank() && it != "null" },
            email = parsedObj.optString("email").takeIf { it.isNotBlank() && it != "null" },
            website = parsedObj.optString("website").takeIf { it.isNotBlank() && it != "null" },
            address = parsedObj.optString("address").takeIf { it.isNotBlank() && it != "null" },
            rawText = parsedObj.optString("rawText").ifBlank { textPart },
            detectedLanguage = parsedObj.optString("detectedLanguage", "English"),
            confidenceScore = 0.95f,
            engineUsed = engineName,
            logoDetected = parsedObj.optBoolean("logoDetected", false),
            isFallback = false
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int
        if (width > height) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt()
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    companion object {
        private const val TAG = "GoogleVisionLlmEngine"
    }
}
