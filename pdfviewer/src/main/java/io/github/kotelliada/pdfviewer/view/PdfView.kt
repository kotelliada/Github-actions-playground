package io.github.kotelliada.pdfviewer.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import io.github.kotelliada.pdfviewer.engine.PdfViewEngine

class PdfView : RecyclerView {

    private val engine = PdfViewEngine(this, Handler(Looper.getMainLooper()))

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun fromAssets(pdfFileName: String): PdfView {
        engine.fromAsset(pdfFileName)
        return this
    }

    fun load() {
        engine.load()
    }

    fun release(saveState: Boolean) {
        engine.release(saveState)
    }
}
