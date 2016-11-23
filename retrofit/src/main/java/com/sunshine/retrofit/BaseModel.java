package com.sunshine.retrofit;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 耿 on 2016/6/28.
 */
public class BaseModel {
    public String data;
    public String msg;
    public boolean status;

    public boolean trueStatus(Context context) {
        if (status) {
            return true;
        } else {
            Toast.makeText(context, msg,Toast.LENGTH_SHORT);
            return false;
        }
    }

    public BaseModel(String json) {
        JSONObject obj = new JSONObject();
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int state = -1;
        try {
            state = obj.getInt("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (state == 1) {
            this.status = true;
        } else if (state == 0) {
            this.status = false;
        } else {
            try {
                this.status = obj.getBoolean("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            this.data = obj.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.msg = obj.getString("error");
        } catch (JSONException e) {
            e.printStackTrace();
            msg = "";
        }
        if (HttpUtil.checkNULL(msg)) {
            try {
                this.msg = obj.getString("msg");
            } catch (JSONException e) {
                e.printStackTrace();
                msg = "";
            }
        }


    }
}
