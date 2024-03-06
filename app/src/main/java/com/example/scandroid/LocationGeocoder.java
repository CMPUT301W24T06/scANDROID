package com.example.scandroid;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * LocationGeocoder can be used to determine the latitude and longitude of locations
 * It can also determine a location based on latitude and longitude
 * @author Simon Thang
 */
public class LocationGeocoder {
    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    Context context;

    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     *Constructor for a LocationGeocoder object
     * @param context Activity's context
     */
    LocationGeocoder(Context context){
        this.context = context;
    }

    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Creates an array holding the latitude and longitude of a location
     * @param eventLocation String description of the location
     * @return
     * Return an array of 2 values, the latitude and longitude
     */
    //Source: https://stackoverflow.com/questions/9698328/how-to-get-coordinates-of-an-address-in-android
    public ArrayList<Double> addressToCoordinates(String eventLocation){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        ArrayList<Double> coords = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(eventLocation, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (addresses.size() == 0){
            return coords;
        }


        coords.add(addresses.get(0).getLatitude());
        coords.add(addresses.get(0).getLongitude());
        return coords;
    }

    /**
     * Creates a String describing the location from an array holding a latitude and longitude
     * @param coordinates An ArrayList holding the latitude and longitude
     * @return
     * Returns a String of the location
     */
    public String coordinatesToAddress(ArrayList<Double> coordinates){
        android.location.Geocoder geocoder = new android.location.Geocoder(context, Locale.getDefault());
        String eventLocation;
        try {
            eventLocation = geocoder.getFromLocation(coordinates.get(0), coordinates.get(1), 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return eventLocation;
    }

}

