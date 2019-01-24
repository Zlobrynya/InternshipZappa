package com.zlobrynya.internshipzappa


import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log
import org.jetbrains.anko.doAsync
import java.net.URL
import java.io.*
import java.lang.Exception
import javax.net.ssl.HttpsURLConnection


class PostLoginData {
     fun postServer( entryData:String){
        doAsync{
            val url = URL("https://118dbdc8-bed8-4697-9c8e-452c67b8eeec.mock.pstmn.io/createaccont/") // URL for request
            // val sParameters = "login=1&password=2" // POST data
            val sParameters = entryData // POST data
            Log.i("Check", sParameters)
            var urlConnection: HttpsURLConnection? = null
            try {
                urlConnection = url.openConnection() as HttpsURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.useCaches = false
                urlConnection.doInput = true
                urlConnection.doOutput = true
                //проверка на наличие в преф допилить не смог
                /*if (urlConnection.responseCode == 200){
                      val sPref = getPreferences(Context.MODE_PRIVATE)
                      val savedText = sPref.getString(email.text.toString() ,"")
                      Log.i("Check", savedText.toString())
                      if(savedText==null)
                          Log.i("Check", "nope")

                    val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
                    var editor = sharedPreferences.edit()
                    editor.putString("username","qwerty")
                    editor.commit()
                    Log.i("Check", sharedPreferences.getString("users", "").toString())
                    if(sharedPreferences.getString("users", email.text.toString())!=null)
                    {
                        Log.i("Check", "nope")
                    }
                }*/
                Log.i("Post", urlConnection.responseCode.toString())
                // Send request
                val output = DataOutputStream(urlConnection.getOutputStream())
                output.writeBytes(sParameters)
                output.flush()
                output.close()
                val reader = BufferedReader(InputStreamReader(urlConnection.getInputStream(), "UTF-8"))
                var line: String? = null
                while (true){
                    line = reader.readLine()
                    if (line == null)
                        break
                    Log.i("Post", line)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (urlConnection != null) {
                    urlConnection!!.disconnect()
                }
            }

        }
    }
    companion object {
        private var instance: PostLoginData? = null
        fun getInstance(): PostLoginData {
            if (instance == null)
                instance = PostLoginData()
            return instance as PostLoginData

        }
    }
}