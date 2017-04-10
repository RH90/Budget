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
    Context context = null;

    public  void start(Context context) throws SQLException, ClassNotFoundException, IOException {
        String s ;
        //comment
        //etwet
        this.context=context;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            s = "wwwqq";
            intent.putExtra("message", s);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            while (!MainActivity.message.equals(s)) {

            }

        }
        Statement stmt = null;
        ResultSet rs = null;
        Connection con = null;
        try {

            con = DriverManager.getConnection("jdbc:mysql://194.47.45.136:3306/world", "RH9011", "RH9011");
            stmt = con.createStatement();
            String query = "select * from city";
            rs = stmt.executeQuery(query);
            ResultSetMetaData columns = rs.getMetaData();

            s = String.format("%4s | %-34s | %3s | %-10s\n", columns.getColumnName(1), columns.getColumnName(2), columns.getColumnName(3), columns.getColumnName(4));
            intent.putExtra("message", s);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            while (!MainActivity.message.equals(s)) {

            }


            //System.out.printf("%4s | %-34s | %3s | %-10s\n", columns.getColumnName(1), columns.getColumnName(2), columns.getColumnName(3), columns.getColumnName(4));
            //System.out.println("------------------------------------------------------------------");
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

            rs.beforeFirst();
            while (rs.next()) {//get first result
                s = String.format("%1$-" + a + "s | %2$-" + b + "s | %3$-" + 11 + "s | %4$-" + d + "s\n", rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                intent.putExtra("message", s);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                while (!MainActivity.message.equals(s)) {

                }
            }

        } catch (SQLException ex) {
            System.out.println("Login fail");
        } finally {
            s = String.format("Hej");
            intent.putExtra("message", s);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            while (!MainActivity.message.equals(s)) {

            }
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
