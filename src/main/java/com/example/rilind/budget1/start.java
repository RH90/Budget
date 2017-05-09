package com.example.rilind.budget1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class start extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("event1"));
        setContentView(R.layout.activity_start);

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox_b);
        checkBox.setOnCheckedChangeListener(this);

        Spinner s1 = (Spinner) findViewById(R.id.buy_moms);
        Spinner s2 = (Spinner) findViewById(R.id.sell_moms);
        setSpinner(s1);
        setSpinner(s2);

    }

    //save the information for when you sell something
    public void savesell(View view) {
        EditText sell_item = (EditText) findViewById(R.id.sell_item);
        EditText sell_price = (EditText) findViewById(R.id.sell_price);
        EditText sell_comment = (EditText) findViewById(R.id.sell_comment);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox_s);
        Spinner sell_moms = (Spinner) findViewById(R.id.sell_moms);
        String s = sell_moms.getSelectedItem().toString();
        save(sell_item, sell_price, sell_comment, checkBox, s, "IN");

        sell_item.setText("");
        sell_price.setText("");
        sell_comment.setText("");
    }

    //save the information for when you buy something
    public void savebuy(View view) {
        EditText buy_item = (EditText) findViewById(R.id.buy_item);
        EditText buy_price = (EditText) findViewById(R.id.buy_price);
        EditText buy_comment = (EditText) findViewById(R.id.buy_comment);
        Spinner buy_moms = (Spinner) findViewById(R.id.buy_moms);
        String s = buy_moms.getSelectedItem().toString();
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox_b);
        save(buy_item, buy_price, buy_comment, checkBox, s, "UT");

        buy_item.setText("");
        buy_price.setText("");
        buy_comment.setText("");
    }

    //save information to local database
    public void save(EditText sell_item, EditText sell_price, EditText sell_comment, CheckBox checkBox, String s, String IN_UT) {
        try {
            String check = "no";
            //checks if the "used" button is pressed
            if (checkBox.isChecked())
                check = "yes";

            String item = sell_item.getText().toString();
            double price = Double.parseDouble(sell_price.getText().toString());
            String comment = sell_comment.getText().toString();
            double moms;
            //get the amount of VAT
            if (s.charAt(0) == '2')
                moms = 0.25;
            else if (s.charAt(0) == '1')
                moms = 0.12;
            else if (s.charAt(0) == '6')
                moms = 0.06;
            else if (s.charAt(0) == '0')
                moms = 0;
            else
                moms = 1;
            if (moms != 1) {
                String finalCheck = check;
                SQLiteDatabase myDB = this.openOrCreateDatabase("Budget", MODE_PRIVATE, null);
                myDB.execSQL("CREATE TABLE IF NOT EXISTS " + "Budget" + " (id INT(11), item VARCHAR(45), moms FLOAT,price FLOAT," +
                        "comment VARCHAR(45),date DATE,IN_UT VARCHAR(45),used VARCHAR(45));");
                Cursor c = myDB.rawQuery("SELECT * FROM Budget ", null);
                System.out.println(c.getCount());
                int i = 0;
                //get the index for the new item by getting the index of the last item and incrementing it
                if (c.getCount() != 0) {
                    c.moveToLast();
                    System.out.println(c.getInt(0));
                    i = c.getInt(0) + 1;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String d = sdf.format(date);

                //inserts the new item to database
                myDB.execSQL("INSERT INTO Budget (id,Item,Moms,Price,Comment,Date,IN_UT,used) " +
                        "VALUES (" + i + ",'" + item + "'," + moms + "," + price + ",'" + comment + "','" + d + "','" + IN_UT + "','" + finalCheck + "');");
            }
        } catch (Exception e) {
            //error
        }
    }

    //update the remote database
    public void sync(View view) {
        Button b = (Button) findViewById(R.id.button8);
        b.setBackgroundColor(Color.LTGRAY);
        //start new thread
        SQL fc = new SQL();
        Thread thread = new Thread() {
            public void run() {
                try {
                    fc.input(getApplicationContext());
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

    public void setSpinner(Spinner spinner) {
        // Application of the Array to the Spinner
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.moms, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //got to result page
    public void results(View view) {
        Intent intent = new Intent(this, Results.class);
        startActivity(intent);
    }

    //go to history page
    public void history(View view) {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    //changes the colour of the sync button
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Button b = (Button) findViewById(R.id.button8);
            System.out.println(message);
            if (message.equalsIgnoreCase("1")) {
                b.setBackgroundColor(Color.GREEN);
            } else if (message.equalsIgnoreCase("0")) {
                b.setBackgroundColor(Color.RED);
            } else {
                b.setBackgroundColor(Color.YELLOW);
            }

        }
    };

    //changes the VAT value when the "used" button is pressed
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b == false) {
            Spinner spinner = (Spinner) findViewById(R.id.buy_moms);
            ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.moms, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } else {
            Spinner spinner = (Spinner) findViewById(R.id.buy_moms);
            ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.zero, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

    }
}
