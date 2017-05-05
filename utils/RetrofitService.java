package com.fta.myapplication.recyclerviewpack;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 文件描述：
 * 作者： Created by fta on 2017/4/26.
 */

public class RetrofitService {
    private volatile static RetrofitService ourInstance;


    private RetrofitService() {

    }

    public static RetrofitService getInstance() {
        if (ourInstance == null) {
            synchronized (RetrofitService.class) {
                if (ourInstance == null) {
                    ourInstance = new RetrofitService();
                }
            }
        }
        return ourInstance;
    }
    private OkHttpClient getOkHttpClient(){
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//默认就是 10 秒
                .readTimeout(10,TimeUnit.SECONDS)//默认 10 秒
                .build();
    }
    public Retrofit getRetrofit(String url){
        return new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build();
    }
}