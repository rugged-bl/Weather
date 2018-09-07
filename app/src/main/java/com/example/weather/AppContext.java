package com.example.weather;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by василий on 24.01.2016.
 */

class AppContext {
    public static final Map<String, Integer> mapWeatherIconsAssoc = new DefaultHashMap<>(R.raw.na);
    public static final Map<String, String> mapDayNamesAssoc = new DefaultHashMap<>("en");

    static public class DefaultHashMap<K, V> extends HashMap<K, V> {
        V defaultValue;

        DefaultHashMap(V defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public V get(Object k) {
            return containsKey(k) ? super.get(k) : defaultValue;
        }
    }
    //public static JSONObject json
}
