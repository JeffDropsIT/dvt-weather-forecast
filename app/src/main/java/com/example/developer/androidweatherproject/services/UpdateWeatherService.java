package com.example.developer.androidweatherproject.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.developer.androidweatherproject.MainActivity;
import com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask;

public class UpdateWeatherService extends Service{

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("WSX", "onStartCommand: get data from cache or network");
        if(MainActivity.getStorageDBServer() != null){
            new HttpRequestTask().execute("21.21","22.22");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
