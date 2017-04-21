package com.example.rilind.budget1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class History extends AppCompatActivity {
    static String message = "";
    SQL sql;
    static TextView textView = null;
    String x = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("event"));
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            x = savedInstanceState.getString("x");
        }
        setContentView(R.layout.activity_history);
        start();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("x", x);
        // Always call the superclass so it can save the view hierarchy state

    }

    public void start() {

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(x);

    }

    public void month(View view) {
        x = "";
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");


        new Thread(() -> {
            try {
                sql = new SQL();
                sql.read(MainActivity.ip, getApplicationContext());
            } catch (Exception ex) {

            }
        }).start();


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            message = intent.getStringExtra("message");
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.append(message);
            x += message;
        }

    };
}
