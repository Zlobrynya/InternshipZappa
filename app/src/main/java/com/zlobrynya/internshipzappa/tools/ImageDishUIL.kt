package com.zlobrynya.internshipzappa.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.support.v4.content.Loader
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.zlobrynya.internshipzappa.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.net.HttpURLConnection
import java.net.URL



class ImageDishUIL(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    val config = ImageLoaderConfiguration.Builder(context)
        .imageDownloader(BaseImageDownloader(context, 5 *1000, 30*1000 ))

    fun uilImage(url: String){
        val imageLoader = ImageLoader.getInstance()
        val imageView: ImageDishUIL? = findViewById(R.id.imageView)
        val spinner: ProgressBar? = findViewById(R.id.progressBar)
        imageLoader.init(config.build())
        imageLoader.displayImage("https://miro.medium.com/max/712/1*EnF9uIN_u2_X7ey24lB7Tg.png", imageView, SimpleImageLoadingListener( ))

    }
}

