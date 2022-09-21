package com.vibs.maashakti.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData

class NetworkReceiver : BroadcastReceiver() {

    companion object {
        val internetObservable = MutableLiveData<Boolean>()
        private const val TAG = "NetworkReceiver"
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        private var isInternetActive = true

        fun isNetworkConnected(context: Context): Boolean {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork      = connectivityManager.activeNetwork ?: return false
                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    //for other device how are able to connect with Ethernet
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    //for check internet over Bluetooth
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                    else -> false
                }
            } else {
                val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                return nwInfo.isConnected
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val connMgr: ConnectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi: NetworkInfo? = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile: NetworkInfo? = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifi?.isConnected == true || mobile?.isConnected == true) {
            // Do something
            Log.d(TAG, "onReceive: active")
            if (isNetworkConnected(context)) {
                if (!isInternetActive){
                    internetObservable.postValue(true)
                    isInternetActive = true
                }
            }
        } else {
            Log.d(TAG, "onReceive: inactive")
            if (isInternetActive){
                internetObservable.postValue(false)
                isInternetActive = false
            }
        }
    }

}
