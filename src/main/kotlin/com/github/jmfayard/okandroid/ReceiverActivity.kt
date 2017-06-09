package com.github.jmfayard.okandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class ReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_screen)
    }

    override fun onResume() {
        super.onResume()
        val view = findViewById(R.id.detail_label_content) as TextView
        view.text = "Got intent ${intent.description()}"
    }


}
