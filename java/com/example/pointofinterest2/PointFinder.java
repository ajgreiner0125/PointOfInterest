package com.example.pointofinterest2;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Alex on 11/14/2016.
 */

//this class finds all the points near the route and returns them to the map
public class PointFinder extends AsyncTask<String, Integer, String> {

    //this list of latitudes and longitudes represents the route on the map
    ArrayList<LatLng> latLngs;

    //list that holds all the point of interest information from
    //the roadtrippers website
    ArrayList<Place> places;

    //all points of interest near the route
    ArrayList<Place> relevantPlaces;

    //pass the list of LatLng's that make the route and the list of all places
    public PointFinder(ArrayList<LatLng> x, ArrayList<Place> y){
        latLngs = x;
        places = y;
    }

    public ArrayList<Place> getList(){
        return relevantPlaces;
    }

    @Override
    protected String doInBackground(String... params) {

        relevantPlaces = new ArrayList<Place>();

        //get points to check distance from
        int divisor = (int) Math.floor(latLngs.size()/10);

        //the 9 points on the route that will be used to find nearby points of interest
        ArrayList<LatLng> points = new ArrayList<LatLng>();

        for(int i = 1; i < 10; i++){
            points.add(latLngs.get((i*divisor)));
            //System.out.println("for loop latLngs index: "+(i*divisor));
        }

        //System.out.println("points array size: "+points.size());

        //find what points of interest are near those points
        LatLng iterativePoint;
        Place iterativePlace;
        LatLng iterativeLatLng;
        int placesSize = places.size();
        for(int i = 0; i < points.size(); i++){
            iterativePoint = points.get(i);
            for(int j = 0; j < placesSize; j++){
                iterativePlace = places.get(j);

                iterativeLatLng = new LatLng(iterativePlace.lat, iterativePlace.lng);

                //if the distance between the route-point and
                //the point of interest is less than 2000 meters
                //add it to the relevant Places list
                if(SphericalUtil.computeDistanceBetween
                        (iterativeLatLng, iterativePoint) < 5000){
                    relevantPlaces.add(iterativePlace);
                }
            }

        }

        //all relevant places found

        return null;
    }
}
