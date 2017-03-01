package com.project.level4.adgo.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by Rob on 2/28/17.
 */

public class Advertisement {

    private String adOwner;
    private Drawable adIcon;
    private double cashPrize;
    private String reward;
    private Drawable qrCode;

    public Advertisement(String adOwner, Drawable adIcon, String reward, Drawable qrCode, double cashPrize){
        this.adOwner = adOwner;
        this.adIcon = adIcon;
        this.cashPrize = cashPrize;
        this.reward = reward;
        this.qrCode = qrCode;
    }

    public Drawable getAdIcon() {
        return adIcon;
    }

    public void setAdIcon(Drawable adIcon) {
        this.adIcon = adIcon;
    }

    public double getCashPrize() {
        return cashPrize;
    }

    public void setCashPrize(double cashPrize) {
        this.cashPrize = cashPrize;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public Drawable getQrCode() {
        return qrCode;
    }

    public void setQrCode(Drawable qrCode) {
        this.qrCode = qrCode;
    }

    public String getAdOwner() {

        return adOwner;
    }

    public void setAdOwner(String adOwner) {
        this.adOwner = adOwner;
    }
}
