package com.example.rilind.budget1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import junit.runner.Version;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rilind on 2017-04-07.
 */
public class SQL {
    Intent intent1 = new Intent("event1");
    Intent intent2 = new Intent("event2");
    Intent intent3 = new Intent("event3");
    Statement stmt = null;
    ResultSet rs = null;
    Connection con = null;

    // connect to database
    public Connection connect() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

        }
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://rh9011.hopto.org:3306/budget", "root", "root");
            return con;
        } catch (Exception ex) {
            System.out.println("Connection fail");
            return null;
        }

    }

    // input data to database
    public void input( Context context,String user) throws SQLException, ClassNotFoundException, IOException {
        String color = "0";
        try {
            //checks how many items there are inside the remote database, so we know how many item we should send to it.
            int index = index(context,user);
            con = connect();
            stmt = con.createStatement();
            //get data from local database
            SQLiteDatabase myDB = context.openOrCreateDatabase("Budget", MODE_PRIVATE, null);
            Cursor c = myDB.rawQuery("SELECT * FROM "+user+" ", null);
            String query = "";
            c.moveToFirst();
            //store the items to a batch
            while (!c.isAfterLast()) {
                int i = c.getInt(0);
                if (index <= i || index == 0) {
                    query = "INSERT INTO "+user+" (id,Item,Moms,Price,Comment,Date,IN_UT,used) VALUES " +
                            "(" + c.getInt(0) + ",'" + c.getString(1) + "'," + c.getString(2) + "," + c.getString(3) + ",'" + c.getString(4) + "','" + c.getString(5) + "','" + c.getString(6) + "','" + c.getString(7) + "');";
                    stmt.addBatch(query);
                }
                c.moveToNext();
            }
            //execute all the statements that where stored in the batch
            stmt.executeBatch();
            //if the remote database is up to date then make the sync button yellow, else make it green
            if (query.equalsIgnoreCase(""))
                color = "2";
            else
                color = "1";

        } catch (Exception ex) {
            System.out.println("Login fail1");
        } finally {
            try {
                //change the sync button color
                intent1.putExtra("message", color);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    //get the amount of items in remote database
    public int index(Context context,String user) {
        int index = 0;
        try {
            con = connect();

            stmt = con.createStatement();
            String query = "SELECT * FROM "+user+"";
            rs = stmt.executeQuery(query);
            //ResultSetMetaData columns = rs.getMetaData();
            //get the index of the last item
            rs.last();
            index = rs.getRow();

        } catch (Exception ex) {
            //if it cannot connect to database then change the button color to red
            intent1.putExtra("message", "0");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
            System.out.println("Login fail");
        } finally {
            try {

                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return index;
    }
    public void login(Context context,String user,String pass){
        String check="0";
        try{
            con=connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from users where username='"+user+"' AND password='"+pass+"'");
            if(rs.next()==false){
                check="1";
            }


        } catch (Exception ex) {
            check="1";
            System.out.println("Login fail3");
        } finally {

            intent2.putExtra("message", check);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    public void create(Context context,String user,String pass){
        if(checkuser(user)) {
            try {
                con = connect();
                stmt = con.createStatement();
                stmt.addBatch("CREATE TABLE IF NOT EXISTS " + user + " (id INT(11), item VARCHAR(45), moms FLOAT,price FLOAT," +
                        "comment VARCHAR(45),date DATE,IN_UT VARCHAR(45),used VARCHAR(45));");
                stmt.addBatch("INSERT INTO users (username,password,date_added) VALUES " +
                        "('" +user + "','"+pass+"',CURDATE() );");
                stmt.executeBatch();
                SQLiteDatabase myDB = context.openOrCreateDatabase("Budget", MODE_PRIVATE, null);
                myDB.execSQL("CREATE TABLE IF NOT EXISTS " + user + " (id INT(11), item VARCHAR(45), moms FLOAT,price FLOAT," +
                        "comment VARCHAR(45),date DATE,IN_UT VARCHAR(45),used VARCHAR(45));");

                intent3.putExtra("message", "a");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent3);

            } catch (Exception ex) {
                System.out.println("Login fail5");
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    Logger lgr = Logger.getLogger(Version.class.getName());
                    lgr.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }

    }
    public boolean checkuser(String user){
        boolean check=false;
        try{
            con=connect();
            stmt = con.createStatement();
            String query = "select * from users where username='"+user+"'";
            rs = stmt.executeQuery(query);
            if(rs.next()==false){
                check=true;
            }

        } catch (Exception ex) {
            check=true;
            System.out.println("Login fail4");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return check;
    }
}
