package com.example.developer.androidweatherproject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.developer.androidweatherproject.localCache.StorageDB;
import com.example.developer.androidweatherproject.services.UpdateWeatherService;
import com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "WeatherDB";
    private SQLiteDatabase weatherDB;
    public static StorageDB storageDBServer;
    TextView ttvCurrent, ttvMin, ttvMax;
    TextView ttvDay1, ttvDay2, ttvDay3, ttvDay4, ttvDay5;
    ArrayList<TextView> daysTextViewList;

    @Override
    protected void onStart() {
        super.onStart();
        weatherDB = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        storageDBServer = new StorageDB(weatherDB, getApplicationContext());
    }

    public static StorageDB getStorageDBServer(){
        return storageDBServer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        daysTextViewList = new ArrayList<>();
        ttvCurrent = findViewById(R.id.ttv_current);
        ttvMin = findViewById(R.id.ttv_min);
        ttvMax = findViewById(R.id.ttv_max);

        ttvDay1 = findViewById(R.id.ttv_day1);
        ttvDay2 = findViewById(R.id.ttv_day2);
        ttvDay3 = findViewById(R.id.ttv_day3);
        ttvDay4 = findViewById(R.id.ttv_day4);
        ttvDay5 = findViewById(R.id.ttv_day5);


        daysTextViewList.add(ttvDay1);
        daysTextViewList.add(ttvDay2);
        daysTextViewList.add(ttvDay3);
        daysTextViewList.add(ttvDay4);
        daysTextViewList.add(ttvDay5);
        updateWeatherInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new HttpRequestTask(ttvCurrent, ttvMin, ttvMax, daysTextViewList).execute("21.21","22.22");
    }
    public void fetchWeatherData(){

    }
    private void updateWeatherInfo(){
        int currentTime  = Calendar.getInstance().get(Calendar.HOUR);
        int remainderNxtTime = currentTime % 3;
        Log.i("WSX", "updateWeatherInfo: "+currentTime);
        //get the current time and calculate the next hour data needs
        //to be updated and set alarm manager to get refreshed on current time
        if(remainderNxtTime == 0){
            currentTime += 0;
        }else if(remainderNxtTime == 1){
            currentTime += 2;
        }else if(remainderNxtTime == 2){
            currentTime += 1;
        }

        Intent updateIntent = new Intent(this, UpdateWeatherService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, updateIntent, 0);

        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        long interval =  60 * 1000;  //60 * 60 * 1000 * 3; //180 minutes
        Calendar timeTillUpdate = Calendar.getInstance();
        timeTillUpdate.add(Calendar.HOUR, currentTime);
        timeTillUpdate.add(Calendar.MINUTE, 0);
        timeTillUpdate.add(Calendar.SECOND, 0);
        long timeTillUpdateLong = timeTillUpdate.getTimeInMillis();

        manager.setRepeating(AlarmManager.RTC_WAKEUP, 10000, interval, alarmIntent);



    }

}
