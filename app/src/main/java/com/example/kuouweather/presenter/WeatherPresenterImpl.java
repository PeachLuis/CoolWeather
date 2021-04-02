package com.example.kuouweather.presenter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kuouweather.gson.Weather;
import com.example.kuouweather.model.WeatherModel;
import com.example.kuouweather.model.WeatherModelImpl;
import com.example.kuouweather.util.HttpUtil;
import com.example.kuouweather.util.ToastUtil;
import com.example.kuouweather.util.Utility;
import com.example.kuouweather.view.WeatherActivity;
import com.example.kuouweather.view.WeatherView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherPresenterImpl implements WeatherPresenter{

    private WeatherView weatherView;

    private WeatherModel weatherModel;

    private WeatherActivity activity;

    public WeatherPresenterImpl() {
        weatherModel = new WeatherModelImpl();
    }

    @Override
    public void onAttachView(WeatherView view) {
        weatherView = view;
        activity = (WeatherActivity) view;
    }

    @Override
    public void detachView() {
        weatherView = null;
    }

    @Override
    public boolean isViewAttached() {
        return weatherView != null;
    }

    @Override
    public String getPrefs(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getString(key, null);
    }

    @Override
    public void putPrefs(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void downloadBingPic() {
        weatherModel.downloadBingPic(new OnGetWeatherListener() {
            @Override
            public void onFailed() {
                activity.runOnUiThread(()->{
                    ToastUtil.showToastShort(activity,"加载BingPic失败");
                });
            }

            @Override
            public void onSucceed(String data) {
                putPrefs("bing_pic", data);
                activity.runOnUiThread(()->{
                    weatherView.loadBingPic();
                });
            }
        });
    }

    @Override
    public void requestWeather(String weatherId) {
        weatherModel.getWeatherInfo(weatherId, new OnGetWeatherListener() {
            @Override
            public void onFailed() {
                activity.runOnUiThread(()->{
                    ToastUtil.showToastShort(activity, "requestWeather请求失败");
                    weatherView.stopRefreshing();
                });
            }

            @Override
            public void onSucceed(String responseText) {
                final Weather weather = Utility.handleWeatherResponse(responseText);
                activity.runOnUiThread(()->{
                    if (weather != null && "ok".equals(weather.status)) {
                        putPrefs("weather", responseText);
                        weatherView.showWeatherInfo(weather);
                    } else {
                        ToastUtil.showToastShort(activity, "获取天气信息失败");
                    }
                    weatherView.stopRefreshing();
                });
            }
        });
    }
}
