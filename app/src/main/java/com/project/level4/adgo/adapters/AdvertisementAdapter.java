package com.project.level4.adgo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.level4.adgo.R;
import com.project.level4.adgo.utils.Advertisement;
import com.project.level4.adgo.utils.AdvertisementViewHolder;

import java.util.List;

/**
 * Created by Rob on 3/1/17.
 */

public class AdvertisementAdapter extends ArrayAdapter<Advertisement> {

    private Context context;
    int layoutResourceId;
    Advertisement ads[] = null;


    public AdvertisementAdapter(Context context, int resource, Advertisement[] objects) {
        super(context, resource, objects);

        this.context = context;
        this.layoutResourceId = resource;
        this.ads = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

        }

        Advertisement ad = ads[position];

        ImageView owner = (ImageView) convertView.findViewById(R.id.ad_owner_image);
        owner.setImageDrawable(ad.getAdIcon());

        ImageView qrCode = (ImageView) convertView.findViewById(R.id.qr_code_placeholder_image) ;
        qrCode.setImageDrawable(ad.getQrCode());

        TextView reward = (TextView) convertView.findViewById(R.id.reward);
        reward.setText(ad.getReward());

        TextView prize = (TextView) convertView.findViewById(R.id.cash_prize);
        prize.setText("£" + Double.toString(ad.getCashPrize()) + "added to wallet");

        return convertView;

    }
}
