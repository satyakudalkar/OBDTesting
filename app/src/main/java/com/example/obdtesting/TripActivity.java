package com.example.obdtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TripActivity extends AppCompatActivity implements OnMapReadyCallback {

    double srcLat, srcLong;
    private int utteranceID;
    private String behavior;
    private ObdData []obdObjects= new ObdData[10];
    private int objCount,agrCount,normCount,bNorCount,rashCount;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location l;
    private LatLng pos;
    private TextToSpeech ttsobj;
    private int rpm,speed,maf,tpos,eload,etemp;
    private PointerSpeedometer rpmSpeedometer,speedometer,engineTemp;
    private TextView milage,user;
    private MqttConnection mqttConnection;
    private OkHttpClient httpclient;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Intent intent = getIntent();
        srcLat = intent.getDoubleExtra("SrcLat", 0);
        srcLong = intent.getDoubleExtra("SrcLong", 0);
        l=new Location(LocationManager.GPS_PROVIDER);
        l.setLongitude(srcLong);
        l.setLatitude(srcLat);
        MarkerOptions markerOptions = new MarkerOptions();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 1000, (float) 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                l=(Location) location;
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        });

        //Start of the Gauges
        objCount=0;
        rpmSpeedometer = (PointerSpeedometer) findViewById(R.id.rpmSpeedometer);
        speedometer= (PointerSpeedometer) findViewById(R.id.speedometer);
        engineTemp=(PointerSpeedometer) findViewById(R.id.engineTemp);
        milage = (TextView) findViewById(R.id.milage);
        user = (TextView) findViewById(R.id.user_name_txt);


        mqttConnection=new MqttConnection();
        mqttConnection.connect(this.getApplicationContext(),this);
        utteranceID=0;



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        pos = new LatLng(l.getLatitude(), l.getLongitude());
        // Add a marker in Sydney and move the camera
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(pos.latitude, pos.longitude)).zoom((float)17.0).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.moveCamera(cameraUpdate);
    }

    @SuppressLint("SetTextI18n")
    public void setView(String strdata){
        try
        {
            JSONObject jsonObject = new JSONObject(strdata);
            rpm = jsonObject.getInt("RPM");
            speed = jsonObject.getInt("SPEED");
            eload = jsonObject.getInt("TPOS");
            etemp = jsonObject.getInt("CTEMP");
            maf = jsonObject.getInt("MAF");
            tpos = jsonObject.getInt("TPOS");
            behavior= jsonObject.getString("BEHAVIOR");
//            Log.i("obdjson","RPM Value from jason is "+rpm);
//            Log.i("obdjson","Speed Value from jason is "+speed);
//            Log.i("obdjson","Throttle Position Value from jason is "+tpos);
//            Log.i("obdjson","Engine Temperature Value from jason is "+etemp);
//            Log.i("obdjson","Mass Air Flow Value from jason is "+maf);
            Log.i("obdjson","Behavior from jason is "+behavior);

            rpmSpeedometer.setSpeedometerColor(Color.parseColor("#1842D1"));
            speedometer.setSpeedometerColor(Color.parseColor("#1842D1"));
            float kmpl = (float) (speed/(maf*0.24489));
            @SuppressLint("DefaultLocale") final String kmplstr = String.format("Milage\n%.2f kmpl",kmpl);
            Typeface tf =Typeface.createFromAsset(getAssets(),"fonts/7Segment.ttf");
            milage.setTypeface(tf);
            user.setText("Welcome \n"+HomeActivity.current_user);
            user.setTypeface(tf);
            runOnUiThread(new Runnable(){
                public void run() {
                    rpmSpeedometer.speedTo(rpm);
                    speedometer.speedTo(speed);
                    engineTemp.speedTo(etemp);
                    milage.setText(kmplstr);
                }
            });
            //Cloud Implementation
            if(ttsobj!=null)
                sendDataToCloud();
            obdObjects[objCount]= new ObdData(maf,etemp,speed,rpm,eload,tpos,behavior);
            objCount++;
            //TextToSpeech
            if(objCount==10)
            {
                makeCountsZero();
                for(int i=0;i<10;i++)
                {
                    //Log.i("ForLoop","In For i: "+i);
                    switch (obdObjects[i].getBehavior()) {
                        case "BelowNormal":
                            bNorCount++;
                            break;
                        case "Normal":
                            normCount++;
                            break;
                        case "Aggressive":
                            agrCount++;
                            break;
                        case "Rash":
                            rashCount++;
                            break;
                    }
                }
                Log.i("DrivingBehavior","BNCount is "+bNorCount+" NCount is "+normCount+" AgrCount is "+agrCount+" RashCount is "+rashCount);
                if(bNorCount>normCount && bNorCount>agrCount && bNorCount>rashCount)
                {
                    ttsobj.speak("You are Driving too slow ",TextToSpeech.QUEUE_FLUSH,null,"SpeechNumber"+utteranceID++);
                    if(obdObjects[9].getSpeed()<20){
                        ttsobj.speak(", Please Maintain your speed at Normal pace else you need to fill up your tank", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }
                    if(obdObjects[9].getRpm()<1200){
                        ttsobj.speak(", Please Maintain your RPM for better performance", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }

                }
                else if(normCount>bNorCount && normCount>agrCount && normCount>rashCount)
                {
                    ttsobj.speak("You are Driving Normal",TextToSpeech.QUEUE_FLUSH,null,"SpeechNumber"+utteranceID++);
                    if(obdObjects[9].getSpeed()>40 && obdObjects[9].getSpeed()<70){
                        ttsobj.speak("Maintain this speed for better fuel economy", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }
                    else{
                        ttsobj.speak("Maintain speed in between 40 to 70", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }
                    if(obdObjects[9].getRpm()>2000 && obdObjects[9].getRpm()<4000)
                    {
                        ttsobj.speak("Maintain this RPM for better fuel economy and enhancing engine's life", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }
                    else{
                        ttsobj.speak("Maintain RPM in between 2000 to 4000", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }
                }
                else if(agrCount>bNorCount && agrCount>normCount && agrCount>rashCount) {
                    ttsobj.speak("You are driving aggressively", TextToSpeech.QUEUE_FLUSH, null, "SpeechNumber" + utteranceID++);
                    if(obdObjects[9].getSpeed()>70){
                        ttsobj.speak(", Please control the Speed", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }
                    if(obdObjects[9].getRpm()>4000) {
                        ttsobj.speak(", Please lower your RPM", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }
                }
                else if(rashCount>bNorCount && rashCount>normCount && rashCount>agrCount) {
                    ttsobj.speak("You are driving too rash", TextToSpeech.QUEUE_FLUSH, null, "SpeechNumber" + utteranceID++);
                    if(obdObjects[9].getSpeed()>100){
                        ttsobj.speak(", Please control the Speed", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }
                    if(obdObjects[9].getRpm()>5000) {
                        ttsobj.speak(", Please lower your RPM", TextToSpeech.QUEUE_ADD, null, "SpeechNumber" + utteranceID++);
                    }
                }
            }

        }
        catch (Exception e) {
            Log.i("myapp",e.toString());
        }
    }
    private void makeCountsZero(){
        objCount=0;
        rashCount=0;
        bNorCount=0;
        normCount=0;
        agrCount=0;
    }
    @Override
    public void onStart() {
        super.onStart();
        ttsobj=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsobj.setLanguage(Locale.UK);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ttsobj=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsobj.setLanguage(Locale.UK);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(ttsobj != null){
            ttsobj.stop();
            ttsobj.shutdown();
            ttsobj=null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(ttsobj != null){
            ttsobj.stop();
            ttsobj.shutdown();
            ttsobj=null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(ttsobj != null){
            ttsobj.stop();
            ttsobj.shutdown();
        }
    }
    private void sendDataToCloud()
    {
        Log.i("SendData","sendDataToCloud Called");
        httpclient = new OkHttpClient();
        request =  new Request.Builder().url("https://api.thingspeak.com/update?api_key=AV92PBHG37Z3ZMLS&field1="+eload+"&field2="+speed+"&field3="+rpm+"&field4="+behavior).build();
        Call call=httpclient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Error ","In onFailure: Call is not Successful");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String respuesta = response.body().string();
//                        Log.i("Onresponse", "In Try Block: "+respuesta);
                    if (response.isSuccessful()) {
                        Log.i("SuccessFul","Data published Successfully");
                    } else {
                        Log.i("Error ","In else: Call is not Successful");
                    }
                } catch (IOException e) {
//                        Log.e("", "Exception caught: ", e);
                }
            }
        });
    }
}
