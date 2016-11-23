package com.sunshine.httputil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sunshine.retrofit.HttpUtil;
import com.sunshine.retrofit.interfaces.Error;
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
        new HttpUtil.SingletonBuilder(getApplicationContext())
                .baseUrl("")//主服务器IP地址。必传
//                .versionApi("")//API版本，不传不可以追加接口版本号
//                .addServerUrl("")//备份服务器ip地址
//                .addCallFactory()//不传默认StringConverterFactory
//                .addConverterFactory()//不传默认RxJavaCallAdapterFactory
//                .client()//OkHttpClient,不传默认OkHttp3
                .paramsInterceptor(mParamsInterceptor)//不传不进行参数统一处理
                .build();
        new HttpUtil.Builder("url")
                .Success(new Success() {
                    @Override
                    public void Success(String model) {

                    }
                })
                .Error(new Error() {
                    @Override
                    public void Error(Object... values) {

                    }
                })
                .get();
    }


}
