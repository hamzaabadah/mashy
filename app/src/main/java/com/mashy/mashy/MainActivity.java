package com.mashy.mashy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    CardView mashyToroodCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        mashyToroodCard.setOnClickListener(v->{
            Intent myIntent = new Intent(MainActivity.this,
                    TardActivity.class);
            MainActivity.this.startActivity(myIntent);
        });
    }

    private void init(){
        mashyToroodCard = findViewById(R.id.mashyToroodCard);
    }
}