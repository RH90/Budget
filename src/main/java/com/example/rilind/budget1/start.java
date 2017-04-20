package com.example.rilind.budget1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
    public void results(View view){

        Intent intent = new Intent(this, Results.class);
        startActivity(intent);
    }
    public void history(View view){

        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }
}
