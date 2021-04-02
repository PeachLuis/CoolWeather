package com.example.kuouweather.presenter;



import com.example.kuouweather.db.City;
import com.example.kuouweather.db.County;
import com.example.kuouweather.db.Province;
import com.example.kuouweather.view.ChooseAreaView;

import java.util.List;

public interface ChooseAreaPresenter {


    void attachView(ChooseAreaView view);

    void detachView();

    boolean isViewAttached();

//    void queryFromServer(String address, final String type);

    List<Province> getProvinceList();

    List<City> getCityList(final Province province);

    List<County> getCountyList(final Province province, final City city);

}
