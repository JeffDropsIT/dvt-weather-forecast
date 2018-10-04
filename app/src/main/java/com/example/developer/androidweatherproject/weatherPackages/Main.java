package com.example.developer.androidweatherproject.weatherPackages;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Main {

    private Float temp;
    private Float temp_min;
    private Float temp_max;


    public Main(){

    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }

    public void setTemp_max(Float temp_max) {
        this.temp_max = temp_max;
    }

    public void setTemp_min(Float temp_min) {
        this.temp_min = temp_min;
    }

    public Float getTemp() {
        return temp;
    }

    public Float getTemp_max() {
        return temp_max;
    }

    public Float getTemp_min() {
        return temp_min;
    }

    @Override
    public String toString() {
        return " { \n"+"temp: "+temp.toString()+" \ntemp_min: "+temp_min.toString()+" \ntemp_max: "+temp_max.toString()+" \n}";
    }
}
