package com.example.weather;

import android.content.Intent;

/**
 * Created by василий on 10.02.2016.
 */
public interface AsyncContext {
    void startGetWeatherInformation(String sWeatherApi);

    void sendBroadcast(Intent intent);
}
