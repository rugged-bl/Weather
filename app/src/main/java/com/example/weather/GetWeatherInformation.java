package com.example.weather;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetWeatherInformation extends AsyncTask<String, Void, String[]> {
    public AsyncResponse delegate = null;

    @Override
    protected void onPostExecute(String result[]) {
        delegate.processFinish(result);
    }

    @Override
    protected String[] doInBackground(String... strings) {
        String result[] = new String[2];
        result[0] = strings[0];

        try {
                /*URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric");

                Scanner in = new Scanner((InputStream) url.getContent());*/

            //http://icomms.ru/inf/meteo.php?tid=24
            URL url = new URL(String.format(strings[0],
                    "Kaliningrad",
                    strings[1],
                    strings[2]));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //connection.addRequestProperty("x-api-key", contexts[0].getString(R.string.open_weather_maps_app_id));
            connection.setRequestMethod("POST");

            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            //String fname = new File(getFilesDir(), "test.png").getAbsolutePath();


                /*String a = String.format("http://openweathermap.org/img/w/%02dd.png", 1);
                url = new URL(a);

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                OutputStream stream = context.openFileOutput("test.png", MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();

                bitmap =  BitmapFactory.decodeStream(openFileInput("test.png"));*/

            StringBuilder json = new StringBuilder(4096);
            String tmp;
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");

            reader.close();
            inputStreamReader.close();
            inputStream.close();
            connection.disconnect();

            //result = "{\"gis\":" + json + "}";
            result[1] = json.toString();
        } catch (Exception e) {
            result[1] = e.toString();
        }

        return result;
    }
}
