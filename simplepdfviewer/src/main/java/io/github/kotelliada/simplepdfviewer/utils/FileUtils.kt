package io.github.kotelliada.simplepdfviewer.utils

import android.content.Context
import androidx.annotation.WorkerThread
import java.io.*

internal object FileUtils {

    @WorkerThread
    fun createFileFromAsset(context: Context, assetName: String): File {
        val outFile = File(context.cacheDir, assetName)
        if (outFile.exists()) {
            return outFile
        }

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = context.assets.open(assetName)
            outputStream = FileOutputStream(outFile)

            inputStream.copyTo(outputStream, IoUtils.DEFAULT_BUFFER_SIZE)
            outputStream.flush()
        } catch (ignored: Throwable) {
            // NOP
        } finally {
            try {
                inputStream?.close()
            } catch (ignored: Throwable) {
                // NOP
            }

            try {
                outputStream?.close()
            } catch (ignored: Throwable) {
                // NOP
            }
        }

        return outFile
    }

}
