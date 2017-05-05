package com.example.rilind.budget1;

/**
 * Created by Rilind on 2017-05-04.
 */

public class Item implements java.io.Serializable{
    private int id;
    private String item;
    private double moms;
    private double price;
    private String comment;
    private String date;
    private String IN_UT;
    private String used;

    Item(int id, String item, double moms,double price,String comment,String date,String IN_UT,String used){
        this.id=id;
        this.item=item;
        this.moms=moms;
        this.price=price;
        this.comment=comment;
        this.date=date;
        this.IN_UT=IN_UT;
        this.used=used;

    }
    public String getItem() {
        return item;
    }

    public int getId() {
        return id;
    }

    public double getMoms() {
        return moms;
    }

    public double getPrice() {
        return price;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getIN_UT() {
        return IN_UT;
    }

    public String getUsed() {
        return used;
    }
}
