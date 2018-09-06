package com.github.jmfayard.okandroid

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_screen)
    }

    override fun onResume() {
        super.onResume()
        val view : TextView = findViewById(R.id.detail_label_content)
        view.text = "Got intent ${intent.description()}"
    }


}
