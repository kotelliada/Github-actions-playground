package io.github.kotelliada.simplepdfviewer

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor

internal class LoadPdfResult(
    val fd: ParcelFileDescriptor,
    val pdfRenderer: PdfRenderer
)
