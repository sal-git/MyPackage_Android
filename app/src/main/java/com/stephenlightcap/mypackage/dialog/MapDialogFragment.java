package com.stephenlightcap.mypackage.dialog;


import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.stephenlightcap.mypackage.R;
import com.stephenlightcap.mypackage.model.TrackingList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Germex on 4/20/2017.
 */

public class MapDialogFragment extends DialogFragment implements OnMapReadyCallback {

    GoogleMap mMap;
    ArrayList<TrackingList> list;
    ArrayList<TrackingList> defaultValue = new ArrayList<>();
    ArrayList<LatLng> ll = new ArrayList<>();

    public MapDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_map, container, false);
        //AIzaSyCevbNtBkUp8aoPa-yUBLL1NSvcY2ik5WQ
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);

        list = getArguments().getParcelableArrayList("list");

        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        for (TrackingList ms : list) {
            Log.d("print out", "" + ms.getMessage());
            getLatLng(ms.getMessage());
        }

//        LatLng latLng = new LatLng(37.7688472,-122.4130859);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mMap.addMarker(markerOptions);

        PolylineOptions line = new PolylineOptions();
        MarkerOptions newMarker = new MarkerOptions();
        for (LatLng latLng : ll) {
            Log.d("size after: ", "" + ll.size());
            line.add(latLng);
            Log.d("position", "this is a position:" + latLng);
            mMap.addMarker(new MarkerOptions().position(latLng).title("place").snippet("some desc").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
//            newMarker.position(latLng);
//            newMarker.title("place");
//            newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//            mMap.addMarker(newMarker);
        }

        line.width(20).color(Color.BLUE);

//        PolylineOptions line=
//                new PolylineOptions().add(new LatLng(40.70686417491799,
//                                -74.01572942733765),
//                        new LatLng(40.76866299974387,
//                                -73.98268461227417),
//                        new LatLng(40.765136435316755,
//                                -73.97989511489868),
//                        new LatLng(40.748963847316034,
//                                -73.96807193756104))
//                        .width(50).color(Color.RED);

        mMap.addPolyline(line);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll.get(ll.size() - 1)));
        ll.clear();


    }

    private void getLatLng(String address) {

        if (Geocoder.isPresent()) {
            try {
                String location = address;
                Geocoder gc = new Geocoder(getContext());
                List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                //ll = new ArrayList<>(addresses.size()); // A list to save the coordinates if they are available


//                for (int i = 0; i < addresses.size(); i++) {
//                    if (addresses.get(i).hasLatitude() && addresses.get(i).hasLongitude()) {
//                        ll.add(i, new LatLng(addresses.get(i).getLatitude(), addresses.get(i).getLongitude()));
//                    }
//                }

                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        Log.d("lanlng", "" + a.getLatitude() + "  " + a.getLongitude());
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));

                        Log.d("size making: ", "" + ll.size());
                    }
                }
            } catch (IOException e) {
                // handle the exception
            }
        }
    }
}
