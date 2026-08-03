package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val title: String?,
    val company: String?,
    val phone: String?,
    val email: String?,
    val website: String?,
    val address: String?,
    val rawText: String,
    val detectedLanguage: String, // "English", "Bangla", "Bilingual"
    val engineUsed: String,
    val confidenceScore: Float,
    val imagePath: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isSyncedToContacts: Boolean = false,
    val isSyncedToDrive: Boolean = false,
    val isStarred: Boolean = false,
    val category: String = "Professional"
)
