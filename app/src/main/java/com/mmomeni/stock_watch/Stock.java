package com.mmomeni.stock_watch;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class Stock implements Serializable, Comparable<Stock> { //if we are going to pass this object over threads, we need to implement serializable
            //in order to be able to use Collection.sort() we have to implement comparable and the function to explain in case of sorting what need to
            //be compared to what
    private String symbol;
    private String company;
    private double price;
    private double pChange;
    private double cPercentage;


    Stock(String s, String c, double p, double pc, double cp) {
        this.symbol = s;
        this.company = c;
        this.price = p;
        this.pChange = pc;
        this.cPercentage = cp;

    }

    public String getSymbol() {
        return symbol;
    }
    public String getCompany() {
        return company;
    }
    public double getPrice() {
        return price;
    }
    public double getPChange() {
        return pChange;
    }
    public double getCPercentage() {
        return cPercentage;
    }


    @NonNull
    @Override
    public String toString() {
        return symbol + company + price + pChange + cPercentage;
    }

    @Override
    public int compareTo(Stock s) {
        return symbol.compareTo(s.getSymbol());
    }
}
