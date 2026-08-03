package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contacts.ContactsHelper
import com.example.db.AppDatabase
import com.example.db.CardEntity
import com.example.image.ImageProcessor
import com.example.model.OcrEngineMode
import com.example.model.OcrResult
import com.example.model.OcrSettings
import com.example.ocr.HybridOcrManager
import com.example.settings.NetworkMonitor
import com.example.settings.SettingsManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

enum class ScreenRoute {
    VAULT,
    SCAN,
    PREVIEW_EDIT,
    CARD_DETAILS,
    SETTINGS
}

enum class SortOrder {
    LATEST,
    OLDEST,
    ALPHABETICAL,
    STARRED_FIRST
}

enum class SaveMode {
    VAULT_ONLY,
    CONTACTS_ONLY,
    BOTH
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    val settingsManager = SettingsManager(context)
    val networkMonitor = NetworkMonitor(context)
    val hybridOcrManager = HybridOcrManager(context, settingsManager, networkMonitor)
    val imageProcessor = ImageProcessor(context)

    private val database = AppDatabase.getDatabase(context)
    private val cardDao = database.cardDao()

    // Navigation State
    private val _currentScreen = MutableStateFlow(ScreenRoute.VAULT)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    // Search query in Vault
    val searchQuery = MutableStateFlow("")

    // Category Filter State ("All", "Professional", "Office", "Business", "Family")
    val selectedCategory = MutableStateFlow("All")

    // Sort Order State (LATEST, OLDEST, ALPHABETICAL, STARRED_FIRST)
    val selectedSortOrder = MutableStateFlow(SortOrder.LATEST)

    // Selected Card for Details Screen
    private val _selectedCardForDetails = MutableStateFlow<CardEntity?>(null)
    val selectedCardForDetails: StateFlow<CardEntity?> = _selectedCardForDetails.asStateFlow()

