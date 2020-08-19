package io.github.kotelliada.simplepdfviewer.listener

interface OnLoadingFailedListener {
    /** Called when an error occurs while opening a PDF file */
    fun onLoadingFailed(ex: Throwable)
}
