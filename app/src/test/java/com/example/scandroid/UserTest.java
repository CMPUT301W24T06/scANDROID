package com.example.scandroid;

import android.location.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * User test cases for all methods
 * @author Moyo Dawodu
 */
public class UserTest {
    //TODO - initialize mock Event and User for testing
    ArrayList<Integer> dateValues = new ArrayList<>(Arrays.asList(2024, 12, 24, 16, 0));
    //location: Fiarmont Hotel Macdonald
    ArrayList<Double> locationValues = new ArrayList<>(Arrays.asList(53.540349083363616, -113.48952321566745));


    private User mockUser(){
        return new User(
                "6754",
                "John Doe",
                "7801234567",
                "About John Doe",
                null,
                "email@domain.com"
        );
    }
    private Event mockEvent(ArrayList<Integer> dateValues, ArrayList<Double> locationValues){
        Calendar christmasEveBallDate = Calendar.getInstance();
        christmasEveBallDate.set(
                dateValues.get(0),  // year   = 2024
                dateValues.get(1),  // month  = 12
                dateValues.get(2),  // day    = 24
                dateValues.get(3),  // hour   = 16
                dateValues.get(4)); // minute = 0

        Location ballLocation = new Location("fairmont_hotel_macdonald");
        ballLocation.setLatitude(locationValues.get(0));   // Lat  =  53.540349083363616
        ballLocation.setLongitude(locationValues.get(1));  // Long = -113.48952321566745

        return new Event(
                "EventOrganizerID",
                "Christmas-Eve Ball",
                "Balldance",
                null,
                christmasEveBallDate,
                ballLocation
        );
    }
    //TODO - Test isAdmin
    //TODO - Test addEventToEventsAttending
    //TODO - Test addEventToEventsOrganized
    //TODO - Test getters
    //TODO - Test setters

}
