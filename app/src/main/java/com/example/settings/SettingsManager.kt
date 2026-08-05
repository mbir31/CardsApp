package com.example.settings

import android.content.Context
import android.content.SharedPreferences
import com.example.model.OcrEngineMode
import com.example.model.OcrSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists and exposes application configuration settings for CardsApp.
 */
class SettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settingsState = MutableStateFlow(loadSettings())
    val settingsState: StateFlow<OcrSettings> = _settingsState.asStateFlow()

    private fun loadSettings(): OcrSettings {
        val onlineAiMode = prefs.getBoolean(KEY_ONLINE_AI_MODE, true)
        val engineModeName = prefs.getString(KEY_ENGINE_MODE, OcrEngineMode.AUTO.name) ?: OcrEngineMode.AUTO.name
        val engineMode = try {
            OcrEngineMode.valueOf(engineModeName)
        } catch (e: Exception) {
            OcrEngineMode.AUTO
        }
        val driveBackup = prefs.getBoolean(KEY_DRIVE_BACKUP, false)
        val dailyDriveBackup = prefs.getBoolean(KEY_DAILY_DRIVE_BACKUP, true)
        val lastSync = prefs.getLong(KEY_LAST_SYNC, 0L)
        val isDriveConnected = prefs.getBoolean(KEY_DRIVE_CONNECTED, false)
        val driveAccountEmail = prefs.getString(KEY_DRIVE_EMAIL, null)
        val autoLocalBackup = prefs.getBoolean(KEY_AUTO_LOCAL_BACKUP, true)
        val lastLocalBackup = prefs.getLong(KEY_LAST_LOCAL_BACKUP, 0L)

        return OcrSettings(
            onlineAiMode = onlineAiMode,
            engineMode = engineMode,
            driveBackupEnabled = driveBackup,
            dailyDriveBackupEnabled = dailyDriveBackup,
            lastBackupTimestamp = lastSync,
            isDriveConnected = isDriveConnected,
            driveAccountEmail = driveAccountEmail,
            autoLocalBackupEnabled = autoLocalBackup,
            lastLocalBackupTimestamp = lastLocalBackup
        )
    }

    fun setOnlineAiMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ONLINE_AI_MODE, enabled).apply()
        _settingsState.value = _settingsState.value.copy(onlineAiMode = enabled)
    }

    fun setEngineMode(mode: OcrEngineMode) {
        prefs.edit().putString(KEY_ENGINE_MODE, mode.name).apply()
        _settingsState.value = _settingsState.value.copy(engineMode = mode)
    }

    fun setDriveBackupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DRIVE_BACKUP, enabled).apply()
        _settingsState.value = _settingsState.value.copy(driveBackupEnabled = enabled)
    }

    fun setDailyDriveBackupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DAILY_DRIVE_BACKUP, enabled).apply()
        _settingsState.value = _settingsState.value.copy(dailyDriveBackupEnabled = enabled)
    }

    fun setDriveConnected(connected: Boolean, email: String? = "mbr.uhq@gmail.com") {
        prefs.edit()
            .putBoolean(KEY_DRIVE_CONNECTED, connected)
            .putString(KEY_DRIVE_EMAIL, if (connected) email else null)
            .apply()
        _settingsState.value = _settingsState.value.copy(
            isDriveConnected = connected,
            driveAccountEmail = if (connected) email else null,
            driveBackupEnabled = connected
        )
    }

    fun setAutoLocalBackupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_LOCAL_BACKUP, enabled).apply()
        _settingsState.value = _settingsState.value.copy(autoLocalBackupEnabled = enabled)
    }

    fun updateLastBackupTime(timestamp: Long = System.currentTimeMillis()) {
        prefs.edit().putLong(KEY_LAST_SYNC, timestamp).apply()
        _settingsState.value = _settingsState.value.copy(lastBackupTimestamp = timestamp)
    }

    fun updateLastLocalBackupTime(timestamp: Long = System.currentTimeMillis()) {
        prefs.edit().putLong(KEY_LAST_LOCAL_BACKUP, timestamp).apply()
        _settingsState.value = _settingsState.value.copy(lastLocalBackupTimestamp = timestamp)
    }

    companion object {
        private const val PREFS_NAME = "cardsapp_settings_prefs"
        private const val KEY_ONLINE_AI_MODE = "online_ai_mode"
        private const val KEY_ENGINE_MODE = "ocr_engine_mode"
        private const val KEY_DRIVE_BACKUP = "drive_backup_enabled"
        private const val KEY_DAILY_DRIVE_BACKUP = "daily_drive_backup_enabled"
        private const val KEY_LAST_SYNC = "last_backup_timestamp"
        private const val KEY_DRIVE_CONNECTED = "drive_connected"
        private const val KEY_DRIVE_EMAIL = "drive_account_email"
        private const val KEY_AUTO_LOCAL_BACKUP = "auto_local_backup_enabled"
        private const val KEY_LAST_LOCAL_BACKUP = "last_local_backup_timestamp"
    }
}
