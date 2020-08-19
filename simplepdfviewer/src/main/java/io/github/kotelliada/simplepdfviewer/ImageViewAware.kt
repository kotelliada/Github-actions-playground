package io.github.kotelliada.simplepdfviewer

import android.graphics.Bitmap
import android.os.Looper
import android.widget.ImageView
import androidx.annotation.MainThread
import java.lang.ref.WeakReference

internal class ImageViewAware(imageView: ImageView) {

    private val imageViewRef = WeakReference(imageView)

    fun getId(): Int {
        val imageView = imageViewRef.get()
        return imageView?.hashCode() ?: hashCode()
    }

    @MainThread
    fun setBitmap(bitmap: Bitmap) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            val imageView = imageViewRef.get()
            imageView?.setImageBitmap(bitmap)
        } else {
            throw IllegalStateException("Method has to be called on the main thread")
        }
    }

    fun isCollected(): Boolean {
        return imageViewRef.get() == null
    }
}
