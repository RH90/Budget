package com.example.rilind.budget1;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class start extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_start, container, false);

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        v= getView();

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("barcode"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(getget,
                new IntentFilter("getget"));

        CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox_b);
        checkBox.setOnCheckedChangeListener(this);
        Button sell = (Button) v.findViewById(R.id.savesell);
        sell.setOnClickListener(this);
        Button buy = (Button) v.findViewById(R.id.savebuy);
        buy.setOnClickListener(this);
        Button barcode = (Button) v.findViewById(R.id.barcode_button);
        barcode.setOnClickListener(this);

        Spinner s1 = (Spinner) v.findViewById(R.id.buy_moms);
        Spinner s2 = (Spinner) v.findViewById(R.id.sell_moms);

        setSpinner(s1);
        setSpinner(s2);


    }

    //save the information for when you sell something
    public void savesell() {
        EditText sell_item = (EditText) v.findViewById(R.id.sell_item);
        EditText sell_price = (EditText) v.findViewById(R.id.sell_price);
        EditText sell_comment = (EditText) v.findViewById(R.id.sell_comment);
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox_s);
        Spinner sell_moms = (Spinner) v.findViewById(R.id.sell_moms);
        String s = sell_moms.getSelectedItem().toString();
        save(sell_item, sell_price, sell_comment, checkBox, s, "IN");

        sell_item.setText("");
        sell_price.setText("");
        sell_comment.setText("");
    }

    //save the information for when you buy something
    public void savebuy() {
        EditText buy_item = (EditText) v.findViewById(R.id.buy_item);
        EditText buy_price = (EditText) v.findViewById(R.id.buy_price);
        EditText buy_comment = (EditText) v.findViewById(R.id.buy_comment);
        Spinner buy_moms = (Spinner) v.findViewById(R.id.buy_moms);
        String s = buy_moms.getSelectedItem().toString();
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox_b);
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
                SQLiteDatabase myDB = getActivity().openOrCreateDatabase("Budget", getActivity().MODE_PRIVATE, null);
                Cursor c = myDB.rawQuery("SELECT * FROM "+MainActivity.username+" ", null);
                int i = 0;
                //get the index for the new item by getting the index of the last item and incrementing it
                if (c.getCount() != 0) {
                    c.moveToLast();
                    i = c.getInt(0) + 1;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String d = sdf.format(date);

                //inserts the new item to database
                myDB.execSQL("INSERT INTO "+MainActivity.username+" (id,Item,Moms,Price,Comment,Date,IN_UT,used) " +
                        "VALUES (" + i + ",'" + item + "'," + moms + "," + price + ",'" + comment + "','" + d + "','" + IN_UT + "','" + finalCheck + "');");

                if(IN_UT.equalsIgnoreCase("in")) {
                    EditText serialnum = (EditText) v.findViewById(R.id.serialnum);
                    SQL fc = new SQL();
                    Thread thread = new Thread() {
                        public void run() {
                            fc.saveitem(getActivity().getApplicationContext()
                                    , MainActivity.username, serialnum.getText().toString(), item, String.valueOf(moms), String.valueOf(price));
                        }
                    };
                    thread.start();
                }

            }
        } catch (Exception e) {
            //error
        }
    }


    public void setSpinner(Spinner spinner) {
        // Application of the Array to the Spinner
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.moms, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    public void barcodebutton(){
        Intent in = new Intent(getActivity(), BarcodeRead.class);
        startActivity(in);
    }


    //changes the VAT value when the "used" button is pressed
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b == false) {
            Spinner spinner = (Spinner) v.findViewById(R.id.buy_moms);
            ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.moms, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } else {
            Spinner spinner = (Spinner) v.findViewById(R.id.buy_moms);
            ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.zero, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            EditText serialnum = (EditText) v.findViewById(R.id.serialnum);
            serialnum.setText(message);



            SQL fc = new SQL();
            Thread thread = new Thread() {
                public void run() {
                        fc.getitem(getActivity().getApplicationContext(),MainActivity.username,message);
                }
            };
            thread.start();

        }
    };

    private BroadcastReceiver getget = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("get");
            if(message.equalsIgnoreCase("")){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("This item is not in the DB");
                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_NULL);
                builder.setView(input);
                // Set up the buttons

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                        }catch (Exception e){

                        }
                    }
                });

                builder.show();
            }else {
                String parts[] = message.split("\\n");
                EditText sell_item = (EditText) v.findViewById(R.id.sell_item);
                EditText sell_price = (EditText) v.findViewById(R.id.sell_price);
                Spinner sell_moms = (Spinner) v.findViewById(R.id.sell_moms);
                sell_item.setText(parts[0]);
                float i = Float.parseFloat(parts[1]);
                if (i == 0.25)
                    sell_moms.setSelection(1);
                else if (i == 0.12)
                    sell_moms.setSelection(2);
                else
                    sell_moms.setSelection(3);
                sell_price.setText(parts[2]);

            }

        }
    };


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.savesell)
        savesell();
        else if(view.getId()==R.id.savebuy)
            savebuy();
        else
            barcodebutton();
    }
}
