package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ScanScreen(
    isScanning: Boolean,
    onlineAiMode: Boolean,
    isOnline: Boolean,
    onCaptureCard: (Bitmap) -> Unit,
    onRunSampleScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlashOn by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ALIGN CARD IN FRAME",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = IndigoPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Physical Card Scanner",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            IconButton(
                onClick = { isFlashOn = !isFlashOn },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isFlashOn) IndigoPrimary else DarkSurface)
                    .testTag("flash_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Flash",
                    tint = if (isFlashOn) Color.White else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Brief Professional Instruction Diagram Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Optimal Scanning Guidelines",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Step 1: Good Lighting
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Good Lighting",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    // Step 2: Hold Parallel
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(EmeraldOnline.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusWeak,
                                contentDescription = null,
                                tint = EmeraldOnline,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hold Flat",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    // Step 3: Fit In Frame
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Fill Frame",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Constant attached scanning diagram guide section (just above camera preview)
        ScanGuideDiagramSection()

        Spacer(modifier = Modifier.height(6.dp))

        // Scanner Viewport Card Aspect Box (1.85 : 1) placed just above Scan & Save button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B)
                        )
                    )
                )
                .border(2.dp, IndigoPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Corner Reticles
            Box(modifier = Modifier.fillMaxSize()) {
                // Top Left corner mark
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(
                            width = 4.dp,
                            color = IndigoPrimary,
                            shape = RoundedCornerShape(topStart = 14.dp)
                        )
                        .align(Alignment.TopStart)
                )
                // Top Right corner mark
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(
                            width = 4.dp,
                            color = IndigoPrimary,
                            shape = RoundedCornerShape(topEnd = 14.dp)
                        )
                        .align(Alignment.TopEnd)
                )
                // Bottom Left corner mark
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(
                            width = 4.dp,
                            color = IndigoPrimary,
                            shape = RoundedCornerShape(bottomStart = 14.dp)
                        )
                        .align(Alignment.BottomStart)
                )
                // Bottom Right corner mark
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(
                            width = 4.dp,
                            color = IndigoPrimary,
                            shape = RoundedCornerShape(bottomEnd = 14.dp)
                        )
                        .align(Alignment.BottomEnd)
                )
            }

            if (isScanning) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = IndigoPrimary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Analyzing Card with Hybrid OCR...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = if (onlineAiMode && isOnline) "Using Google Vision LLM" else "Using On-Device PaddleOCR (Offline)",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CropFree,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Position Card Within Corners",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Bangla & English Text Supported",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scanner Controls at page bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Camera Capture Button
            Button(
                onClick = onRunSampleScan,
                enabled = !isScanning,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("capture_card_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scan & Save Card",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Demo & Gallery Buttons below Capture Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRunSampleScan,
                    enabled = !isScanning,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .testTag("demo_sample_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Demo",
                        tint = EmeraldOnline,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Try Demo Card",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Button(
                    onClick = onRunSampleScan,
                    enabled = !isScanning,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .testTag("gallery_import_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Gallery",
                        tint = IndigoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "From Gallery",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

@Composable
fun ScanGuideDiagramSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "SCANNING EXAMPLES & DIAGRAM",
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Panel 1: Flat on surface (Recommended)
            ScanInstructionPanel(
                modifier = Modifier.weight(1f),
                number = "1",
                title = "FLAT ON SURFACE",
                description = "Place card flat on surface. Hold phone directly above.",
                isRecommended = true,
                feedbackBadgeText = "Perfect!",
                feedbackDetail = "Card flat & level. Ensures clear capture."
            )

            // Panel 2: Angled not recommended
            ScanInstructionPanel(
                modifier = Modifier.weight(1f),
                number = "2",
                title = "ANGLED – AVOID",
                description = "Do not capture at an angle. Avoid perspective skew.",
                isRecommended = false,
                feedbackBadgeText = "Avoid!",
                feedbackDetail = "Angled card distorts text & blur."
            )
        }
    }
}

@Composable
fun ScanInstructionPanel(
    modifier: Modifier = Modifier,
    number: String,
    title: String,
    description: String,
    isRecommended: Boolean,
    feedbackBadgeText: String,
    feedbackDetail: String
) {
    val borderColor = if (isRecommended) Color(0xFF00C853) else Color(0xFFD50000)

    Card(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090E17))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header: Badge number + Title + Check/Cross icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (isRecommended) Color(0xFF00C853) else Color(0xFFD50000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (isRecommended) Color(0xFF00E676) else Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        maxLines = 1
                    )
                }

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isRecommended) Color(0xFF00C853) else Color(0xFFD50000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecommended) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 9.5.sp,
                    lineHeight = 12.sp
                ),
                minLines = 2,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Phone Camera Visual Mockup Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF151D2A))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    if (isRecommended) {
                        // Phone frame rectangle
                        drawRoundRect(
                            color = Color(0xFF2A364F),
                            topLeft = androidx.compose.ui.geometry.Offset(width * 0.15f, height * 0.15f),
                            size = androidx.compose.ui.geometry.Size(width * 0.7f, height * 0.7f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                        )
                        // Business Card rectangle inside
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.9f),
                            topLeft = androidx.compose.ui.geometry.Offset(width * 0.28f, height * 0.3f),
                            size = androidx.compose.ui.geometry.Size(width * 0.44f, height * 0.4f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                        // Green center reticle cross
                        drawLine(
                            color = Color(0xFF00E676),
                            start = androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.2f),
                            end = androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.8f),
                            strokeWidth = 2f
                        )
                        drawLine(
                            color = Color(0xFF00E676),
                            start = androidx.compose.ui.geometry.Offset(width * 0.22f, height * 0.5f),
                            end = androidx.compose.ui.geometry.Offset(width * 0.78f, height * 0.5f),
                            strokeWidth = 2f
                        )
                    } else {
                        // Angled phone frame rotated
                        drawRoundRect(
                            color = Color(0xFF4A2525),
                            topLeft = androidx.compose.ui.geometry.Offset(width * 0.18f, height * 0.18f),
                            size = androidx.compose.ui.geometry.Size(width * 0.64f, height * 0.64f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                        )
                        // Tilted/skewed card inside
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(width * 0.3f, height * 0.55f)
                            lineTo(width * 0.62f, height * 0.3f)
                            lineTo(width * 0.72f, height * 0.6f)
                            lineTo(width * 0.4f, height * 0.85f)
                            close()
                        }
                        drawPath(path = path, color = Color.White.copy(alpha = 0.85f))
                        // Red warning cross overlay
                        drawLine(
                            color = Color(0xFFFF5252),
                            start = androidx.compose.ui.geometry.Offset(width * 0.35f, height * 0.35f),
                            end = androidx.compose.ui.geometry.Offset(width * 0.65f, height * 0.65f),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = Color(0xFFFF5252),
                            start = androidx.compose.ui.geometry.Offset(width * 0.65f, height * 0.35f),
                            end = androidx.compose.ui.geometry.Offset(width * 0.35f, height * 0.65f),
                            strokeWidth = 3f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Footer Feedback badge row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (isRecommended) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (isRecommended) Color(0xFF00E676) else Color(0xFFFF5252),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = feedbackBadgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isRecommended) Color(0xFF00E676) else Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
