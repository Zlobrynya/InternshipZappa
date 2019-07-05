package com.zlobrynya.narogah.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class StaticMethods {
    companion object {

        /**
         * Проверка на наличие интерента
         * @return Наличие интенета
         */
        fun checkInternetConnection(context: Context?): Boolean {
            val connected: Boolean
            if (context != null) {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                connected =
                    connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(
                        ConnectivityManager.TYPE_WIFI
                    ).state == NetworkInfo.State.CONNECTED
                return connected
            }
            return false
        }
    }
}