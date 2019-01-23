package com.zlobrynya.internshipzappa.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.Base64
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


/*
*  View на замену ImageView в item_category
*  Качает с сервера Base64 код изображения, перекодирует, и вставляется в ImageView
*  кроме того, сохраняет в кэш память, и при повторном запуске, качает уже с кэш памяти
*  По окончании загрузки, делает видимой textView и скрывает prograssBar
 */

class DishImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {
    var progressBar: ProgressBar? = null
    var textView: TextView? = null
    private var bitmap: Bitmap? = null


    fun setURL(url: String){

    }

    private fun downloadFromFile(path: String){

    }

    private fun toFile(name: String){

    }

    private fun downloadImg(url: String){
        doAsync {
            val iamgeUrl = URL(url)
            val conn = iamgeUrl.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            val input = BufferedInputStream(conn.inputStream)
            var base64 = getString(input)
            val bitmap = devodeBase64(base64)

            //удалить префикс 'data:image/png;base64,'
            uiThread {
                setImageBitmap(bitmap)
            }
        }
    }

    //Декодирует Base64 в Bitmap
    private fun devodeBase64(base64: String): Bitmap{
        val strBase64 = base64.substring(base64.indexOf(",")+1)
        val stream = ByteArrayInputStream(Base64.decode(strBase64.toByteArray(), Base64.DEFAULT))
        return BitmapFactory.decodeStream(stream)
    }

    private fun getString(inputStream: BufferedInputStream): String{
        val bufferSize = 1024
        val buffer = CharArray(bufferSize)
        val out = StringBuilder()
        val input = InputStreamReader(inputStream, "UTF-8")
        while (true) {
            val rsz = input.read(buffer, 0, buffer.size)
            if (rsz < 0)
                break
            out.append(buffer, 0, rsz)
        }
        return out.toString()
    }


}