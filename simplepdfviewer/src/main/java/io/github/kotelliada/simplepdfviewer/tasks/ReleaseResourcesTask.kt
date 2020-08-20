package io.github.kotelliada.simplepdfviewer.tasks

import io.github.kotelliada.simplepdfviewer.LoadPdfResult
import io.github.kotelliada.simplepdfviewer.rendering.BitmapsCache

internal class ReleaseResourcesTask(
    private val loadPdfResult: LoadPdfResult,
    private val bitmapsCache: BitmapsCache
) : Runnable {

    override fun run() {
        loadPdfResult.fd.close()
        loadPdfResult.pdfRenderer.close()
        bitmapsCache.clear()
    }
}
