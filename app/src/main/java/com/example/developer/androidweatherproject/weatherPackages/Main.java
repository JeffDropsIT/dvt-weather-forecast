package com.example.developer.androidweatherproject.weatherPackages;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Main {

    private Double temp;
    private Double temp_min;
    private Double temp_max;
    private final Double KELVIN_CONSTANT = 273.15;

    public Main(){

    }


    private long kelvinToCelsius(Double temperatureInKelvin){
        //T(°C) = T(K) - 273.15
        double tempInCelsius = temperatureInKelvin - KELVIN_CONSTANT;
        Log.i("WSX", "kelvinToCelsius: "+Math.round(tempInCelsius));
        return Math.round(tempInCelsius);
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public void setTemp_max(Double temp_max) {
        this.temp_max = temp_max;
    }

    public void setTemp_min(Double temp_min) {
        this.temp_min = temp_min;
    }

    public long getTemp() {
        return kelvinToCelsius(temp);
    }

    public long getTemp_max() {
        return kelvinToCelsius(temp_max);
    }

    public long getTemp_min() {
        return kelvinToCelsius(temp_min);
    }

    @Override
    public String toString() {
        return " { \n"+"temp: "+temp.toString()+" \ntemp_min: "+temp_min.toString()+" \ntemp_max: "+temp_max.toString()+" \n}";
    }
}
