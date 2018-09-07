package com.example.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment implements AsyncContext {
    public AsyncContext delegate = null;
    private TextView cityField;
    private TextView updatedField;
    private TextView day_name;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private ImageView weatherImg;
    private Handler handler;
    private View rootView;
    private JSONObject json = null;
    private BroadcastReceiver br = new BroadcastReceiver() {
        // действия при получении сообщений
        public void onReceive(Context context, Intent intent) {
            String sJSON = intent.getStringExtra(MainActivity.BROADCAST_NEW_JSON);
            try {
                json = new JSONObject(sJSON);
                updateWeatherData();
            } catch (JSONException e) {
                SetWeatherImageAsyncTask setWeatherImageAsyncTask = new SetWeatherImageAsyncTask();
                setWeatherImageAsyncTask.execute("na");
                e.printStackTrace();
            }

            // Ловим сообщения о старте задач
        }
    };

    public WeatherFragment() {
        handler = new Handler() {
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*String sJSON = getArguments().getString("json");
        try {
            json = new JSONObject(sJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        // создаем фильтр для BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(MainActivity.BROADCAST_ACTION_CURRENT);
        // регистрируем (включаем) BroadcastReceiver
        getActivity().registerReceiver(br, intentFilter);

        GetJsonFromFile getJsonFromFile = new GetJsonFromFile();
        getJsonFromFile.delegate = MainActivity.delegate;
        getJsonFromFile.execute(MainActivity.BROADCAST_ACTION_CURRENT, MainActivity.OPEN_WEATHER_MAP_API_CURRENT);
    }

    public void sendBroadcast(Intent intent) {
        delegate.sendBroadcast(intent);
    }

    public void startGetWeatherInformation(String sWeatherApi) {
        delegate.startGetWeatherInformation(sWeatherApi);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        day_name = (TextView) rootView.findViewById(R.id.day_name);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        weatherImg = (ImageView) rootView.findViewById(R.id.weather_img);

        weatherImg.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        weatherImg.setMinimumHeight(rootView.getHeight() / 3);

        //updateWeatherData();

        return rootView;
    }

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
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");


            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));

            String sDayName = new SimpleDateFormat("EEEE", Locale.US).format(new Date());
            if (getString(R.string.lang).equals("ru"))
                sDayName = AppContext.mapDayNamesAssoc.get(sDayName);

            cityField.setText(String.format("%s, %s",
                    json.getString("name").toUpperCase(Locale.US),
                    json.getJSONObject("sys").getString("country")));
            updatedField.setText(String.format("%s: %s",
                    getString(R.string.last_update),
                    updatedOn));
            day_name.setText(sDayName);
            detailsField.setText(String.format("%s\n%s: %d%%\n%s: %d %s",
                    details.getString("description").toUpperCase(Locale.US),
                    getString(R.string.humidity),
                    main.getInt("humidity"),
                    getString(R.string.pressure),
                    (int) (main.getDouble("pressure") * 0.75006375541921 + 0.5),
                    getString(R.string.pressureV)));
            currentTemperatureField.setText(
                    String.format("%.2f ℃",
                            main.getDouble("temp")));

            SetWeatherImageAsyncTask setWeatherImageAsyncTask = new SetWeatherImageAsyncTask();
            setWeatherImageAsyncTask.execute(details.getString("icon"));

        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private class SetWeatherImageAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(final String... icon) {
            try {
                /*String sURL = String.format("http://openweathermap.org/img/w/%02dd.png", 1);
                URL url = new URL(sURL);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                //final Drawable drawable = Drawable.createFromStream(input, "01d.png");*/

                handler.post(new Runnable() {
                    public void run() {
                        try {
                            weatherImg.setImageDrawable(WeatherExtra.getWeatherIconDrawable(
                                    getResources(), icon[0], rootView.getHeight() / 3, rootView.getHeight() / 3));

                            //PictureDrawable drawable = new PictureDrawable(SVG.getFromResource(getResources(), R.raw.sunss).renderToPicture());
                            //drawable.setColorFilter(0xcccccc, PorterDuff.Mode.MULTIPLY);
                            //weatherImg.setImageDrawable(drawable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}