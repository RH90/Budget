package com.example.rilind.budget1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.sql.SQLException;

public class Settings extends AppCompatActivity {
    int in =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button b= (Button) findViewById(R.id.numEmp);
        b.setText("Antal anst\u00e4llda: 0");
    }
    public void numb(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ange antal Inst\u00e4llda: ");
        // Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        builder.setView(input);

// Set up the buttons

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    in = Integer.parseInt(input.getText().toString());
                    Button b = (Button) view;
                    b.setText("Antal anst\u00e4llda: " + in);
                }catch (Exception e){

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
    public void sync(View view){
        SQL fc = new SQL();
        Thread thread = new Thread() {
            public void run() {
                try {
                    fc.input(getApplicationContext(),MainActivity.username);
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
    public void importRemote(View view){
        SQL fc = new SQL();
        Thread thread = new Thread() {
            public void run() {
                    fc.importdb(getApplicationContext(),MainActivity.username);
            }
        };
        thread.start();
    }
}
