package com.zlobrynya.internshipzappa.tools.retrofit

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import org.jetbrains.anko.doAsync
import java.io.*
import java.lang.Exception
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

class LogCheck {
    fun getLog():Observable<String>{
        return Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                doAsync{
                    val endpointUtl = URL("https://na-rogah-api.herokuapp.com/check_update")
                    val conn = endpointUtl.openConnection() as HttpURLConnection
                    var strLog: String
                    try {
                        conn.requestMethod = "GET"
                        val input = BufferedInputStream(conn.inputStream)
                        strLog = convertStreamToString(input)
                        Log.i("parse", strLog)
                        emitter.onNext(strLog)
                    } catch (e: Exception) {
                        Log.i("parse", "error")
                    }
                }
            }
        })
    }
    private fun convertStreamToString(input: InputStream): String {
        val reader = BufferedReader(InputStreamReader(input))
        val sb = StringBuilder()

        var line: String?
        try {
            do{
                line = reader.readLine()
                if (line == null)
                    break
                sb.append(line).append('\n')
            }while (true)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return sb.toString()
    }
    companion object {
        private var instance: LogCheck? = null
        fun getInstance(): LogCheck {
            if (instance == null)
                instance =
                    LogCheck()
            return instance as LogCheck

        }
    }

}