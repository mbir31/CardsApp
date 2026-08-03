package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.BottomNav
import com.example.ui.components.TopHeader
import com.example.ui.screens.PreviewEditScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.AmberOffline
import com.example.ui.theme.CardsAppTheme
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CardsAppTheme {
                CardsAppMainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CardsAppMainScreen(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val settingsState by viewModel.settingsState.collectAsState()
    val isOnline by viewModel.networkMonitor.isOnline.collectAsState()
    val isStrongConnection by viewModel.networkMonitor.isStrongConnection.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSortOrder by viewModel.selectedSortOrder.collectAsState()
    val selectedCardForDetails by viewModel.selectedCardForDetails.collectAsState()

    val cards by viewModel.cardsState.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    val currentOcrResult by viewModel.currentOcrResult.collectAsState()
    val currentBitmap by viewModel.currentBitmap.collectAsState()

    var activeToastMessage by remember { mutableStateOf<String?>(null) }

    // System Back Press Handler
    BackHandler(enabled = currentScreen != ScreenRoute.VAULT) {
        when (currentScreen) {
            ScreenRoute.CARD_DETAILS -> viewModel.navigateTo(ScreenRoute.VAULT)
            ScreenRoute.PREVIEW_EDIT -> viewModel.navigateTo(ScreenRoute.SCAN)
            ScreenRoute.SCAN, ScreenRoute.SETTINGS -> viewModel.navigateTo(ScreenRoute.VAULT)
            else -> {}
        }
    }

    // Listen to ViewModel toast events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            activeToastMessage = message
            delay(3500)
            activeToastMessage = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopHeader(
                onlineAiMode = settingsState.onlineAiMode,
                isOnline = isOnline,
                engineModeName = settingsState.engineMode.name
            )
        },
        bottomBar = {
            BottomNav(
                currentRoute = currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenRoute.VAULT -> {
                    VaultScreen(
                        cards = cards,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        selectedSortOrder = selectedSortOrder,
                        onSearchQueryChange = { viewModel.searchQuery.value = it },
                        onCategoryChange = { viewModel.selectedCategory.value = it },
                        onSortOrderChange = { viewModel.selectedSortOrder.value = it },
                        onSelectCard = { card -> viewModel.selectCardForDetails(card) },
                        onToggleStar = { card -> viewModel.toggleStarCard(card) },
                        onScanClick = { viewModel.navigateTo(ScreenRoute.SCAN) },
                        onDeleteCard = { viewModel.deleteCard(it) },
                        onRunSampleScan = { viewModel.runSampleCardScan() }
                    )
                }

                ScreenRoute.CARD_DETAILS -> {
                    com.example.ui.screens.CardDetailsScreen(
                        card = selectedCardForDetails,
                        onSaveCard = { updatedCard -> viewModel.updateCard(updatedCard) },
                        onDeleteCard = { cardToDelete -> viewModel.deleteCard(cardToDelete) },
                        onExportToContacts = { cardToExport -> viewModel.exportCardToContacts(cardToExport) },
                        onBack = { viewModel.navigateTo(ScreenRoute.VAULT) }
                    )
                }

                ScreenRoute.SCAN -> {
                    ScanScreen(
                        isScanning = isScanning,
                        onlineAiMode = settingsState.onlineAiMode,
                        isOnline = isOnline,
                        onCaptureCard = { bitmap -> viewModel.processCapturedCard(bitmap) },
                        onRunSampleScan = { viewModel.runSampleCardScan() }
                    )
                }

                ScreenRoute.PREVIEW_EDIT -> {
                    PreviewEditScreen(
                        ocrResult = currentOcrResult,
                        bitmap = currentBitmap,
                        onUpdateResult = { viewModel.updateOcrResult(it) },
                        onConfirmSaveToVault = { onSaved ->
                            viewModel.confirmSaveToVault(onSaved)
                        },
                        onSaveToContacts = {
                            viewModel.saveToPhoneContacts {
                                viewModel.navigateTo(ScreenRoute.VAULT)
                            }
                        },
                        onNavigateToVault = {
                            viewModel.navigateTo(ScreenRoute.VAULT)
                        },
                        onRetakeScan = {
                            viewModel.navigateTo(ScreenRoute.SCAN)
                        },
                        onBack = { viewModel.navigateTo(ScreenRoute.SCAN) }
                    )
                }

                ScreenRoute.SETTINGS -> {
                    SettingsScreen(
                        settings = settingsState,
                        isOnline = isOnline,
                        isStrongConnection = isStrongConnection,
                        onToggleOnlineAiMode = { viewModel.toggleOnlineAiMode(it) },
                        onSetEngineMode = { viewModel.setEngineMode(it) },
                        onConnectDrive = { viewModel.connectGoogleDrive() },
                        onDisconnectDrive = { viewModel.disconnectGoogleDrive() },
                        onToggleDriveBackup = { viewModel.toggleDriveBackup(it) },
                        onBackupNow = { viewModel.triggerBackupNow() },
                        onRestoreNow = { viewModel.triggerRestoreNow() },
                        onToggleAutoLocalBackup = { viewModel.toggleAutoLocalBackup(it) },
                        onExportLocalBackup = { viewModel.exportLocalBackup() },
                        onRestoreLocalBackup = { viewModel.restoreLocalBackup() }
                    )
                }
            }

            // Minimal Glass-style Toast Overlay (PRD Spec Section 12)
            AnimatedVisibility(
                visible = activeToastMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp)
            ) {
                activeToastMessage?.let { msg ->
                    val isOfflineNotice = msg.contains("Offline", ignoreCase = true) || msg.contains("weak", ignoreCase = true)
                    val pillBorderColor = if (isOfflineNotice) AmberOffline else EmeraldOnline

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface)
                            .border(1.dp, pillBorderColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = pillBorderColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = msg,
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
}
