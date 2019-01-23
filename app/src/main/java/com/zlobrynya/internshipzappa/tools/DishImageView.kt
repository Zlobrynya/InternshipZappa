package com.zlobrynya.internshipzappa.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.zlobrynya.internshipzappa.R
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
    var isImageLoad: Boolean = false;
    //ыременно пока нет url на настоящие изображения
    private val string = listOf<String>("hot","salads","soup","nonalc","burger","beer")
    private val SCALE = 0

    fun setURL(url: String){
        if (checkFile(url)){
            downloadFromFile(url)
            textView?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
        }else{
            downloadImg(url)
        }
        isImageLoad = true
    }

    private fun checkFile(path: String): Boolean{
        return File(context.cacheDir.toString() + path.hashCode()).exists()
    }

    //раскодирует файл в bitmap и устанавливается в imageView
    private fun downloadFromFile(path: String){
        doAsync {
            val options = BitmapFactory.Options()
            options.inSampleSize = SCALE
            val bitmap = BitmapFactory.decodeFile(context.cacheDir.toString() + path.hashCode(), options)
            uiThread {
                setImageBitmap(bitmap)
            }
        }

    }

    private fun toFile(bitmap: Bitmap, path: String){
        //создает поток для записи файла, где имя файла есть hashCode ссылки на изображения
        //(хз, как еще их можно назвать)
        val file = File(context.cacheDir.toString() + path.hashCode())
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
    }

    private fun downloadImg(url: String){
        doAsync {
            var bitmap: Bitmap? = null
            try {
                val iamgeUrl = URL(url)
                val conn = iamgeUrl.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val input = BufferedInputStream(conn.inputStream)
                val base64 = getString(input)
                bitmap = devodeBase64(base64)
                toFile(bitmap,url)
            }catch (e: IOException){
                Log.e("ErrorDownload", e.printStackTrace().toString())
                /*if (url in string){
                    val options = BitmapFactory.Options()
                    options.inSampleSize = SCALE
                    bitmap = BitmapFactory.decodeResource(context.getResources(),context.resources.getIdentifier(url,"drawable", context!!.packageName),options);
                    if (bitmap == null){
                        Log.e("ErrorDownload","NULLLLL")
                    }
                    if (!checkFile(url))
                        toFile(bitmap,url)

                }*/
            }
            uiThread {
                setImageBitmap(bitmap)
                textView?.visibility = View.VISIBLE
                progressBar?.visibility = View.GONE
            }
        }
    }

    //Декодирует Base64 в Bitmap
    private fun devodeBase64(base64: String): Bitmap{
        //удалить префикс 'data:image/png;base64,'
        val strBase64 = base64.substring(base64.indexOf(",")+1)
        val stream = ByteArrayInputStream(Base64.decode(strBase64.toByteArray(), Base64.DEFAULT))
        return BitmapFactory.decodeStream(stream)
    }

    //Преобразует из потока в строку
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
        input.close()
        return out.toString()
    }


}