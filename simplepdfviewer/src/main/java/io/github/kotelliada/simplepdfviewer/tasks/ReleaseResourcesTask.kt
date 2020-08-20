package io.github.kotelliada.simplepdfviewer.tasks

import io.github.kotelliada.simplepdfviewer.LoadPdfResult

internal class ReleaseResourcesTask(private val loadPdfResult: LoadPdfResult) : Runnable {

    override fun run() {
        loadPdfResult.fd.close()
        loadPdfResult.pdfRenderer.close()
    }
}
