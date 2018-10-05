package com.example.developer.androidweatherproject.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.developer.androidweatherproject.MainActivity;

public class UpdateWeatherService extends Service{

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("WSX", "onStartCommand: get data from cache or network");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
