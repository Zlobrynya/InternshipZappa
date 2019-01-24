package com.zlobrynya.internshipzappa.tools


import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import org.jetbrains.anko.doAsync
import java.net.URL
import java.io.*
import java.lang.Exception
import javax.net.ssl.HttpsURLConnection


class PostLoginData {


    fun getPost(entryData:String): Observable<Int>{
        return Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                doAsync{
                    val url = URL("https://118dbdc8-bed8-4697-9c8e-452c67b8eeec.mock.pstmn.io/createaccont/") // URL for request
                    // val sParameters = "login=1&password=2" // POST data
                    val sParameters = entryData // POST data
                    //.i("Check", sParameters)
                    var urlConnection: HttpsURLConnection? = null
                    try {
                        urlConnection = url.openConnection() as HttpsURLConnection
                        urlConnection.requestMethod = "POST"
                        urlConnection.useCaches = false
                        urlConnection.doInput = true
                        urlConnection.doOutput = true
                        //Log.i("Post", urlConnection.responseCode.toString())
                        // Send request
                        val output = DataOutputStream(urlConnection.getOutputStream())
                        output.writeBytes(sParameters)
                        output.flush()
                        output.close()
                        emitter.onNext(urlConnection.responseCode)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        if (urlConnection != null) {
                            urlConnection!!.disconnect()
                        }
                    }
                }
            }
        })
    }

    companion object {
        private var instance: PostLoginData? = null
        fun getInstance(): PostLoginData {
            if (instance == null)
                instance =
                        PostLoginData()
            return instance as PostLoginData

        }
    }
}