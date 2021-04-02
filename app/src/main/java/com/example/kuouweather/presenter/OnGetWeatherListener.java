package com.example.kuouweather.presenter;

public interface OnGetWeatherListener {

    void onFailed();

    void onSucceed(String weatherString);
}
