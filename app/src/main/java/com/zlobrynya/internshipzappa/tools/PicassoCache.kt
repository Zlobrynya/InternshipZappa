import android.content.ContentValues.TAG
import android.content.Context
import com.squareup.picasso.Downloader
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import android.net.http.HttpResponseCache
import android.util.Log
import java.io.File
import java.io.IOException


class PicassoCache
/**
 * PicassoCache Constructor
 *
 * @param context application Context
 */
private constructor(context: Context) {

    init {

        val downloader = OkHttp3Downloader(context, Long.MAX_VALUE)
        val builder = Picasso.Builder(context)
        builder.downloader(downloader)

        picassoInstance = builder.build()
    }

    companion object {

        /**
         * Static Picasso Instance
         */
        private var picassoInstance: Picasso? = null

        /**
         * Get Singleton Picasso Instance
         *
         * @param context application Context
         * @return Picasso instance
         */
        fun getPicassoInstance(context: Context): Picasso? {

            try {
                val httpCacheDir = File(context.cacheDir, "http")
                val httpCacheSize = (2 * 2).toLong() //Размеры чисто для теста. Реальные цифры 1024 * 1024 * 10 = 10MB
                HttpResponseCache.install(httpCacheDir, httpCacheSize)
            } catch (e: IOException) {
                Log.i(TAG, "HTTP response cache installation failed:$e")
            }


            if (picassoInstance == null) {

                PicassoCache(context)
                return picassoInstance
            }

            return picassoInstance
        }
    }

} 