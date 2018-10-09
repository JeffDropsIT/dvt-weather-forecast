package com.example.developer.androidweatherproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SomethingWentWrongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_layout);

        //onNetworkFound(); dumb F"n mistake
    }


    private void onNetworkFound(){
        if(MainActivity.isNetworkAvailable()){
            startMainActivity();
        }
    }
    private void startMainActivity(){
        Intent errorIntent = new Intent(this, MainActivity.class);
        startActivity(errorIntent);
        finish();
    }
    @Override
    protected void onResume() {

        //onNetworkFound();
        super.onResume();
    }
}
