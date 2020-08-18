package io.github.kotelliada.simplepdfviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pdfView.fromAssets("sample.pdf")
            .load()
    }

    override fun onDestroy() {
        pdfView.release(false)
        super.onDestroy()
    }
}
