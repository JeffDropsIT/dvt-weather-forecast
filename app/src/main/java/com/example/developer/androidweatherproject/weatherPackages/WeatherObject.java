package com.example.developer.androidweatherproject.weatherPackages;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherObject {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private String dt_txt, dt;
    private ArrayList<Weather> weather;
    private Main main;
    public WeatherObject(){

    }

    public String getDayOfWeek(){
        String dayOfWeek, fullDatePattern = YYYY_MM_DD_HH_MM_SS, dayPattern = "EEEE";
        Date fullDateFormat = null;
        SimpleDateFormat dayFormat = new SimpleDateFormat(dayPattern);
        try {
            fullDateFormat =  new SimpleDateFormat(fullDatePattern).parse(getDt_txt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(fullDateFormat != null){
           dayOfWeek = dayFormat.format(fullDateFormat);
            return dayOfWeek;
        }else {
            return "null";
        }

    }
    public String getDayOfWeek(String dtTxt){
        String dayOfWeek, fullDatePattern = "yyyy-MM-dd HH:mm:ss", dayPattern = "EEEE";
        Date fullDateFormat = null;
        SimpleDateFormat dayFormat = new SimpleDateFormat(dayPattern);
        try {
            fullDateFormat =  new SimpleDateFormat(fullDatePattern).parse(dtTxt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(fullDateFormat != null){
            dayOfWeek = dayFormat.format(fullDateFormat);
            return dayOfWeek;
        }else {
            return "null";
        }

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
