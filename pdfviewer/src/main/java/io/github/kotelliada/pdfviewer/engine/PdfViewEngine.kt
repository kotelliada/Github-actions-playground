package io.github.kotelliada.pdfviewer.engine

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.Handler
import android.os.ParcelFileDescriptor
import io.github.kotelliada.pdfviewer.utils.FileUtils
import io.github.kotelliada.pdfviewer.view.PdfView
import java.util.concurrent.Executors

internal class PdfViewEngine(
    private val pdfView: PdfView,
    private val mainThreadHandler: Handler
) {

    private val context: Context
        get() = pdfView.context.applicationContext

    private val tasksExecutor = Executors.newSingleThreadExecutor()

    @Volatile
    private var pdfFileName: String? = null

    @Volatile
    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    @Volatile
    private var pdfRenderer: PdfRenderer? = null

    @Volatile
    private var isInitialized = false

    fun fromAsset(pdfFileName: String) {
        throwIfAlreadyInitialized()
        this.pdfFileName = pdfFileName
    }

    fun load() {
        throwIfAlreadyInitialized()

        if (pdfFileName == null) {
            throw IllegalStateException("PdfFileName is null")
        }

        isInitialized = true

        tasksExecutor.execute {
            try {
                val pdfFile = FileUtils.createFileFromAsset(context, pdfFileName!!)
                parcelFileDescriptor = ParcelFileDescriptor.open(
                    pdfFile,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
                pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
            } catch (ex: Throwable) {
                isInitialized = false
                mainThreadHandler.post { throw ex }
            }
        }
    }

    fun release(saveState: Boolean) {
        // 1. Release page
        // 2. Release renderer
        // 3. Release file descriptor
        // 4. IsInitialized = false
    }

    private fun throwIfAlreadyInitialized() {
        if (isInitialized) {
            throw IllegalStateException("PdfView is already initialized. Call release() first.")
        }
    }
}
