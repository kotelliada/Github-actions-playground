package io.github.kotelliada.simplepdfviewer

import android.graphics.Bitmap
import android.os.Handler
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicBoolean

internal class RenderPdfTask(
    private val pageRenderer: PageRenderer,
    private val tasksQueue: BlockingQueue<RenderPageTask>,
    private val isRecycled: AtomicBoolean,
    private val recycledImageViews: ConcurrentMap<Int, Int>,
    private val mainThreadHandler: Handler
) : Runnable {

    override fun run() {
        while (true) {
            if (isRecycled.get()) {
                break
            }

            val task = tasksQueue.take()
            if (!shouldRenderThePage(task)) {
                continue
            }

            var cached = getCached(task)
            if (cached == null) {
                cached = pageRenderer.render(task)
            }

            if (isRecycled.get()) {
                break
            }

            if (shouldRenderThePage(task)) {
                saveToCache(cached)
                mainThreadHandler.post {
                    if (shouldRenderThePage(task)) {
                        task.imageViewAware.setBitmap(cached)
                    }
                }
            }
        }
    }

    private fun shouldRenderThePage(task: RenderPageTask): Boolean {
        return !task.imageViewAware.isCollected() &&
                task.pageNumber == recycledImageViews[task.imageViewAware.getId()]

    }

    private fun getCached(task: RenderPageTask): Bitmap? {
        return null
    }

    private fun saveToCache(bitmap: Bitmap) {
    }
}
