package com.example.rilind.budget1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("event1"));
        setContentView(R.layout.activity_start);

        Spinner s1 = (Spinner) findViewById(R.id.buy_moms);
        Spinner s2 = (Spinner) findViewById(R.id.sell_moms);
        setSpinner(s1);
        setSpinner(s2);

    }
    public  void savesell(View view){
        EditText sell_item =(EditText) findViewById(R.id.sell_item);
        EditText sell_price =(EditText) findViewById(R.id.sell_price);
        EditText sell_comment =(EditText) findViewById(R.id.sell_comment);
        CheckBox checkBox =(CheckBox) findViewById(R.id.checkBox);
        Spinner sell_moms= (Spinner) findViewById(R.id.sell_moms);
        String s = sell_moms.getSelectedItem().toString();
        String check="no";
        if(checkBox.isChecked())
            check="yes";
        String item= sell_item.getText().toString();
        double price;
        try {
            price = Double.parseDouble(sell_price.getText().toString());
            String comment=sell_comment.getText().toString();
            double moms;
            if(s.charAt(0)=='2')
                moms = 0.25;
            else if(s.charAt(0)=='1')
                moms=0.12;
            else if(s.charAt(0)=='6')
                moms=0.06;
            else
                moms=0;
            if(moms!=0) {
                String finalCheck = check;
                SQLiteDatabase myDB = this.openOrCreateDatabase("Budget", MODE_PRIVATE, null);
                myDB.execSQL("CREATE TABLE IF NOT EXISTS " + "Budget" + " (id INT(11), item VARCHAR(45), moms FLOAT,price FLOAT," +
                        "comment VARCHAR(45),date DATE,IN_UT VARCHAR(45),used VARCHAR(45));");
                Cursor c = myDB.rawQuery("SELECT * FROM Budget ", null);
                System.out.println(c.getCount());
                int i=0;
                if(c.getCount()!=0) {
                    c.moveToLast();
                    System.out.println(c.getInt(0));
                    i=c.getInt(0)+1;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String d=sdf.format(date);
                myDB.execSQL("INSERT INTO Budget (id,Item,Moms,Price,Comment,Date,IN_UT,used) " +
                        "VALUES ("+i+",'"+item+"',"+moms+","+price+",'"+comment+"','"+d+"','IN','"+finalCheck+"');");
            }
        }catch (Exception e){
            //error
        }


        sell_item.setText("");
        sell_price.setText("");
        sell_comment.setText("");
    }
    public void savebuy(View view){
        EditText buy_item =(EditText) findViewById(R.id.buy_item);
        EditText buy_price =(EditText) findViewById(R.id.buy_price);
        EditText buy_comment =(EditText) findViewById(R.id.buy_comment);
        Spinner buy_moms= (Spinner) findViewById(R.id.buy_moms);
        String s = buy_moms.getSelectedItem().toString();

        String item= buy_item.getText().toString();
        double price;
        try {
            price = Double.parseDouble(buy_price.getText().toString());

        double moms;
        String comment=buy_comment.getText().toString();
        if(s.charAt(0)=='2')
            moms = 0.25;
        else if(s.charAt(0)=='1')
            moms=0.12;
        else if(s.charAt(0)=='6')
            moms=0.06;
        else
            moms=0;

        if(moms!=0) {
            SQLiteDatabase myDB = this.openOrCreateDatabase("Budget", MODE_PRIVATE, null);

            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + "Budget" + " (id INT(11), item VARCHAR(45), moms FLOAT,price FLOAT," +
                    "comment VARCHAR(45),date DATE,IN_UT VARCHAR(45),used VARCHAR(45));");

            //ArrayList<Item> al =new ArrayList<>();
            Cursor c = myDB.rawQuery("SELECT * FROM Budget ", null);
            System.out.println(c.getCount());
            int i=0;
            if(c.getCount()!=0) {
                c.moveToLast();
                System.out.println(c.getInt(0));
                i=c.getInt(0)+1;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String d=sdf.format(date);
            myDB.execSQL("INSERT INTO Budget (id,Item,Moms,Price,Comment,Date,IN_UT,used) " +
                    "VALUES ("+i+",'"+item+"',"+moms+","+price+",'"+comment+"','"+d+"','UT','no');");
        }
        }catch (Exception e){
            //error
        }

        buy_item.setText("");
        buy_price.setText("");
        buy_comment.setText("");
    }
    public void sync(View view){
        Button b =(Button) findViewById(R.id.button8);
        b.setBackgroundColor(Color.LTGRAY);

        SQL fc = new SQL();
        Thread thread = new Thread(){
            public void run(){
                try {
                    fc.input(MainActivity.ip,getApplicationContext());
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    public void setSpinner(Spinner spinner){
        // Application of the Array to the Spinner
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.moms, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void results(View view){

        Intent intent = new Intent(this, Results.class);
        startActivity(intent);
    }
    public void history(View view){

        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Button b =(Button) findViewById(R.id.button8);
            System.out.println(message);
            if(message.equalsIgnoreCase("1")){
                b.setBackgroundColor(Color.GREEN);
            }else if(message.equalsIgnoreCase("0")){
                b.setBackgroundColor(Color.RED);
            }else{
                b.setBackgroundColor(Color.YELLOW);
            }

        }
    };

}
