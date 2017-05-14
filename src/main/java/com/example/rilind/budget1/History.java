package com.example.rilind.budget1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;

public class History extends Fragment{
    String x = "";
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_history, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v= getView();
        start();
        try {
            month();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("x", x);
        // Always call the superclass so it can save the view hierarchy state

    }


    // reloads the textfield when rotating
    public void start() {
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(x);
        textView.setTextColor(Color.BLACK);
    }

    //go here when the 1 month button is pressed
    //prints out the transactions from now to 1 month
    public void month() throws IOException, ClassNotFoundException {
        x = "";
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText("");
        //open the database file
        SQLiteDatabase myDB = getActivity().openOrCreateDatabase("Budget", getActivity().MODE_PRIVATE, null);
        //get data from database
        Cursor c = myDB.rawQuery("SELECT * FROM "+MainActivity.username+" ", null);
        //moves to the last item so we get the newest item first
        c.moveToLast();

        String s = "";
        s = String.format("%-10s|%4s|%7s|%-13s|%-10s\n", "Item", "Moms", "Price", "Comment", "Date");
        s = s + "------------------------------------------------\n";
        //loops through the database and prints it out
        while (!c.isBeforeFirst()) {
            if (c.getString(6).endsWith("UT"))
                //it the item is a "utgift" then show a minus in price
                s = s + String.format("%-10s|%4s|%7s|%-13s|%-10s\n", c.getString(1), c.getString(2), "-" + c.getString(3), c.getString(4), c.getString(5));
            else
                s = s + String.format("%-10s|%4s|%7s|%-13s|%-10s\n", c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5));
            c.moveToPrevious();
        }
        //output to Textview
        TextView textView1 = (TextView) v.findViewById(R.id.textView);
        textView1.setText(s);

        //Spannable wordtoSpan = new SpannableString(message);
        //for (int i =0;i<message.length()/47;i++){
        //    wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 11+(47*i), 16+(47*i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //}
        //TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setText(wordtoSpan);
    }

}
