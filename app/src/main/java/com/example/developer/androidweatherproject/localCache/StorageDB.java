package com.example.developer.androidweatherproject.localCache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.developer.androidweatherproject.weatherPackages.WeatherObject.YYYY_MM_DD_HH_MM_SS;

public class StorageDB {

    private final String MIDDAY = "12:00:00";
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
            return dayOfWeek;
        }else {
            return "null";
        }

    }

    public static void clearCache(){
        createWeatherDataTable();
        mDatabase.execSQL("DELETE FROM "+ WEATHER_FORECAST);


    }

    private ArrayList<String> sortDays(ArrayList<String> datestring){
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

        return datestring;
    }

    //returns the week forecast data
    public  Map< String, ArrayList<String>> getWeatherForecast(){
        ArrayList<String> daysOfWeek = new ArrayList<>();
        ArrayList<String> dtTxtList = new ArrayList<>();
        ArrayList<String> mainList = new ArrayList<>();
        ArrayList<String> tempList = new ArrayList<>();
        Map< String, ArrayList<String>> weatherList = new HashMap<>();

        Set dtTxtSet = getLocalForecastData().keySet();
        dtTxtList.addAll(dtTxtSet);

        dtTxtList = sortDays(dtTxtList); //sort and reassign


        for(int i = 0;  i < dtTxtList.size(); i++){
            String dtTxt = dtTxtList.get(i);

            String day =  getDayOfWeek(dtTxt); //return the day format of each date

            //only add day to list if it has not been added
            if(!daysOfWeek.contains(day)){
                String dtMidday = dtTxt.split(" ")[0]+" "+MIDDAY; //midday date for each day forecast


                //if the day has a midday return the midday forecast
                if(dtTxtList.contains(dtMidday)){
                    String main = getLocalForecastData().get(dtMidday).get("main").toString();  //main i.e Cloudy, Rainy or Sunny
                    String temp = getLocalForecastData().get(dtMidday).get("temperature").toString(); //current temperature for each midday on week
                    daysOfWeek.add(day);
                    tempList.add(temp);
                    mainList.add(main);
                }else {

                    //if no midday take whatever date and get its forecast
                    String main = getLocalForecastData().get(dtTxt).get("main").toString();
                    String temp = getLocalForecastData().get(dtTxt).get("temperature").toString();
                    daysOfWeek.add(day);
                    tempList.add(temp);
                    mainList.add(main);
                }

            }

        }

        weatherList.put("days",daysOfWeek); //list of the five days to forecast for i.e see api
        weatherList.put("main",mainList); //main i.e Cloudy, Rainy or Sunny for each day forecast
        weatherList.put("temp",tempList); //temperature list for the five days  i.e see api
        return  weatherList;
    }




    public Map<String, Object> getDayForecast(String currentForecastDate, String futureForecastDate){
        Map<String, Map<String, Object> > forecastData = getLocalForecastData(); //get data from the cache
        try {

            Map<String, Object> currentForecastData = forecastData.get(currentForecastDate);

            //if the forecast for the current hour is not there get one for the next hour
            if(currentForecastData == null){
                currentForecastData =  forecastData.get(futureForecastDate);
            }
            //error found api inconsistent current times no there

            return currentForecastData;

        }catch (Exception e){
            Log.i("WSX", "confusion: error "+ e);
        }


        return null;


    }
    public Map<String, Map<String, Object>> getLocalForecastData(){

        //query local cache database

        String[] tableCols = getColumnNames(); //get all columns in the database
        Cursor forecastCursor;
        forecastCursor = getCacheWeatherData(); //return all data on table
        Map<String, Map<String, Object>> tableData = new HashMap<>();

        //put the data in map
        if(forecastCursor.moveToNext()){
            do{
                Map<String, Object> data = new HashMap<>();
                for(int i = 0 ; i < tableCols.length ; i++){
                    String colContent = forecastCursor.getString(forecastCursor.getColumnIndex(tableCols[i]));

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
                //key is the date of the forecast since unique
                tableData.put(data.get("dtTxt").toString(), data);

            }while (forecastCursor.moveToNext());
        }

        return tableData;
    }



    public String[] getColumnNames() {
        String[] colNames = {"dt", "dtTxt", "id", "temperature", "tempMin", "tempMax", "icon", "main"};
        return colNames;
    }

}
