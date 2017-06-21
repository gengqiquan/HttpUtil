package com.sunshine.retrofit;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.sunshine.retrofit.interfaces.Error;
import com.sunshine.retrofit.interfaces.Progress;
import com.sunshine.retrofit.interfaces.Success;
import com.sunshine.retrofit.utils.NetUtils;
import com.sunshine.retrofit.utils.WriteFileUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.sunshine.retrofit.HttpUtil.checkHeaders;
import static com.sunshine.retrofit.HttpUtil.checkParams;
import static com.sunshine.retrofit.HttpUtil.putCall;

/**
 * Created by gengqiquan on 2017/3/24.
 */

public class HttpBuilder {
    Map<String, String> params = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    String url;
    String path;
    Error mErrorCallBack;
    Success mSuccessCallBack;
    Progress mProgressListener;
    Object tag;
    Context mContext;
    boolean checkNetConnected = false;

    public HttpBuilder(@NonNull String url) {
        this.setParams(url);
    }

    /**
     * 是否允许缓存，传入时间如：1*3600 代表一小时缓存时效
     *
     * @param time 缓存时间 单位：秒
     * @author gengqiquan
     * @date 2017/3/25 下午3:27
     */
    public HttpBuilder cacheTime(int time) {
        header("Cache-Time", time + "");
        return this;
    }

    public HttpBuilder path(@NonNull String path) {
        this.path = path;
        return this;
    }

    public HttpBuilder tag(@NonNull Object tag) {
        this.tag = tag;
        return this;
    }

    public HttpBuilder params(@NonNull Map<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    public HttpBuilder params(@NonNull String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public HttpBuilder headers(@NonNull Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public HttpBuilder header(@NonNull String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    @CheckResult
    public HttpBuilder success(@NonNull Success success) {
        this.mSuccessCallBack = success;
        return this;
    }

    public HttpBuilder progress(@NonNull Progress progress) {
        this.mProgressListener = progress;
        return this;
    }

    @CheckResult
    public HttpBuilder error(@NonNull Error error) {
        this.mErrorCallBack = error;
        return this;
    }

    /**
     * 检查网络是否连接，未连接跳转到网络设置界面
     *
     * @author gengqiquan
     * @date 2017/3/25 下午3:27
     */
    public HttpBuilder isConnected(@NonNull Context context) {
        checkNetConnected = true;
        mContext = context;
        return this;
    }


    private void setParams(String url) {
        if (HttpUtil.getmInstance() == null) {
            throw new NullPointerException("HttpUtil has not be initialized");
        }
        this.url = url;
        this.params = new HashMap<>();
        this.mErrorCallBack = (v) -> {
        };
        this.mSuccessCallBack = (s) -> {
        };
        this.mProgressListener = new Progress() {
            @Override
            public void progress(float p) {

            }
        };
    }

    @CheckResult
    private String checkUrl(String url) {
        if (HttpUtil.checkNULL(url)) {
            throw new NullPointerException("absolute url can not be empty");
        }
        return url;
    }

    @CheckResult
    public String message(String mes) {
        if (HttpUtil.checkNULL(mes)) {
            mes = "服务器异常，请稍后再试";
        }

        if (mes.equals("timeout") || mes.equals("SSL handshake timed out")) {
            return "网络请求超时";
        } else {
            return mes;
        }

    }

    /**
     * 请求前初始检查
     *
     * @author gengqiquan
     * @date 2017/3/25 下午4:12
     */
    boolean allready() {
        if (!NetUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "检测到网络已关闭，请先打开网络", Toast.LENGTH_SHORT).show();
            NetUtils.openSetting(mContext);//跳转到网络设置界面
            return false;
        }
        return true;
    }

    public void get() {
        if (!allready()) {
            return;
        }
        Call call = HttpUtil.getService().get(checkUrl(this.url), checkParams(params), checkHeaders(headers));
        putCall(tag, url, call);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    mSuccessCallBack.Success(response.body());
                } else {
                    mErrorCallBack.Error(message(response.message()));
                }
                if (tag != null)
                    HttpUtil.removeCall(url);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                mErrorCallBack.Error(message(t.getMessage()));
                if (tag != null)
                    HttpUtil.removeCall(url);
            }
        });
    }

    public void post() {
        if (!allready()) {
            return;
        }
        Call call = HttpUtil.getService().post(checkUrl(this.url), checkParams(params), checkHeaders(headers));
        putCall(tag, url, call);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    mSuccessCallBack.Success(response.body());
                } else {
                    mErrorCallBack.Error(message(response.message()));
                }
                if (tag != null)
                    HttpUtil.removeCall(url);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                mErrorCallBack.Error(message(t.getMessage()));
                if (tag != null)
                    HttpUtil.removeCall(url);
            }
        });
    }

    public Observable<ResponseBody> Obdownload() {
        this.url = checkUrl(this.url);
        this.params = checkParams(this.params);
        this.headers.put(Constant.DOWNLOAD, Constant.DOWNLOAD);
        this.headers.put(Constant.DOWNLOAD_URL, this.url);
        return HttpUtil.getService().Obdownload(checkHeaders(headers), url, checkParams(params));
    }

    //下载
    public void download() {
        this.url = checkUrl(this.url);
        this.params = checkParams(this.params);
        this.headers.put(Constant.DOWNLOAD, Constant.DOWNLOAD);
        this.headers.put(Constant.DOWNLOAD_URL, this.url);
        Call call = HttpUtil.getService().download(checkHeaders(headers), url, checkParams(params));
        putCall(tag, url, call);
        Observable<ResponseBody> observable = Observable.create(subscriber -> {
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            subscriber.onNext(response.body());
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            mErrorCallBack.Error(t);
                        }
                    });
                }
        );
        observable.observeOn(Schedulers.io())
                .subscribe(body -> WriteFileUtil.writeFile(body, path, mProgressListener, mSuccessCallBack, mErrorCallBack), t -> {
                            mErrorCallBack.Error(t);
                        }
                );
    }


    @CheckResult
    public Observable<String> Obget() {
        return HttpUtil.getService().Obget(checkUrl(this.url), checkParams(params), checkHeaders(headers))
                ;
    }

    @CheckResult
    public Observable<String> Obpost() {
        return HttpUtil.getService().Obpost(checkUrl(this.url), checkParams(params), checkHeaders(headers))
                ;
    }

    @CheckResult
    public Observable<String> Obput() {
        return HttpUtil.getService().Obput(checkUrl(this.url), checkParams(params), checkHeaders(headers))
                ;
    }

}