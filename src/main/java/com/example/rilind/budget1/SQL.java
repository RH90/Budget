package com.example.rilind.budget1;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import junit.runner.Version;
// Hello , ihere is Saif
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Rilind on 2017-04-07.
 */
// Hello , eng saif is here :D 
public class SQL {

    Intent intent = new Intent("event");
    Context context = null;

    //saif
    public void start(String ip,String Item,double price, double moms, String comment) throws SQLException, ClassNotFoundException, IOException {
        String s;
        this.context = context;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }
        Statement stmt = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/mydb", "RH9011", "RH9011");
            stmt = con.createStatement();
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String query = "INSERT INTO Budget (Item,Price,Moms,Comment,Date) VALUES ('"+Item+"', "+price+","+moms+",'"+comment+"',CURDATE());";
            stmt.executeUpdate(query);
            /*
            ResultSetMetaData columns = rs.getMetaData();

            s = String.format("%4s | %-34s | %3s | %-10s\n", columns.getColumnName(1), columns.getColumnName(2), columns.getColumnName(3), columns.getColumnName(4));
            intent.putExtra("message", s);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            while (!MainActivity.message.equals(s)) {

            }
            */

            //System.out.printf("%4s | %-34s | %3s | %-10s\n", columns.getColumnName(1), columns.getColumnName(2), columns.getColumnName(3), columns.getColumnName(4));
            //System.out.println("------------------------------------------------------------------");

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
