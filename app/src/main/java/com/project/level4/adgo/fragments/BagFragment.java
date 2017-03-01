package com.project.level4.adgo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.project.level4.adgo.R;
import com.project.level4.adgo.adapters.AdvertisementAdapter;
import com.project.level4.adgo.utils.Advertisement;

public class BagFragment extends Fragment {

    Advertisement advertisements[] = new Advertisement[1];


    public static BagFragment newInstance() {
        BagFragment fragment = new BagFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        /**
         *  (1) get Ad prior to onStart() method.
         *  (2) create Advertisement object
         *  (3) add advertisement object to array
         *  (4) use array to instantiate adapter
         *  done
         */

        Advertisement ad = new Advertisement("nike", getResources().getDrawable(R.drawable.nikeicon, null), "20% Discount", getResources().getDrawable(R.drawable.qrcodeicon, null), 0.05);

        advertisements[0] = ad;
        // our adapter instance
        AdvertisementAdapter adapter = new AdvertisementAdapter(getContext(), R.layout.row_layout, advertisements);
        // create a new ListView, set the adapter and item click listener

        ListView listViewItems = (ListView) getView().findViewById(R.id.advertisement_list);

        listViewItems.setAdapter(adapter);
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
