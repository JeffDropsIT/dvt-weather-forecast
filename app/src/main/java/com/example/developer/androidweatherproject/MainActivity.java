package com.example.developer.androidweatherproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.developer.androidweatherproject.localCache.StorageDB;
import com.example.developer.androidweatherproject.services.UpdateWeatherService;
import com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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
    private static final int REQUEST_ACCESS_LOCATION_PERMISSIONS = 111 ;
    public static final String MAIN = "main";
    public static final String CLEAR = "Clear";
    public static final String CLOUDS = "Clouds";
    public static final String RAIN = "Rain";
    public static StorageDB storageDBServer;
    private static ConnectivityManager connectivityManager;
    public static SharedPreferences preferences;
    private ProgressBar progressBar;
    private SQLiteDatabase weatherDB;
    private final char DEGREES_SYMBOL = (char) 0x00B0; // degree symbol

    TextView ttvCurrent, ttvMin, ttvMax;
    TextView ttvDay1, ttvDay2, ttvDay3, ttvDay4, ttvDay5;
    TextView ttvTemp1, ttvTemp2, ttvTemp3, ttvTemp4, ttvTemp5, ttvCurrentCoverHeader, ttvCurrentTempHeader;
    ImageView ttvIcon1, ttvIcon2, ttvIcon3, ttvIcon4, ttvIcon5, ImgCoverImage;
    ArrayList<TextView> daysTextViewList;
    ArrayList<ImageView> iconsTextViewList;
    ArrayList<TextView> tempsTextViewList;

    private FusedLocationProviderClient mFusedLocationClient;
    private LinearLayout linTemps, linHeadings, linWeekForecast;
    private Map< String, ArrayList<String>> getWeatherForecastList;
    public static StorageDB getStorageDBServer() {
        return storageDBServer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermission();
        connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //shared prefs value to check whether data has been cached
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!isNetworkAvailable() && !getBoolean(IS_CACHED)) {
            startErrorActivity();
        }


        //create local cache if not exist
        weatherDB = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        storageDBServer = new StorageDB(weatherDB);


        daysTextViewList = new ArrayList<>();
        iconsTextViewList = new ArrayList<>();
        tempsTextViewList = new ArrayList<>();

        linTemps = findViewById(R.id.lin_header_temperature);
        linHeadings = findViewById(R.id.lin_header_headings);
        linWeekForecast = findViewById(R.id.lin_week_forecast);
        ttvCurrent = findViewById(R.id.ttv_current);
        ttvMin = findViewById(R.id.ttv_min);
        ttvMax = findViewById(R.id.ttv_max);

        ttvCurrentTempHeader = findViewById(R.id.ttv_current_temp_header);
        ttvCurrentCoverHeader = findViewById(R.id.ttv_current_temp_cover);

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


        ImgCoverImage = findViewById(R.id.img_forecast);

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

        setToolBar();


    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

    }

    public ProgressBar getProgressBar() {
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
    public static String getString(String key) {
        return preferences.getString(key, "0");
    }
    public static void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }


    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_ACCESS_LOCATION_PERMISSIONS);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_LOCATION_PERMISSIONS);

            }
        }
    }

    private void getLastKnownLocation(final OnlocationListener onlocationListener) {
        checkPermission();
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            Log.i("WSX", "onSuccess: lat "+location.getLatitude()+" lon "+location.getLongitude());
                            putString("lat", Double.toString(location.getLatitude()));
                            putString("lon", Double.toString(location.getLongitude()));
                            onlocationListener.onLocationComplete();

                        }
                    }
                });
    }


    private void setWeekForecastIcons(){
        ArrayList<String> weekDaysIcons = getWeatherForecastList.get("main");
        Log.i("WSX", "setWeekForecastIcons  icons "+weekDaysIcons);
        if(!weekDaysIcons.isEmpty() && !iconsTextViewList.isEmpty()){
            if(weekDaysIcons.size() > 5){
                weekDaysIcons.remove(0);
            }
            if(weekDaysIcons.size() == iconsTextViewList.size()){

                for( int i = 0; i < iconsTextViewList.size(); i++){


                    if(weekDaysIcons.get(i).toString().contains(CLEAR)){
                        iconsTextViewList.get(i).setImageResource(R.drawable.clear2x);
                        Log.i("WSX", "setWeekForecastIcons  cond "+weekDaysIcons.get(i));
                    }else if(weekDaysIcons.get(i).toString().contains(CLOUDS)){
                        iconsTextViewList.get(i).setImageResource(R.drawable.partlysunny2x);
                        Log.i("WSX", "setWeekForecastIcons  cond "+weekDaysIcons.get(i));
                    }else if(weekDaysIcons.get(i).toString().contains(RAIN)){
                        iconsTextViewList.get(i).setImageResource(R.drawable.rain2x);
                        Log.i("WSX", "setWeekForecastIcons  cond "+weekDaysIcons.get(i));
                    }

                    Log.i("WSX", "setWeekForecastIcons  day "+i);
                }
            }else {
                Log.i("WSX", "setWeekForecastIcons: something went wrong size does not match weekDaysIcons.size()"+weekDaysIcons.size()+"|"+iconsTextViewList.size());
            }
        }else {
            Log.i("WSX", "setWeekForecastIcons: something went wrong list empty");
        }

    }

    private void setWeekForecastTemps(){

        ArrayList<String> weekDaysTemp = getWeatherForecastList.get("temp");
        Log.i("WSX", "setWeekForecastTemps  temps "+weekDaysTemp);
        if(!weekDaysTemp.isEmpty() && !tempsTextViewList.isEmpty()){
            if(weekDaysTemp.size() > 5){
                weekDaysTemp.remove(0);
            }
            if(weekDaysTemp.size() == tempsTextViewList.size()){


                for(int i = 0; i < tempsTextViewList.size(); i++){


                    tempsTextViewList.get(i).setText(weekDaysTemp.get(i)+DEGREES_SYMBOL);
                    Log.i("WSX", "setWeekForecastTemps  day "+i+" "+weekDaysTemp.get(i));
                }
            }else {
                Log.i("WSX", "setWeekForecastTemps: something went wrong size does not match ");
                Log.i("WSX", "setWeekForecastIcons: something went wrong size does not match weekDaysIcons.size()"+weekDaysTemp.size()+"|"+iconsTextViewList.size());
            }
        }else {
            Log.i("WSX", "setWeekForecastTemps: something went wrong list empty");
        }

    }



    private void restartActivity(){
        if(Build.VERSION.SDK_INT > 11){
            recreate();

        }else {

            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0,0);
        }
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

    private void setAppWeatherTheme(String main){
        if(main.contains(CLEAR)){
            ImgCoverImage.setImageResource(R.drawable.forest_sunny);
            linTemps.setBackgroundResource(R.color.colorSunny);
            linHeadings.setBackgroundResource(R.color.colorSunny);
            ttvCurrentCoverHeader.setText("SUNNY");
            linWeekForecast.setBackgroundResource(R.color.colorSunny);

            setStatusBar(R.color.colorSunnyStatusBar);
        }else if(main.contains(CLOUDS)){
            ImgCoverImage.setImageResource(R.drawable.forest_cloudy);
            linTemps.setBackgroundResource(R.color.colorCloudy);
            ttvCurrentCoverHeader.setText("CLOUDY");
            linHeadings.setBackgroundResource(R.color.colorCloudy);
            linWeekForecast.setBackgroundResource(R.color.colorCloudy);
            setStatusBar(R.color.colorCloudyStatusBar);
        }else if(main.contains(RAIN)){
            ImgCoverImage.setImageResource(R.drawable.forest_rainy);
            linTemps.setBackgroundResource(R.color.colorRainy);
            linHeadings.setBackgroundResource(R.color.colorRainy);
            ttvCurrentCoverHeader.setText("RAINY");
            setStatusBar(R.color.colorRainyStatusBar);
            linWeekForecast.setBackgroundResource(R.color.colorRainy);
        }
    }


    private void setLayoutViews(){
        updateWeatherInfo();
        displayWeekDays();
        displayWeatherInfo();
        setWeekForecastIcons();
        setWeekForecastTemps();
    }
    private void updateUI(){

        getWeatherForecastList = storageDBServer.getWeatherForecast();
        setLayoutViews();


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
        int futureForecastHour = calculateForecastHours()[0];
        Map<String, Object> currentDayForecast;
        String currentForecastDate = getCurrentForecastDate(currentForecastHour);
        String futureForecastDate = getNxtForecastDate(futureForecastHour);
        currentDayForecast =  storageDBServer.getDayForecast(currentForecastDate, futureForecastDate);


        if(currentDayForecast != null){
            String main = currentDayForecast.get(MAIN).toString();
            current = currentDayForecast.get(CURRENT_TEMPERATURE).toString();
            min = currentDayForecast.get(TEMP_MIN).toString();
            max = currentDayForecast.get(TEMP_MAX).toString();

            setAppWeatherTheme(main);

        }else {
            startErrorActivity();
        }

        Log.i("WSX", "updateWeatherInfo: currentForecast hour "+currentForecastHour);
        Log.i("WSX", "updateWeatherInfo: currentForecastDate "+currentForecastDate);
        Log.i("WSX", "displayWeatherInfo: "+current+" "+min+" "+max);
        ttvCurrentTempHeader.setText(current+DEGREES_SYMBOL);
        ttvCurrent.setText(current+DEGREES_SYMBOL);
        ttvMax.setText(max+DEGREES_SYMBOL);
        ttvMin.setText(min+DEGREES_SYMBOL);
    }

    private void setStatusBar(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color)); // .setStatusBarColor(R.color.colorSunny);
        }
    }

    private void displayWeekDays(){
        ArrayList<String> weekDays = getWeatherForecastList.get("days");
        if(!weekDays.isEmpty() && !daysTextViewList.isEmpty()){
            //weekDays.remove(0);
            if(weekDays.size() > 5){
                weekDays.remove(0);
            }
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
        Log.i("WSX", "isNetworkAvailable: activeNetworkInfo != null "+(activeNetworkInfo != null));
        return activeNetworkInfo != null;
    }

    @Override
    public void onTaskCompleted() {

        updateUI();

    }

    private void startErrorActivity(){
        Intent errorIntent = new Intent(this, SomethingWentWrongActivity.class);
        startActivity(errorIntent);
        finish();
    }



    private void fetchData(){
        if(isNetworkAvailable()){
            if(!MainActivity.getString("lat").equals("0") && !MainActivity.getString("lon" ).equals("0")){
                Log.i("WSX", "fetchData one: lat "+getString("lat")+" lon "+getString("lon"));
                new HttpRequestTask(this).execute(getString("lat"),getString("lon"));
            }else {
                Log.i("WSX", "fetchData zero: lat "+getString("lat")+" lon "+getString("lon"));
                getLastKnownLocation(new OnlocationListener() {
                    @Override
                    public void onLocationComplete() {
                        new HttpRequestTask(MainActivity.this).execute(getString("lat"),getString("lon"));
                        Log.i("WSX", "fetchData done: lat "+getString("lat")+" lon "+getString("lon"));
                    }
                });
            }
            Log.i("WSX", "fetchData: lat "+getString("lat")+" lon "+getString("lon"));
            Log.i("WSX", "fetchData: internet connection ");
        }else{
            Log.i("WSX", "fetchData: no internet connection ");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        fetchData();
        if(getBoolean(IS_CACHED)){
            updateUI();
        }
    }

    @Override
    public void onTaskFailed() {
        startErrorActivity();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i("WSX", "onRequestPermissionsResult: Granted");
                    fetchData();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    public interface OnlocationListener{
         void onLocationComplete();
    }
}
