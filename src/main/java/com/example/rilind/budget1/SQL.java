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
import java.sql.PreparedStatement;
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
    Intent intent5 = new Intent("getget");
    Statement stmt = null;
    PreparedStatement pstmt = null;
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
            int index = index(context);
            con = connect();
            //stmt = con.createStatement();
            pstmt= con.prepareStatement("INSERT INTO budget (Item,Moms,Price,Comment,Date,IN_UT,used,user) VALUES " +
                    "(?,?,?,?,?,?,?,?)");
            //get data from local database
            SQLiteDatabase myDB = context.openOrCreateDatabase("Budget", MODE_PRIVATE, null);
            Cursor c = myDB.rawQuery("SELECT * FROM "+user+" ", null);
            String query = "";
            c.moveToFirst();
            System.out.println(MainActivity.id);
            System.out.println("::"+index);
            //store the items to a batch
            int i = 0;
            while (!c.isAfterLast()) {
                if (index <= i || index == 0) {
                    query="1";
                    pstmt.setString(1,c.getString(1));
                    pstmt.setFloat(2,c.getFloat(2));
                    pstmt.setFloat(3,c.getFloat(3));
                    pstmt.setString(4,c.getString(4));
                    pstmt.setString(5,c.getString(5));
                    pstmt.setString(6,c.getString(6));
                    pstmt.setString(7,c.getString(7));
                    pstmt.setInt(8,MainActivity.id);
                    pstmt.execute();
                }
                i++;
                c.moveToNext();
            }
            //execute all the statements that where stored in the batch
            //stmt.executeBatch();
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
                if (pstmt != null) {
                    pstmt.close();
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
    public int index(Context context) {
        int index = 0;
        try {
            con = connect();

            pstmt = con.prepareStatement("SELECT * FROM budget where user= ?");
            pstmt.setInt(1,MainActivity.id);

            //String query = "SELECT * FROM "+user+"";
            rs = pstmt.executeQuery();

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
                if (pstmt != null) {
                    pstmt.close();
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
            pstmt=  con.prepareStatement("select * from users where username=binary ? AND password=binary ? ");
            pstmt.setString(1,user);
            pstmt.setString(2,pass);

            rs= pstmt.executeQuery();
            if(rs.next()==false){
                check="1";
            }else{
                MainActivity.id=rs.getInt(1);
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
                if (pstmt != null) {
                    pstmt.close();
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
                pstmt = con.prepareStatement("INSERT INTO users (username,password,date_added) VALUES " +
                        "(?,?,CURDATE() );");
                pstmt.setString(1,user);
                pstmt.setString(2,pass);
                pstmt.execute();
                SQLiteDatabase myDB = context.openOrCreateDatabase("Budget", MODE_PRIVATE, null);
                myDB.execSQL("CREATE TABLE IF NOT EXISTS " + user + " (id INT(11), item VARCHAR(45), moms FLOAT,price FLOAT," +
                        "comment VARCHAR(45),date DATE,IN_UT VARCHAR(45),used VARCHAR(45));");

                intent3.putExtra("message", "1");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent3);

            } catch (Exception ex) {
                System.out.println("Login fail5");
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (pstmt != null) {
                        pstmt.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    Logger lgr = Logger.getLogger(Version.class.getName());
                    lgr.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }else{
            intent3.putExtra("message", "0");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent3);
        }

    }
    public boolean checkuser(String user){
        boolean check=false;
        try{
            con=connect();
            pstmt = con.prepareStatement("select * from users where username= ?");
            pstmt.setString(1,user);
            rs = pstmt.executeQuery();
            if(rs.next()==false){
                check=true;
            }
            if(user.equalsIgnoreCase("users")){
                check=false;
            }

        } catch (Exception ex) {
            check=false;
            System.out.println("Login fail4");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
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
    //import db from remote db
    public void importdb(Context context,String user){

        try {
            con = connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from "+user+"");

            SQLiteDatabase myDB = context.openOrCreateDatabase("Budget", MODE_PRIVATE, null);
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + user + " (id INT(11), item VARCHAR(45), moms FLOAT,price FLOAT," +
                    "comment VARCHAR(45),date DATE,IN_UT VARCHAR(45),used VARCHAR(45));");
            myDB.delete(user, null, null);
            rs.first();
            while(!rs.isAfterLast()){
                myDB.execSQL("INSERT INTO "+MainActivity.username+" (id,Item,Moms,Price,Comment,Date,IN_UT,used) " +
                        "VALUES (" + rs.getString(1) + ",'" + rs.getString(2) + "'," + rs.getString(3) + "," + rs.getString(4) + ",'" +
                        rs.getString(5) + "','" + rs.getString(6) + "','" + rs.getString(7) + "','" + rs.getString(8) + "');");
                rs.next();
            }

        } catch (Exception ex) {
            System.out.println("Login fail5");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
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
    public void getitem(Context context,String serial){
        String s="";
        try {
            con = connect();
            pstmt = con.prepareStatement("select * from budget_items where serial= ? AND user= ?");
            pstmt.setString(1,serial);
            pstmt.setInt(2,MainActivity.id);
            rs = pstmt.executeQuery();

            if(rs.next()==true){
                s=rs.getString(2)+"\n"+rs.getString(3)+"\n"+rs.getString(4);
            }
            System.out.println(s);

        } catch (Exception ex) {

            System.out.println("Login fail77");
        } finally {
            try {
                intent5.putExtra("get", s);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent5);
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
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
    public void saveitem(String serial,String name,Double moms,Double price){
        try {

            System.out.println("serial::" +serial);
            con = connect();
            pstmt = con.prepareStatement("INSERT INTO budget_items (serial,name,moms,price,user) VALUES (?,?,?,?,?)");
            pstmt.setString(1,serial);
            pstmt.setString(2,name);
            pstmt.setDouble(3,moms);
            pstmt.setDouble(4,price);
            pstmt.setInt(5,MainActivity.id);
            pstmt.execute();


        } catch (Exception ex) {
            System.out.println("INSERT INTO budget_items (serial,name,moms,price,user) " +
                    "VALUES ("+serial+",'"+name+"',"+moms+","+moms+","+MainActivity.id+")");
            System.out.println("Login fail6: saveitem");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
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
