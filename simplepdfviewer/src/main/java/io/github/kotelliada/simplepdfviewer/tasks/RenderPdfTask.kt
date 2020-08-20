package io.github.kotelliada.simplepdfviewer.tasks

import android.graphics.Bitmap
import android.os.Handler
import io.github.kotelliada.simplepdfviewer.rendering.PageRenderer
import io.github.kotelliada.simplepdfviewer.RenderPageRequest
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

internal class RenderPdfTask(
    private val pageRenderer: PageRenderer,
    private val requestsQueue: BlockingQueue<RenderPageRequest>,
    private val isRecycled: AtomicBoolean,
    private val recycledImageViews: ConcurrentMap<Int, Int>,
    private val mainThreadHandler: Handler
) : Runnable {

    override fun run() {
        while (true) {
            if (isRecycled.get()) {
                break
            }

            val request = requestsQueue.poll(500, TimeUnit.MILLISECONDS) ?: continue

            if (!shouldRenderThePage(request)) {
                continue
            }

            var cached = getCached(request)
            if (cached == null) {
                cached = pageRenderer.render(request)
            }

            if (isRecycled.get()) {
                break
            }

            if (shouldRenderThePage(request)) {
                saveToCache(cached)
                mainThreadHandler.post {
                    if (shouldRenderThePage(request)) {
                        request.imageViewAware.setBitmap(cached)
                    }
                }
            }
        }
    }

    private fun shouldRenderThePage(request: RenderPageRequest): Boolean {
        return !request.imageViewAware.isCollected() &&
                request.pageNumber == recycledImageViews[request.imageViewAware.getId()]

    }

    private fun getCached(request: RenderPageRequest): Bitmap? {
        return null
    }

    private fun saveToCache(bitmap: Bitmap) {
    }
}
