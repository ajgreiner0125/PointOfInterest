package com.example.pointofinterest2;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    //count of chosen destinations currently limited to three
    int count = 0;


    //the list of all place data taken from the
    //roadtrippers website
    ArrayList<Place> places;

    //the list of all places that are near the route
    ArrayList<Place> nearbyPlaces;


    //List of latitude and longitudes used to generate the route line
    private ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

    //arraylist that stores the locations selected by the user that they would like to visit
    ArrayList<Place> finalPlaces;

    //stores the origin and destination
    String message0;
    String message1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        places = new ArrayList<Place>();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        try {
            databaseHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            databaseHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        places = databaseHelper.grabPlaces();
    }

    private static void callFetcher(DirectionsFetcher fetcher){

        fetcher.execute("test");
    }

    private static void callFetcher2(DirectionsFetcher2 fetcher){

        fetcher.execute("test");
    }

    private static void callPointFinder(PointFinder finder){
        finder.execute("test");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Center map on fort myers airport
        LatLng FortMyersAirport = new LatLng(26.534, -81.755);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(FortMyersAirport));

        // Set a listener for marker click.
        //mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        finalPlaces = new ArrayList<Place>();


        //Get text from previous activity
        Intent intent = getIntent();
        message0 = intent.getStringExtra(MainActivity.EXTRA_MESSAGE0);
        message1 = intent.getStringExtra(MainActivity.EXTRA_MESSAGE1);

        //places = (ArrayList<Place>) intent.getSerializableExtra("places");

        message0 = message0.replace(" ", "");
        message1 = message1.replace(" ", "");

        //System.out.println("passed through intent0: " + message0);
        //System.out.println("passed through intent1: " + message1);

        DirectionsFetcher directionsFetcher = new DirectionsFetcher(message0, message1);
        callFetcher(directionsFetcher);
        try {

            //System.out.println("Wait for direction results...");
            directionsFetcher.get();

            //System.out.println("finished getting directions");

            latLngs = directionsFetcher.getList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //System.out.println("latLngs length: " + latLngs.size());
        PolylineOptions rectLine = new PolylineOptions().width(3).color(
                    Color.RED);

        for (int i = 0; i < latLngs.size(); i++) {
                rectLine.add(latLngs.get(i));
            }
        Polyline polylin = mMap.addPolyline(rectLine);


        //line drawn, add markers for places

        //System.out.println(places.size());
        PointFinder pointFinder = new PointFinder(latLngs, places);
        callPointFinder(pointFinder);
        try {

            //System.out.println("Wait for point finder results...");
            pointFinder.get();

            //System.out.println("finished getting points");

            nearbyPlaces = pointFinder.getList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Place nearbyPlace;
        Marker mark;
        for(int i = 0; i < nearbyPlaces.size(); i++){
            nearbyPlace = nearbyPlaces.get(i);
            LatLng latLng = new LatLng(nearbyPlace.lat, nearbyPlace.lng);
            mark = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(nearbyPlace.name)
                    .snippet(nearbyPlace.primaryCategory));
            mark.setTag(nearbyPlace);
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        if(count < 3) {
            Place windowPlace = (Place) marker.getTag();
            finalPlaces.add(windowPlace);
            count++;
        }
    }


    public void startRoute(View view){

        //clear all markers and the route polyLine and create a new polyline
        mMap.clear();

        //System.out.println("finalPlaces size: "+ finalPlaces.size());
        if(finalPlaces.size() == 0) {
            return;
        }
        DirectionsFetcher2 directionsFetcher2 = new DirectionsFetcher2(message0, message1, finalPlaces);
        callFetcher2(directionsFetcher2);
        try {

            //System.out.println("Wait for direction results...");
            directionsFetcher2.get();

            //System.out.println("finished getting directions");

            latLngs = directionsFetcher2.getList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //System.out.println("latLngs length: " + latLngs.size());
        PolylineOptions rectLine = new PolylineOptions().width(3).color(
                Color.RED);

        for (int i = 0; i < latLngs.size(); i++) {
            rectLine.add(latLngs.get(i));
        }
        Polyline polylin = mMap.addPolyline(rectLine);




    }
}
