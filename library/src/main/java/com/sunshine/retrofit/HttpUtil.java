package com.sunshine.retrofit;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.sunshine.retrofit.converter.StringConverterFactory;
import com.sunshine.retrofit.interfaces.Error;
import com.sunshine.retrofit.interfaces.HeadersInterceptor;
import com.sunshine.retrofit.interfaces.ParamsInterceptor;
import com.sunshine.retrofit.interfaces.Success;
import com.sunshine.retrofit.utils.OkhttpProvidede;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 * Created by 耿 on 2016/6/28.
 */
public class HttpUtil {
    private static volatile HttpUtil mInstance;
    private static volatile RetrofitHttpService mService;
    private Context mAppliactionContext;
    private static String mVersionApi;
    private ParamsInterceptor mParamsInterceptor;
    private HeadersInterceptor mHeadersInterceptor;

    //构造函数私有，不允许外部调用
    private HttpUtil(RetrofitHttpService mService, Context mAppliactionContext, String mVersionApi, ParamsInterceptor mParamsInterceptor, HeadersInterceptor mHeadersInterceptor) {
        this.mService = mService;
        this.mAppliactionContext = mAppliactionContext;
        this.mVersionApi = mVersionApi;
        this.mParamsInterceptor = mParamsInterceptor;
        this.mHeadersInterceptor = mHeadersInterceptor;
    }

    public static RetrofitHttpService getService() {
        if (mInstance == null) {
            throw new NullPointerException("HttpUtil has not be initialized");
        }
        return mService;
    }

    public static class SingletonBuilder {
        private Context appliactionContext;
        private String baseUrl;
        private List<String> servers = new ArrayList<>();
        private String versionApi;
        private ParamsInterceptor paramsInterceptor;
        private HeadersInterceptor headersInterceptor;
        private List<Converter.Factory> converterFactories = new ArrayList<>();
        private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        OkHttpClient client;

        public SingletonBuilder(Context context) {
            try {//防止传入的是activity的上下文
                Activity activity = (Activity) context;
                appliactionContext = context.getApplicationContext();
            } catch (Exception e) {
                e.printStackTrace();
                appliactionContext = context;
            }
        }

        public SingletonBuilder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public SingletonBuilder versionApi(String versionApi) {
            this.versionApi = versionApi;
            return this;
        }

        public SingletonBuilder paramsInterceptor(ParamsInterceptor interceptor) {
            this.paramsInterceptor = interceptor;
            return this;
        }

        public SingletonBuilder headersInterceptor(HeadersInterceptor headersInterceptor) {
            this.headersInterceptor = headersInterceptor;
            return this;
        }

        public SingletonBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
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

        public SingletonBuilder addConverterFactory(Converter.Factory factory) {
            this.converterFactories.add(factory);
            return this;
        }

        public SingletonBuilder addCallFactory(CallAdapter.Factory factory) {
            this.adapterFactories.add(factory);
            return this;
        }

        public HttpUtil build() {
            if (checkNULL(this.baseUrl)) {
                throw new NullPointerException("BASE_URL can not be null");
            }
            if (converterFactories.size() == 0) {
                converterFactories.add(StringConverterFactory.create());
            }
            if (adapterFactories.size() == 0) {
                adapterFactories.add(RxJavaCallAdapterFactory.create());
            }
            if (client == null) {
                client = OkhttpProvidede.okHttpClient(appliactionContext, baseUrl, servers);
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

            mInstance = new HttpUtil(retrofitHttpService, appliactionContext, versionApi, paramsInterceptor,headersInterceptor);
            return mInstance;
        }
    }


