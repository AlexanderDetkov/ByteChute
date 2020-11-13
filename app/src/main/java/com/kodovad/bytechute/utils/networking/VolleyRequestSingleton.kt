package com.kodovad.bytechute.utils.networking

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy

class VolleyRequestSingleton constructor(context: Context){
    companion object {
        @Volatile
        private var INSTANCE: VolleyRequestSingleton? = null
        fun getInstance(context: Context) =
            INSTANCE
                ?: synchronized(this) {
                INSTANCE
                    ?: VolleyRequestSingleton(
                        context
                    ).also {
                    INSTANCE = it
                }
            }
    }

    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String): Bitmap {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    val persistentCookieStore: PersistentCookieStore by lazy {
        PersistentCookieStore(context.applicationContext)
    }

    val requestQueue: RequestQueue by lazy {

        //  persistent cookies even after closing app
        CookieHandler.setDefault(CookieManager(persistentCookieStore, CookiePolicy.ACCEPT_ALL))

        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

}