package com.example.weather;

import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by василий on 10.02.2016.
 */
public class GetJsonFromFile extends AsyncTask<String, Void, Void> {
    public AsyncContext delegate = null;

    @Override
    protected Void doInBackground(String... strings) {
        File file = new File(MainActivity.sdPath + "/" + strings[0] + ".json");

        if (file.exists()) {
            try {
                Intent intent = new Intent(strings[0]);

                InputStream inputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder str = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                    str.append(line);
                str.append('\n');
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();

                intent.putExtra(MainActivity.BROADCAST_NEW_JSON, str.toString());
                //Thread.sleep(1000);
                delegate.sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
                delegate.startGetWeatherInformation(strings[1]);
            }
        } else {
            delegate.startGetWeatherInformation(strings[1]);
        }

        return null;
    }
}
