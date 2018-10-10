package com.example.developer.androidweatherproject.weatherPackages.apiCall;

import android.os.AsyncTask;
import android.util.Log;

import com.example.developer.androidweatherproject.localCache.StorageDB;
import com.example.developer.androidweatherproject.weatherPackages.Main;
import com.example.developer.androidweatherproject.weatherPackages.WeatherObject;
import com.example.developer.androidweatherproject.weatherPackages.WeekForecast;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.example.developer.androidweatherproject.MainActivity.IS_CACHED;
import static com.example.developer.androidweatherproject.MainActivity.getStorageDBServer;
import static com.example.developer.androidweatherproject.MainActivity.isNetworkAvailable;
import static com.example.developer.androidweatherproject.MainActivity.putBoolean;
import static  com.example.developer.androidweatherproject.localCache.StorageDB.clearCache;

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
            String longitude = params[1], latitude = params[0]; //get coordinates from ui thread




            //api endpoint
            final String url = BASE_PATH + "?lon=" + longitude + "&lat=" + latitude + "&appid=" + appid;


            //if there is internet access collect data from api
            if (hasInternetAccess()){

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
                restTemplate.getMessageConverters().add(
                        new MappingJackson2HttpMessageConverter());
                WeekForecast weekForecast = restTemplate.getForObject(url, WeekForecast.class);
                if(weekForecast != null)
                    clearCache();
                return weekForecast;
            }else {
                //add no internet connection activity
                onTaskFailed();
            }

        } catch (Exception e) {
            Log.e("WSX", e.getMessage(), e);
            //add something went wrong activity
            onTaskFailed();
        }

        return null;
    }


    private void onTaskFailed(){
        if(!isOnTaskListenerNull())
            listener.onTaskFailed();
    }



    private boolean isOnTaskListenerNull(){
        return listener == null;
    }


    @Override
    protected void onPostExecute(WeekForecast weekForecast) {

        //update cache
        cacheWeatherData(weekForecast, listener);




    }

    public interface OnTaskCompleted{
        void onTaskCompleted();
        void onTaskFailed();
    }

    private void cacheWeatherData(final WeekForecast weekForecast, OnTaskCompleted onTaskCompleted){



        //no data was returned throw error - something went wrong
        if(weekForecast == null){
            if(onTaskCompleted != null)
                onTaskCompleted.onTaskFailed();
            return;
        }

        //update cache data with new data
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

            getStorageDBServer().addWeatherEvents(StorageDB.toContentValues(weatherForecastMap));

        }


        putBoolean(IS_CACHED,true);

        if(!isOnTaskListenerNull()) {
            listener.onTaskCompleted();
        }

    }




    private boolean hasInternetAccess(){
        boolean connection = false;
        try {
            if (isNetworkAvailable()) {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("https://www.google.com/") //ping google
                                .openConnection());
                urlc.setConnectTimeout(10000);
                urlc.connect();
                connection = (urlc.getResponseCode() == 200);
                return connection;
            } else {

                return connection;
            }


        } catch (IOException e) {
            Log.e("WSX", "Error checking internet connection", e);
            return connection;
        }

    }

}
