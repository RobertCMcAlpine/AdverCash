package com.project.level4.adgo.utils;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Rob on 3/1/17.
 */

public class AdvertisementViewHolder {
    private TextView prize;
    private TextView reward;
    private ImageView adImage;
    private ImageView qrCode;

    public AdvertisementViewHolder(TextView prize, TextView reward, ImageView adImage, ImageView qrCode) {
        this.prize = prize;
        this.reward = reward;
        this.adImage = adImage;
        this.qrCode = qrCode;
    }

    public TextView getPrize() {
        return prize;
    }

    public void setPrize(TextView prize) {
        this.prize = prize;
    }

    public TextView getReward() {
        return reward;
    }

    public void setReward(TextView reward) {
        this.reward = reward;
    }

    public ImageView getAdImage() {
        return adImage;
    }

    public void setAdImage(ImageView adImage) {
        this.adImage = adImage;
    }

    public ImageView getQrCode() {
        return qrCode;
    }

    public void setQrCode(ImageView qrCode) {
        this.qrCode = qrCode;
    }
}
