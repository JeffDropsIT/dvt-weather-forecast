package com.example.developer.androidweatherproject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.developer.androidweatherproject.localCache.StorageDB;
import com.example.developer.androidweatherproject.services.UpdateWeatherService;
import com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.developer.androidweatherproject.weatherPackages.WeatherObject.YYYY_MM_DD_HH_MM_SS;
import static com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask.CURRENT_STR;
import static com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask.MAX_STR;
import static com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask.MIN_STR;
import static com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask.WEEK_DAYS;


public class MainActivity extends AppCompatActivity implements HttpRequestTask.OnTaskCompleted {

    private static final String DATABASE_NAME = "WeatherDB";
    private SQLiteDatabase weatherDB;
    public static StorageDB storageDBServer;
    TextView ttvCurrent, ttvMin, ttvMax;
    TextView ttvDay1, ttvDay2, ttvDay3, ttvDay4, ttvDay5;
    ArrayList<TextView> daysTextViewList;



    public static StorageDB getStorageDBServer(){
        return storageDBServer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new HttpRequestTask(this).execute("21.21","22.22");
        weatherDB = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        storageDBServer = new StorageDB(weatherDB, getApplicationContext());


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
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new HttpRequestTask(this).execute("21.21","22.22");
        displayWeatherInfo(storageDBServer.getString(CURRENT_STR),storageDBServer.getString(MIN_STR) ,storageDBServer.getString(MAX_STR));
        displayWeekDays(storageDBServer.getListString(WEEK_DAYS));
    }


    private int getCurrentHour(){
        Calendar currentForecastTmp = Calendar.getInstance();
        currentForecastTmp.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm aa");
        String time = dateFormat.format(currentForecastTmp.getTime());

        return  Integer.parseInt(time.split(":")[0]);

    }
    private void updateWeatherInfo(){



        int currentTime  = getCurrentHour();
        int remainderNxtTime = currentTime % 3;
        int currentForecastTime = currentTime;

        //get the current time and calculate the next hour data needs
        //to be updated and set alarm manager to get refreshed on current time
        if(remainderNxtTime == 0){
            currentTime += 0;

        }else if(remainderNxtTime == 1){
            currentTime += 2;
            currentForecastTime -= 1;



        }else if(remainderNxtTime == 2){
            currentTime += 1;
            currentForecastTime -= 2;
        }

        int futureForecastTime = currentTime;



        Log.i("WSX", "updateWeatherInfo: currentForecastDate "+currentForecastTime);
        Calendar currentForecastCal = Calendar.getInstance();
        currentForecastCal.setTimeInMillis(System.currentTimeMillis());
        currentForecastCal.set(Calendar.HOUR, currentForecastTime);
        currentForecastCal.set(Calendar.MINUTE, 0);
        currentForecastCal.set(Calendar.SECOND, 0);
        //SimpleDateFormat dayFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);

        Date currentForecastDate = currentForecastCal.getTime();
        Log.i("WSX", "updateWeatherInfo: currentForecastDate "+currentForecastDate);
        //getLocalForecastData(currentForecastDate);

        Intent updateIntent = new Intent(this, UpdateWeatherService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, updateIntent, 0);

        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        long interval =  60 * 1000;  //60 * 60 * 1000 * 3; //180 minutes
        Calendar timeTillUpdate = Calendar.getInstance();
        timeTillUpdate.add(Calendar.HOUR, futureForecastTime);
        timeTillUpdate.add(Calendar.MINUTE, 0);
        timeTillUpdate.add(Calendar.SECOND, 0);


        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        Log.i("WSX", "updateWeatherInfo: ddddd "+df.format(dateobj).replace("/", "-"));


        long timeTillUpdateLong = timeTillUpdate.getTimeInMillis();
        Log.i("WSX", "updateWeatherInfo: timeTillUpdate "+timeTillUpdate.getTime());
        manager.setRepeating(AlarmManager.RTC_WAKEUP, 10000, interval, alarmIntent);



    }
    private void displayWeatherInfo(String current, String min, String max){


        Log.i("WSX", "displayWeatherInfo: "+current+" "+min+" "+max);
        char degrees = (char) 0x00B0; // degree symbol
        ttvCurrent.setText(current+degrees);
        ttvMax.setText(max+degrees);
        ttvMin.setText(min+degrees);
    }

    private void displayWeekDays(ArrayList<String> weekDays){

        if(!weekDays.isEmpty() && !daysTextViewList.isEmpty()){
            weekDays.remove(0);
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

    private Map<String, Object> getLocalForecastData(String currentForecastTime){
        String[] tableCols = storageDBServer.getColumnNames();
        Cursor forecastCursor;
        forecastCursor = storageDBServer.getMatch("dtTxt", currentForecastTime);
        Map<String, Object> data = new HashMap<>();
        if(forecastCursor.moveToNext()){
            do{
                for(int i = 0 ; i < tableCols.length ; i++){
                    String colContent = forecastCursor.getString(forecastCursor.getColumnIndex(tableCols[i]));
                    switch (tableCols[i]){
                        case "dt":
                            data.put("dt", colContent);
                            break;
                        case "dtTime":
                            data.put("dtTime", colContent);
                            break;
                        case "icon":
                            data.put("icon", colContent);
                            break;
                        case "main":
                            data.put("main", colContent);
                            break;
                        case "tempMax":
                            data.put("tempMax", colContent);
                            break;
                        case "tempMin":
                            data.put("tempMin", colContent);
                            break;
                        case "temperature":
                            data.put("temperature", colContent);
                            break;
                        case "id":
                            data.put("id", colContent);
                            break;

                    }
                }
            }while (forecastCursor.moveToNext());
        }

        Log.i("WSX", "getLocalForecastData: "+data.keySet() +"|"+data);
        return data;
    }

    @Override
    public void onTaskCompleted() {


        displayWeatherInfo(storageDBServer.getString(CURRENT_STR),storageDBServer.getString(MIN_STR) ,storageDBServer.getString(MAX_STR));
        displayWeekDays(storageDBServer.getListString(WEEK_DAYS));
    }
}
