package com.example.kuouweather.model;

import com.example.kuouweather.presenter.OnGetWeatherListener;

public interface WeatherModel {

    void getWeatherInfo(String weatherId, OnGetWeatherListener data);

    void downloadBingPic(OnGetWeatherListener data);

}
