package com.example.obdtesting;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;
@SuppressWarnings("serial")
public class MainActivity extends AppCompatActivity implements Serializable {
    private Button SubButton;
    public static final String SETTINGS_PACKAGE = "com.android.settings";
    public static final String HOTSPOT_SETTINGS_CLASS = "com.android.settings.Settings$WifiApSettingsActivity";
    private Button setHotspot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SubButton = (Button) findViewById(R.id.SubButton);
        SubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity();

            }
        });
    }
    public void openActivity()
    {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
}
