package com.example.kuouweather.presenter;

import com.example.kuouweather.db.City;
import com.example.kuouweather.db.County;
import com.example.kuouweather.db.Province;
import com.example.kuouweather.model.ChooseAreaModel;
import com.example.kuouweather.model.ChooseAreaModelImpl;
import com.example.kuouweather.view.ChooseAreaFragment;
import com.example.kuouweather.view.ChooseAreaView;

import org.litepal.LitePal;

import java.util.List;

public class ChooseAreaPresenterImpl implements ChooseAreaPresenter {

    private ChooseAreaView chooseAreaView;

    private ChooseAreaModel chooseAreaModel;

    private ChooseAreaFragment fragment;

    public ChooseAreaPresenterImpl() {
        chooseAreaModel = new ChooseAreaModelImpl();
    }

    @Override
    public void attachView(ChooseAreaView view) {
        chooseAreaView = view;
        fragment = (ChooseAreaFragment) view;
    }

    @Override
    public void detachView() {
        chooseAreaView = null;
    }

    @Override
    public boolean isViewAttached() {
        return chooseAreaView != null;
    }

    @Override
    public List<Province> getProvinceList() {
        List<Province> provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0) {
            return provinceList;
        } else {
            chooseAreaView.showProgressDialog();
            chooseAreaModel.getProvinceInfo(new OnGetServerInfoListener() {
                @Override
                public void onFailed() {
                    fragment.getActivity().runOnUiThread(() -> {
                        chooseAreaView.closeProgressDialog();
                        chooseAreaView.loadAreaInfoFailed();
                    });
                }

                @Override
                public void onSucceed() {
                    fragment.getActivity().runOnUiThread(() -> {
                        chooseAreaView.closeProgressDialog();
                        chooseAreaView.queryProvinces();
                    });
                }
            });
        }
        return provinceList;
    }

    @Override
    public List<City> getCityList(Province province) {
        List<City> cityList = LitePal.where("provinceid = ?",
                String.valueOf(province.getId())).find(City.class);
        if (cityList.size() > 0) {
            return cityList;
        } else {
            chooseAreaView.showProgressDialog();
            chooseAreaModel.getCityInfo(province, new OnGetServerInfoListener() {
                @Override
                public void onFailed() {
                    fragment.getActivity().runOnUiThread(() -> {
                        chooseAreaView.closeProgressDialog();
                        chooseAreaView.loadAreaInfoFailed();
                    });
                }

                @Override
                public void onSucceed() {
                    fragment.getActivity().runOnUiThread(() -> {
                        chooseAreaView.closeProgressDialog();
                        chooseAreaView.queryCities();
                    });
                }
            });
            return cityList;
    }
}

    @Override
    public List<County> getCountyList(Province province, City city) {
        List<County> countyList = LitePal.where("cityid = ?",
                String.valueOf(city.getId())).find(County.class);
        if (countyList.size() > 0) {
            return countyList;
        } else {
            chooseAreaView.showProgressDialog();
            chooseAreaModel.getCountyInfo(province, city, new OnGetServerInfoListener() {
                @Override
                public void onFailed() {
                    fragment.getActivity().runOnUiThread(() -> {
                        chooseAreaView.closeProgressDialog();
                        chooseAreaView.loadAreaInfoFailed();
                    });
                }

                @Override
                public void onSucceed() {
                    fragment.getActivity().runOnUiThread(() -> {
                        chooseAreaView.closeProgressDialog();
                        chooseAreaView.queryCounties();
                    });
                }
            });
            return countyList;
        }
    }
}
