package nhn.ntech.ndraw.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var isShowOfflineDialog: Boolean = false

    fun shouldShowOfflineDialog(): Boolean {
        if (!isShowOfflineDialog) {
            isShowOfflineDialog = true
            return true
        }
        return false
    }

    fun closeAllDialog() {
        isShowOfflineDialog = false
    }

    private fun resetIsShowOfflineDialog() {
        isShowOfflineDialog = false
    }

    val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                val hasInternet =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                if (hasInternet) resetIsShowOfflineDialog()
                trySend(hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)
        val currentStatus = checkCurrentNetwork()
        trySend(currentStatus)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun checkCurrentNetwork(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    companion object {
        @Volatile
        private var INSTANCE: NetworkObserver? = null

        fun getNetworkObserver(context: Context): NetworkObserver {
            return INSTANCE ?: synchronized(this) {
                val instance = NetworkObserver(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}