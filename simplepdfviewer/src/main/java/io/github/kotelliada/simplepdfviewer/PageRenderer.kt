package io.github.kotelliada.simplepdfviewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer

internal class PageRenderer(private val pdfRenderer: PdfRenderer) {

    // TODO: Implement calculation of width and height
    fun render(task: RenderPageTask): Bitmap {
        val page = pdfRenderer.openPage(task.pageNumber)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        return bitmap
    }

}
