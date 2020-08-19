package io.github.kotelliada.simplepdfviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.github.kotelliada.simplepdfviewer.listener.OnLoadingCompletedListener
import io.github.kotelliada.simplepdfviewer.listener.OnLoadingFailedListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pdfView.fromAsset("sample.pdf")
            .setOnLoadingCompletedListener(object : OnLoadingCompletedListener {
                override fun onLoadingCompleted(pagesCount: Int) {
                    Toast.makeText(
                        applicationContext,
                        "Loading completed. Pages count: $pagesCount",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            .setOnLoadingFailedListener(object : OnLoadingFailedListener {
                override fun onLoadingFailed(ex: Throwable) {
                    Toast.makeText(applicationContext, "Loading failed", Toast.LENGTH_LONG).show()
                }
            })
            .load()
    }
}
