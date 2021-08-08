package com.mashy.mashy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    private String TAG = "WelcomeActivityTAG";
    CardView captainRegisterCard, clientRegisterCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SharedPreferences preferences = getSharedPreferences("myprefs", MODE_PRIVATE);
        String token = preferences.getString("access_token", "");
        Log.i(TAG, "TOKEN IS : "+token);
        init();

        captainRegisterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WelcomeActivity.this, CaptainRegisterActivity.class);
                WelcomeActivity.this.startActivity(myIntent);
            }
        });


        clientRegisterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WelcomeActivity.this, ClientRegisterActivity.class);
                WelcomeActivity.this.startActivity(myIntent);
            }
        });
    }
    private void init(){
        captainRegisterCard = findViewById(R.id.captainRegisterCard);
        clientRegisterCard = findViewById(R.id.clientRegisterCard);
    }
}