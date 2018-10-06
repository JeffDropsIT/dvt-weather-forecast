package com.example.developer.androidweatherproject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.developer.androidweatherproject.localCache.StorageDB;
import com.example.developer.androidweatherproject.services.UpdateWeatherService;
import com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static com.example.developer.androidweatherproject.weatherPackages.WeatherObject.YYYY_MM_DD_HH_MM_SS;


public class MainActivity extends AppCompatActivity implements HttpRequestTask.OnTaskCompleted {

    private static final String DATABASE_NAME = "WeatherDB";
    public static final String CURRENT_TEMPERATURE = "temperature";
    public static final String TEMP_MIN = "tempMin";
    public static final String TEMP_MAX = "tempMax";
    private SQLiteDatabase weatherDB;
    public static StorageDB storageDBServer;
    TextView ttvCurrent, ttvMin, ttvMax;
    TextView ttvDay1, ttvDay2, ttvDay3, ttvDay4, ttvDay5;
    ArrayList<TextView> daysTextViewList;
    private static ConnectivityManager connectivityManager;


    public static StorageDB getStorageDBServer(){
        return storageDBServer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(isNetworkAvailable()){
            new HttpRequestTask(this).execute("21.21","22.22");
            Log.i("WSX", "onCreate: internet connection ");
        }else{
            Log.i("WSX", "onCreate: no internet connection ");
        }


        weatherDB = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        storageDBServer = new StorageDB(weatherDB);


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





    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNetworkAvailable()){
            new HttpRequestTask(this).execute("21.21","22.22");
            Log.i("WSX", "onResume: internet connection ");
        }else{
            Log.i("WSX", "onResume: no internet connection ");
        }
        updateWeatherInfo();
        displayWeatherInfo();
        displayWeekDays();
    }


    private int getCurrentHour(){

        Date currentForecastTmp =  new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm aa");
        String time = dateFormat.format(currentForecastTmp);

        return  Integer.parseInt(time.split(":")[0]);

    }

    private String getCurrentForecastDate(int currentForecastTime){

        SimpleDateFormat dayFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Calendar currentForecastCal = Calendar.getInstance();
        currentForecastCal.setTimeInMillis(System.currentTimeMillis());
        currentForecastCal.set(Calendar.HOUR_OF_DAY, currentForecastTime);
        currentForecastCal.set(Calendar.MINUTE, 0);
        currentForecastCal.set(Calendar.SECOND, 0);
        String currentForecastDate = dayFormat.format(currentForecastCal.getTime());

        return currentForecastDate;
    }

    private Calendar getNxtForecastCalender(int futureForecastTime){

        Calendar timeTillUpdateCal = Calendar.getInstance();
        timeTillUpdateCal.set(Calendar.HOUR_OF_DAY, futureForecastTime);
        timeTillUpdateCal.set(Calendar.MINUTE, 0);
        timeTillUpdateCal.set(Calendar.SECOND, 0);

        return timeTillUpdateCal;
    }

    private String getNxtForecastDate(int futureForecastTime){

        SimpleDateFormat dayFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Calendar timeTillUpdateCal = Calendar.getInstance();
        timeTillUpdateCal.set(Calendar.HOUR_OF_DAY, futureForecastTime);
        timeTillUpdateCal.set(Calendar.MINUTE, 0);
        timeTillUpdateCal.set(Calendar.SECOND, 0);
        String timeTillUpdate = dayFormat.format(timeTillUpdateCal.getTime());

        return timeTillUpdate;
    }



    private int[] calculateForecastHours(){
        int currentHour  = getCurrentHour();
        int remainderNxtHour = currentHour % 3;
        int currentForecastHour = currentHour;

        //get the current time and calculate the next hour, data needs
        //to be updated and set alarm manager to get refreshed on current time
        if(remainderNxtHour == 0){
            currentHour += 3;

        }else if(remainderNxtHour == 1){
            currentHour += 2;
            currentForecastHour -= 1;

        }else if(remainderNxtHour == 2){
            currentHour += 1;
            currentForecastHour -= 2;
        }
        int futureForecastHour = currentHour;
        Log.i("WSX", "calculateForecastHours: futureForecastHour hour "+futureForecastHour);
        Log.i("WSX", "calculateForecastHours: currentForecastHour hour "+currentForecastHour);
        return new int[]{ futureForecastHour , currentForecastHour};
    }
    private void updateWeatherInfo(){


        int futureForecastHour = calculateForecastHours()[0];



        Log.i("WSX", "updateWeatherInfo: futureForecast hour "+futureForecastHour);



        Intent updateIntent = new Intent(this, UpdateWeatherService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, updateIntent, 0);

        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        long interval =  60 * 1000;  //60 * 60 * 1000 * 3; //180 minutes
        String timeTillUpdate = getNxtForecastDate(futureForecastHour);
        Calendar timeTillUpdateCal = getNxtForecastCalender(futureForecastHour);
        long timeTillUpdateLong = timeTillUpdateCal.getTimeInMillis();


        Log.i("WSX", "updateWeatherInfo: timeTillUpdate "+timeTillUpdate);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, 10000, interval, alarmIntent); //update every three hours

    }



    private void displayWeatherInfo(){

        String current = "0", min = "0", max = "0";
        int currentForecastHour = calculateForecastHours()[1];
        String currentForecastDate = getCurrentForecastDate(currentForecastHour);
        Map<String, Object> currentDayForecast =  storageDBServer.getDayForecast(currentForecastDate);

        if(!currentDayForecast.isEmpty()){
            current = currentDayForecast.get(CURRENT_TEMPERATURE).toString();
            min = currentDayForecast.get(TEMP_MIN).toString();
            max = currentDayForecast.get(TEMP_MAX).toString();
        }

        Log.i("WSX", "updateWeatherInfo: currentForecast hour "+currentForecastHour);
        Log.i("WSX", "updateWeatherInfo: currentForecastDate "+currentForecastDate);
        Log.i("WSX", "displayWeatherInfo: "+current+" "+min+" "+max);
        char degrees = (char) 0x00B0; // degree symbol
        ttvCurrent.setText(current+degrees);
        ttvMax.setText(max+degrees);
        ttvMin.setText(min+degrees);
    }

    private void displayWeekDays(){
        ArrayList<String> weekDays = storageDBServer.getAllWeekDays();
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


    public static boolean isNetworkAvailable() {

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onTaskCompleted() {

        displayWeatherInfo();
        displayWeekDays();
        updateWeatherInfo();
    }
}
