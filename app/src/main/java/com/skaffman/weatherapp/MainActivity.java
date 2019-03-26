package com.skaffman.weatherapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 0;
    public static final String ACTION_GOT_WEATHER = "com.skaffman.weatherApp.ACTION_GOT_WEATHER";
    public static final String EXTRA_CURRENT_WEATHER = "current_weather";
    private static final String TAG = "Weather";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestGeoPermission();

        SharedPreferences sharedPreferences = getSharedPreferences("weather", MODE_PRIVATE);
        long lastUpdate = sharedPreferences.getLong("last_update", 0);

        Log.i(TAG, "Last update: " + lastUpdate);
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(ACTION_GOT_WEATHER);
        registerReceiver(weatherReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(weatherReceiver);
        super.onStop();
    }

    private void checkAndRequestGeoPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            startWeatherService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startWeatherService();
            } else {
                checkAndRequestGeoPermission();
            }
        }
    }

    private void startWeatherService() {
        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
    }

    private class WeatherReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_GOT_WEATHER.equals(intent.getAction())) {
                CurrentWeather currentWeather = intent.getParcelableExtra(EXTRA_CURRENT_WEATHER);
                Log.i(TAG, "Got weather: " + currentWeather);
            }
        }
    }

    private final WeatherReceiver weatherReceiver = new WeatherReceiver();
}
