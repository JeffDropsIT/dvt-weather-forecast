package com.example.developer.androidweatherproject.weatherPackages;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather  {
    private Integer id;
    private String main, description, icon;

    public Weather(){

    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getMain() {
        return main;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return " { \n"+"id: "+id.toString()+" \nmain: "+main+" \nicon: "+icon+" \ndescription: "+description+" \n}";
    }
}
