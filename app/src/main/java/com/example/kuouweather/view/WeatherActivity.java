package com.example.kuouweather.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kuouweather.R;
import com.example.kuouweather.gson.Forecast;
import com.example.kuouweather.gson.Weather;
import com.example.kuouweather.presenter.WeatherPresenter;
import com.example.kuouweather.presenter.WeatherPresenterImpl;
import com.example.kuouweather.service.AutoUpdateService;
import com.example.kuouweather.util.HttpUtil;
import com.example.kuouweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements WeatherView{

    private WeatherPresenter weatherPresenter;

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

//    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefreshLayout;

    public DrawerLayout drawerLayout;

    private Button nav_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //初始化控件
        bingPicImg = findViewById(R.id.bing_pic_img);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
//        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);
        drawerLayout = findViewById(R.id.drawer_layout);
        nav_button = findViewById(R.id.nav_button);
        //绑定Presenter
        weatherPresenter = new WeatherPresenterImpl();
        weatherPresenter.onAttachView(this);

        init();
    }

    private void init() {
        //设置状态栏透明
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //加载背景图
        loadBingPic();
        //获取天气信息
        loadWeatherInfo();
        //设置nav_button的点击事件
        nav_button.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherPresenter.detachView();
    }

    /**
     * 加载天气信息
     */
    public void loadWeatherInfo() {
        String weatherString = weatherPresenter.getPrefs("weather");
        final String weatherId;
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        swipeRefreshLayout.setOnRefreshListener(()->{
            requestWeather(weatherId);
        });
    }

    /**
     * 加载必应每日一图
     */
    @Override
    public void loadBingPic() {
        String imgPrefs = weatherPresenter.getPrefs("bing_pic");
        if (imgPrefs == null) {
            //如果Prefs里没有，则下载
            weatherPresenter.downloadBingPic();
        } else {
            //如果有，则加载
            Glide.with(this).load(imgPrefs).into(bingPicImg);
        }
    }

    /**
     * 根据天气id请求城市天气信息
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        weatherPresenter.requestWeather(weatherId);
        loadBingPic();
    }

    /**
     * 处理并展示Weather是立体中的数据
     */
    @Override
    public void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dataText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
//        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
//        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void stopRefreshing() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void startRefreshing() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }
}