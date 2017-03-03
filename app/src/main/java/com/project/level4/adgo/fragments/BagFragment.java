package com.project.level4.adgo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.project.level4.adgo.R;
import com.project.level4.adgo.adapters.AdvertisementAdapter;
import com.project.level4.adgo.utils.Advertisement;
import com.project.level4.adgo.utils.Wallet;

public class BagFragment extends Fragment {

    private ImageView walletIcon;
    private TextView message;
    private TextView balance;

    private Advertisement advertisements[] = new Advertisement[1];
    private Wallet wallet;


    public static BagFragment newInstance() {
        BagFragment fragment = new BagFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wallet = new Wallet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bag, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();

        walletIcon = (ImageView) getView().findViewById(R.id.wallet_icon);
        message = (TextView) getView().findViewById(R.id.balance_message);
        balance = (TextView) getView().findViewById(R.id.balance);

        walletIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_balance_wallet_black_48dp, null));

        balance.setText(String.format( "%.2f", wallet.getBalance()));

        /**
         *  (1) get Ad prior to onStart() method.
         *  (2) create Advertisement object
         *  (3) add advertisement object to array
         *  (4) use array to instantiate adapter
         *  done
         */

        Advertisement ad = new Advertisement("nike", getResources().getDrawable(R.drawable.nikeicon, null), "20% Discount in store", getResources().getDrawable(R.drawable.qrcodeicon, null), 0.05);

        advertisements[0] = ad;
        // our adapter instance
        AdvertisementAdapter adapter = new AdvertisementAdapter(getContext(), R.layout.row_layout, advertisements);
        // create a new ListView, set the adapter and item click listener

        ListView listViewItems = (ListView) getView().findViewById(R.id.advertisement_list);
        listViewItems.setAdapter(adapter);
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder qrDialog = new AlertDialog.Builder(getContext());
                LayoutInflater factory = LayoutInflater.from(getContext());
                final View dialogView = factory.inflate(R.layout.dialog_layout, null);

                ImageView dialogAdImage = (ImageView) dialogView.findViewById(R.id.dialog_ad_image);
                TextView dialogAdMessage = (TextView) dialogView.findViewById(R.id.dialog_reward_message);

                dialogAdImage.setImageDrawable(advertisements[position].getAdIcon());
                dialogAdMessage.setText(advertisements[position].getReward());

                qrDialog.setView(dialogView);
                qrDialog.show();
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
