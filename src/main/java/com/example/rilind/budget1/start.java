package com.example.rilind.budget1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
    public  void savesell(View view){
        SQL fc = new SQL();
        EditText sell_item =(EditText) findViewById(R.id.sell_item);
        //EditText sell_price =(EditText) findViewById(R.id.sell_price);
        EditText sell_price =(EditText) findViewById(R.id.sell_price);
        EditText sell_comment =(EditText) findViewById(R.id.sell_comment);
        Spinner sell_moms= (Spinner) findViewById(R.id.sell_moms);
        String s = sell_moms.getSelectedItem().toString();
        double moms;
        if(s.charAt(0)=='2')
            moms = 0.25;
        else if(s.charAt(0)=='1')
            moms=0.12;
        else if(s.charAt(0)=='6')
            moms=0.06;
        else
            moms=0;
        new Thread(() -> {
            try {
                fc.input(MainActivity.ip,
                        sell_item.getText().toString(),
                        moms,
                        Double.parseDouble(sell_price.getText().toString()),
                        sell_comment.getText().toString(),"IN");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex){

            }
        }).start();

    }
    public void savebuy(View view){
        SQL fc = new SQL();
        EditText buy_item =(EditText) findViewById(R.id.buy_item);
        EditText buy_price =(EditText) findViewById(R.id.buy_price);
        EditText buy_comment =(EditText) findViewById(R.id.buy_comment);
        Spinner buy_moms= (Spinner) findViewById(R.id.buy_moms);
        String s = buy_moms.getSelectedItem().toString();
        double moms;
        if(s.charAt(0)=='2')
            moms = 0.25;
        else if(s.charAt(0)=='1')
            moms=0.12;
        else if(s.charAt(0)=='6')
            moms=0.06;
        else
            moms=0;
        new Thread(() -> {
            try {
                fc.input(MainActivity.ip,
                        buy_item.getText().toString(),
                        moms,
                        Double.parseDouble(buy_price.getText().toString()),
                        buy_comment.getText().toString(),"UT");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex){

            }
        }).start();

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
