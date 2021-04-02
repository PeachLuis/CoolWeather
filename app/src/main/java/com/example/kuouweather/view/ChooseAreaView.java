package com.example.kuouweather.view;

public interface ChooseAreaView {

    void showProgressDialog();

    void closeProgressDialog();

    /**
     * 处理ListView和其item点击事件
     */
    void handleCityList();

    /**
     * 处理backButton的点击事件
     */
    void handleBackButton();

    void queryProvinces();

    void queryCities();

    void queryCounties();

    void loadAreaInfoFailed();

}
