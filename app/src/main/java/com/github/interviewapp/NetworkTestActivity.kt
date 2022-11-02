package com.github.interviewapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.core.network.SimpleRetrofit

class NetworkTestActivity : AppCompatActivity() {

    lateinit var showText:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_test)
        supportActionBar?.title = "网络测试"
        showText = findViewById(R.id.request_result)
        findViewById<Button>(R.id.do_request).setOnClickListener {
            val weiboHot = SimpleRetrofit.getServices().weiboHot()
            Thread {
                val get = weiboHot.get()
                val code = get.code()
                runOnUiThread {
                    showText.text = get.body()?.string()
                }
            }.start()
        }
    }
}