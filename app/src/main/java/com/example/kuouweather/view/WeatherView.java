package com.example.kuouweather.view;

import com.example.kuouweather.gson.Weather;

public interface WeatherView {

    void loadBingPic();

    void loadWeatherInfo();

    void requestWeather(String weatherId);

    void showWeatherInfo(Weather weather);

    void stopRefreshing();

    void startRefreshing();

}
