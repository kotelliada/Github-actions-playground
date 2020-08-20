package io.github.kotelliada.simplepdfviewer.rendering

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import io.github.kotelliada.simplepdfviewer.RenderPageRequest

internal class PageRenderer(
    private val pdfRenderer: PdfRenderer,
    private val pageSizeCalculator: PageSizeCalculator
) {

    fun render(request: RenderPageRequest): Bitmap {
        val page = pdfRenderer.openPage(request.pageNumber)
        val (width, height) = pageSizeCalculator.calculate(
            page,
            request.screenWidth,
            request.screenHeight
        )
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        return bitmap
    }
}
