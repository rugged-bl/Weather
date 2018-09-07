package com.example.weather;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ForecastFragment extends ListFragment implements AsyncContext {
    public AsyncContext delegate = null;
    ListAdapter adapter;
    ArrayList<ForecastItem> forecastItems = new ArrayList<>();
    private TextView day_name;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private Handler handler;
    private View rootView;
    private JSONObject json = null;
    private Integer ct = 0;
    private BroadcastReceiver br = new BroadcastReceiver() {
        // действия при получении сообщений
        public void onReceive(Context context, Intent intent) {
            String sJSON = intent.getStringExtra(MainActivity.BROADCAST_NEW_JSON);
            try {
                json = new JSONObject(sJSON);

                updateWeatherData();
            } catch (JSONException e) {
                Log.e("SimpleWeather", "JSONObject creation failed");
            }

            // Ловим сообщения о старте задач

        }
    };

    public ForecastFragment() {
        handler = new Handler() {
        };

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SimpleAdapter(getActivity(), forecastItems, R.layout.fragment_forecast,
                new String[]{ForecastItem.DAY_NAME, ForecastItem.DAY_TIME, ForecastItem.DETAILS_FIELD, ForecastItem.CURRENT_TEMPERATURE_FIELD},
                new int[]{R.id.day_name, R.id.day_time, R.id.details_field, R.id.current_temperature_field});
        setListAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter(MainActivity.BROADCAST_ACTION_FORECAST);
        getActivity().registerReceiver(br, intentFilter);

        GetJsonFromFile getJsonFromFile = new GetJsonFromFile();
        getJsonFromFile.delegate = MainActivity.delegate;
        getJsonFromFile.execute(MainActivity.BROADCAST_ACTION_FORECAST, MainActivity.OPEN_WEATHER_MAP_API_FORECAST);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        delegate.sendBroadcast(intent);
    }

    public void startGetWeatherInformation(String sWeatherApi) {
        delegate.startGetWeatherInformation(sWeatherApi);
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        day_name = (TextView) rootView.findViewById(R.id.day_name);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);

        return rootView;
    }*/

    private void updateWeatherData() {
        new Thread() {
            public void run() {
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            JSONArray forecastDays = json.getJSONArray("list");

            forecastItems.clear();

            for (int i = 0; i < forecastDays.length(); i++) {
                JSONObject list = forecastDays.getJSONObject(i);
                JSONObject main = list.getJSONObject("main");
                JSONObject weather = list.getJSONArray("weather").getJSONObject(0);

                long dt = list.getLong("dt") * 1000;
                Double temperature = main.getDouble("temp");
                String sDescription = weather.getString("description");

                String sTemperature = String.format("%3d ℃", Math.round(temperature));
                String sDayName = new SimpleDateFormat("EEEE", Locale.US).format(dt);
                String sDayTime = new SimpleDateFormat("HH:mm", Locale.US).format(dt);
                if (getString(R.string.lang).equals("ru"))
                    sDayName = AppContext.mapDayNamesAssoc.get(sDayName);
                forecastItems.add(new ForecastItem(sDayName, sDayTime, sDescription, sTemperature));
            }

            getListView().invalidateViews();
        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }
}
