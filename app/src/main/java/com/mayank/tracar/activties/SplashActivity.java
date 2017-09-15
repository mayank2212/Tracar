package com.mayank.tracar.activties;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mayank.tracar.R;
import com.mayank.tracar.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        try{

            getSupportActionBar().hide();
        }catch (Exception e){

        }
        // Session Manager
        session = new SessionManager(getApplicationContext());

        if(session.checkLogin()){
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            SplashActivity.this.finish();
        }else{
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            SplashActivity.this.finish();

        }

    }
}
