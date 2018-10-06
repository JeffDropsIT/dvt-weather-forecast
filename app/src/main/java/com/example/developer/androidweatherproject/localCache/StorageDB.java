package com.example.developer.androidweatherproject.localCache;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.developer.androidweatherproject.weatherPackages.WeatherObject.YYYY_MM_DD_HH_MM_SS;

public class StorageDB {


    public static final String WEATHER_FORECAST = "WeatherForecast";
    private static SQLiteDatabase mDatabase;
    public StorageDB(SQLiteDatabase db){
        this.mDatabase = db;
    }
    private static void createWeatherDataTable(){

        String sqlSite = "CREATE TABLE IF NOT EXISTS WeatherForecast (\n" +
                "\tdt int ,\n" +
                "\tid int , \n" +
                "\tmain  varchar(200),\n" +
                "\ticon  varchar(200), \n" +
                "\tdtTxt varchar(200) PRIMARY KEY, \n" +
                "\ttempMax varchar(200),  \n" +
                "\ttempMin varchar(200), \n" +
                "\ttemperature varchar(200) \n" +
                ");";
        mDatabase.execSQL(sqlSite);
    }



    public static void addWeatherEvents(ContentValues values){
        createWeatherDataTable();
        mDatabase.insertWithOnConflict(WEATHER_FORECAST, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static ContentValues toContentValues(Map<String, Object> dataMap){
        ContentValues values = new ContentValues();
        Object[] keys = dataMap.keySet().toArray();
        for(int i = 0 ; i < keys.length ; i++){
            values.put(keys[i].toString(), dataMap.get(keys[i].toString()).toString());
        }
        return values;
    }

    public Cursor getCacheWeatherData(){
        createWeatherDataTable();
        String select = "SELECT * FROM  " + WEATHER_FORECAST;
        return  mDatabase.rawQuery(select, null);
    }
    public Cursor getMatch(String field, String value){
        createWeatherDataTable();
        String select = "SELECT * FROM " + WEATHER_FORECAST + " WHERE " +field + " = ?";
        return  mDatabase.rawQuery(select, new String[]{value});
    }



    private String getDayOfWeek(String dtTxt){
        String dayOfWeek, fullDatePattern = "yyyy-MM-dd HH:mm:ss", dayPattern = "EEEE";
        Date fullDateFormat = null;
        SimpleDateFormat dayFormat = new SimpleDateFormat(dayPattern);
        try {
            fullDateFormat =  new SimpleDateFormat(fullDatePattern).parse(dtTxt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(fullDateFormat != null){
            dayOfWeek = dayFormat.format(fullDateFormat);
            Log.i("DDD", "getDayOfWeek: "+dayOfWeek);
            return dayOfWeek;
        }else {
            return "null";
        }

    }


    private ArrayList<String> sortDays(ArrayList<String> datestring){
        Log.i("WSX", "sort  unsortedDays: "+datestring);
        Collections.sort(datestring, new Comparator<String>() {
            DateFormat f = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        Log.i("WSX", "sort sortDays: "+datestring);
        return datestring;
    }
    public ArrayList getAllWeekDays(){
        ArrayList<String> daysOfWeek = new ArrayList<>();
        ArrayList<String> dtTxtList = new ArrayList<>();



        Set dtTxtSet = getLocalForecastData().keySet();
        dtTxtList.addAll(dtTxtSet);

        dtTxtList = sortDays(dtTxtList); //sort and reassign


        Log.i("WSX", "WEEKLIST: keys "+dtTxtList);

        for(int i = 0;  i < dtTxtList.size(); i++){
            String dtTxt = dtTxtList.get(i);
            String tmpDay =  getDayOfWeek(dtTxt);
            if(!daysOfWeek.contains(tmpDay)){
                daysOfWeek.add(tmpDay);
                Log.i("WSX", "WEEKLIST: "+tmpDay);
            }

        }



        return daysOfWeek;
    }


    public Map<String, Object> getDayForecast(String currentForecastTime){
        Map<String, Map<String, Object> > forecastData = getLocalForecastData();
        Log.i("WSX", "confusion: currentForecastTime "+currentForecastTime);
        Log.i("WSX", "confusion: "+ forecastData.get(currentForecastTime));
        return forecastData.get(currentForecastTime);

    }
    public Map<String, Map<String, Object>> getLocalForecastData(){


        String[] tableCols = getColumnNames();
        Cursor forecastCursor;
        forecastCursor = getCacheWeatherData();
        Log.i("WSX", "count: "+forecastCursor.getCount());
        Map<String, Map<String, Object>> tableData = new HashMap<>();
        if(forecastCursor.moveToNext()){
            do{
                Map<String, Object> data = new HashMap<>();
                for(int i = 0 ; i < tableCols.length ; i++){
                    String colContent = forecastCursor.getString(forecastCursor.getColumnIndex(tableCols[i]));
                    Log.i("WSX", "getLocalForecastData: colContent "+colContent);
                    switch (tableCols[i]){
                        case "dt":
                            data.put("dt", colContent);
                            break;
                        case "dtTxt":
                            data.put("dtTxt", colContent);
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

                tableData.put(data.get("dtTxt").toString(), data);
                Log.i("WSX", "getLocalForecastData: "+data.keySet() +"|"+data.size() +" | "+data+ "| "+data.get("dtTxt").toString());
            }while (forecastCursor.moveToNext());
        }

        Log.i("WSX", "getLocalForecastData: table data "+"|"+tableData);
        return tableData;
    }



    public String[] getColumnNames() {
        String[] colNames = {"dt", "dtTxt", "id", "temperature", "tempMin", "tempMax", "icon", "main"};
        return colNames;
    }
}
