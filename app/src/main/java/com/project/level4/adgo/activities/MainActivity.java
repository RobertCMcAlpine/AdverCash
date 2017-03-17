package com.project.level4.adgo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.project.level4.adgo.R;
import com.project.level4.adgo.adapters.FixedTabsPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends FragmentActivity {

    private BeaconManager beaconManager;
    private Region region;
    private boolean viewed;
    private boolean listening = true;

    private AlertDialog.Builder dialog;
    private LayoutInflater factory;

    public List<String> viewedAds = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewed = false;
        String tempAd = null;

        dialog = new AlertDialog.Builder(this);
        factory = LayoutInflater.from(this);



        if(savedInstanceState !=null) {
            if (savedInstanceState.containsKey("ad")) {
                tempAd = savedInstanceState.getString("ad");
                viewedAds.add(tempAd);
                viewed = true;
            }
        }

        if (tempAd == null) {
            Intent intent = getIntent();
            tempAd = intent.getStringExtra("ad");
            if (tempAd != null) {
                viewedAds.add(intent.getStringExtra("ad"));
                viewed = true;
            }
        }

        if (viewed){
            final View buyView = factory.inflate(R.layout.dialog_purchase, null);
            ImageView adImage = (ImageView) buyView.findViewById(R.id.dialog_ad);
            adImage.setImageDrawable(getResources().getDrawable(R.drawable.nike_hightop, null));
            ImageView buyImage = (ImageView) buyView.findViewById(R.id.dialog_buy_now);
            buyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("https://www.amazon.co.uk/Nike-Skateboarding-Zoom-Trainers-Black/dp/B01N9R8LP0/ref=sr_1_6?ie=UTF8&qid=1489059216&sr=8-6&keywords=zoom+dunk"));
                    startActivity(viewIntent);
                }
            });
            TextView purchaseMessage = (TextView) buyView.findViewById(R.id.buy_now_reward);
            purchaseMessage.setText("Nike SkateBoarding Zoom Trainer");
            buyImage.setImageDrawable(getResources().getDrawable(R.drawable.buy_now, null));
            dialog.setView(buyView);
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // nothing
                }
            });
            dialog.show();
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter = new FixedTabsPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        // setup advertisment estimote info here
        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), null, null);

        startBeaconManager();
        determineBeaconProximity();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.log_out:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startBeaconManager(){

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.d("Advertisement", "Enetered Beacon Region");
            }
            @Override
            public void onExitedRegion(Region region) {
                // could add an "exit" notification too if you want (-:
            }
        });
    }

    public void determineBeaconProximity(){
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    double distance = Utils.computeAccuracy(nearestBeacon);
                    Log.d("Advertisement", "DISTANCE TO AD: " + distance);

                    if (distance < 1 && viewedAds.isEmpty()){
                        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        beaconManager.stopRanging(region);
                    }
                    else if (distance > 1 && listening){
                        listening = false;
                        final View foundAdView = factory.inflate(R.layout.dialog_found_ad, null);

                        ImageView appImage = (ImageView) foundAdView.findViewById(R.id.dialog_found_image);
                        appImage.setImageDrawable(getResources().getDrawable(R.drawable.ad_finder_icon, null));

                        TextView foundAdMessage1 = (TextView) foundAdView.findViewById(R.id.dialog_found_message1);
                        foundAdMessage1.setText("Ad found in your vicinity!");

                        TextView foundAdMessage2 = (TextView) foundAdView.findViewById(R.id.dialog_found_message2);
                        foundAdMessage2.setText("Look for an Ad with the alien symbol");


                        dialog.setView(foundAdView);
                        dialog.show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart(){
        startBeaconManager();
        determineBeaconProximity();
        super.onStart();
    }

    @Override
    protected void onResume() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });

        super.onResume();
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!viewedAds.isEmpty()) {
            outState.putString("ad", viewedAds.get(0));
        }
    }

    // would map beacons in full development cycle
    private static final Map<String, List<String>> ADVERTISEMENTS_BY_BEACONS;


    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("22504:48827", new ArrayList<String>() {{
            add("Nike");
        }});
        placesByBeacons.put("648:12", new ArrayList<String>() {{
            add("Nike");
        }});
        ADVERTISEMENTS_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }
}
