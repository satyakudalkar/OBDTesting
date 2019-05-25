package com.example.obdtesting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.anastr.speedviewlib.DeluxeSpeedView;
import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.github.anastr.speedviewlib.ProgressiveGauge;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GaugeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gauge);
        MqttConnection mqttConnection=new MqttConnection();
        mqttConnection.connect(this.getApplicationContext(),this);
    }
    public void setView(String strdata){
        try
        {
            JSONObject jsonObject = new JSONObject(strdata);
            final int rpm = jsonObject.getInt("RPM");
            final int speed = jsonObject.getInt("SPEED");
            //final int eload = jsonObject.getInt("TPOS");
            final int etemp = jsonObject.getInt("CTEMP");
            final int maf = jsonObject.getInt("MAF");
            final int tpos = jsonObject.getInt("TPOS");
            Log.i("obdjson","RPM Value from jason is "+rpm);
            Log.i("obdjson","Speed Value from jason is "+speed);
            Log.i("obdjson","Throttle Position Value from jason is "+tpos);
            Log.i("obdjson","Engine Temperature Value from jason is "+etemp);
            Log.i("obdjson","Mass Air Flow Value from jason is "+maf);
            final PointerSpeedometer rpmSpeedometer = (PointerSpeedometer) findViewById(R.id.rpmSpeedometer);
            final PointerSpeedometer speedometer= (PointerSpeedometer) findViewById(R.id.speedometer);
            final PointerSpeedometer engineTemp=(PointerSpeedometer) findViewById(R.id.engineTemp);
            final TextView milage = (TextView) findViewById(R.id.milage);
            rpmSpeedometer.setSpeedometerColor(Color.parseColor("#1842D1"));
            speedometer.setSpeedometerColor(Color.parseColor("#1842D1"));
            float kmpl = (float) (speed/(maf*0.24489));
            final String kmplstr = String.format("%.2f",kmpl);
            Typeface tf =Typeface.createFromAsset(getAssets(),"fonts/7Segment.ttf");
            milage.setTypeface(tf);
            milage.setTextSize(15);
            runOnUiThread(new Runnable(){
                public void run() {
                    rpmSpeedometer.speedTo(rpm);
                    speedometer.speedTo(speed);
                    engineTemp.speedTo(etemp);
                    milage.setText(kmplstr);
                }
            });

        } catch (Exception e) {
            Log.i("myapp",e.getStackTrace().toString());
        }
    }
}
