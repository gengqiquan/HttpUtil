package com.sunshine.retrofit;

import org.json.JSONStringer;

import retrofit2.Call;

/**
 * Created by 耿 on 2016/7/15.
 */
public class ProxyInfo {
    ProxyInfo(Call<JSONStringer> p) {
        mProxy = p;
        mInfo = null;
    }

    ProxyInfo(Call<JSONStringer> mProxy, Object i) {
        mProxy = mProxy;
        mInfo = i;
    }

    ProxyInfo(Object i) {
        mProxy = null;
        mInfo = i;
    }

    public void Retry() {
        if (mProxy == null) {
            return;
        }
        mProxy.clone().request();
    }

    public Call<JSONStringer> mProxy;
    public Object mInfo;
}
