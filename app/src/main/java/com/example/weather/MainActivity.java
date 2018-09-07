package com.example.weather;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends FragmentActivity implements AsyncResponse, AsyncContext {
    public static final String BROADCAST_NEW_JSON = "BROADCAST_NEW_JSON";
    public static final String BROADCAST_ACTION_CURRENT = "BROADCAST_ACTION_CURRENT";
    public static final String BROADCAST_ACTION_FORECAST = "BROADCAST_ACTION_FORECAST";

    public static final String OPEN_WEATHER_MAP_API_CURRENT =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=%s&appid=%s";
    public static final String OPEN_WEATHER_MAP_API_FORECAST =
            "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&lang=%s&appid=%s";

    private static final int NUM_PAGES = 2;
    static File sdPath;
    static AsyncContext delegate;
    private ViewPager mPager;
    /*private WeatherFragment weatherFragment;
    private WeatherFragment weatherFragment1;
    private FragmentManager fragmentManager;*/
    private TextView firstFragmentActive;
    private TextView secondFragmentActive;
    private ImageView update;
    private GetWeatherInformation getWeatherInformation;

    public void processFinish(String output[]) {
        try {
            /*Bundle args = new Bundle();
            args.putString("json", output);
            weatherFragment.setArguments(args);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, weatherFragment);
            fragmentTransaction.commit();*/
            update.setVisibility(View.VISIBLE);

            Intent intent;
            File file;

            if (output[0].equals(OPEN_WEATHER_MAP_API_CURRENT)) {
                intent = new Intent(BROADCAST_ACTION_CURRENT);
                intent.putExtra(BROADCAST_NEW_JSON, output[1]);

                file = new File(sdPath + "/" + BROADCAST_ACTION_CURRENT + ".json");
            } else if (output[0].equals(OPEN_WEATHER_MAP_API_FORECAST)) {
                intent = new Intent(BROADCAST_ACTION_FORECAST);
                intent.putExtra(BROADCAST_NEW_JSON, output[1]);

                file = new File(sdPath + "/" + BROADCAST_ACTION_FORECAST + ".json");
            } else throw new IllegalArgumentException("Не то с возвратом json");

            sendBroadcast(intent);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(output[1].getBytes());

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Ошибка при работе с json" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        AppContext.mapWeatherIconsAssoc.put("01d", R.raw.sun);
        AppContext.mapWeatherIconsAssoc.put("01n", R.raw.moon);
        AppContext.mapWeatherIconsAssoc.put("02d", R.raw.cloud_sun);
        AppContext.mapWeatherIconsAssoc.put("02n", R.raw.cloud_moon);
        AppContext.mapWeatherIconsAssoc.put("03d", R.raw.cloud);
        AppContext.mapWeatherIconsAssoc.put("03n", R.raw.cloud);
        AppContext.mapWeatherIconsAssoc.put("04d", R.raw.cloud_much);
        AppContext.mapWeatherIconsAssoc.put("04n", R.raw.cloud_much);
        AppContext.mapWeatherIconsAssoc.put("09d", R.raw.cloud_hail);
        AppContext.mapWeatherIconsAssoc.put("09n", R.raw.cloud_hail);
        AppContext.mapWeatherIconsAssoc.put("10d", R.raw.cloud_rain_sun);
        AppContext.mapWeatherIconsAssoc.put("10n", R.raw.cloud_rain_moon);
        AppContext.mapWeatherIconsAssoc.put("11d", R.raw.cloud_lightning);
        AppContext.mapWeatherIconsAssoc.put("11n", R.raw.cloud_lightning);
        AppContext.mapWeatherIconsAssoc.put("13d", R.raw.cloud_snow_sun);
        AppContext.mapWeatherIconsAssoc.put("13n", R.raw.cloud_snow_moon);
        AppContext.mapWeatherIconsAssoc.put("50d", R.raw.cloud_fog_sun);
        AppContext.mapWeatherIconsAssoc.put("50n", R.raw.cloud_fog_moon);
        AppContext.mapWeatherIconsAssoc.put("na", R.raw.na);

        AppContext.mapDayNamesAssoc.put("Monday", "Понедельник");
        AppContext.mapDayNamesAssoc.put("Tuesday", "Вторник");
        AppContext.mapDayNamesAssoc.put("Wednesday", "Среда");
        AppContext.mapDayNamesAssoc.put("Thursday", "Четверг");
        AppContext.mapDayNamesAssoc.put("Friday", "Пятница");
        AppContext.mapDayNamesAssoc.put("Saturday", "Суббота");
        AppContext.mapDayNamesAssoc.put("Sunday", "Воскресенье");
        //fragmentManager = getSupportFragmentManager();

        //weatherFragment = new WeatherFragment();
        //weatherFragment1 = new WeatherFragment();

       /* FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, weatherFragment);
        fragmentTransaction.commit();*/

        sdPath = Environment.getExternalStorageDirectory();

        firstFragmentActive = (TextView) findViewById(R.id.firstActive);
        secondFragmentActive = (TextView) findViewById(R.id.secondActive);

        mPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        //startGetWeatherInformation(OPEN_WEATHER_MAP_API_CURRENT);
        //startGetWeatherInformation(OPEN_WEATHER_MAP_API_FORECAST);
        delegate = this;

        update = (ImageView) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update.setVisibility(View.INVISIBLE);

                startGetWeatherInformation(OPEN_WEATHER_MAP_API_CURRENT);
                startGetWeatherInformation(OPEN_WEATHER_MAP_API_FORECAST);
            }
        });
    }

    //BROADCAST_ACTION_CURRENT
    //OPEN_WEATHER_MAP_API_CURRENT
    //BROADCAST_ACTION_FORECAST
    //OPEN_WEATHER_MAP_API_FORECAST
    @Override
    public void sendBroadcast(Intent intent) {
        super.sendBroadcast(intent);
    }

    public void startGetWeatherInformation(String sWeatherApi) {
        final String getWeatherInformationContext[] = {
                sWeatherApi,
                getString(R.string.lang),
                getString(R.string.open_weather_maps_app_id),
        };
        GetWeatherInformation getWeatherInformation = new GetWeatherInformation();
        getWeatherInformation.delegate = this;
        getWeatherInformation.execute(getWeatherInformationContext);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if (position == 0)
                fragment = new WeatherFragment();
            else if (position == 1)
                fragment = new ForecastFragment();
            else fragment = null;
            /*Bundle args = new Bundle();
            args.putInt("num", position);
            weatherFragment.setArguments(args);
            //int iFragmentNumber = getArguments().getInt("num");*/

            return fragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            if (position == 0) {
                firstFragmentActive.setVisibility(View.VISIBLE);
                secondFragmentActive.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                firstFragmentActive.setVisibility(View.INVISIBLE);
                secondFragmentActive.setVisibility(View.VISIBLE);
            } else {
                firstFragmentActive.setVisibility(View.INVISIBLE);
                secondFragmentActive.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
