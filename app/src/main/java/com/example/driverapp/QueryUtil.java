package com.example.driverapp;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xianwei li on 11/20/2017.
 */

public class QueryUtil {

    public static String getJsonString(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response responses;
        String jsonData = null;
        try {
            responses = client.newCall(request).execute();
            jsonData = responses.body().string();
            System.out.println("JSON:" +jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("12345jsonData", String.valueOf(jsonData == null));
        return jsonData;
    }

    public static List<List<String>> ParseJson(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) return null;
        List<List<String>> result = new ArrayList<>();
        List<String> location;

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject locationJsonObject = jsonArray.getJSONObject(i);
                final String latitude = locationJsonObject.getString("latitude");
                final String longitude = locationJsonObject.getString("longitude");
                final String head = locationJsonObject.getString("heading");
                final String code = locationJsonObject.getString("image_code");

                location = new ArrayList<String>() {
                    {
                        add(latitude);
                        add(longitude);
                        add(head);
                        add(code);
                    }

                };
                result.add(location);
            }
            System.out.println(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
