package com.example.obdtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
import org.w3c.dom.Text;

public class TripActivity extends AppCompatActivity implements OnMapReadyCallback {

    double destlLat, destLong, srcLat, srcLong;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location l;
    private LatLng pos;


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
        MqttConnection mqttConnection=new MqttConnection();
        mqttConnection.connect(this.getApplicationContext(),this);
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
            final TextView user = (TextView) findViewById(R.id.user_name_txt);
            rpmSpeedometer.setSpeedometerColor(Color.parseColor("#1842D1"));
            speedometer.setSpeedometerColor(Color.parseColor("#1842D1"));
            float kmpl = (float) (speed/(maf*0.24489));
            final String kmplstr = String.format("Milage\n%.2f kmpl",kmpl);
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

        } catch (Exception e) {
            Log.i("myapp",e.getStackTrace().toString());
        }
    }
}
