package com.example.scandroid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * User test cases for all methods
 * @author Moyo Dawodu
 */
public class UserTest {
    //TODO - initialize mock Event and User for testing
    ArrayList<Integer> dateValues = new ArrayList<>(Arrays.asList(2024, 12, 24, 16, 0));
    //location: Fairmont Hotel Macdonald
    ArrayList<Double> locationValues = new ArrayList<>(Arrays.asList(53.540349083363616, -113.48952321566745));


    private User mockUser(){
        return new User(
                "6754",
                "John Doe",
                "7801234567",
                "About John Doe",
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


        return new Event(
                "EventOrganizerID",
                "Christmas-Eve Ball",
                "Balldance",
                null,
                christmasEveBallDate,
                locationValues
        );
    }
    /* -------------- *
     * TEST : METHODS *
     * -------------- */
    //TODO - Test isAdmin
    @Test
    public void testIsAdmin(){
        User mockUser = mockUser();
        assertFalse(mockUser.isAdmin("UserAdminKey"));
    }
    //TODO - Test addEventToEventsAttending
    @Test
    public void testAddEventToEventsAttending(){
        Event mockEvent = mockEvent(dateValues,locationValues);
        String eventID = mockEvent.getEventID();
        User mockUser = mockUser();
        mockUser.addEventToEventsAttending(eventID);

        assertEquals(eventID,mockUser.getEventsAttending().get(0));
        assertEquals(1, mockUser.getEventsAttending().size());
    }
    //TODO - Test addEventToEventsOrganized
    @Test
    public void testAddEventToEventsOrganized(){
        Event mockEvent = mockEvent(dateValues,locationValues);
        String eventID = mockEvent.getEventID();
        User mockUser = mockUser();
        mockUser.addEventToEventsOrganized(eventID);

        assertEquals(eventID,mockUser.getEventsOrganized().get(0));
        assertEquals(mockUser.getEventsOrganized().size(),1);
    }

    /* -------------- *
     * TEST : GETTERS *
     * -------------- */
    //TODO - Test getters
    @Test
    public void testGetUserID(){
        User mockUser = mockUser();
        assertEquals("6754", mockUser.getUserID());
    }
    @Test
    public void testGetUserName(){
        User mockUser = mockUser();
        assertEquals("John Doe", mockUser.getUserName());
    }

    @Test
    public void testGetUserEmail(){
        User mockUser = mockUser();
        assertEquals("email@domain.com",mockUser.getUserEmail());
    }

    @Test
    public void testGetUSerPhoneNumber(){
        User mockUser = mockUser();
        assertEquals("7801234567",mockUser.getUserPhoneNumber());
    }
    @Test
    public void testGetUserAboutMe(){
        User mockUser = mockUser();
        assertEquals("About John Doe", mockUser.getUserAboutMe());
    }
    //TODO - test event UUIDS remain constant and change accordingly within the list of events
    @Test
    public void testGetEventsAttending(){
        User mockUser = mockUser();
        ArrayList<String> shouldBeEmpty = mockUser.getEventsAttending();
        assertTrue(shouldBeEmpty.isEmpty());
    }
    //TODO - test that event UUIDS remain constant and change accordingly within this list of events
    @Test
    public void testGetNotifiedBy(){
        User mock = mockUser();
        ArrayList<String> shouldBeEmpty = mock.getNotifiedBy();
        assertTrue((shouldBeEmpty.isEmpty()));
    }
    //TODO - test event UUIDS remain constant and change accordingly within the list of events
    @Test
    public void testGetEventsOrganized(){
        User mockUser = mockUser();
        ArrayList<String> shouldBeEmpty = mockUser.getEventsOrganized();
        assertTrue(shouldBeEmpty.isEmpty());
    }
    //TODO - Test getTimesAttended method. Need to have access to event uuid.
    @Test
    public void testGetTimesAttended(){
        Event mockEvent = mockEvent(dateValues,locationValues);
        String eventID = mockEvent.getEventID();
        User mockUser = mockUser();
        Integer times = 0;
        mockUser.addEventToEventsAttending(eventID);

        assertEquals(times,mockUser.getTimesAttended(eventID));

    }


    /* -------------- *
     * TEST : SETTERS *
     * -------------- */
    //TODO - Test setters
    @Test
    public void testSetUserName(){

    }
    @Test
    public void testSetUserEmail(){

    }
    @Test
    public void testSetUserPhoneNumber(){

    }
    @Test
    public void testSetUserAboutMe(){

    }
    @Test
    public void testSetEventsAttending(){

    }
    @Test
    public void testSetEventsOrganized(){

    }
    @Test
    public void testSetNotifiedBy(){

    }
}
