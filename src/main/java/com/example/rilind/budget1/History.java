package com.example.rilind.budget1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.Stack;

public class History extends AppCompatActivity {
    String message = "";
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

    public void month(View view) throws IOException, ClassNotFoundException {
        x = "";
        TextView textView = (TextView) findViewById(R.id.textView);

        textView.setText("");

        SQLiteDatabase myDB = this.openOrCreateDatabase("Budget", MODE_PRIVATE, null);
        Cursor c = myDB.rawQuery("SELECT * FROM Budget ", null);
        c.moveToLast();

         String s="";
        s = String.format("%-10s|%4s|%7s|%-13s|%-10s\n", "Item", "Moms", "Price", "Comment","Date");
        s = s+ "------------------------------------------------\n";
        while(!c.isBeforeFirst()){
            if(c.getString(6).endsWith("UT"))
                s = s + String.format("%-10s|%4s|%7s|%-13s|%-10s\n",c.getString(1), c.getString(2), "-"+c.getString(3), c.getString(4), c.getString(5));
            else
                s = s + String.format("%-10s|%4s|%7s|%-13s|%-10s\n", c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5));
            c.moveToPrevious();
        }
        TextView textView1 =(TextView) findViewById(R.id.textView);
        textView1.setText(s);





    }
    // update textfield
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            message = intent.getStringExtra("message");
            //Spannable wordtoSpan = new SpannableString(message);
            //for (int i =0;i<message.length()/47;i++){
            //    wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 11+(47*i), 16+(47*i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //}
            TextView textView = (TextView) findViewById(R.id.textView);
            //textView.setText(wordtoSpan);
            textView.setText(message);
            x += message;

        }

    };
}