    public static String V(String url) {
        if (checkNULL(mVersionApi)) {
            throw new NullPointerException("can not add VersionApi ,because of VersionApi is null");
        }
        if (!url.contains(mVersionApi)) {
            return mVersionApi + url;
        }
        return url;
    }


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
        //retrofit的headers的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getValue() == null) {
                headers.put(entry.getKey(), "");
            }
        }
        return headers;
    }

    // 判断是否NULL
    public static boolean checkNULL(String str) {
        return str == null || "null".equals(str) || "".equals(str);

    }

    // 判断是否NULL
    public static void Error(Context context, String msg) {
        if (checkNULL(msg)) {
            msg = "未知异常";
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String message(String mes) {
        if (checkNULL(mes)) {
            mes = "似乎已断开与互联网连接";
        }

        if (mes.equals("timeout") || mes.equals("SSL handshake timed out")) {
            return "网络请求超时";
        } else {
            return mes;
        }

    }

    final static Map<String, Call> CALL_MAP = new HashMap<>();

    /*
    *添加某个请求
    *@author Administrator
    *@date 2016/10/12 11:00
    */
    private static synchronized void putCall(Object tag, String url, Call call) {
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
    private static synchronized void removeCall(String url) {
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

    public static class Builder {
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        String url;
        Error mErrorCallBack;
        Success mSuccessCallBack;
        boolean addVersion = false;
        Object tag;

        public Builder CacheTime(String time) {
            headers.put("Cache-Time", time);
            return this;
        }

        public Builder Url(String url) {
            this.url = url;
            return this;
        }

        public Builder Tag(Object tag) {
            this.tag = tag;
            return this;
        }


        public Builder Params(Map<String, String> params) {
            this.params.putAll(params);
            return this;
        }

        public Builder Params(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        public Builder Headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder Headers(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder Success(Success success) {
            this.mSuccessCallBack = success;
            return this;
        }

        public Builder Version() {
            this.addVersion = true;
            return this;
        }

        public Builder Error(Error error) {
            this.mErrorCallBack = error;
            return this;
        }

        public Builder() {
            this.setParams();
        }

        public Builder(String url) {
            this.setParams(url);
        }

        private void setParams() {
            this.setParams(null);
        }

        private void setParams(String url) {
            if (mInstance == null) {
                throw new NullPointerException("HttpUtil has not be initialized");
            }
            this.url = url;
            this.params = new HashMap<>();
            this.mErrorCallBack = new Error() {
                @Override
                public void Error(Object... values) {

                }
            };
            this.mSuccessCallBack = new Success() {
                @Override
                public void Success(String model) {

                }
            };
        }


        private String checkUrl(String url) {
            if (checkNULL(url)) {
                throw new NullPointerException("absolute url can not be empty");
            }
            if (addVersion) {
                url = mInstance.V(url);
            }
            return url;
        }

        public void get() {
            Call call = mService.get(checkHeaders(headers), checkUrl(this.url), checkParams(params));
            putCall(tag, url, call);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        mSuccessCallBack.Success(response.body().toString());
                    } else {
                        mErrorCallBack.Error(response.code(), message(response.message()), null);
                    }
                    if (tag != null)
                        removeCall(url);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    mErrorCallBack.Error(200, message(t.getMessage()), t);
                    if (tag != null)
                        removeCall(url);
                }
            });
        }

        public void post() {
            Call call = mService.post(checkHeaders(headers), checkUrl(this.url), checkParams(params));
            putCall(tag, url, call);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        mSuccessCallBack.Success(response.body().toString());
                    } else {
                        mErrorCallBack.Error(response.code(), message(response.message()), null);
                    }
                    if (tag != null)
                        removeCall(url);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    mErrorCallBack.Error(200, message(t.getMessage()), t);
                    if (tag != null)
                        removeCall(url);
                }
            });
        }

        public Observable<String> Obget() {
            this.url = checkUrl(this.url);
            this.params = checkParams(this.params);
            return mService.Obget(checkHeaders(headers), url, checkParams(params));
        }


        public Observable<String> Obpost() {
            this.url = checkUrl(this.url);
            this.params = checkParams(this.params);
            return mService.Obpost(checkHeaders(headers), url, checkParams(params));
        }

    }
}
