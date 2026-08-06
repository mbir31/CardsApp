package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.CardEntity
import com.example.image.ImageProcessor
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
import com.example.util.CardExportHelper
import com.example.util.PhoneticConverter

@Composable
fun CardDetailsScreen(
    card: CardEntity?,
    onSaveCard: (CardEntity) -> Unit,
    onDeleteCard: (CardEntity) -> Unit,
    onExportToContacts: (CardEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (card == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("No card selected", color = TextMuted)
        }
        return
    }

    val context = LocalContext.current
    val imageProcessor = remember { ImageProcessor(context) }
    val imagePath = card.imagePath
    val cardBitmap: Bitmap? = remember(imagePath) {
        if (!imagePath.isNullOrBlank()) imageProcessor.loadSavedCardImage(imagePath) else null
    }

    var name by remember(card) { mutableStateOf(card.name) }
    var title by remember(card) { mutableStateOf(card.title ?: "") }
    var company by remember(card) { mutableStateOf(card.company ?: "") }
    var phone by remember(card) { mutableStateOf(card.phone ?: "") }
    var email by remember(card) { mutableStateOf(card.email ?: "") }
    var website by remember(card) { mutableStateOf(card.website ?: "") }
    var address by remember(card) { mutableStateOf(card.address ?: "") }
    var category by remember(card) { mutableStateOf(card.category) }
    var isStarred by remember(card) { mutableStateOf(card.isStarred) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    val categories = listOf("Professional", "Office", "Business", "Family", "Personal")

    // QR Code Dialog
    if (showQrDialog) {
        val currentCard = card.copy(
            name = name,
            title = title,
            company = company,
            phone = phone,
            email = email,
            website = website,
            address = address,
            category = category
        )
        val vcardString = remember(currentCard) { CardExportHelper.generateVCardString(currentCard) }
        val qrBitmap = remember(vcardString) { CardExportHelper.generateQrBitmap(vcardString, 600) }

        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Scan Contact QR Code",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Scan with any camera app to instantly import ${name.ifBlank { "this contact" }}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    if (qrBitmap != null) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .size(220.dp)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "Contact QR Code",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        CardExportHelper.shareQrCode(context, currentCard)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("share_qr_image_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share QR Code", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showQrDialog = false },
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
                    modifier = Modifier.testTag("close_qr_dialog_button")
                ) {
                    Text("Close", color = TextPrimary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Delete Business Card?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${name.ifBlank { "this card" }}'? This card will be permanently removed from your vault.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteCard(card)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("confirm_delete_detail_button")
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = false },
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
                    modifier = Modifier.testTag("cancel_delete_detail_button")
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_from_details_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "CARD DETAILS & EDITOR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = name.ifBlank { "Scanned Card" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // QR Code Button
                IconButton(
                    onClick = { showQrDialog = true },
                    modifier = Modifier.testTag("detail_qr_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Scan QR Code",
                        tint = IndigoPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Priority Star Toggle
                IconButton(
                    onClick = {
                        isStarred = !isStarred
                        onSaveCard(card.copy(isStarred = isStarred))
                    },
                    modifier = Modifier.testTag("detail_star_button")
                ) {
                    Icon(
                        imageVector = if (isStarred) Icons.Default.Star else Icons.Default.FavoriteBorder,
                        contentDescription = "Priority Star",
                        tint = if (isStarred) AmberOffline else TextMuted,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.testTag("detail_delete_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Card",
                        tint = Color(0xFFEF4444)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Digital Card Image Preview on Top
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (cardBitmap != null) {
                    Image(
                        bitmap = cardBitmap.asImageBitmap(),
                        contentDescription = "Scanned Card Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = IndigoPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (company.isNotBlank()) {
                            Text(
                                text = company,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Action Row (Call, Email)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (phone.isNotBlank()) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldOnline),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("call_action_button")
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            if (email.isNotBlank()) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                        .testTag("email_action_button")
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Email", tint = TextPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Email", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Save / Edit on Phone Contacts Prominent Action Button
        Button(
            onClick = {
                val updatedCard = card.copy(
                    name = name,
                    title = title,
                    company = company,
                    phone = phone,
                    email = email,
                    website = website,
                    address = address,
                    category = category,
                    isStarred = isStarred
                )
                onSaveCard(updatedCard)
                onExportToContacts(updatedCard)
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldOnline),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("export_phone_contacts_button")
        ) {
            Icon(Icons.Default.Person, contentDescription = "Save / Edit on Phone Contacts", tint = Color.Black, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save / Edit on Phone Contacts", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Export & Share Options Section
        Text(
            text = "SHARE & EXPORT FORMATS",
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. QR Code
            OutlinedButton(
                onClick = { showQrDialog = true },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("share_option_qr")
            ) {
                Icon(Icons.Default.Share, contentDescription = "QR Code", tint = IndigoPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("QR Code", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // 2. vCard (.vcf)
            OutlinedButton(
                onClick = {
                    val currentCard = card.copy(
                        name = name,
                        title = title,
                        company = company,
                        phone = phone,
                        email = email,
                        website = website,
                        address = address,
                        category = category
                    )
                    CardExportHelper.shareVCard(context, currentCard)
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("share_option_vcard")
            ) {
                Icon(Icons.Default.Person, contentDescription = "vCard", tint = AmberOffline, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("vCard", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // 3. CSV (.csv)
            OutlinedButton(
                onClick = {
                    val currentCard = card.copy(
                        name = name,
                        title = title,
                        company = company,
                        phone = phone,
                        email = email,
                        website = website,
                        address = address,
                        category = category
                    )
                    CardExportHelper.shareCsv(context, currentCard)
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("share_option_csv")
            ) {
                Icon(Icons.Default.Info, contentDescription = "CSV", tint = EmeraldOnline, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("CSV", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // 4. Scanned Card Image
            if (!card.imagePath.isNullOrBlank()) {
                OutlinedButton(
                    onClick = {
                        val currentCard = card.copy(
                            name = name,
                            title = title,
                            company = company,
                            phone = phone,
                            email = email,
                            website = website,
                            address = address,
                            category = category
                        )
                        CardExportHelper.shareScannedImage(context, currentCard)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("share_option_image")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Card Image", tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Image", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Editable Details Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EDITABLE EXTRACTED DETAILS",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            // Category Selector Chip
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(IndigoPrimary.copy(alpha = 0.15f))
                        .clickable { categoryDropdownExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("category_selector_chip")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelSmall.copy(color = IndigoPrimary, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                DropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, color = TextPrimary) },
                            onClick = {
                                category = cat
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Name Field
        DetailTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name (English / Phonetic)",
            icon = Icons.Default.Person,
            testTag = "detail_name_input"
        )

        // Job Title
        DetailTextField(
            value = title,
            onValueChange = { title = it },
            label = "Designation / Job Title",
            icon = Icons.Default.Edit,
            testTag = "detail_title_input"
        )

        // Company
        DetailTextField(
            value = company,
            onValueChange = { company = it },
            label = "Company / Organization Name",
            icon = Icons.Default.Home,
            testTag = "detail_company_input"
        )

        // Phone
        DetailTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            icon = Icons.Default.Phone,
            testTag = "detail_phone_input"
        )

        // Email
        DetailTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            icon = Icons.Default.Person,
            testTag = "detail_email_input"
        )

        // Website
        DetailTextField(
            value = website,
            onValueChange = { website = it },
            label = "Website URL",
            icon = Icons.Default.Share,
            testTag = "detail_website_input"
        )

        // Address
        DetailTextField(
            value = address,
            onValueChange = { address = it },
            label = "Office / Postal Address",
            icon = Icons.Default.LocationOn,
            testTag = "detail_address_input"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Save Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Save / Edit on Contacts
            Button(
                onClick = {
                    val updatedCard = card.copy(
                        name = name,
                        title = title,
                        company = company,
                        phone = phone,
                        email = email,
                        website = website,
                        address = address,
                        category = category,
                        isStarred = isStarred
                    )
                    onSaveCard(updatedCard)
                    onExportToContacts(updatedCard)
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldOnline),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("bottom_contacts_save_button")
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("To Phone Contacts", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Save Modifications
            Button(
                onClick = {
                    val updatedCard = card.copy(
                        name = name,
                        title = title,
                        company = company,
                        phone = phone,
                        email = email,
                        website = website,
                        address = address,
                        category = category,
                        isStarred = isStarred
                    )
                    onSaveCard(updatedCard)
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("save_card_details_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailTextField(
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
