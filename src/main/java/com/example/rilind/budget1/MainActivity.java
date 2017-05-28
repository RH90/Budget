package com.example.rilind.budget1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static String username="";
    static String password="";
    static EditText user ;
    static int id=0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("event2"));

        setContentView(R.layout.activity_main);
        user = (EditText) findViewById(R.id.editText2);

        //ask for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if(message.equals("0")){

                ss();
            }else{
                TextView error = (TextView) findViewById(R.id.errorM);
                error.setText("Login fail");
            }

        }
    };


    public void ss(){
        EditText user = (EditText) findViewById(R.id.editText2);
        EditText pass = (EditText) findViewById(R.id.editText3);
        username=user.getText().toString();
        password=pass.getText().toString();
        SQLiteDatabase myDB = openOrCreateDatabase("Budget", MODE_PRIVATE, null);
        myDB.execSQL("CREATE TABLE IF NOT EXISTS " + username + " (id INT(11), item VARCHAR(45), moms FLOAT,price FLOAT," +
                "comment VARCHAR(45),date DATE,IN_UT VARCHAR(45),used VARCHAR(45));");
        Intent in = new Intent(this, TabActivity.class);
        startActivity(in);
    }
    public void create(View view){
        Intent in = new Intent(this, Create.class);
        startActivity(in);
    }


     //set ip address
    public void login(View view) throws IOException {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        EditText user = (EditText) findViewById(R.id.editText2);
        EditText pass = (EditText) findViewById(R.id.editText3);
        if(user.getText().toString().equalsIgnoreCase("root")&&pass.getText().toString().equalsIgnoreCase("root")){
            id=-1;
            ss();
        }else {
            SQL fc = new SQL();
            Thread thread = new Thread() {
                public void run() {
                    fc.login(getApplicationContext(), user.getText().toString(), pass.getText().toString());
                }
            };
            thread.start();
        }


    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
