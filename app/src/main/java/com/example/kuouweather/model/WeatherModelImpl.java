package com.example.kuouweather.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.kuouweather.gson.Weather;
import com.example.kuouweather.presenter.OnGetWeatherListener;
import com.example.kuouweather.util.HttpUtil;
import com.example.kuouweather.util.Utility;
import com.example.kuouweather.view.WeatherActivity;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherModelImpl implements WeatherModel{

    @Override
    public void getWeatherInfo(String weatherId, OnGetWeatherListener listener) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId +
                "&key=1e4e4d634eae4a37a307d5530c8a93c0";
        HttpUtil.sendOkHttpRequest(weatherUrl, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                listener.onFailed();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                listener.onSucceed(responseText);
            }
        });
    }

    @Override
    public void downloadBingPic(OnGetWeatherListener listener) {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                listener.onFailed();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                listener.onSucceed(response.body().string());
            }
        });
    }
}
