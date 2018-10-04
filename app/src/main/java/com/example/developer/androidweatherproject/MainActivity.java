package com.example.developer.androidweatherproject;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.developer.androidweatherproject.weatherPackages.apiCall.HttpRequestTask;


public class MainActivity extends AppCompatActivity {

    TextView ttvCurrent, ttvMin, ttvMax;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ttvCurrent = findViewById(R.id.ttv_current);
        ttvMin = findViewById(R.id.ttv_min);
        ttvMax = findViewById(R.id.ttv_max);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new HttpRequestTask(ttvCurrent, ttvMin, ttvMax).execute("21.21","22.22");
    }






}
