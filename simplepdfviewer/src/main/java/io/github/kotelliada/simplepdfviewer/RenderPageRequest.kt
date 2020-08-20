package io.github.kotelliada.simplepdfviewer

import io.github.kotelliada.simplepdfviewer.aware.ImageViewAware

internal class RenderPageRequest(
    val imageViewAware: ImageViewAware,
    val pageNumber: Int,
    val screenWidth: Int,
    val screenHeight: Int
)
