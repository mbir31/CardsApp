package com.example.contacts

import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.example.model.OcrResult
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Helper to export business cards to Android System Contacts.
 */
object ContactsHelper {

    private const val TAG = "ContactsHelper"

    /**
     * Inserts contact into system database directly using ContentResolver.
     */
    fun saveToSystemContacts(
        context: Context,
        ocrResult: OcrResult,
        cardImageFile: File? = null
    ): Boolean {
        try {
            val ops = ArrayList<ContentProviderOperation>()
            val rawContactInsertIndex = ops.size

            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )

            // Name
            val name = ocrResult.name ?: "Unknown Contact"
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build()
            )

            // Organization & Title
            if (!ocrResult.company.isNull_or_empty() || !ocrResult.title.isNull_or_empty()) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, ocrResult.company ?: "")
                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, ocrResult.title ?: "")
                        .build()
                )
            }

            // Phone
            if (!ocrResult.phone.isNull_or_empty()) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, ocrResult.phone)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .build()
                )
            }

            // Email
            if (!ocrResult.email.isNull_or_empty()) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, ocrResult.email)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build()
                )
            }

            // Website
            if (!ocrResult.website.isNull_or_empty()) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Website.URL, ocrResult.website)
                        .withValue(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_WORK)
                        .build()
                )
            }

            // Address
            if (!ocrResult.address.isNull_or_empty()) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, ocrResult.address)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
                        .build()
                )
            }

            // Card Image / Photo Thumbnail
            if (cardImageFile != null && cardImageFile.exists()) {
                try {
                    val bitmap = BitmapFactory.decodeFile(cardImageFile.absolutePath)
                    if (bitmap != null) {
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream)
                        val bytes = stream.toByteArray()

                        ops.add(
                            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, bytes)
                                .build()
                        )
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to attach card photo to contact", e)
                }
            }

            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save contact via ContentResolver", e)
            return false
        }
    }

    /**
     * Fallback Intent to launch Android system "Add / Edit Contact" UI.
     */
    fun createAddContactIntent(ocrResult: OcrResult): Intent {
        return Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
            type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, ocrResult.name ?: "")
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, ocrResult.title ?: "")
            putExtra(ContactsContract.Intents.Insert.COMPANY, ocrResult.company ?: "")
            putExtra(ContactsContract.Intents.Insert.PHONE, ocrResult.phone ?: "")
            putExtra(ContactsContract.Intents.Insert.EMAIL, ocrResult.email ?: "")
            putExtra(ContactsContract.Intents.Insert.POSTAL, ocrResult.address ?: "")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
}
