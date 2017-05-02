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
            Connection con = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/mydb", "RH9011", "RH9011");
            return con;
        } catch (SQLException ex) {
            System.out.println("Connection fail");
            return null;
        }
    }
    // input data to database
    public void input(String ip, String Item, double moms, double price, String comment,String type) throws SQLException, ClassNotFoundException, IOException {
        try {
            con = connect(ip);
            stmt = con.createStatement();
            String query = "INSERT INTO Budget (Item,Moms,Price,Comment,Date,IN_UT) VALUES ('" + Item + "', " +moms + "," + price  + ",'" + comment + "',CURDATE(),'"+type+"');";
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
            s = String.format("%-10s|%4s|%5s|%-13s|%-10s\n----------------------------------------------\n", columns.getColumnName(2), columns.getColumnName(3), columns.getColumnName(4), columns.getColumnName(5), columns.getColumnName(6));


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
                if(rs.getString(7).endsWith("UT"))
                s = s + String.format("%-10s|%4s|%5s|%-13s|%-10s\n", rs.getString(2), rs.getString(3), "-"+rs.getString(4), rs.getString(5), rs.getString(6));
                else
                    s = s + String.format("%-10s|%4s|%5s|%-13s|%-10s\n", rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
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

    public void results(String ip, Context context,String from,String to){
        try {
            con = connect(ip);
            stmt = con.createStatement();
            String query = "SELECT * FROM budget WHERE (Date BETWEEN '"+from+"' AND '"+to+"')";
            rs = stmt.executeQuery(query);
            String s="";
            double moms_in=0;
            double moms_ut = 0;
            double solt_med_moms=0;
            double kopt_med_moms=0;
            while (rs.next()) {//get first result
                if(rs.getString(7).endsWith("UT")) {
                    moms_ut += Double.parseDouble(rs.getString(3)) * Double.parseDouble(rs.getString(4));
                    kopt_med_moms+=Double.parseDouble(rs.getString(4))+moms_ut;
                }else {
                    moms_in += Double.parseDouble(rs.getString(3)) * Double.parseDouble(rs.getString(4));
                    solt_med_moms+=Double.parseDouble(rs.getString(4)) +moms_in;
                }
            }
            double moms_total = moms_in-moms_ut;
            double vinst= solt_med_moms-kopt_med_moms;
            s=String.format("%.2f,%.2f,%.2f,%.2f",solt_med_moms,kopt_med_moms,moms_total,vinst);
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
