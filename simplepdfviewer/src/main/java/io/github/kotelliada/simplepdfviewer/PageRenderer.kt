package io.github.kotelliada.simplepdfviewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer

internal class PageRenderer(
    private val pdfRenderer: PdfRenderer,
    private val pageSizeCalculator: PageSizeCalculator
) {

    fun render(task: RenderPageTask): Bitmap {
        val page = pdfRenderer.openPage(task.pageNumber)
        val (width, height) = pageSizeCalculator.calculate(
            page,
            task.screenWidth,
            task.screenHeight
        )
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        return bitmap
    }
}
