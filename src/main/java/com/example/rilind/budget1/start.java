package com.example.rilind.budget1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.sql.SQLException;

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
    public  void savesell(View view){
        SQL fc = new SQL();
        EditText sell_item =(EditText) findViewById(R.id.sell_item);
        EditText sell_price =(EditText) findViewById(R.id.sell_price);
        EditText sell_moms =(EditText) findViewById(R.id.sell_moms);
        EditText sell_comment =(EditText) findViewById(R.id.sell_comment);
        new Thread(() -> {
            try {
                fc.start(MainActivity.ip,
                        sell_item.getText().toString(),
                        Double.parseDouble(sell_price.getText().toString()),
                        Double.parseDouble(sell_moms.getText().toString()),
                        sell_comment.getText().toString());
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
        EditText buy_moms =(EditText) findViewById(R.id.buy_moms);
        EditText buy_comment =(EditText) findViewById(R.id.buy_comment);
        new Thread(() -> {
            try {
                fc.start(MainActivity.ip,
                        buy_item.getText().toString(),
                        Double.parseDouble(buy_price.getText().toString()),
                        Double.parseDouble(buy_moms.getText().toString()),
                        buy_comment.getText().toString());
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
