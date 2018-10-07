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
            new HttpRequestTask().execute("-29.844776","31.014339");
        }else{
            Log.i("WSX", "onStartCommand: no internet connection or database not initialized");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
