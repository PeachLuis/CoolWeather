package com.example.kuouweather.model;

import com.example.kuouweather.db.City;
import com.example.kuouweather.db.Province;
import com.example.kuouweather.presenter.OnGetServerInfoListener;
import com.example.kuouweather.util.HttpUtil;
import com.example.kuouweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaModelImpl implements ChooseAreaModel{

    private String url = "http://guolin.tech/api/china";

    @Override
    public void getProvinceInfo(OnGetServerInfoListener listener) {
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean flag = Utility.handleProvinceResponse(response.body().string());
                if (flag) {
                    listener.onSucceed();
                } else {
                    listener.onFailed();
                }
            }
        });
    }

    @Override
    public void getCityInfo(Province province, OnGetServerInfoListener listener) {
        String address = url + "/" + province.getProvinceCode();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                boolean flag = Utility.handleCityResponse(response.body().string(), province.getId());
                if (flag) {
                    listener.onSucceed();
                } else {
                    listener.onFailed();
                }
            }
        });
    }

    @Override
    public void getCountyInfo(Province province, City city, OnGetServerInfoListener listener) {
        String address = url + "/" + province.getProvinceCode() + "/" + city.getCityCode();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                boolean flag = Utility.handleCountyResponse(response.body().string(), city.getId());
                if (flag) {
                    listener.onSucceed();
                } else {
                    listener.onFailed();
                }
            }
        });
    }
}
