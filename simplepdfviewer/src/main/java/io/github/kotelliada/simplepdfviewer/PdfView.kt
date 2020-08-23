package io.github.kotelliada.simplepdfviewer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.kotelliada.simplepdfviewer.aware.ImageViewAware
import io.github.kotelliada.simplepdfviewer.listener.OnLoadingCompletedListener
import io.github.kotelliada.simplepdfviewer.listener.OnLoadingFailedListener
import io.github.kotelliada.simplepdfviewer.rendering.BitmapsCache
import io.github.kotelliada.simplepdfviewer.rendering.PageRenderer
import io.github.kotelliada.simplepdfviewer.rendering.PageSizeCalculator
import io.github.kotelliada.simplepdfviewer.tasks.LoadPdfTask
import io.github.kotelliada.simplepdfviewer.tasks.ReleaseResourcesTask
import io.github.kotelliada.simplepdfviewer.tasks.RenderPdfTask
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap

class PdfView : RecyclerView {

    private val isRecycled = AtomicBoolean(true)

    private var loadPdfResult: LoadPdfResult? = null

    private var recyclingImageViews: MutableMap<Int, Int>? = null

    private var tasksExecutor: ExecutorService? = null

    private var requests: BlockingQueue<RenderPageRequest>? = null

    private var bitmapsCache: BitmapsCache? = null

    private var onLoadingFailedListener: OnLoadingFailedListener? = null
    private var onLoadingCompletedListener: OnLoadingCompletedListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recycle()
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        throwFromUnsupportedMethod()
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        throwFromUnsupportedMethod()
    }

    private inline fun throwFromUnsupportedMethod() {
        throw UnsupportedOperationException("You are not allowed to call this method")
    }

    fun fromAsset(pdfFileName: String): Configuration {
        return Configuration(pdfFileName)
    }

    @MainThread
    internal fun loadError(ex: Throwable) {
        onLoadingFailedListener?.onLoadingFailed(ex)
        recycle()
    }

    @MainThread
    internal fun loadComplete(result: LoadPdfResult) {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        onLoadingCompletedListener?.onLoadingCompleted(result.pdfRenderer.pageCount)
        loadPdfResult = result
        bitmapsCache = BitmapsCache(cacheSize)
        requests = LinkedBlockingQueue<RenderPageRequest>()
        recyclingImageViews = Collections.synchronizedMap(HashMap())
        val pageRenderer = PageRenderer(loadPdfResult!!.pdfRenderer, PageSizeCalculator())
        tasksExecutor!!.execute(
            RenderPdfTask(
                pageRenderer,
                requests!!,
                isRecycled,
                recyclingImageViews!!,
                Handler(Looper.getMainLooper()),
                bitmapsCache!!
            )
        )
        super.setAdapter(PdfViewAdapter(context, result.pdfRenderer.pageCount))
        super.setLayoutManager(LinearLayoutManager(context))
    }

    private fun load(pdfFileName: String) {
        if (!isRecycled.get()) {
            throw IllegalStateException("PdfView should be recycled first")
        }

        isRecycled.set(false)

        tasksExecutor = Executors.newSingleThreadExecutor()
        tasksExecutor!!.execute(
            LoadPdfTask(
                this,
                pdfFileName,
                context.applicationContext,
                Handler(Looper.getMainLooper())
            )
        )
    }

    private fun recycle() {
        isRecycled.set(true)
        if (loadPdfResult != null) {
            tasksExecutor?.execute(ReleaseResourcesTask(loadPdfResult!!, bitmapsCache!!))
            loadPdfResult = null
        }
        bitmapsCache = null
        requests = null
        tasksExecutor?.shutdown()
        tasksExecutor = null
        recyclingImageViews = null
        onLoadingFailedListener = null
    }

    private fun render(imageView: ImageView, pageNumber: Int) {
        if (!isRecycled.get()) {
            val imageViewAware = ImageViewAware(imageView)
            recyclingImageViews!![imageViewAware.getId()] = pageNumber
            val request = RenderPageRequest(
                imageViewAware,
                pageNumber,
                measuredWidth,
                measuredHeight
            )
            requests!!.put(request)
        }
    }

    private fun setOnLoadingFailedListener(listener: OnLoadingFailedListener?) {
        onLoadingFailedListener = listener
    }

    private fun setOnLoadingCompletedListener(listener: OnLoadingCompletedListener?) {
        onLoadingCompletedListener = listener
    }

    inner class Configuration(private val pdfFileName: String) {

        private var onLoadingFailedListener: OnLoadingFailedListener? = null
        private var onLoadingCompletedListener: OnLoadingCompletedListener? = null

        fun setOnLoadingFailedListener(listener: OnLoadingFailedListener): Configuration {
            this.onLoadingFailedListener = listener
            return this
        }

        fun setOnLoadingCompletedListener(listener: OnLoadingCompletedListener): Configuration {
            this.onLoadingCompletedListener = listener
            return this
        }

        fun load() {
            this@PdfView.recycle()
            this@PdfView.setOnLoadingFailedListener(this.onLoadingFailedListener)
            this@PdfView.setOnLoadingCompletedListener(this.onLoadingCompletedListener)
            this@PdfView.load(pdfFileName)
        }
    }

    internal inner class PdfViewAdapter(
        context: Context,
        private val pagesCount: Int
    ) : RecyclerView.Adapter<PdfViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
            val itemView = inflater.inflate(R.layout.list_item_default, parent, false)
            return PdfViewHolder(itemView)
        }

        override fun getItemCount(): Int = pagesCount

        override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
            this@PdfView.render(holder.pdfImageView, position)
        }
    }

    internal class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pdfImageView = itemView.findViewById<ImageView>(R.id.pdfImg)!!
    }
}
