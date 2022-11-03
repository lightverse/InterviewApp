package com.github.core.network

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.http.GET
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
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
    fun bilihot():Call<ResponseBody> //使用默认BuiltInConverters
}

interface JsonResultService{

    @GET(value = "resou")
    fun weiboHot():MyJob<String> //返回不是ResponseBody了但是Response还在

    @GET(value = "bilihot")
    fun bilihot():MyJob<String>

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

class StringCallAdapterFactory:CallAdapter.Factory(){
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        val parameterUpperBound = getParameterUpperBound(0,returnType as ParameterizedType)
        return StringCallAdapter<Any>(parameterUpperBound)
    }


}
interface MyJob<T>{
    fun run(res:ResultBack<T>)
}

interface ResultBack<T>{
    fun onResult(t:T)
}

class StringCallAdapter<R>(val returnType: Type) :CallAdapter<R,MyJob<R>>{
    override fun responseType(): Type  = returnType
    override fun adapt(call: Call<R>): MyJob<R> {
        return object :MyJob<R>{
            override fun run(res: ResultBack<R>) {
                call.enqueue(object :Callback<R>{
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        //这里实现了将响应的泛型 和 返回值泛型结合 注意此处泛型设计
                        res.onResult(response.body()!!)
                    }

                    override fun onFailure(call: Call<R>, t: Throwable) {
                    }
                })
            }

        }
    }
}
class StringConvertFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if(getRawType(type) != String::class.java){
            return null
        }
        return StringConvert()
    }

    class StringConvert:Converter<ResponseBody,String>{
        override fun convert(value: ResponseBody): String? {
            return value.string()
        }
    }
}


object StringResultRetrofit{
    val apiService by lazy {
        createRetrofitBuilder()
            .addConverterFactory(StringConvertFactory())
            .addCallAdapterFactory(StringCallAdapterFactory())
            .build().create(JsonResultService::class.java)
    }
}

