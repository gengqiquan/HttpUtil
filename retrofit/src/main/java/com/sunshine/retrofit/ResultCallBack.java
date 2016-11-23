package com.sunshine.retrofit;


public abstract class ResultCallBack {
    public abstract void onSuccess(String url, String model);

    public abstract void onFailure(int statusCode, String errorMsg, Throwable t, ProxyInfo proxyInfo);

    public static final ResultCallBack DEFAULT_RESULT_CALLBACK = new ResultCallBack() {
        @Override
        public void onSuccess(String t, String str) {
        }

        @Override
        public void onFailure(int statusCode, String errorMsg, Throwable t,ProxyInfo proxyInfo) {
        }
    };

}
