package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OcrEngineMode
import com.example.model.OcrSettings
import com.example.security.ApiKeyManager
import com.example.ui.theme.AmberOffline
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCardBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    settings: OcrSettings,
    isOnline: Boolean,
    isStrongConnection: Boolean,
    onToggleOnlineAiMode: (Boolean) -> Unit,
    onSetEngineMode: (OcrEngineMode) -> Unit,
    onConnectDrive: () -> Unit,
    onDisconnectDrive: () -> Unit,
    onToggleDriveBackup: (Boolean) -> Unit,
    onToggleDailyDriveBackup: (Boolean) -> Unit = {},
    onBackupNow: () -> Unit,
    onRestoreNow: () -> Unit,
    onToggleAutoLocalBackup: (Boolean) -> Unit,
    onExportLocalBackup: () -> Unit,
    onRestoreLocalBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val hasCloudKey = ApiKeyManager.hasValidCloudKey()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Column {
            Text(
                text = "SETTINGS & BACKUP CONFIGURATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = IndigoPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. GOOGLE DRIVE ONLINE CLOUD BACKUP & SYNC
        Text(
            text = "GOOGLE DRIVE ONLINE BACKUP & CLOUD SYNC",
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Connection status row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (settings.isDriveConnected) IndigoPrimary.copy(alpha = 0.15f) else DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (settings.isDriveConnected) Icons.Default.Check else Icons.Default.Share,
                                contentDescription = null,
                                tint = if (settings.isDriveConnected) IndigoPrimary else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Google Drive Sync",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (settings.isDriveConnected)
                                    "Account: ${settings.driveAccountEmail ?: "mbr.uhq@gmail.com"}"
                                else "Not connected to Google Drive",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }

                    if (settings.isDriveConnected) {
                        Switch(
                            checked = settings.driveBackupEnabled,
                            onCheckedChange = onToggleDriveBackup,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = IndigoPrimary
                            ),
                            modifier = Modifier.testTag("drive_backup_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Automatic Daily Backup Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Automatic Daily Backup",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Backs up contact database daily to Google Drive to protect against device loss.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                        if (settings.dailyDriveBackupEnabled) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Schedule: Every 24 hours at 02:00 AM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldOnline,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = settings.dailyDriveBackupEnabled,
                        onCheckedChange = onToggleDailyDriveBackup,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = IndigoPrimary
                        ),
                        modifier = Modifier.testTag("daily_drive_backup_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Connect / Disconnect button
                if (!settings.isDriveConnected) {
                    Button(
                        onClick = onConnectDrive,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("connect_drive_button")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connect Google Drive Account",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onBackupNow,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("backup_now_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Backup Now")
                        }

                        Button(
                            onClick = onRestoreNow,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                                .testTag("restore_now_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore", color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (settings.lastBackupTimestamp > 0)
                                "Last cloud sync: ${SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(settings.lastBackupTimestamp))}"
                            else "No cloud sync recorded yet",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                        )

                        Text(
                            text = "Disconnect",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AmberOffline,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clickable { onDisconnectDrive() }
                                .padding(4.dp)
                                .testTag("disconnect_drive_button")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. CONTINUOUS OFFLINE LOCAL BACKUP & EXPORT/RESTORE
        Text(
            text = "CONTINUOUS OFFLINE LOCAL BACKUP",
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldOnline.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = EmeraldOnline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Auto Local Backup",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Continuously backs up vault data offline",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }

                    Switch(
                        checked = settings.autoLocalBackupEnabled,
                        onCheckedChange = onToggleAutoLocalBackup,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldOnline
                        ),
                        modifier = Modifier.testTag("auto_local_backup_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (settings.lastLocalBackupTimestamp > 0)
                        "Last local backup: ${SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(settings.lastLocalBackupTimestamp))}"
                    else "No local backup exported yet",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onExportLocalBackup,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldOnline),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_local_backup_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Backup")
                    }

                    Button(
                        onClick = onRestoreLocalBackup,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                            .testTag("restore_local_backup_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Local", color = TextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. ONLINE AI MODE TOGGLE CARD
        Text(
            text = "AI ENGINE & NETWORK ROUTING",
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (settings.onlineAiMode) EmeraldOnline.copy(alpha = 0.15f) else AmberOffline.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (settings.onlineAiMode) Icons.Default.Star else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (settings.onlineAiMode) EmeraldOnline else AmberOffline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Online AI Mode",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (settings.onlineAiMode) "Cloud OCR & Gemini Enabled" else "App strictly Offline Mode",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }

                    Switch(
                        checked = settings.onlineAiMode,
                        onCheckedChange = onToggleOnlineAiMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldOnline,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier.testTag("online_ai_mode_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Security notice PRD requirement
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBackground)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = EmeraldOnline,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Images are processed securely using Google AI services. No image data is stored on external servers.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. OCR ENGINE ROUTING MODE
        Text(
            text = "ENGINE ROUTING PRIORITY",
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                EngineOptionRow(
                    title = "Auto Hybrid Mode (Recommended)",
                    subtitle = "Uses Cloud LLM when online, falls back to PaddleOCR offline",
                    isSelected = settings.engineMode == OcrEngineMode.AUTO,
                    onClick = { onSetEngineMode(OcrEngineMode.AUTO) },
                    testTag = "engine_mode_auto"
                )

                EngineOptionRow(
                    title = "Force Cloud Vision LLM",
                    subtitle = "Always attempt Google Vision LLM multimodal API first",
                    isSelected = settings.engineMode == OcrEngineMode.FORCE_CLOUD,
                    onClick = { onSetEngineMode(OcrEngineMode.FORCE_CLOUD) },
                    testTag = "engine_mode_force_cloud"
                )

                EngineOptionRow(
                    title = "Force On-Device Offline OCR",
                    subtitle = "Exclusively use PaddleOCR (NCNN) & Tesseract 5 (ben+eng)",
                    isSelected = settings.engineMode == OcrEngineMode.FORCE_OFFLINE,
                    onClick = { onSetEngineMode(OcrEngineMode.FORCE_OFFLINE) },
                    testTag = "engine_mode_force_offline"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. NETWORK & CLOUD API STATUS
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SYSTEM & NETWORK DIAGNOSTICS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = IndigoPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.Check else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isOnline) EmeraldOnline else AmberOffline,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Internet Connection",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                        )
                    }

                    Text(
                        text = if (isOnline) (if (isStrongConnection) "Strong (Broadband/Wi-Fi)" else "Weak / Cellular") else "Disconnected",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isOnline) EmeraldOnline else AmberOffline,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google AI Studio API Key",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                        )
                    }

                    Text(
                        text = if (hasCloudKey) "Configured & Active" else "Auto-Fallback Active",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (hasCloudKey) EmeraldOnline else AmberOffline,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun EngineOptionRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = IndigoPrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp
                )
            )
        }
    }
}

