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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.CardEntity
import com.example.image.ImageProcessor
import com.example.ui.SortOrder
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
import com.example.util.PhoneticConverter

@Composable
fun VaultScreen(
    cards: List<CardEntity>,
    searchQuery: String,
    selectedCategory: String,
    selectedSortOrder: SortOrder,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onSelectCard: (CardEntity) -> Unit,
    onToggleStar: (CardEntity) -> Unit,
    onScanClick: () -> Unit,
    onDeleteCard: (CardEntity) -> Unit,
    onRunSampleScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var cardToDelete by remember { mutableStateOf<CardEntity?>(null) }

    val categories = listOf("All", "Professional", "Office", "Business", "Family")

    // Confirmation Dialog before Deleting Card
    if (cardToDelete != null) {
        val targetCard = cardToDelete
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
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
                    text = "Are you sure you want to delete '${targetCard?.name?.ifBlank { "this card" }}'? This card will be permanently removed from your vault.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        targetCard?.let { onDeleteCard(it) }
                        cardToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { cardToDelete = null },
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
                    modifier = Modifier.testTag("cancel_delete_button")
                ) {
                    Text("Cancel", color = TextPrimary)
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
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header Banner & Scan Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E1B4B),
                            Color(0xFF312E81)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = EmeraldOnline,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFLINE VAULT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldOnline,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Scanned Card Vault",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onScanClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("scan_new_card_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Scan Card", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar & Sort Dropdown Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search by name or company...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IndigoPrimary) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_vault_input"),
                shape = RoundedCornerShape(16.dp),
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

            // Sort Selector Button
            Box {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .clickable { sortMenuExpanded = true }
                        .testTag("sort_order_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Sort Order",
                        tint = IndigoPrimary
                    )
                }

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Latest First (Time)", color = TextPrimary) },
                        onClick = {
                            onSortOrderChange(SortOrder.LATEST)
                            sortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Oldest First (Time)", color = TextPrimary) },
                        onClick = {
                            onSortOrderChange(SortOrder.OLDEST)
                            sortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Alphabetical Name (A-Z)", color = TextPrimary) },
                        onClick = {
                            onSortOrderChange(SortOrder.ALPHABETICAL)
                            sortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Starred / Priority First", color = TextPrimary) },
                        onClick = {
                            onSortOrderChange(SortOrder.STARRED_FIRST)
                            sortMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Categories List Filter Chips (All, Professional, Office, Business, Family)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory.equals(category, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) IndigoPrimary else DarkSurface)
                        .border(1.dp, if (isSelected) IndigoPrimary else DarkBorder, RoundedCornerShape(14.dp))
                        .clickable { onCategoryChange(category) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("category_chip_$category")
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (isSelected) Color.White else TextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cards List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CARDS (${cards.size})",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Text(
                text = when (selectedSortOrder) {
                    SortOrder.LATEST -> "Sorted: Latest"
                    SortOrder.OLDEST -> "Sorted: Oldest"
                    SortOrder.ALPHABETICAL -> "Sorted: Name A-Z"
                    SortOrder.STARRED_FIRST -> "Sorted: Priority"
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    color = IndigoPrimary,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Single-Column Card List
        if (cards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (searchQuery.isNotBlank()) "No cards matching '$searchQuery'" else "No scanned cards in this category",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRunSampleScan,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = EmeraldOnline, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan Demo Card", color = TextPrimary)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    CardRowItem(
                        card = card,
                        onSelectCard = { onSelectCard(card) },
                        onToggleStar = { onToggleStar(card) },
                        onDeleteClick = { cardToDelete = card }
                    )
                }
            }
        }
    }
}

/**
 * Single-column row item layout as specified by PRD:
 * Left-to-right:
 * 1. Miniature image (icon thumbnail) of the scanned card
 * 2. Name of person (in English)
 * 3. Company name
 * 4. Priority star button
 */
@Composable
private fun CardRowItem(
    card: CardEntity,
    onSelectCard: () -> Unit,
    onToggleStar: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    val imageProcessor = remember { ImageProcessor(context) }
    val imagePath = card.imagePath
    val cardBitmap: Bitmap? = remember(imagePath) {
        if (!imagePath.isNullOrBlank()) imageProcessor.loadSavedCardImage(imagePath) else null
    }

    val englishName = remember(card.name) {
        PhoneticConverter.toPhoneticEnglish(card.name)
    }
    val englishCompany = remember(card.company) {
        PhoneticConverter.toPhoneticEnglish(card.company)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .clickable { onSelectCard() }
            .testTag("card_row_item_${card.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Miniature image / icon of the scanned card (Left)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCardBackground)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (cardBitmap != null) {
                    Image(
                        bitmap = cardBitmap.asImageBitmap(),
                        contentDescription = "Card Miniature",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = englishName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Name of person & 3. Company Name (Middle)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectCard() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = englishName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (card.category.isNotBlank() && !card.category.equals("All", ignoreCase = true)) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(IndigoPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = card.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = IndigoPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (englishCompany.isNotBlank()) englishCompany else (card.title ?: "Contact"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Star / Priority Toggle & Delete Buttons (Right)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                IconButton(
                    onClick = onToggleStar,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("star_card_button_${card.id}")
                ) {
                    Icon(
                        imageVector = if (card.isStarred) Icons.Default.Star else Icons.Default.FavoriteBorder,
                        contentDescription = "Star Contact",
                        tint = if (card.isStarred) AmberOffline else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("delete_card_button_${card.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Card",
                        tint = Color(0xFFEF4444).copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
