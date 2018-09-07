package com.example.weather;

import java.util.HashMap;

/**
 * Created by василий on 29.01.2016.
 */
public class ForecastItem extends HashMap<String, String> {
    public static final String DAY_NAME = "DAY_NAME";
    public static final String DAY_TIME = "DAY_TIME";
    public static final String DETAILS_FIELD = "DETAILS_FIELD";
    public static final String CURRENT_TEMPERATURE_FIELD = "CURRENT_TEMPERATURE_FIELD";
    private static final long serialVersionUID = 1L;

    ForecastItem(String day_name, String day_time, String details_field, String current_temperature_field) {
        super();
        super.put(DAY_NAME, day_name);
        super.put(DETAILS_FIELD, details_field);
        super.put(CURRENT_TEMPERATURE_FIELD, current_temperature_field);
        super.put(DAY_TIME, day_time);
    }
}
