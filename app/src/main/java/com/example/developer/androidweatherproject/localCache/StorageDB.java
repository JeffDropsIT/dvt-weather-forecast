package com.example.developer.androidweatherproject.localCache;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class StorageDB {


    public static final String WEATHER_FORECAST = "WeatherForecast";
    private static SQLiteDatabase mDatabase;
    private static SharedPreferences preferences;
    public StorageDB(SQLiteDatabase db, Context appContext){
        this.mDatabase = db;
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }
    private static void createWeatherDataTable(){

        String sqlSite = "CREATE TABLE IF NOT EXISTS WeatherForecast (\n" +
                "\tdt int PRIMARY KEY,\n" +
                "\tid int , \n" +
                "\tmain  varchar(200),\n" +
                "\ticon  varchar(200), \n" +
                "\tdtTxt varchar(200), \n" +
                "\ttempMax varchar(200),  \n" +
                "\ttempMin varchar(200), \n" +
                "\ttemperature varchar(200) \n" +
                ");";
        mDatabase.execSQL(sqlSite);
    }
    public static void checkForNullKey(String key){
        if (key == null){
            throw new NullPointerException();
        }
    }
    public ArrayList<String> getListString(String key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }
    public static void putListString(String key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
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


}
