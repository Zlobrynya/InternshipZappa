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

    fun getLog():Observable<LogClass>{
        return Observable.create(object : ObservableOnSubscribe<LogClass> {
            override fun subscribe(emitter: ObservableEmitter<LogClass>) {
                doAsync{
                    val logClass = LogClass()
                    val endpointUtl = URL("https://na-rogah-api.herokuapp.com/check_update")
                    val conn = endpointUtl.openConnection() as HttpURLConnection
                    try {
                        conn.requestMethod = "GET"
                        logClass.code = conn.responseCode
                        //Log.i("parse", conn.responseCode.toString())
                        val input = BufferedInputStream(conn.inputStream)
                        logClass.str = convertStreamToString(input)
                        Log.i("parse", logClass.str)
                        emitter.onNext(logClass)
                    } catch (e: Exception) {
                        emitter.onNext(logClass)
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