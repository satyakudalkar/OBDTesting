package com.example.obdtesting;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.maps.model.MarkerOptions;

public class TripActivity extends AppCompatActivity {

    double destlLat,destLong,srcLat,srcLong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Intent intent = getIntent();
        destlLat = intent.getDoubleExtra("DestLat",0);
        destLong= intent.getDoubleExtra("DestLong",0);
        srcLat = intent.getDoubleExtra("SrcLat",0);
        srcLong = intent.getDoubleExtra("SrcLong",0);
        MarkerOptions markerOptions = new MarkerOptions();
    }
}
