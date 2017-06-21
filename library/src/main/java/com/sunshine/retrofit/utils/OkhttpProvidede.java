package com.sunshine.retrofit.utils;

import android.content.Context;

import com.sunshine.retrofit.BuildConfig;
import com.sunshine.retrofit.cacahe.CacheProvide;
import com.sunshine.retrofit.interceptor.CacheInterceptor;
import com.sunshine.retrofit.interceptor.DownLoadInterceptor;
import com.sunshine.retrofit.interceptor.HttpLoggingInterceptor;
import com.sunshine.retrofit.interceptor.RetryAndChangeIpInterceptor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by 耿 on 2016/8/27.
 */
public class OkhttpProvidede {
    static OkHttpClient okHttpClient;

    public static OkHttpClient okHttpClient(final Context context, String BASE_URL) {
        if (okHttpClient == null) {
            synchronized (OkhttpProvidede.class) {
                if (okHttpClient == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new DownLoadInterceptor(BASE_URL))
                            .addNetworkInterceptor(new CacheInterceptor())
                            .cache(new CacheProvide(context).provideCache())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(8, TimeUnit.SECONDS)
                            .writeTimeout(8, TimeUnit.SECONDS)
                            .build();
                    if (BuildConfig.DEBUG) {//printf logs while  debug
                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                        client = client.newBuilder().addInterceptor(logging).build();
                    }
                    okHttpClient = client;
                }

            }

        }
        return okHttpClient;

    }
}
