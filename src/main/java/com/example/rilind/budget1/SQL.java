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
import java.sql.ResultSetMetaData;
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
    Statement stmt = null;
    ResultSet rs = null;
    Connection con = null;
    // connect to database
    public Connection connect(String ip) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {

            }try {
                Connection con = DriverManager.getConnection("jdbc:mysql://rh9011.hopto.org:3306/mydb", "RH9011", "RH9011");
                return con;
            } catch (Exception ex) {
                System.out.println("Connection fail");
                return null;
            }

    }
    // input data to database
    public void input(String ip,Context context) throws SQLException, ClassNotFoundException, IOException {
        String color="0";
        try {
            int index =index(ip,context);
            System.out.println(index);
            con = connect(ip);
            stmt = con.createStatement();

            SQLiteDatabase myDB = context.openOrCreateDatabase("Budget", MODE_PRIVATE, null);
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + "Budget" + " (id INT(11), item VARCHAR(45), moms FLOAT,price FLOAT," +
                    "comment VARCHAR(45),date DATE,IN_UT VARCHAR(45),used VARCHAR(45));");
            Cursor c = myDB.rawQuery("SELECT * FROM Budget ", null);

            String query="";
            c.moveToFirst();
            while(!c.isAfterLast()){
                int i= c.getInt(0);
                if(index<i||index==0){
                   query = "INSERT INTO Budget (id,Item,Moms,Price,Comment,Date,IN_UT,used) VALUES " +
                           "("+c.getInt(0)+",'"+c.getString(1)+"',"+c.getString(2) + ","+c.getString(3)+ ",'" +c.getString(4)+ "','"+c.getString(5)+"','"+c.getString(6)+"','"+c.getString(7)+"');";
                    stmt.addBatch(query);
                }
                c.moveToNext();
            }

            stmt.executeBatch();
            if(query.equalsIgnoreCase(""))
                color="2";
            else
                color="1";

        } catch (Exception ex) {
            System.out.println("Login fail1");
        } finally {
            try {
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
    public int index(String ip,Context context){
        int index =0;
        try {
            con = connect(ip);

            stmt = con.createStatement();
            String query = "select * from budget";
            rs = stmt.executeQuery(query);
            ResultSetMetaData columns = rs.getMetaData();

            rs.afterLast();
            if(rs.previous())
                index=Integer.parseInt(rs.getString(1));



        } catch (Exception ex) {
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
}
