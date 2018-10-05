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

import static com.example.developer.androidweatherproject.MainActivity.getStorageDBServer;

public class HttpRequestTask extends AsyncTask<String, Void, WeekForecast> {
    public static final String WEEK_DAYS = "weekDays";
    private final String BASE_PATH = "http://api.openweathermap.org/data/2.5/forecast", appid = "0ba4a7729669b8c072c20f5daa13b4a9";
    TextView ttvCurrent, ttvMin, ttvMax;
    ArrayList<TextView> daysTextViewList;
    public HttpRequestTask(TextView ttvCurrent, TextView ttvMin, TextView ttvMax, ArrayList<TextView> daysTextViewList){
        this.ttvCurrent = ttvCurrent;
        this.ttvMax = ttvMax;
        this.ttvMin = ttvMin;
        this.daysTextViewList = daysTextViewList;
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

        Main main = weekForecast.getList().get(0).getMain();
        Double current = main.getTemp();
        Double max = main.getTemp_max();
        Double min = main.getTemp_min();


        String currentStr =  Long.toString(main.kelvinToCelsius(current));
        String maxStr =  Long.toString(main.kelvinToCelsius(max));
        String minStr =  Long.toString(main.kelvinToCelsius(min));
        displayWeatherInfo(currentStr, minStr, maxStr);
        Log.i("WSX", "onPostExecute: "+weekForecast);
        Log.i("WSX", "onPostExecute: day of week"+weekForecast.getCurrentDay());



        getStorageDBServer().putListString(WEEK_DAYS,weekForecast.getAllWeekDays() );
        displayWeekDays(getStorageDBServer().getListString(WEEK_DAYS));
        cacheWeatherData(weekForecast);

    }


    private void cacheWeatherData(WeekForecast weekForecast){


        Map<String, Object> weatherForecastMap = new HashMap<>();

        for(int i = 0; i < weekForecast.getList().size(); i++){
            Main main = weekForecast.getList().get(i).getMain();
            WeatherObject weatherObject = weekForecast.getList().get(i);

            Double current = main.getTemp();
            Double max = main.getTemp_max();
            Double min = main.getTemp_min();



            String currentStr =  Long.toString(main.kelvinToCelsius(current));
            String maxStr =  Long.toString(main.kelvinToCelsius(max));
            String minStr =  Long.toString(main.kelvinToCelsius(min));



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



        }

        getStorageDBServer().addWeatherEvents(StorageDB.toContentValues(weatherForecastMap));


    }

    public void displayWeatherInfo(String current, String min, String max){

        char degrees = (char) 0x00B0; // degree symbol
        ttvCurrent.setText(current+degrees);
        ttvMax.setText(max+degrees);
        ttvMin.setText(min+degrees);
    }

    public void displayWeekDays(ArrayList<String> weekDays){
        weekDays.remove(0);
        if(!weekDays.isEmpty() && !daysTextViewList.isEmpty()){
            if(weekDays.size() == daysTextViewList.size()){
                for(int i = 0; i < daysTextViewList.size(); i++){
                    daysTextViewList.get(i).setText(weekDays.get(i));
                    Log.i("WSX", "day "+i);
                }
            }else {
                Log.i("WSX", "displayWeekDays: something went wrong size does not match");
            }
        }else {
            Log.i("WSX", "displayWeekDays: something went wrong list empty");
        }

    }
}
