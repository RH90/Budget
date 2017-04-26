package com.example.rilind.budget1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

public class History extends AppCompatActivity {
    static String message = "";
    SQL sql;
    TextView textView = null;
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
    // reloads the textfield when rotating
    public void start() {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(x);

    }

    public void month(View view) {
        x = "";
        textView = (TextView) findViewById(R.id.textView);

        textView.setText("");

        //connect to database
        new Thread(() -> {
            try {
                sql = new SQL();
                sql.read(MainActivity.ip, getApplicationContext());
            } catch (Exception ex) {

            }
        }).start();



    }
    // update textfield
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            message = intent.getStringExtra("message");
            Spannable wordtoSpan = new SpannableString(message);
            for (int i =0;i<message.length()/47;i++){
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 11+(47*i), 16+(47*i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(wordtoSpan);

            x += message;

        }

    };
}
