package com.example.settings

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Monitors device network connectivity state & signal quality.
 */
class NetworkMonitor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isOnline = MutableStateFlow(checkInitialNetworkState())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isStrongConnection = MutableStateFlow(true)
    val isStrongConnection: StateFlow<Boolean> = _isStrongConnection.asStateFlow()

    init {
        registerNetworkCallback()
    }

    private fun checkInitialNetworkState(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
                checkSignalStrength(network)
            }

            override fun onLost(network: Network) {
                _isOnline.value = false
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                _isOnline.value = hasInternet
                if (hasInternet) {
                    checkSignalStrength(network)
                }
            }
        })
    }

    private fun checkSignalStrength(network: Network) {
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        if (capabilities != null) {
            val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            val linkDownstream = capabilities.linkDownstreamBandwidthKbps
            // Connection is considered strong if wifi or broadband > 1000 kbps
            _isStrongConnection.value = isWifi || (isCellular && linkDownstream > 1000)
        }
    }
}
