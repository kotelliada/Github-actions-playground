package io.github.kotelliada.simplepdfviewer

import android.graphics.pdf.PdfRenderer

internal class PageSizeCalculator {

    // Returns width and height
    // TODO: Improve calculation logic
    fun calculate(page: PdfRenderer.Page, screenWidth: Int, screenHeight: Int): Pair<Int, Int> {
        val width = screenWidth
        val height = page.height * screenWidth / page.width
        return Pair(width, height)
    }
}
