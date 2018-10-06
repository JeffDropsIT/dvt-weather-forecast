package com.example.developer.androidweatherproject.weatherPackages.apiCall;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.developer.androidweatherproject.MainActivity;
import com.example.developer.androidweatherproject.localCache.StorageDB;
import com.example.developer.androidweatherproject.weatherPackages.Main;
import com.example.developer.androidweatherproject.weatherPackages.Weather;
import com.example.developer.androidweatherproject.weatherPackages.WeatherObject;
import com.example.developer.androidweatherproject.weatherPackages.WeekForecast;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.developer.androidweatherproject.MainActivity.IS_CACHED;
import static com.example.developer.androidweatherproject.MainActivity.getStorageDBServer;
import static com.example.developer.androidweatherproject.MainActivity.putBoolean;

public class HttpRequestTask extends AsyncTask<String, Void, WeekForecast> {
    private final String BASE_PATH = "http://api.openweathermap.org/data/2.5/forecast", appid = "0ba4a7729669b8c072c20f5daa13b4a9";
    private OnTaskCompleted listener;
    public HttpRequestTask(OnTaskCompleted listener){
        this.listener = listener;
    }

    public HttpRequestTask() {

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

            return weekForecast;
        } catch (Exception e) {
            Log.e("WSX", e.getMessage(), e);
        }

        return null;
    }




    @Override
    protected void onPostExecute(WeekForecast weekForecast) {

        Log.i("WSX", "onPostExecute: "+weekForecast);

        cacheWeatherData(weekForecast);


        if(listener != null){
            listener.onTaskCompleted();
        }

    }

    public interface OnTaskCompleted{
        void onTaskCompleted();
    }
    private void cacheWeatherData(WeekForecast weekForecast){


        Map<String, Object> weatherForecastMap = new HashMap<>();

        for(int i = 0; i < weekForecast.getList().size(); i++){
            Main main = weekForecast.getList().get(i).getMain();
            WeatherObject weatherObject = weekForecast.getList().get(i);

            long current = main.getTemp();
            long max = main.getTemp_max();
            long min = main.getTemp_min();


            String currentStr =  Long.toString(current);
            String maxStr =  Long.toString(max);
            String minStr =  Long.toString(min);



            weatherForecastMap.put("dt", weatherObject.getDt());
            weatherForecastMap.put("id", weatherObject.getWeather().get(0).getId());
            weatherForecastMap.put("main", weatherObject.getWeather().get(0).getMain());
            weatherForecastMap.put("icon", weatherObject.getWeather().get(0).getIcon());
            weatherForecastMap.put("dtTxt", weatherObject.getDt_txt());
            weatherForecastMap.put("temperature", currentStr);
            weatherForecastMap.put("tempMin", minStr);
            weatherForecastMap.put("tempMax", maxStr);


            Log.i("WSX", "cacheWeatherData: dt: "+ weatherForecastMap.get("dt"));
            Log.i("WSX", "cacheWeatherData: id: "+ weatherForecastMap.get("id"));
            Log.i("WSX", "cacheWeatherData: main: "+ weatherForecastMap.get("main"));
            Log.i("WSX", "cacheWeatherData: icon: "+ weatherForecastMap.get("icon"));
            Log.i("WSX", "cacheWeatherData: dtTxt: "+ weatherForecastMap.get("dtTxt"));
            Log.i("WSX", "cacheWeatherData: temperature: "+ weatherForecastMap.get("temperature"));
            Log.i("WSX", "cacheWeatherData: tmpMin: "+ weatherForecastMap.get("tempMin"));
            Log.i("WSX", "cacheWeatherData: tmpMax: "+ weatherForecastMap.get("tempMax"));

            getStorageDBServer().addWeatherEvents(StorageDB.toContentValues(weatherForecastMap));

        }


        putBoolean(IS_CACHED,true);


    }



}