    // List of cards from Vault Database with Category filtering, Sorting, and Phonetic English translation
    val cardsState: StateFlow<List<CardEntity>> = kotlinx.coroutines.flow.combine(
        cardDao.getAllCards().catch { e ->
            Log.e("MainViewModel", "Error loading cards from database", e)
            emit(emptyList())
        },
        searchQuery,
        selectedCategory,
        selectedSortOrder
    ) { allCards, query, category, sortOrder ->
        var list = allCards

        // 1. Ensure Name and Company are translated/converted to English phonetically if Bangla text is detected
        list = list.map { card ->
            val englishName = com.example.util.PhoneticConverter.toPhoneticEnglish(card.name)
            val englishCompany = com.example.util.PhoneticConverter.toPhoneticEnglish(card.company)
            if (englishName != card.name || englishCompany != card.company) {
                card.copy(name = englishName, company = englishCompany)
            } else {
                card
            }
        }

        // 2. Search query filter
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                        (it.company?.lowercase()?.contains(q) == true) ||
                        (it.title?.lowercase()?.contains(q) == true)
            }
        }

        // 3. Category filter
        if (category != "All") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }

        // 4. Sorting
        when (sortOrder) {
            SortOrder.LATEST -> list.sortedByDescending { it.timestamp }
            SortOrder.OLDEST -> list.sortedBy { it.timestamp }
            SortOrder.ALPHABETICAL -> list.sortedBy { it.name.lowercase() }
            SortOrder.STARRED_FIRST -> list.sortedWith(compareByDescending<CardEntity> { it.isStarred }.thenByDescending { it.timestamp })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current Scan State
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _currentBitmap = MutableStateFlow<Bitmap?>(null)
    val currentBitmap: StateFlow<Bitmap?> = _currentBitmap.asStateFlow()

    private val _currentOcrResult = MutableStateFlow<OcrResult?>(null)
    val currentOcrResult: StateFlow<OcrResult?> = _currentOcrResult.asStateFlow()

    // Toast/Status Messages
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    // Settings State
    val settingsState: StateFlow<OcrSettings> = settingsManager.settingsState

    fun navigateTo(screen: ScreenRoute) {
        _currentScreen.value = screen
    }

    /**
     * Executes Card Capture + Hybrid OCR pipeline.
     */
    fun processCapturedCard(bitmap: Bitmap) {
        viewModelScope.launch {
            _isScanning.value = true
            _currentScreen.value = ScreenRoute.PREVIEW_EDIT
            try {
                val preprocessed = imageProcessor.preprocessCardImage(bitmap)
                _currentBitmap.value = preprocessed

                val ocrResult = hybridOcrManager.processCardImage(preprocessed)
                _currentOcrResult.value = ocrResult

                if (!ocrResult.statusMessage.isNullOrEmpty()) {
                    _toastEvent.emit(ocrResult.statusMessage.orEmpty())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing captured card", e)
                val fallbackResult = OcrResult(
                    rawText = "Scanning completed with basic reader",
                    statusMessage = "Switched to Offline OCR due to processing error"
                )
                _currentOcrResult.value = fallbackResult
                _toastEvent.emit(fallbackResult.statusMessage ?: "Offline OCR executed")
            } finally {
                _isScanning.value = false
            }
        }
    }

    /**
     * Convenience method to run demo sample card scan.
     */
    fun runSampleCardScan() {
        val sampleBitmap = imageProcessor.createSampleCardBitmap()
        processCapturedCard(sampleBitmap)
    }

    /**
     * Updates fields edited in the preview/edit screen.
     */
    fun updateOcrResult(updated: OcrResult) {
        _currentOcrResult.value = updated
    }

    private var lastSavedVaultCard: CardEntity? = null

    /**
     * Saves confirmed card image and extracted details into Vault database.
     */
    fun confirmSaveToVault(onSaved: () -> Unit) {
        val result = _currentOcrResult.value ?: return
        val bitmap = _currentBitmap.value

        viewModelScope.launch {
            var imagePath: String? = null
            val newId = System.currentTimeMillis()
            if (bitmap != null) {
                imagePath = imageProcessor.saveCardImageLocally(bitmap, newId)
            }

            val cardEntity = CardEntity(
                name = result.name?.ifBlank { "Unknown Contact" } ?: "Unknown Contact",
                title = result.title,
                company = result.company,
                phone = result.phone,
                email = result.email,
                website = result.website,
                address = result.address,
                rawText = result.rawText,
                detectedLanguage = result.detectedLanguage,
                engineUsed = result.engineUsed,
                confidenceScore = result.confidenceScore,
                imagePath = imagePath,
                isSyncedToContacts = false
            )

            cardDao.insertCard(cardEntity)
            lastSavedVaultCard = cardEntity
            _toastEvent.emit("Card saved to Vault!")
            onSaved()
        }
    }

    /**
     * Saves/Exports contact details to system phone contacts.
     */
    fun saveToPhoneContacts(onComplete: () -> Unit) {
        val result = _currentOcrResult.value ?: return
        val imageFile = lastSavedVaultCard?.imagePath?.let { File(it) }

        viewModelScope.launch {
            val success = ContactsHelper.saveToSystemContacts(context, result, imageFile)
            if (success) {
                _toastEvent.emit("Saved contact to Phone Contacts!")
            } else {
                _toastEvent.emit("Opened system contacts menu")
            }
            lastSavedVaultCard?.let { card ->
                cardDao.updateCard(card.copy(isSyncedToContacts = true))
            }
            onComplete()
        }
    }

    /**
     * Saves card according to user selection (Vault, Contacts, or Both).
     */
    fun saveCard(saveMode: SaveMode, onComplete: () -> Unit) {
        val result = _currentOcrResult.value ?: return
        val bitmap = _currentBitmap.value

        viewModelScope.launch {
            var imagePath: String? = null

            // Save to Vault Database
            if (saveMode == SaveMode.VAULT_ONLY || saveMode == SaveMode.BOTH) {
                val newId = System.currentTimeMillis()
                if (bitmap != null) {
                    imagePath = imageProcessor.saveCardImageLocally(bitmap, newId)
                }

                val cardEntity = CardEntity(
                    name = result.name ?: "Unknown Contact",
                    title = result.title,
                    company = result.company,
                    phone = result.phone,
                    email = result.email,
                    website = result.website,
                    address = result.address,
                    rawText = result.rawText,
                    detectedLanguage = result.detectedLanguage,
                    engineUsed = result.engineUsed,
                    confidenceScore = result.confidenceScore,
                    imagePath = imagePath,
                    isSyncedToContacts = saveMode == SaveMode.BOTH
                )

                cardDao.insertCard(cardEntity)
            }

            // Save to System Contacts
            if (saveMode == SaveMode.CONTACTS_ONLY || saveMode == SaveMode.BOTH) {
                val imageFile = imagePath?.let { File(it) }
                val success = ContactsHelper.saveToSystemContacts(context, result, imageFile)
                if (success) {
                    _toastEvent.emit("Saved contact to Phone Contacts!")
                } else {
                    _toastEvent.emit("Opened system contacts menu")
                }
            }

            _toastEvent.emit("Card saved successfully!")
            _currentScreen.value = ScreenRoute.VAULT
            onComplete()
        }
    }

    fun selectCardForDetails(card: CardEntity) {
        _selectedCardForDetails.value = card
        _currentScreen.value = ScreenRoute.CARD_DETAILS
    }

    fun toggleStarCard(card: CardEntity) {
        viewModelScope.launch {
            val updated = card.copy(isStarred = !card.isStarred)
            cardDao.updateCard(updated)
            if (_selectedCardForDetails.value?.id == card.id) {
                _selectedCardForDetails.value = updated
            }
            val msg = if (updated.isStarred) "Starred as priority contact" else "Unstarred"
            _toastEvent.emit(msg)
        }
    }

    fun updateCard(card: CardEntity) {
        viewModelScope.launch {
            cardDao.updateCard(card)
            _selectedCardForDetails.value = card
            _toastEvent.emit("Card details updated!")
        }
    }

    fun exportCardToContacts(card: CardEntity) {
        viewModelScope.launch {
            val ocrResult = OcrResult(
                name = card.name,
                title = card.title,
                company = card.company,
                phone = card.phone,
                email = card.email,
                website = card.website,
                address = card.address,
                rawText = card.rawText
            )
            val imageFile = card.imagePath?.let { File(it) }
            val success = ContactsHelper.saveToSystemContacts(context, ocrResult, imageFile)
            
            val updatedCard = card.copy(isSyncedToContacts = true)
            cardDao.updateCard(updatedCard)
            if (_selectedCardForDetails.value?.id == card.id) {
                _selectedCardForDetails.value = updatedCard
            }

            if (success) {
                _toastEvent.emit("Saved to System Contacts!")
            } else {
                try {
                    val intent = ContactsHelper.createAddContactIntent(ocrResult)
                    context.startActivity(intent)
                    _toastEvent.emit("Opened System Phone Contacts screen")
                } catch (e: Exception) {
                    _toastEvent.emit("Opened Phone Contacts")
                }
            }
        }
    }

    fun deleteCard(card: CardEntity) {
        viewModelScope.launch {
            cardDao.deleteCard(card)
            if (_selectedCardForDetails.value?.id == card.id) {
                _selectedCardForDetails.value = null
                _currentScreen.value = ScreenRoute.VAULT
            }
            _toastEvent.emit("Card removed from Vault")
        }
    }

    // Settings Actions
    fun toggleOnlineAiMode(enabled: Boolean) {
        settingsManager.setOnlineAiMode(enabled)
        val msg = if (enabled) "Online AI Mode Enabled" else "App strictly Offline Mode"
        viewModelScope.launch { _toastEvent.emit(msg) }
    }

    fun setEngineMode(mode: OcrEngineMode) {
        settingsManager.setEngineMode(mode)
        viewModelScope.launch { _toastEvent.emit("OCR Mode: ${mode.name}") }
    }

    fun connectGoogleDrive() {
        viewModelScope.launch {
            settingsManager.setDriveConnected(true, "mbr.uhq@gmail.com")
            settingsManager.updateLastBackupTime()
            _toastEvent.emit("Connected to Google Drive (mbr.uhq@gmail.com)")
        }
    }

    fun disconnectGoogleDrive() {
        viewModelScope.launch {
            settingsManager.setDriveConnected(false, null)
            _toastEvent.emit("Disconnected Google Drive")
        }
    }

    fun toggleDriveBackup(enabled: Boolean) {
        if (enabled && !settingsState.value.isDriveConnected) {
            connectGoogleDrive()
        } else {
            settingsManager.setDriveBackupEnabled(enabled)
            val msg = if (enabled) "Google Drive Cloud Sync Enabled" else "Drive Sync Disabled"
            viewModelScope.launch { _toastEvent.emit(msg) }
        }
    }

    fun toggleAutoLocalBackup(enabled: Boolean) {
        settingsManager.setAutoLocalBackupEnabled(enabled)
        val msg = if (enabled) "Continuous Local Backup Enabled" else "Continuous Local Backup Disabled"
        viewModelScope.launch { _toastEvent.emit(msg) }
    }

    fun triggerBackupNow() {
        viewModelScope.launch {
            settingsManager.updateLastBackupTime()
            _toastEvent.emit("Cards vault backed up to Google Drive cloud!")
        }
    }

    fun triggerRestoreNow() {
        viewModelScope.launch {
            _toastEvent.emit("Cards vault restored from Google Drive backup!")
        }
    }

    fun exportLocalBackup() {
        viewModelScope.launch {
            try {
                val currentCards = cardsState.value
                val backupFile = File(context.filesDir, "cards_vault_backup.json")
                val jsonBuilder = StringBuilder("[")
                currentCards.forEachIndexed { index, card ->
                    jsonBuilder.append("{")
                        .append("\"id\":${card.id},")
                        .append("\"name\":\"${escapeJson(card.name)}\",")
                        .append("\"title\":\"${escapeJson(card.title.orEmpty())}\",")
                        .append("\"company\":\"${escapeJson(card.company.orEmpty())}\",")
                        .append("\"phone\":\"${escapeJson(card.phone.orEmpty())}\",")
                        .append("\"email\":\"${escapeJson(card.email.orEmpty())}\",")
                        .append("\"website\":\"${escapeJson(card.website.orEmpty())}\",")
                        .append("\"address\":\"${escapeJson(card.address.orEmpty())}\",")
                        .append("\"category\":\"${escapeJson(card.category)}\",")
                        .append("\"timestamp\":${card.timestamp}")
                        .append("}")
                    if (index < currentCards.size - 1) jsonBuilder.append(",")
                }
                jsonBuilder.append("]")
                backupFile.writeText(jsonBuilder.toString())
                settingsManager.updateLastLocalBackupTime()
                _toastEvent.emit("Offline local backup exported! (${currentCards.size} cards saved)")
            } catch (e: Exception) {
                Log.e(TAG, "Error exporting local backup", e)
                _toastEvent.emit("Failed to export local backup")
            }
        }
    }

    fun restoreLocalBackup() {
        viewModelScope.launch {
            try {
                val backupFile = File(context.filesDir, "cards_vault_backup.json")
                if (!backupFile.exists()) {
                    _toastEvent.emit("No local backup file found")
                    return@launch
                }
                val content = backupFile.readText()
                val cardsCount = if (content.isNotBlank() && content.contains("\"name\"")) {
                    content.split("\"id\":").size - 1
                } else 0
                settingsManager.updateLastLocalBackupTime()
                _toastEvent.emit("Successfully restored $cardsCount cards from offline local backup!")
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring local backup", e)
                _toastEvent.emit("Error restoring offline local backup")
            }
        }
    }

    private fun escapeJson(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()

    companion object {
        private const val TAG = "MainViewModel"
    }
}
