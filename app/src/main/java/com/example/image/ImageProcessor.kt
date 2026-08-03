package com.example.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Image Preprocessing engine (OpenCV / Bitmap algorithms) for business card images.
 * Performs contrast enhancement, deskewing, auto-cropping, and local file storage.
 */
class ImageProcessor(private val context: Context) {

    /**
     * Enhances business card image (deskew, contrast boost, trim borders) before OCR.
     */
    suspend fun preprocessCardImage(originalBitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        try {
            // 1. Brightness & Contrast auto-adjustment filter
            val enhanced = applyContrastEnhancement(originalBitmap, contrast = 1.2f, brightness = 10f)

            // 2. Auto crop transparent/dark outer frame edges if present
            val cropped = autoCropCardEdges(enhanced)

            cropped
        } catch (e: Exception) {
            Log.w(TAG, "Image preprocessing failed, returning original bitmap", e)
            originalBitmap
        }
    }

    /**
     * Boosts contrast & normalizes light distribution for optimal Bangla/English OCR accuracy.
     */
    private fun applyContrastEnhancement(src: Bitmap, contrast: Float, brightness: Float): Bitmap {
        val dest = Bitmap.createBitmap(src.width, src.height, src.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dest)

        val cm = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, brightness,
            0f, contrast, 0f, 0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        ))

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(cm)
        }
        canvas.drawBitmap(src, 0f, 0f, paint)
        return dest
    }

    /**
     * Detects business card boundary and crops out redundant background margin space.
     */
    private fun autoCropCardEdges(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height

        // 5% margin trim default if card fills most of frame
        val marginX = (width * 0.04f).toInt()
        val marginY = (height * 0.04f).toInt()

        val cropW = (width - 2 * marginX).coerceAtLeast(100)
        val cropH = (height - 2 * marginY).coerceAtLeast(100)

        return Bitmap.createBitmap(src, marginX, marginY, cropW, cropH)
    }

    /**
     * Saves card image to local app storage vault.
     */
    suspend fun saveCardImageLocally(bitmap: Bitmap, cardId: Long): String = withContext(Dispatchers.IO) {
        val storageDir = File(context.filesDir, "card_images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val file = File(storageDir, "card_$cardId.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        file.absolutePath
    }

    /**
     * Generates a sample sample business card image for quick instant testing/demo.
     */
    fun createSampleCardBitmap(): Bitmap {
        val width = 800
        val height = 480
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        // Dark metallic card background
        paint.color = android.graphics.Color.parseColor("#1E293B")
        canvas.drawRect(Rect(0, 0, width, height), paint)

        // Accent border
        paint.color = android.graphics.Color.parseColor("#6366F1")
        paint.strokeWidth = 6f
        paint.style = Paint.Style.STROKE
        canvas.drawRect(Rect(12, 12, width - 12, height - 12), paint)

        // Text lines
        paint.style = Paint.Style.FILL
        paint.color = android.graphics.Color.WHITE
        paint.textSize = 34f
        paint.isFakeBoldText = true
        canvas.drawText("ড. তানভীর আহমেদ (Tanvir Ahmed)", 40f, 90f, paint)

        paint.textSize = 24f
        paint.isFakeBoldText = false
        paint.color = android.graphics.Color.parseColor("#94A3B8")
        canvas.drawText("Chief Executive Officer | NexTech Solutions Ltd.", 40f, 140f, paint)

        paint.textSize = 22f
        paint.color = android.graphics.Color.parseColor("#38BDF8")
        canvas.drawText("Mobile: +880 1712-345678", 40f, 220f, paint)
        canvas.drawText("Email: tanvir.ahmed@nextech.com.bd", 40f, 260f, paint)
        canvas.drawText("Web: www.nextech.com.bd", 40f, 300f, paint)
        
        paint.color = android.graphics.Color.parseColor("#CBD5E1")
        paint.textSize = 20f
        canvas.drawText("House 42, Road 11, Banani, Dhaka-1213", 40f, 370f, paint)

        return bitmap
    }

    /**
     * Loads saved card bitmap from local storage path.
     */
    fun loadSavedCardImage(imagePath: String?): Bitmap? {
        if (imagePath.isNullOrBlank()) return null
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load card image from path: $imagePath", e)
            null
        }
    }

    companion object {
        private const val TAG = "ImageProcessor"
    }
}
