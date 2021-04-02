package com.example.kuouweather.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kuouweather.R;
import com.example.kuouweather.db.City;
import com.example.kuouweather.db.County;
import com.example.kuouweather.db.Province;
import com.example.kuouweather.presenter.ChooseAreaPresenter;
import com.example.kuouweather.presenter.ChooseAreaPresenterImpl;
import com.example.kuouweather.util.HttpUtil;
import com.example.kuouweather.util.ToastUtil;
import com.example.kuouweather.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment implements ChooseAreaView{

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ChooseAreaPresenter chooseAreaPresenter;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    private static final String TAG = "ChooseAreaFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        //初始化ChooseAreaPresenter
        chooseAreaPresenter = new ChooseAreaPresenterImpl();
        chooseAreaPresenter.attachView(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handleCityList();
        handleBackButton();
        queryProvinces();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        chooseAreaPresenter.detachView();
    }

    @Override
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载数据...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void handleCityList() {
        //listView的点击事件
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(position);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                queryCounties();
            } else if (currentLevel == LEVEL_COUNTY) {
                String weatherId = countyList.get(position).getWeatherId();
                if (getActivity() instanceof MainActivity) {
                    //如果是在MainActivity，则跳转到WeatherActivity
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                } else if (getActivity() instanceof WeatherActivity) {
                    //如果是在WeatherActivity，则此时的Fragment为侧边菜单，关闭即可
                    WeatherActivity activity = (WeatherActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefreshLayout.setRefreshing(true);
                    activity.requestWeather(weatherId);
                }
            }
        });
    }

    @Override
    public void handleBackButton() {
        //返回按钮的点击事件
        backButton.setOnClickListener(v -> {
            if (currentLevel == LEVEL_COUNTY) {
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                queryProvinces();
            }
        });
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    @Override
    public void queryProvinces() {
        //设置Title和backButton
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);

        dataList.clear();
        currentLevel = LEVEL_PROVINCE;

        provinceList = chooseAreaPresenter.getProvinceList();
        for (Province province : provinceList) {
            dataList.add(province.getProvinceName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
    }

    /**
     * 查询选中省内是所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    @Override
    public void queryCities() {
        //设置Title和backButton
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        dataList.clear();
        currentLevel = LEVEL_CITY;

        cityList = chooseAreaPresenter.getCityList(selectedProvince);
        for (City city : cityList) {
            dataList.add(city.getCityName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);

    }

    /**
     * 查询选中市内所有的县，优先从数据库中查询，如果没有查询到再去服务器上查询
     */
    @Override
    public void queryCounties() {
        //设置Title和backButton
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);

        dataList.clear();
        currentLevel = LEVEL_COUNTY;

        countyList = chooseAreaPresenter.getCountyList(selectedProvince, selectedCity);
        for (County county : countyList) {
            dataList.add(county.getCountyName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
    }

    public void loadAreaInfoFailed() {
        ToastUtil.showToastShort(getContext(), "获取区域信息失败");
    }

}
