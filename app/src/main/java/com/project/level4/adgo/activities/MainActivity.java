package com.project.level4.adgo.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
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

    public List<String> viewedAds = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String tempAd = null;

        if(savedInstanceState !=null) {
            if (savedInstanceState.containsKey("ad")) {
                tempAd = savedInstanceState.getString("ad");
                viewedAds.add(tempAd);
            }
        }

        if (tempAd == null) {
            Intent intent = getIntent();
            tempAd = intent.getStringExtra("ad");
            if (tempAd != null) {
                viewedAds.add(intent.getStringExtra("ad"));
            }
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

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);
                    double distance = Utils.computeAccuracy(nearestBeacon);
                    Log.d("Advertisement", "Nearest ads: " + places);
                    Log.d("Advertisement", "DISTANCE TO AD: " + distance);

                    if (distance < 1 && viewedAds.isEmpty()){
                        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!viewedAds.isEmpty()) {
            outState.putString("ad", viewedAds.get(0));
        }
    }

    private static final Map<String, List<String>> ADVERTISEMENTS_BY_BEACONS;

    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("22504:48827", new ArrayList<String>() {{
            add("Nike");
            // read as: "Heavenly Sandwiches" is closest
            // to the beacon with major 22504 and minor 48827
            // add("Green & Green Salads");
            // "Green & Green Salads" is the next closest
            // add("Mini Panini");
            // "Mini Panini" is the furthest away
        }});
        placesByBeacons.put("648:12", new ArrayList<String>() {{
            add("Nike");
            // add("Green & Green Salads");
            // add("Heavenly Sandwiches");
        }});
        ADVERTISEMENTS_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (ADVERTISEMENTS_BY_BEACONS.containsKey(beaconKey)) {
            return ADVERTISEMENTS_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }
}
