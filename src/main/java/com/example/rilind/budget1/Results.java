package com.example.rilind.budget1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Results extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    Spinner month_f;
    Spinner month_t;
    Spinner year_t;
    Spinner year_f;
    Spinner day_t;
    Spinner day_f;
    float size=0;

    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_results, container, false);

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v= getView();
        // Array of months for the month spinners
        String months[] = {"Month", "Januari", "Februari", "Mars", "April", "Maj", "Juni", "Juli", "Agusti", "September", "Oktober", "November", "December"};
        Button b = (Button) v.findViewById(R.id.button4);
        b.setVisibility(View.GONE);
        month_f = (Spinner) v.findViewById(R.id.month_f);
        month_t = (Spinner) v.findViewById(R.id.month_t);
        year_t = (Spinner) v.findViewById(R.id.year_t);
        year_f = (Spinner) v.findViewById(R.id.year_f);
        day_t = (Spinner) v.findViewById(R.id.day_t);
        day_f = (Spinner) v.findViewById(R.id.day_f);

        setSpinner(months, month_f);
        setSpinner(months, month_t);
        Button sync = (Button) v.findViewById(R.id.button6);
        sync.setOnClickListener(this);
        b.setOnClickListener(this);

        int y;
        //get current year
        y = Calendar.getInstance().get(Calendar.YEAR);

        String[] years = new String[31];
        years[0] = "Year";
        for (int i = 1; i < years.length; i++) {
            years[i] = String.valueOf((y + 1) - i);
        }

        setSpinner(years, year_f);
        setSpinner(years, year_t);

        days(32, day_f);
        days(32, day_t);
        //listener for when the month spinners are pressed
        month_f.setOnItemSelectedListener(this);
        month_t.setOnItemSelectedListener(this);

        TextView one = (TextView) v.findViewById(R.id.textToPdf);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        System.out.println(dpWidth);

        TextPaint paint = one.getPaint();
        double wordwidth= paint.measureText("a",0,1);
        float w =(float) wordwidth / getResources().getDisplayMetrics().scaledDensity;
        System.out.println("w:" +w);
        float z =dpWidth/w;
        float t =z/(float)80.0;
        float sp = one.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
        one.setTextColor(Color.BLACK);
        float hey = sp*t;
        System.out.println(wordwidth);
        one.setTextSize(TypedValue.COMPLEX_UNIT_SP,hey);


        wordwidth= paint.measureText("a",0,1);
        w =(float) wordwidth / getResources().getDisplayMetrics().scaledDensity;
        z=dpWidth/w;
        if(z>80){
            t =z/(float)82.0;
            sp = one.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
            one.setTextColor(Color.BLACK);
            hey = sp*t;
            System.out.println(wordwidth);
            one.setTextSize(TypedValue.COMPLEX_UNIT_SP,hey);
        }
        w =(float) wordwidth / getResources().getDisplayMetrics().scaledDensity;
        z=dpWidth/w;
        size=hey;
        System.out.println("z:" +z);


    }

    //changes the amount of days in the days spinners
    public void days(int z, Spinner spinner) {
        String[] days = new String[z];
        days[0] = "Day";
        for (int i = 1; i < days.length; i++) {
            days[i] = String.valueOf(i);
        }
        setSpinner(days, spinner);
    }

    //prints out the results for a time interval when the results button is pressed
    public void results() throws ParseException, IOException {
        //you can only show results if you first give the year interval
        if (year_t.getSelectedItemPosition() != 0 && year_f.getSelectedItemPosition() != 0) {
            //get date and save it as in the format of "YYYY-MM-DD"
            int mf = month_f.getSelectedItemPosition();
            if(mf==0)
                mf++;
            int df = day_f.getSelectedItemPosition();
            if(df==0)
                df++;
            int mt = month_t.getSelectedItemPosition();
            if(mt==0)
                mt++;
            int dt = day_f.getSelectedItemPosition();
            if(dt==0)
                dt++;
            String from = year_f.getSelectedItem().toString() + "-" + mf + "-" + df;
            String to = year_t.getSelectedItem().toString() + "-" + mt + "-" + dt;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date_f = sdf.parse(from);
            Date date_t = sdf.parse(to);
            //open database file
            SQLiteDatabase myDB = getActivity().openOrCreateDatabase("Budget", getActivity().MODE_PRIVATE, null);
            //retrieve only the items in the given time interval
            Cursor c = myDB.rawQuery("SELECT * FROM "+MainActivity.username+" where date between \"" + sdf.format(date_f) + "\" and \"" + sdf.format(date_t) + "\" ", null);

            double moms_ut = 0;
            double kopt_med_moms = 0;
            double moms_in = 0;
            double solt_med_moms = 0;
            c.moveToFirst();
            //loop through the given items and calculate the results
            while (!c.isAfterLast()) {
                //if the item is a expense then: total_VAT_expense += item * item_VAT
                if (c.getString(6).endsWith("UT")) {
                    moms_ut += c.getDouble(3)- c.getDouble(3)/(1+c.getDouble(2));
                    kopt_med_moms += c.getDouble(3);
                } else {
                    if (c.getString(7).equalsIgnoreCase("no"))
                        moms_in +=  c.getDouble(3)- c.getDouble(3)/(1+c.getDouble(2));
                        //if the item is a sold item and is used then: total_revenue_VAT += item * item_vat * 80%
                    else
                        moms_in += c.getDouble(3)- c.getDouble(3)/(1+(c.getDouble(2)*0.8));
                    solt_med_moms += c.getDouble(3) ;
                }
                c.moveToNext();
            }
            double moms_total=-(moms_in - moms_ut);
            //write out the results on the textfields
            String line=            "  ______________________________________________________________________________";
            TextView one = (TextView) v.findViewById(R.id.textToPdf);
            String s= String.format("\n\n    Results                                 "+from+" to "+to+"\n\n" +
                                    line+"\n"+
                                    "    S\u00e5lt med moms:                                               %.2f Kr\n\n" +
                                    line+"\n"+
                                    "    K\u00f6pt med moms:                                               %.2f Kr\n\n" +
                                    line+"\n"+
                                    "    Moms total:                                                  %.2f Kr\n\n" +
                                    line+"\n"+
                                    "    Vinst:                                                       %.2f Kr",
                    solt_med_moms,-kopt_med_moms,moms_total,solt_med_moms - kopt_med_moms + moms_total);

            one.setText(s);
            Button b = (Button) v.findViewById(R.id.button4);
            b.setVisibility(View.VISIBLE);




        } else {
            //error message
        }


    }
    public void save(){
        // Create a shiny new (but blank) PDF document in memory
        // We want it to optionally be printable, so add PrintAttributes
        // and use a PrintedPdfDocument. Simpler: new PdfDocument().

        TextView content =(TextView) v.findViewById(R.id.textToPdf);

        TextPaint paint = content.getPaint();
        //the width of the character 'a' in pixels
        double wordwidth=paint.measureText("a",0,1);
        System.out.println(wordwidth);
        //40 = characters per line
        double test=595.0/80.0;
        float mul =(float) wordwidth/(float)test;
        float sp = content.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
        System.out.println("w2:"+wordwidth/getResources().getDisplayMetrics().scaledDensity);
        float hey = sp/mul;

        content.setTextSize(TypedValue.COMPLEX_UNIT_SP,hey);
        PrintAttributes printAttrs = new PrintAttributes.Builder().
                setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                setMediaSize(PrintAttributes.MediaSize.NA_LETTER).
                setResolution(new PrintAttributes.Resolution("zooey", getActivity().PRINT_SERVICE, 300, 300)).
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                build();

        PdfDocument document = new PrintedPdfDocument(getActivity(), printAttrs);
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        // create a new page from the PageInfo
        PdfDocument.Page page = document.startPage(pageInfo);
        // repaint the user's text into the page

        content.draw(page.getCanvas());
        // do final processing of the page
        document.finishPage(page);
        // Here you could add more pages in a longer doc app, but you'd have
        // to handle page-breaking yourself in e.g., write your own word processor...
        // Now write the PDF document to a file; it actually needs to be a file
        // since the Share mechanism can't accept a byte[]. though it can
        // accept a String/CharSequence. Meh.
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            System.out.println(root);
            File myDir = new File(root + "/yes");
            myDir.mkdir();
            File f = new File (myDir, "yes.pdf");
            FileOutputStream fos = new FileOutputStream(f);
            document.writeTo(fos);
            document.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
            String root = Environment.getExternalStorageDirectory().toString();
            File f = new File(root + "/yes","yes.pdf");
            Uri uri = Uri.fromFile(f);
            Intent mShareIntent = new Intent();
            mShareIntent.setAction(Intent.ACTION_SEND);
            mShareIntent.setType("application/pdf");
            // Assuming it may go via eMail:
            mShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Here is a PDF from PdfSend");
            mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(mShareIntent);


        content.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
    }

    //writes out the text on the spinners
    public void setSpinner(String[] array, Spinner spinner) {
        Spinner s = spinner;
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        s.setAdapter(spinnerArrayAdapter);
    }

    //changes the day spinner, so when you choose a month you get the amount a days corresponding to the chosen month
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner month = null;
        Spinner day = null;
        Spinner year = null;
        //checks which month spinner was pressed
        if (adapterView.getId() == R.id.month_f) {
            day = day_f;
            month = month_f;
            year = year_f;
        }
        if (adapterView.getId() == R.id.month_t) {
            day = day_t;
            month = month_t;
            year = year_t;
        }
        //gives the right amount of days for the chosen month
        int z = month.getSelectedItemPosition();
        if (z == 0 || z == 1 || z == 3 || z == 5 || z == 7 || z == 8 || z == 12 || z == 10) {
            days(32, day);
        }
        if (z == 4 || z == 6 || z == 9 || z == 11) {
            days(31, day);
        }
        //gives february 29 days if it's a leap year, else it gives 28 days
        if (z == (2)) {
            double y = 1;
            if (year.getSelectedItemPosition() != 0)
                y = Integer.parseInt(year.getSelectedItem().toString());
            if ((y % 400 == 0 || y % 100 != 0) && (y % 4 == 0))
                days(30, day);
            else
                days(29, day);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        try {
            if(view.getId()==v.findViewById(R.id.button4).getId())
                save();
            else
                results();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
