package com.example.obdtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class Signup extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    EditText mTextVehicleNo;
    EditText mTextMobileNo;
    EditText mTextEmail;
    EditText mTextDriverName;
    Button mButtonSignUp;
    TextView mTextViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mTextUsername=(EditText)findViewById(R.id.edittext_username);
        mTextDriverName=(EditText)findViewById(R.id.edittext_driver_name);
        mTextVehicleNo=(EditText)findViewById(R.id.edittext_vehicle_no);
        mTextMobileNo=(EditText)findViewById(R.id.edittext_mobile_no);
        mTextEmail=(EditText)findViewById(R.id.edittext_email);
        mTextPassword=(EditText)findViewById(R.id.edittext_password);
        mTextCnfPassword=(EditText)findViewById(R.id.edittext_cnf_password);
        mButtonSignUp=(Button)findViewById(R.id.button_signup);
        mTextViewLogin=(TextView)findViewById(R.id.textview_login);

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTextCnfPassword.getText().toString().equals(mTextPassword.getText().toString())) {
                    mTextCnfPassword.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(mTextCnfPassword, InputMethodManager.SHOW_IMPLICIT);
                    Toast.makeText(Signup.this, "Password doesn't match", Toast.LENGTH_LONG).show();
                } else {
                    DriverDetails driverDetails = new DriverDetails();
                    driverDetails.username = mTextUsername.getText().toString();
                    driverDetails.driver_name = mTextDriverName.getText().toString();
                    driverDetails.email_id = mTextEmail.getText().toString();
                    driverDetails.mobile_no = mTextMobileNo.getText().toString();
                    driverDetails.vehicle_no = mTextVehicleNo.getText().toString();
                    driverDetails.password = mTextPassword.getText().toString();
                    SharedPreferences count = getSharedPreferences(String.valueOf(R.string.shared_pref_user_count),MODE_PRIVATE);
                    int drivercount=count.getInt("count",0);
                    Log.i("DataCount","Count ="+drivercount);
                    SharedPreferences driverData=getSharedPreferences(String.valueOf(R.string.shared_pref_user_data),MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = driverData.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(driverDetails);
                    Log.i("DataJson",""+json);
                    prefsEditor.putString(String.valueOf(R.string.json_objects+drivercount), json);
                    prefsEditor.apply();
                    drivercount++;
                    count.edit().putInt("count",drivercount).apply();
                    Intent intent= new Intent(Signup.this,Login.class);
                    startActivity(intent);
                }
            }
        });
        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent=new Intent(Signup.this,Login.class);
                startActivity(LoginIntent);
            }
        });

    }
}
