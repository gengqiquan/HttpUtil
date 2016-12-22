package com.sunshine.retrofit.interfaces;

import java.util.Map;

/**
 * Created by Administrator on 2016/11/23.
 */
@FunctionalInterface
public interface ParamsInterceptor {
    Map checkParams(Map params);
}
