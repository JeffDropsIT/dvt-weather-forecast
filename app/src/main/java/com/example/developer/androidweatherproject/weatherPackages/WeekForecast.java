package com.example.developer.androidweatherproject.weatherPackages;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeekForecast implements Serializable {

    private String cod;
    private Integer cnt;
    private ArrayList<WeatherObject> list;

    public WeekForecast(){

    }


    public ArrayList getAllWeekDays(){
        ArrayList<String> daysOfWeek = new ArrayList<>();
        for(int i = 0;  i < list.size(); i++){
            String dtTxt = list.get(i).getDt_txt();
            String tmpDay =  list.get(i).getDayOfWeek(dtTxt);
            if(!daysOfWeek.contains(tmpDay)){
                daysOfWeek.add(tmpDay);
                Log.i("WSX", "getAllWeekDays: "+tmpDay);
            }

        }



        return daysOfWeek;
    }
    public String getCurrentDay(){
        String today = list.get(0).getDayOfWeek();
        return today;
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
