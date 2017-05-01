package com.example.rilind.budget1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class Results extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        // Array of choices
        String months[] = {"Month","Janurai","Februari","Mars","April","Maj", "Juni","Juli","Agusti","September","Oktober","November","December"};
        // Selection of the spinner
        Spinner month_f = (Spinner) findViewById(R.id.month_f);
        Spinner month_t = (Spinner) findViewById(R.id.month_t);
        setSpinner(months,month_f);
        setSpinner(months,month_t);

        String[] years = new String[31];
        years[0]="Year";
        for (int i=1;i<years.length;i++){
            years[i]=String.valueOf(2018-i);
        }
        Spinner year_t = (Spinner) findViewById(R.id.year_t);
        Spinner year_f = (Spinner) findViewById(R.id.year_f);
        setSpinner(years,year_f);
        setSpinner(years,year_t);

        String[] days = new String[32];
        days[0]="Day";
        for (int i=1;i<days.length;i++){
            days[i]=String.valueOf(i);
        }
        Spinner day_t = (Spinner) findViewById(R.id.day_t);
        Spinner day_f = (Spinner) findViewById(R.id.day_f);
        setSpinner(days,day_f);
        setSpinner(days,day_t);
    }
    public void results(View view){
        Spinner day_t = (Spinner) findViewById(R.id.day_t);
        System.out.println(day_t.getSelectedItem().toString());

    }
    public void setSpinner(String[] array,Spinner spinner){
        Spinner s = spinner;
        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        s.setAdapter(spinnerArrayAdapter);


    }

}
