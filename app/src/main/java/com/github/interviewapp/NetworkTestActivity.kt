package com.github.interviewapp

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.core.network.ResultBack
import com.github.core.network.SimpleRetrofit
import com.github.core.network.StringResultRetrofit
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CompletableFuture

class NetworkTestActivity : AppCompatActivity() {

    lateinit var showText:TextView

    lateinit var stringConvert:CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_test)
        supportActionBar?.title = "网络测试"
        showText = findViewById(R.id.request_result)
        stringConvert = findViewById(R.id.use_string_convert)
        showText.movementMethod = ScrollingMovementMethod.getInstance()
        findViewById<Button>(R.id.do_weibo_hot).setOnClickListener {

            if(stringConvert.isChecked){
                Thread {
                    try {
                        val weiboHot = StringResultRetrofit.apiService.weiboHot()
                        runOnUiThread {
                            showText.text = weiboHot.toString()
                        }
                    }catch (e:Exception){
                        println(e.stackTrace)
                    }

                }.start()
            }else{
                Thread {
                    try {
                        val weiboHot = SimpleRetrofit.apiService.weiboHot()
                        val res = weiboHot.get()
                        runOnUiThread {
                            showText.text = res.body()?.string()
                        }
                    }catch (e:Exception){
                        println(e.stackTrace)
                    }

                }.start()
            }

        }

        findViewById<Button>(R.id.do_bli_hot).setOnClickListener {
            if(stringConvert.isChecked){
                val biliHot = StringResultRetrofit.apiService.bilihot()
                biliHot.run(object :ResultBack<String>{
                    override fun onResult(t: String) {
                        showText.text = t
                    }

                })
            }else{
                val biliHot = SimpleRetrofit.apiService.bilihot()
                biliHot.enqueue(object :Callback<ResponseBody>{
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        showText.text = response.body().toString()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    }

                })
            }

        }
    }
}