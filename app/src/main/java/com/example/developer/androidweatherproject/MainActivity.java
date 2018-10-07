package com.example.developer.androidweatherproject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    public static final String IS_CACHED = "isDataCached";
    private SQLiteDatabase weatherDB;
    public static StorageDB storageDBServer;
    TextView ttvCurrent, ttvMin, ttvMax;
    TextView ttvDay1, ttvDay2, ttvDay3, ttvDay4, ttvDay5;
    TextView ttvTemp1, ttvTemp2, ttvTemp3, ttvTemp4, ttvTemp5;
    ImageView ttvIcon1, ttvIcon2, ttvIcon3, ttvIcon4, ttvIcon5;
    ArrayList<TextView> daysTextViewList;
    ArrayList<ImageView> iconsTextViewList;
    ArrayList<TextView> tempsTextViewList;
    private final char DEGREES_SYMBOL = (char) 0x00B0; // degree symbol
    private static ConnectivityManager connectivityManager;
    public static SharedPreferences preferences;
    private ProgressBar progressBar;


    public static StorageDB getStorageDBServer(){
        return storageDBServer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //shared prefs value to check whether data has been cached
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(!isNetworkAvailable() && !getBoolean(IS_CACHED)){
            setContentView(R.layout.error_layout);
        }else {
            setContentView(R.layout.activity_main);
        }


        //create local cache if not exist
        weatherDB = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        storageDBServer = new StorageDB(weatherDB);





        //if internet connection update cache else do nothing for now
        if(isNetworkAvailable()){
            new HttpRequestTask(this).execute("-29.844776","31.014339");
            Log.i("WSX", "onCreate: internet connection ");
        }else{
            Log.i("WSX", "onCreate: no internet connection ");
        }





        daysTextViewList = new ArrayList<>();
        iconsTextViewList = new ArrayList<>();
        tempsTextViewList = new ArrayList<>();
        ttvCurrent = findViewById(R.id.ttv_current);
        ttvMin = findViewById(R.id.ttv_min);
        ttvMax = findViewById(R.id.ttv_max);

        ttvDay1 = findViewById(R.id.ttv_day1);
        ttvDay2 = findViewById(R.id.ttv_day2);
        ttvDay3 = findViewById(R.id.ttv_day3);
        ttvDay4 = findViewById(R.id.ttv_day4);
        ttvDay5 = findViewById(R.id.ttv_day5);


        ttvIcon1 = findViewById(R.id.ttv_icon1);
        ttvIcon2 = findViewById(R.id.ttv_icon2);
        ttvIcon3 = findViewById(R.id.ttv_icon3);
        ttvIcon4 = findViewById(R.id.ttv_icon4);
        ttvIcon5 = findViewById(R.id.ttv_icon5);

        ttvTemp1 = findViewById(R.id.ttv_temp1);
        ttvTemp2 = findViewById(R.id.ttv_temp2);
        ttvTemp3 = findViewById(R.id.ttv_temp3);
        ttvTemp4 = findViewById(R.id.ttv_temp4);
        ttvTemp5 = findViewById(R.id.ttv_temp5);


        daysTextViewList.add(ttvDay1);
        daysTextViewList.add(ttvDay2);
        daysTextViewList.add(ttvDay3);
        daysTextViewList.add(ttvDay4);
        daysTextViewList.add(ttvDay5);

        iconsTextViewList.add(ttvIcon1);
        iconsTextViewList.add(ttvIcon2);
        iconsTextViewList.add(ttvIcon3);
        iconsTextViewList.add(ttvIcon4);
        iconsTextViewList.add(ttvIcon5);


        tempsTextViewList.add(ttvTemp1);
        tempsTextViewList.add(ttvTemp2);
        tempsTextViewList.add(ttvTemp3);
        tempsTextViewList.add(ttvTemp4);
        tempsTextViewList.add(ttvTemp5);



    }

    public ProgressBar getProgressBar(){
        View progressView = getLayoutInflater().inflate(
                R.layout.progress_bar_layout, null);
        // Find the progressbar within footer
        progressBar =
                progressView.findViewById(R.id.indeterminateBar);

        return progressBar;

    }
    private boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }
    public static void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    private void setWeekForecastIcons(){
        ArrayList<String> weekDaysIcons = storageDBServer.getWeatherForecast().get("main");
        Log.i("WSX", "setWeekForecastIcons  icons "+weekDaysIcons);
        if(!weekDaysIcons.isEmpty() && !iconsTextViewList.isEmpty()){
            weekDaysIcons.remove(0);
            if(weekDaysIcons.size() == iconsTextViewList.size()){
                for(int i = 0; i < iconsTextViewList.size(); i++){


                    if(weekDaysIcons.get(i).toString().contains("Clear")){
                        iconsTextViewList.get(i).setImageResource(R.drawable.clear);
                        Log.i("WSX", "setWeekForecastIcons  cond "+weekDaysIcons.get(i));
                    }else if(weekDaysIcons.get(i).toString().contains("Clouds")){
                        iconsTextViewList.get(i).setImageResource(R.drawable.partlysunny);
                        Log.i("WSX", "setWeekForecastIcons  cond "+weekDaysIcons.get(i));
                    }else if(weekDaysIcons.get(i).toString().contains("Rain")){
                        iconsTextViewList.get(i).setImageResource(R.drawable.rain);
                        Log.i("WSX", "setWeekForecastIcons  cond "+weekDaysIcons.get(i));
                    }

                    Log.i("WSX", "setWeekForecastIcons  day "+i);
                }
            }else {
                Log.i("WSX", "setWeekForecastIcons: something went wrong size does not match");
            }
        }else {
            Log.i("WSX", "setWeekForecastIcons: something went wrong list empty");
        }

    }

    private void setWeekForecastTemps(){

        ArrayList<String> weekDaysTemp = storageDBServer.getWeatherForecast().get("temp");
        Log.i("WSX", "setWeekForecastTemps  temps "+weekDaysTemp);
        if(!weekDaysTemp.isEmpty() && !tempsTextViewList.isEmpty()){
            weekDaysTemp.remove(0);
            if(weekDaysTemp.size() == tempsTextViewList.size()){
                for(int i = 0; i < tempsTextViewList.size(); i++){


                    tempsTextViewList.get(i).setText(weekDaysTemp.get(i)+DEGREES_SYMBOL);
                    Log.i("WSX", "setWeekForecastTemps  day "+i+" "+weekDaysTemp.get(i));
                }
            }else {
                Log.i("WSX", "setWeekForecastTemps: something went wrong size does not match");
            }
        }else {
            Log.i("WSX", "setWeekForecastTemps: something went wrong list empty");
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNetworkAvailable()){
            new HttpRequestTask(this).execute("-29.844776","31.014339");
            Log.i("WSX", "onResume: internet connection ");
        }else{
            Log.i("WSX", "onResume: no internet connection ");
        }
        if(getBoolean(IS_CACHED)){
            updateUI();
        }
    }


    private void updateUI(){
        updateWeatherInfo();
        displayWeekDays();
        displayWeatherInfo();
        setWeekForecastIcons();
        setWeekForecastTemps();
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
        Map<String, Object> currentDayForecast;
        String currentForecastDate = getCurrentForecastDate(currentForecastHour);
        currentDayForecast =  storageDBServer.getDayForecast(currentForecastDate);

        if(!currentDayForecast.isEmpty()){
            current = currentDayForecast.get(CURRENT_TEMPERATURE).toString();
            min = currentDayForecast.get(TEMP_MIN).toString();
            max = currentDayForecast.get(TEMP_MAX).toString();
        }

        Log.i("WSX", "updateWeatherInfo: currentForecast hour "+currentForecastHour);
        Log.i("WSX", "updateWeatherInfo: currentForecastDate "+currentForecastDate);
        Log.i("WSX", "displayWeatherInfo: "+current+" "+min+" "+max);

        ttvCurrent.setText(current+DEGREES_SYMBOL);
        ttvMax.setText(max+DEGREES_SYMBOL);
        ttvMin.setText(min+DEGREES_SYMBOL);
    }

    private void displayWeekDays(){
        ArrayList<String> weekDays = storageDBServer.getWeatherForecast().get("days");
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

        updateUI();

    }
}
