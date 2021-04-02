package com.example.kuouweather.model;

import com.example.kuouweather.db.City;
import com.example.kuouweather.db.Province;
import com.example.kuouweather.presenter.OnGetServerInfoListener;

public interface ChooseAreaModel {

    void getProvinceInfo(final OnGetServerInfoListener listener);

    void getCityInfo(final Province province,
                     final OnGetServerInfoListener listener);

    void getCountyInfo(Province province, final City city,
                       final OnGetServerInfoListener listener);


}
