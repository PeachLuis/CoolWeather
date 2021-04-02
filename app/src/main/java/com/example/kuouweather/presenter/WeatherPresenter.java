package com.example.kuouweather.presenter;

import com.example.kuouweather.view.WeatherView;

public interface WeatherPresenter {

    void onAttachView(WeatherView view);

    void detachView();

    boolean isViewAttached();

    String getPrefs(String key);

    void putPrefs(String key, String value);

    void downloadBingPic();

    void requestWeather(final String weatherId);

}
