package io.github.kotelliada.simplepdfviewer.tasks

import android.graphics.Bitmap
import android.os.Handler
import io.github.kotelliada.simplepdfviewer.rendering.PageRenderer
import io.github.kotelliada.simplepdfviewer.RenderPageRequest
import io.github.kotelliada.simplepdfviewer.rendering.BitmapsCache
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

internal class RenderPdfTask(
    private val pageRenderer: PageRenderer,
    private val requestsQueue: BlockingQueue<RenderPageRequest>,
    private val isRecycled: AtomicBoolean,
    private val recycledImageViews: Map<Int, Int>,
    private val mainThreadHandler: Handler,
    private val bitmapsCache: BitmapsCache
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

            val cached = getCached(request)

            if (cached != null) {
                mainThreadHandler.post {
                    if (shouldRenderThePage(request)) {
                        request.imageViewAware.setBitmap(cached)
                    }
                }
                continue
            }

            val bitmap = pageRenderer.render(request)

            if (isRecycled.get()) {
                break
            }

            if (shouldRenderThePage(request)) {
                saveToCache(bitmap, request)
                mainThreadHandler.post {
                    if (shouldRenderThePage(request)) {
                        request.imageViewAware.setBitmap(bitmap)
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
        return bitmapsCache.get(BitmapsCache.buildKey(request.pageNumber))
    }

    private fun saveToCache(bitmap: Bitmap, request: RenderPageRequest) {
        val key = BitmapsCache.buildKey(request.pageNumber)
        bitmapsCache.put(key, bitmap)
    }
}
