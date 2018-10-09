package com.example.developer.androidweatherproject.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.developer.androidweatherproject.MainActivity;
import com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask;

import static com.example.developer.androidweatherproject.MainActivity.isNetworkAvailable;

public class UpdateWeatherService extends Service{

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        Log.i("WSX", "onStartCommand: get data from cache or network");
        if(MainActivity.getStorageDBServer() != null && isNetworkAvailable() ){
            if(!MainActivity.getString("lat").equals("0") && !MainActivity.getString("lon" ).equals("0")){
                Log.i("WSX", "onSuccess: lat "+MainActivity.getString("lat")+" lon "+MainActivity.getString("lon"));
                new HttpRequestTask().execute(MainActivity.getString("lat"),MainActivity.getString("lon"));
            }



        }else{
            Log.i("WSX", "onStartCommand: no internet connection or database not initialized");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        //use broadcast to handle update cache
        return null;
    }


}
