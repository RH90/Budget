package com.example.rilind.budget1;

import android.content.Context;
import android.content.Intent;
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

/**
 * Created by Rilind on 2017-04-07.
 */
    public class SQL {

    Intent intent = new Intent("event");
    Statement stmt = null;
    ResultSet rs = null;
    Connection con = null;
    // connect to database
    public Connection connect(String ip) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/mydb", "RH9011", "RH9011");
            return con;
        } catch (SQLException ex) {
            System.out.println("Login fail");
            return null;
        }
    }
    // input data to database
    public void input(String ip, String Item, double price, double moms, String comment) throws SQLException, ClassNotFoundException, IOException {
        try {
            con = connect(ip);
            stmt = con.createStatement();
            String query = "INSERT INTO Budget (Item,Price,Moms,Comment,Date) VALUES ('" + Item + "', " + price + "," + moms + ",'" + comment + "',CURDATE());";
            stmt.executeUpdate(query);

        } catch (SQLException ex) {
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
    }
    // read data from database
    public void read(String ip, Context context) {
        try {
            con = connect(ip);

            stmt = con.createStatement();
            String query = "select * from budget";
            rs = stmt.executeQuery(query);
            ResultSetMetaData columns = rs.getMetaData();
            String s;
            s = String.format("%-10s|%5s|%4s|%-13s|%-10s\n----------------------------------------------\n", columns.getColumnName(2), columns.getColumnName(3), columns.getColumnName(4), columns.getColumnName(5), columns.getColumnName(6));


            //}

            /*
            int a = 0, b = 0, c = 0, d = 0;
            while (rs.next()) {
                if (rs.getString(1).length() > a) {
                    a = rs.getString(1).length();
                }
                if (rs.getString(2).length() > b) {
                    b = rs.getString(2).length();
                }
                if (rs.getString(3).length() > c) {
                    c = rs.getString(3).length();
                }
                if (rs.getString(4).length() > d) {
                    d = rs.getString(4).length();
                }
            }
            */
            rs.afterLast();
            while (rs.previous()) {//get first result

                s = s + String.format("%-10s|%5s|%4s|%-13s|%-10s\n", rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));

            }
            // send string to textfield
            intent.putExtra("message", s);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } catch (SQLException ex) {
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


    }


}
