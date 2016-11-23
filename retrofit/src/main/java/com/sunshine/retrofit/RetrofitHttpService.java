package com.sunshine.retrofit;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by 耿 on 2016/6/28.
 */
public interface RetrofitHttpService {

    @GET()
    Call<String> get(@Url String url, @QueryMap Map<String, String> params, @Header("Cache-Time") String time);

    @FormUrlEncoded
    @POST()
    Call<String> post(@Url String url, @FieldMap Map<String, String> params, @Header("Cache-Time") String time);

    @GET()
    Observable<String> Obget(@Url String url, @QueryMap Map<String, String> params, @Header("Cache-Time") String time);

    @FormUrlEncoded
    @POST()
    Observable<String> Obpost(@Url String url, @FieldMap Map<String, String> params, @Header("Cache-Time") String time);

}
