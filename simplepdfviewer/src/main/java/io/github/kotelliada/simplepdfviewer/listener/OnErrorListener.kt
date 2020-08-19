package io.github.kotelliada.simplepdfviewer.listener

interface OnErrorListener {
    /** Called when an error occurs while opening a PDF file */
    fun onError(ex: Throwable)
}
