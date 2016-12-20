package com.sunshine.retrofit;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by 耿 on 2016/6/28.
 */
public interface RetrofitHttpService {

    @GET()
    Call<String> get(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST()
    Call<String> post(@HeaderMap Map<String, String> headers, @Url String url, @FieldMap Map<String, String> params);

    @GET()
    Observable<String> Obget(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST()
    Observable<String> Obpost(@HeaderMap Map<String, String> headers, @Url String url, @FieldMap Map<String, String> params);

}
