package com.sunshine.retrofit;

import android.content.Context;
import android.support.annotation.CheckResult;

import com.sunshine.retrofit.interfaces.HeadersInterceptor;
import com.sunshine.retrofit.interfaces.ParamsInterceptor;
import com.sunshine.retrofit.utils.OkhttpProvidede;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 网络请求封装类
 * Created by 耿 on 2016/6/28.
 */
public class HttpUtil {
    private static volatile HttpUtil mInstance;
    private static volatile RetrofitHttpService mService;
    private Context mAppliactionContext;
    private ParamsInterceptor mParamsInterceptor;
    private HeadersInterceptor mHeadersInterceptor;

    //构造函数私有，不允许外部调用
    private HttpUtil(RetrofitHttpService mService, Context mAppliactionContext, ParamsInterceptor mParamsInterceptor, HeadersInterceptor mHeadersInterceptor) {
        this.mService = mService;
        this.mAppliactionContext = mAppliactionContext;
        this.mParamsInterceptor = mParamsInterceptor;
        this.mHeadersInterceptor = mHeadersInterceptor;
    }

    @CheckResult
    public static RetrofitHttpService getService() {
        if (mInstance == null) {
            throw new NullPointerException("HttpUtil has not be initialized");
        }
        return mService;
    }

    @CheckResult
    public static HttpUtil getmInstance() {
        if (mInstance == null) {
            throw new NullPointerException("HttpUtil has not be initialized");
        }
        return mInstance;
    }

    public void setParamsInterceptor(ParamsInterceptor interceptor) {
        this.mParamsInterceptor = interceptor;
    }

    public static class SingletonBuilder {
        private Context appliactionContext;
        private String baseUrl;
        private List<String> servers = new ArrayList<>();
        private ParamsInterceptor paramsInterceptor;
        private HeadersInterceptor headersInterceptor;
        private List<Converter.Factory> converterFactories = new ArrayList<>();
        private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        OkHttpClient client;

        public SingletonBuilder(Context context, String baseUrl) {
            appliactionContext = context.getApplicationContext();
            this.baseUrl = baseUrl;
            client = OkhttpProvidede.okHttpClient(appliactionContext, baseUrl);
        }

        public SingletonBuilder client(OkHttpClient client) {
            this.client = client;
            return this;
        }


        public SingletonBuilder headersInterceptor(HeadersInterceptor interceptor) {
            this.headersInterceptor = interceptor;
            return this;
        }

        public SingletonBuilder paramsInterceptor(ParamsInterceptor interceptor) {
            this.paramsInterceptor = interceptor;
            return this;
        }


        public SingletonBuilder addServerUrl(String ipUrl) {
            this.servers.add(ipUrl);
            return this;
        }

        public SingletonBuilder serverUrls(List<String> servers) {
            this.servers = servers;
            return this;
        }

        public SingletonBuilder converterFactory(Converter.Factory factory) {
            this.converterFactories.add(factory);
            return this;
        }

        public SingletonBuilder callFactory(CallAdapter.Factory factory) {
            this.adapterFactories.add(factory);
            return this;
        }

        public HttpUtil build() {
            if (checkNULL(this.baseUrl)) {
                throw new NullPointerException("BASE_URL can not be null");
            }
            if (converterFactories.size() == 0) {
                converterFactories.add(ScalarsConverterFactory.create());
            }
            if (adapterFactories.size() == 0) {
                adapterFactories.add(RxJava2CallAdapterFactory.create());
            }

            Retrofit.Builder builder = new Retrofit.Builder();

            for (Converter.Factory converterFactory : converterFactories) {
                builder.addConverterFactory(converterFactory);
            }
            for (CallAdapter.Factory adapterFactory : adapterFactories) {
                builder.addCallAdapterFactory(adapterFactory);
            }
            Retrofit retrofit = builder
                    .baseUrl(baseUrl + "/")
                    .client(client).build();
            RetrofitHttpService retrofitHttpService =
                    retrofit.create(RetrofitHttpService.class);

            mInstance = new HttpUtil(retrofitHttpService, appliactionContext, paramsInterceptor, headersInterceptor);
            return mInstance;
        }
    }

    @CheckResult
    public static Map<String, String> checkParams(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (mInstance.mParamsInterceptor != null) {
            params = mInstance.mParamsInterceptor.checkParams(params);
        }
        //retrofit的params的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                params.put(entry.getKey(), "");
            }
        }
        return params;
    }

    public static Map<String, String> checkHeaders(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (mInstance.mHeadersInterceptor != null) {
            headers = mInstance.mHeadersInterceptor.checkHeaders(headers);
        }
        //retrofit的params的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getValue() == null) {
                headers.put(entry.getKey(), "");
            }
        }
        return headers;
    }

    // 判断是否NULL
    @CheckResult
    public static boolean checkNULL(String str) {
        return str == null || "null".equals(str) || "".equals(str);

    }


    static Map<String, Call> CALL_MAP = new HashMap<>();

    /*
    *添加某个请求
    *@author Administrator
    *@date 2016/10/12 11:00
    */
    public static synchronized void putCall(Object tag, String url, Call call) {
        if (tag == null)
            return;
        synchronized (CALL_MAP) {
            CALL_MAP.put(tag.toString() + url, call);
        }
    }

    /*
    *取消某个界面都所有请求，或者是取消某个tag的所有请求
    * 如果要取消某个tag单独请求，tag需要转入tag+url
    *@author Administrator
    *@date 2016/10/12 10:57
    */
    public static synchronized void cancel(Object tag) {
        if (tag == null)
            return;
        List<String> list = new ArrayList<>();
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.startsWith(tag.toString())) {
                    CALL_MAP.get(key).cancel();
                    list.add(key);
                }
            }
        }
        for (String s : list) {
            removeCall(s);
        }

    }

    /*
    *移除某个请求
    *@author Administrator
    *@date 2016/10/12 10:58
    */
    public static synchronized void removeCall(String url) {
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.contains(url)) {
                    url = key;
                    break;
                }
            }
            CALL_MAP.remove(url);
        }
    }


}
