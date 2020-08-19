package io.github.kotelliada.simplepdfviewer.listener

interface OnLoadingCompletedListener {
    /** Called when a PDF file is successfully loaded */
    fun onLoadingCompleted(pagesCount: Int)
}
