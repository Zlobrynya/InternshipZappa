package com.zlobrynya.internshipzappa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache
import com.nostra13.universalimageloader.core.download.ImageDownloader
import java.io.InputStream

import java.security.AccessController.getContext


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, FullDescriptionScreen::class.java)
        startActivity(intent)
        //UIL config
        val config = ImageLoaderConfiguration.Builder(this)
            .threadPoolSize(3)
            .denyCacheImageMultipleSizesInMemory()
            .memoryCache(UsingFreqLimitedMemoryCache(8 * 1024 * 1024)) // 2 Mb
            .diskCache(LimitedAgeDiskCache(cacheDir, null, HashCodeFileNameGenerator(), (60 * 60 * 30).toLong()))
            .imageDownloader(BaseImageDownloader(this)) // connectTimeout (5 s), readTimeout (30 s)
            .build()
        ImageLoader.getInstance().init(config)
    }
}
