package com.example.developer.androidweatherproject.weatherPackages;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherObject {

    private String dt_txt, dt;
    private ArrayList<Weather> weather;
    private Main main;
    public WeatherObject(){

    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;

    }

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public void setWeather(ArrayList<Weather> weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {

        return " {\n"+" dt: "+dt.toString()+" \ndt_txt: "+dt_txt.toString()+"\n main: "+main.toString()+" \nweather: "+getWeather()+ " \n}";
    }
}
