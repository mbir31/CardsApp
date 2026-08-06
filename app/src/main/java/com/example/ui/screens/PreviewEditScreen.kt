package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OcrResult
import com.example.ui.SaveMode
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

@Composable
fun PreviewEditScreen(
    ocrResult: OcrResult?,
    bitmap: Bitmap?,
    onUpdateResult: (OcrResult) -> Unit,
    onConfirmSaveToVault: (onSaved: () -> Unit) -> Unit = {},
    onSaveToContacts: () -> Unit = {},
    onNavigateToVault: () -> Unit = {},
    onRetakeScan: () -> Unit = {},
    onSave: ((SaveMode) -> Unit)? = null,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val result = ocrResult ?: OcrResult()

    var name by remember(result) { mutableStateOf(result.name ?: "") }
    var title by remember(result) { mutableStateOf(result.title ?: "") }
    var company by remember(result) { mutableStateOf(result.company ?: "") }
    var phone by remember(result) { mutableStateOf(result.phone ?: "") }
    var email by remember(result) { mutableStateOf(result.email ?: "") }
    var website by remember(result) { mutableStateOf(result.website ?: "") }
    var address by remember(result) { mutableStateOf(result.address ?: "") }

    var showContactsPopup by remember { mutableStateOf(false) }
    var isSavingToVault by remember { mutableStateOf(false) }
    var isVaultSaved by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Save to Phone Contacts Popup Dialog
    if (showContactsPopup) {
        AlertDialog(
            onDismissRequest = {
                showContactsPopup = false
                onNavigateToVault()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Save to Phone Contacts?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "Your business card has been saved to the Vault! Would you also like to save '${name.ifBlank { "this contact" }}' to your phone contacts?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showContactsPopup = false
                        if (onSave != null) {
                            onSave(SaveMode.BOTH)
                        } else {
                            onSaveToContacts()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldOnline),
                    modifier = Modifier.testTag("popup_contacts_yes_button")
                ) {
                    Text("Yes, Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showContactsPopup = false
                        onNavigateToVault()
                    },
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
                    ),
                    modifier = Modifier.testTag("popup_contacts_no_button")
                ) {
                    Text("No, Skip", color = TextPrimary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        // Navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("back_from_preview_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "VERIFY & EDIT DATA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = IndigoPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Scanned Card Preview",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Live Card Surface Mockup (Matching Sophisticated Dark theme specs)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.15f), DarkBorder)),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (name.isNotBlank()) name else "Contact Name",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )

                            if (title.isNotBlank()) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = IndigoPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                )
                            }

                            if (company.isNotBlank()) {
                                Text(
                                    text = company,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        // Confidence badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "CONFIDENCE: ${(result.confidenceScore * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldOnline,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (phone.isNotBlank() || email.isNotBlank()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            if (phone.isNotBlank()) {
                                Text(
                                    text = "📞 $phone",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            if (email.isNotBlank()) {
                                Text(
                                    text = "✉️ $email",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // OCR Engine info banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PRIMARY EXTRACTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = result.engineUsed,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldOnline.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = result.detectedLanguage,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldOnline,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Fields
        Text(
            text = "EDIT EXTRACTED DETAILS",
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditTextField(
            value = name,
            onValueChange = { name = it; onUpdateResult(result.copy(name = it)) },
            label = "Full Name",
            icon = Icons.Default.Person,
            testTag = "edit_name_input"
        )

        EditTextField(
            value = title,
            onValueChange = { title = it; onUpdateResult(result.copy(title = it)) },
            label = "Job Title / Designation",
            icon = Icons.Default.Edit,
            testTag = "edit_title_input"
        )

        EditTextField(
            value = company,
            onValueChange = { company = it; onUpdateResult(result.copy(company = it)) },
            label = "Company / Organization",
            icon = Icons.Default.Home,
            testTag = "edit_company_input"
        )

        EditTextField(
            value = phone,
            onValueChange = { phone = it; onUpdateResult(result.copy(phone = it)) },
            label = "Phone Number",
            icon = Icons.Default.Phone,
            testTag = "edit_phone_input"
        )

        EditTextField(
            value = email,
            onValueChange = { email = it; onUpdateResult(result.copy(email = it)) },
            label = "Email Address",
            icon = Icons.Default.Person,
            testTag = "edit_email_input"
        )

        EditTextField(
            value = website,
            onValueChange = { website = it; onUpdateResult(result.copy(website = it)) },
            label = "Website URL",
            icon = Icons.Default.Share,
            testTag = "edit_website_input"
        )

        EditTextField(
            value = address,
            onValueChange = { address = it; onUpdateResult(result.copy(address = it)) },
            label = "Office / Postal Address",
            icon = Icons.Default.LocationOn,
            testTag = "edit_address_input"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Retake and Confirm Options
        Text(
            text = "SCAN ACTION",
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Option 1: Retake / Again (Red Button)
            Button(
                onClick = onRetakeScan,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("retake_scan_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retake",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Retake / Again",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Option 2: Confirm (Green Button)
            Button(
                onClick = {
                    if (!isVaultSaved && !isSavingToVault) {
                        isSavingToVault = true
                        onConfirmSaveToVault {
                            isSavingToVault = false
                            isVaultSaved = true
                            showContactsPopup = true
                        }
                    } else if (isVaultSaved) {
                        showContactsPopup = true
                    }
                },
                enabled = !isSavingToVault,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("confirm_scan_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSavingToVault) "Saving..." else "Confirm",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextMuted) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = IndigoPrimary) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface,
            focusedBorderColor = IndigoPrimary,
            unfocusedBorderColor = DarkBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        singleLine = true
    )
}
