package com.example.obdtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class Login extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;

    public static int cnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextUsername=(EditText)findViewById(R.id.edittext_username);
        mTextPassword=(EditText)findViewById(R.id.edittext_password);
        mButtonLogin=(Button)findViewById(R.id.button_login);
        mTextViewRegister=(TextView)findViewById(R.id.textview_register);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverDetails driverDetails;
                SharedPreferences count= getSharedPreferences(String.valueOf(R.string.shared_pref_user_count),MODE_PRIVATE);
                cnt=count.getInt("count",0);
                Log.i("DataCount","Count ="+cnt);
                SharedPreferences data=getSharedPreferences(String.valueOf(R.string.shared_pref_user_data),MODE_PRIVATE);
                Gson gson = new Gson();
                int flag=0;
                int i;
                for(i=0;i<cnt;i++){
                    String json = data.getString(String.valueOf(R.string.json_objects+i),"");
                    Log.i("DataJson",""+json);
                    driverDetails = gson.fromJson(json,DriverDetails.class);
                    if(driverDetails.username.equals(mTextUsername.getText().toString())&&driverDetails.password.equals(mTextPassword.getText().toString())){
                        flag = 1;
                        break;
                    }
                }
                if(flag==1)
                {
                    Intent intent=new Intent(Login.this,HomeActivity.class);
                    intent.putExtra("UserID",i);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Login.this,"Wrong Username or Password Try Again!!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(Login.this, Signup.class);
                startActivity(registerIntent);
            }
        });
    }
}
