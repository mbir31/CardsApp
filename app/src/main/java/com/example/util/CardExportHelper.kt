package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.core.content.FileProvider
import com.example.db.CardEntity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream

object CardExportHelper {

    fun generateVCardString(card: CardEntity): String {
        return buildString {
            appendLine("BEGIN:VCARD")
            appendLine("VERSION:3.0")
            card.name?.let { if (it.isNotBlank()) appendLine("N:;$it;;;") }
            card.name?.let { if (it.isNotBlank()) appendLine("FN:$it") }
            card.company?.let { if (it.isNotBlank()) appendLine("ORG:$it") }
            card.title?.let { if (it.isNotBlank()) appendLine("TITLE:$it") }
            card.phone?.let { if (it.isNotBlank()) appendLine("TEL;TYPE=CELL:$it") }
            card.email?.let { if (it.isNotBlank()) appendLine("EMAIL;TYPE=INTERNET:$it") }
            card.website?.let { if (it.isNotBlank()) appendLine("URL:$it") }
            card.address?.let { if (it.isNotBlank()) appendLine("ADR;TYPE=WORK:;;$it;;;;") }
            appendLine("END:VCARD")
        }
    }

    fun generateCsvString(card: CardEntity): String {
        return buildString {
            appendLine("Name,Title,Company,Phone,Email,Website,Address,Category")
            fun escape(s: String?): String {
                val text = s ?: ""
                return "\"${text.replace("\"", "\"\"")}\""
            }
            appendLine(
                listOf(
                    escape(card.name),
                    escape(card.title),
                    escape(card.company),
                    escape(card.phone),
                    escape(card.email),
                    escape(card.website),
                    escape(card.address),
                    escape(card.category)
                ).joinToString(",")
            )
        }
    }

    fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareVCard(context: Context, card: CardEntity) {
        val vcardText = generateVCardString(card)
        val file = File(context.cacheDir, "contact_${card.id}.vcf")
        file.writeText(vcardText)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/x-vcard"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Contact Card: ${card.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share vCard (.vcf)"))
    }

    fun shareCsv(context: Context, card: CardEntity) {
        val csvText = generateCsvString(card)
        val file = File(context.cacheDir, "contact_${card.id}.csv")
        file.writeText(csvText)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Contact Data: ${card.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share CSV"))
    }

    fun shareScannedImage(context: Context, card: CardEntity): Boolean {
        val imagePath = card.imagePath ?: return false
        val file = File(imagePath)
        if (!file.exists()) return false
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Business Card Image: ${card.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Scanned Card Image"))
        return true
    }

    fun shareQrCode(context: Context, card: CardEntity) {
        val vcardText = generateVCardString(card)
        val bitmap = generateQrBitmap(vcardText, 600) ?: return
        val file = File(context.cacheDir, "qr_${card.id}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "QR Code: ${card.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share QR Code"))
    }
}
