package io.github.kotelliada.simplepdfviewer

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.Handler
import android.os.ParcelFileDescriptor
import io.github.kotelliada.simplepdfviewer.utils.FileUtils
import java.lang.ref.WeakReference

internal class LoadPdfTask(
    pdfView: PdfView,
    private val pdfFileName: String,
    private val context: Context,
    private val mainThreadHandler: Handler
) : Runnable {

    private val pdfViewRef = WeakReference(pdfView)

    override fun run() {
        try {
            val pdfFile = FileUtils.createFileFromAsset(context, pdfFileName)
            val fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fd!!)
            val result = LoadPdfResult(fd, pdfRenderer)
            mainThreadHandler.post { pdfViewRef.get()?.loadComplete(result) }
        } catch (ex: Throwable) {
            mainThreadHandler.post { pdfViewRef.get()?.loadError(ex) }
        }
    }
}
