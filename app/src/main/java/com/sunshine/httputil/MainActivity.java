package com.sunshine.httputil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sunshine.retrofit.HttpBuilder;
import com.sunshine.retrofit.HttpUtil;
import com.sunshine.retrofit.interfaces.HeadersInterceptor;
import com.sunshine.retrofit.interfaces.ParamsInterceptor;

import java.io.File;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (TextView) findViewById(R.id.text);
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
        new HttpUtil.SingletonBuilder(getApplicationContext(),"http://sw.bos.baidu.com")
//                .versionApi("")//API版本，不传不可以追加接口版本号
//                .addServerUrl("")//备份服务器ip地址，可多次调用传递
//                .addCallFactory()//不传默认StringConverterFactory
//                .addConverterFactory()//不传默认RxJavaCallAdapterFactory
//                .client()//OkHttpClient,不传默认OkHttp3
                //   .paramsInterceptor(mParamsInterceptor)//不传不进行参数统一处理
                //   .headersInterceptor(mHeadersInterceptor)//不传不进行headers统一处理
                .build();
        new HttpBuilder("http://sw.bos.baidu.com/sw-search-sp/software/c07cde08ce4/Photoshop_CS6.exe")
                .path(getExternalFilesDir(null) + File.separator + "Photoshop_CS6.exe")
                .progress(p -> {
                    progress.setText(p+"");
                })
                .success(s -> {
                    //返回path
                })
                .error(t -> {
                })
                .download();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancel(this);
    }
}
