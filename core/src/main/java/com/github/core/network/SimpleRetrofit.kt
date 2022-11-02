package com.github.core.network

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit


/**
 *
 * 使用Retrofit实现网络请求
 */


interface ApiService{

    @GET(value = "resou")
    fun weiboHot():CompletableFuture<Response<ResponseBody>> //如果都是默认 返回值可以是这个

    @GET(value = "bilihot")
    fun bilihot():Call<ResponseBody>
}

interface JsonResultService{

    @GET(value = "resou")
    fun weiboHot():CompletableFuture<Response<ResponseBody>> //如果都是默认 返回值可以是这个

}


fun createRetrofitBuilder():Retrofit.Builder{
    val DEFAULT_TIMEOUT:Long = 10
    val BASE_URL:String = "https://tenapi.cn"
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    //如果初始化retrofit没配置 返回值如何解析 默认只支持两种返回值 CompleteFuture和Call,如果写其他会报错
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
    return retrofit
}


object SimpleRetrofit{

    val apiService by lazy {
        createRetrofitBuilder().build().create(ApiService::class.java)
    }

}

