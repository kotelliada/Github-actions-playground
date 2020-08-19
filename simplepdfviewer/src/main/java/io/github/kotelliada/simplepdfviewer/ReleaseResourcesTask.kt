package io.github.kotelliada.simplepdfviewer

internal class ReleaseResourcesTask(private val loadPdfResult: LoadPdfResult) : Runnable {

    override fun run() {
        loadPdfResult.fd.close()
        loadPdfResult.pdfRenderer.close()
    }
}
