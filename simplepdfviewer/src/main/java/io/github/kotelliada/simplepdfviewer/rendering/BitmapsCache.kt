package io.github.kotelliada.simplepdfviewer.rendering

import android.graphics.Bitmap
import android.util.LruCache

class BitmapsCache(cacheSize: Int) : LruCache<String, Bitmap>(cacheSize) {

    fun clear() = evictAll()

    override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024

    companion object {
        fun buildKey(pageNumber: Int): String = "$pageNumber"
    }
}
