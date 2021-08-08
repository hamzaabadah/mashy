package com.mashy.mashy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

public class TardActivity extends AppCompatActivity {

    CardView snadTard, cartonTard, otherTard, pageTard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tard);
        init();
        snadTard.setOnClickListener(v-> {
            Intent intent = new Intent(getBaseContext(), TardDetailsActivity.class);
            intent.putExtra("type", "documents");
            startActivity(intent);
        });
        cartonTard.setOnClickListener(v-> {
            Intent intent = new Intent(getBaseContext(), TardDetailsActivity.class);
            intent.putExtra("type", "carton");
            startActivity(intent);
        });
        otherTard.setOnClickListener(v-> {
            Intent intent = new Intent(getBaseContext(), TardDetailsActivity.class);
            intent.putExtra("type", "other");
            startActivity(intent);
        });
        pageTard.setOnClickListener(v-> {
            Intent intent = new Intent(getBaseContext(), TardDetailsActivity.class);
            intent.putExtra("type", "bag");
            startActivity(intent);
        });
    }

    private void init(){
        snadTard = findViewById(R.id.snadTard);
        cartonTard = findViewById(R.id.cartonTard);
        otherTard = findViewById(R.id.otherTard);
        pageTard = findViewById(R.id.pageTard);
    }
}