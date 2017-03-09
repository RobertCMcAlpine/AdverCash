package com.project.level4.adgo.utils;

/**
 * Created by Rob on 3/2/17.
 */

public class Wallet {

    private double balance;

    public Wallet(){
        this.balance = 0.00;
    }

    public double getBalance(){
        return balance;
    }

    public void addToWallet(double value){
        balance += value;
    }

    public void withdrawBalance(){
        balance = 0;
    }
}
