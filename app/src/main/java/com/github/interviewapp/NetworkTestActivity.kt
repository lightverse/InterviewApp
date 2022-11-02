package com.github.interviewapp

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
        showText.movementMethod = ScrollingMovementMethod.getInstance()
        findViewById<Button>(R.id.do_weibo_hot).setOnClickListener {
            val weiboHot = SimpleRetrofit.apiService.weiboHot()
            Thread {
                val get = weiboHot.get()
                val code = get.code()
                runOnUiThread {
                    showText.text = get.body()?.string()
                }
            }.start()
        }

        findViewById<Button>(R.id.do_bli_hot).setOnClickListener {
            val biliHot = SimpleRetrofit.apiService.bilihot()
            Thread {
                var res = biliHot.execute()
                runOnUiThread {
                    showText.text = res.body()?.string()
                }
            }.start()
        }
    }
}