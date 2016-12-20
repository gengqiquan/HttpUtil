package com.sunshine.httputil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sunshine.retrofit.HttpUtil;
import com.sunshine.retrofit.interfaces.Error;
import com.sunshine.retrofit.interfaces.HeadersInterceptor;
import com.sunshine.retrofit.interfaces.ParamsInterceptor;
import com.sunshine.retrofit.interfaces.Success;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParamsInterceptor mParamsInterceptor = new ParamsInterceptor() {
            @Override
            public Map checkParams(Map params) {
                //追加统一参数
                params.put("app_type", "android_price");
                return params;
            }
        };
        HeadersInterceptor mHeadersInterceptor = new HeadersInterceptor() {
            @Override
            public Map checkHeaders(Map headers) {
                //追加统一header，例：数据缓存一天
                headers.put("Cache-Time", "3600*24");
                return headers;
            }
        };
        new HttpUtil.SingletonBuilder(getApplicationContext())
                .baseUrl("http://dingjia.guchele.com")//URL请求前缀地址。必传
//                .versionApi("")//API版本，不传不可以追加接口版本号
//                .addServerUrl("")//备份服务器ip地址，可多次调用传递
//                .addCallFactory()//不传默认StringConverterFactory
//                .addConverterFactory()//不传默认RxJavaCallAdapterFactory
//                .client()//OkHttpClient,不传默认OkHttp3
                .paramsInterceptor(mParamsInterceptor)//不传不进行参数统一处理
                .headersInterceptor(mHeadersInterceptor)//不传不进行headers统一处理
                .build();
        new HttpUtil.Builder("demo/UserService/home?imageView2/1/w/740/h/440")
                .Params("wd", "value")
                // .Version()//需要追加API版本号调用
                .Tag(this)//需要取消请求的tag
                .Success(new Success() {
                    @Override
                    public void Success(String model) {
                        Log.e("model", model);
                    }
                })
                .Error(new Error() {
                    @Override
                    public void Error(Object... values) {

                    }
                })
                .get();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancel(this);
    }
}
