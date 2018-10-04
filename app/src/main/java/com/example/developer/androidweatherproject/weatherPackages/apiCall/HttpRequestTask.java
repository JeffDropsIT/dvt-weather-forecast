package com.example.developer.androidweatherproject.weatherPackages.apiCall;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.developer.androidweatherproject.MainActivity;
import com.example.developer.androidweatherproject.weatherPackages.WeekForecast;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class HttpRequestTask extends AsyncTask<String, Void, WeekForecast> {
    private final String BASE_PATH = "http://api.openweathermap.org/data/2.5/forecast", appid = "0ba4a7729669b8c072c20f5daa13b4a9";
    TextView ttvCurrent, ttvMin, ttvMax;

    public HttpRequestTask(TextView ttvCurrent, TextView ttvMin, TextView ttvMax){
        this.ttvCurrent = ttvCurrent;
        this.ttvMax = ttvMax;
        this.ttvMin = ttvMin;
    }
    @Override
    protected WeekForecast doInBackground(String... params) {
        try {
            String longitude = params[1], latitude = params[0];

            final String url = BASE_PATH + "?lon=" + longitude + "&lat=" + latitude + "&appid=" + appid;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            restTemplate.getMessageConverters().add(
                    new MappingJackson2HttpMessageConverter());
            WeekForecast weekForecast = restTemplate.getForObject(url, WeekForecast.class);
            Log.i("WSX", "onCreate: "+weekForecast.toString());
            return weekForecast;
        } catch (Exception e) {
            Log.e("WSX", e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(WeekForecast weekForecast) {



        String current =  Float.toString(weekForecast.getList().get(0).getMain().getTemp());
        String max =  Float.toString(weekForecast.getList().get(0).getMain().getTemp_max());
        String min =  Float.toString(weekForecast.getList().get(0).getMain().getTemp_min());

        displayWeatherInfo(current,  min, max);
        Log.i("WSX", "onPostExecute: "+weekForecast);
    }


    public void displayWeatherInfo(String current, String min, String max){

        char degrees = (char) 0x00B0;
        ttvCurrent.setText(current+degrees);
        ttvMax.setText(max+degrees);
        ttvMin.setText(min+degrees);
    }
}
