package com.example.developer.androidweatherproject.weatherPackages;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeekForecast  {

    private String cod;
    private Integer cnt;
    private ArrayList<WeatherObject> list;

    public WeekForecast(){

    }

    public ArrayList<WeatherObject> getList() {
        return list;
    }

    public Integer getCnt() {
        return cnt;
    }

    public String getCod() {
        return cod;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public void setList(ArrayList<WeatherObject> list) {
        this.list = list;
    }



    @Override
    public String toString() {
        return "{\n cod: "+cod.toString()+
                "\ncnt: "+cnt.toString()+
                "\nlist: " +list+

                "\n}";
    }
}
