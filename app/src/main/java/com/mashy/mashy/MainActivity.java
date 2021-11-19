package com.mashy.mashy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    CardView mashyToroodCard, myOrders;
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

        myOrders.setOnClickListener(v->{
            Intent myIntent = new Intent(MainActivity.this,
                    MyOrdersActivity.class);
            MainActivity.this.startActivity(myIntent);
        });
    }

    private void init(){
        mashyToroodCard = findViewById(R.id.mashyToroodCard);
        myOrders = findViewById(R.id.myOrders);
    }
}