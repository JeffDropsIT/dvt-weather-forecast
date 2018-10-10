package com.example.developer.androidweatherproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SomethingWentWrongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_layout);

        setStatusBar(R.color.colorDarkGrey);
        refreshLayout(); //swipe down lo refresh layout

    }
    private void setStatusBar(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    private void refreshLayout(){
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.srl_layout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onNetworkFound();
                pullToRefresh.setRefreshing(false);
            }
        });

    }
    private void onNetworkFound(){
        if(MainActivity.isNetworkAvailable() ){
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
