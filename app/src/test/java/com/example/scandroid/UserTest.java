package com.example.scandroid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
                christmasEveBallDate,
                locationValues
        );
    }
    /* -------------- *
     * TEST : METHODS *
     * -------------- */
    @Test
    public void testIsAdmin(){
        User mockUser = mockUser();
        //assertFalse(mockUser.isAdmin("UserAdminKey"));
    }
    @Test
    public void testAddEventToEventsAttending(){
        Event mockEvent = mockEvent(dateValues,locationValues);
        String eventID = mockEvent.getEventID();
        User mockUser = mockUser();
        mockUser.addEventToEventsAttending(eventID);

        assertEquals(eventID,mockUser.getEventsAttending().get(0));
        assertEquals(1, mockUser.getEventsAttending().size());
    }
    @Test
    public void testAddEventToEventsOrganized(){
        Event mockEvent = mockEvent(dateValues,locationValues);
        String eventID = mockEvent.getEventID();
        User mockUser = mockUser();
        mockUser.addEventToEventsOrganized(eventID);

        assertEquals(eventID,mockUser.getEventsOrganized().get(0));
        assertEquals(1,mockUser.getEventsOrganized().size());
    }
    @Test
    public void testAddEventToNotifiedBy(){
        Event mockEvent = mockEvent(dateValues,locationValues);
        String eventID = mockEvent.getEventID();
        User mockUser = mockUser();

        mockUser.addEventToNotifiedBy(eventID);
        assertEquals(eventID,mockUser.getNotifiedBy().get(0));
        assertEquals(1, mockUser.getNotifiedBy().size());
    }

    /* -------------- *
     * TEST : GETTERS *
     * -------------- */
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
    public void testGetUserPhoneNumber(){
        User mockUser = mockUser();
        assertEquals("7801234567",mockUser.getUserPhoneNumber());
    }
    @Test
    public void testGetUserAboutMe(){
        User mockUser = mockUser();
        assertEquals("About John Doe", mockUser.getUserAboutMe());
    }
    @Test
    public void testGetEventsAttending(){
        User mockUser = mockUser();
        ArrayList<String> shouldBeEmpty = mockUser.getEventsAttending();
        assertTrue(shouldBeEmpty.isEmpty());

        Event mock = mockEvent(dateValues,locationValues);
        String id = mock.getEventID();
        mockUser.addEventToEventsAttending(id);
        assertEquals(id,mockUser.getEventsAttending().get(0));
        assertEquals(1,mockUser.getEventsAttending().size());
    }
    @Test
    public void testGetNotifiedBy(){
        User mock = mockUser();
        ArrayList<String> shouldBeEmpty = mock.getNotifiedBy();
        assertTrue((shouldBeEmpty.isEmpty()));

        Event mockEvent = mockEvent(dateValues,locationValues);
        String id = mockEvent.getEventID();
        mock.addEventToNotifiedBy(id);
        assertEquals(id,mock.getNotifiedBy().get(0));
        assertEquals(1,mock.getNotifiedBy().size());
    }
    @Test
    public void testGetEventsOrganized(){
        User mockUser = mockUser();
        ArrayList<String> shouldBeEmpty = mockUser.getEventsOrganized();
        assertTrue(shouldBeEmpty.isEmpty());

        Event mock = mockEvent(dateValues,locationValues);
        String id = mock.getEventID();
        mockUser.addEventToEventsOrganized(id);
        assertEquals(id,mockUser.getEventsOrganized().get(0));
        assertEquals(1,mockUser.getEventsOrganized().size());
    }
    @Test
    public void testGetTimesAttended(){
        Event mockEvent = mockEvent(dateValues,locationValues);
        String eventID = mockEvent.getEventID();
        User mockUser = mockUser();
        Integer times = 1;
        mockUser.addEventToEventsAttending(eventID);

        assertEquals(times,mockUser.getTimesAttended(eventID));

    }
    @Test
    public void testGetProfilePictureURL(){
        User mockUser = mockUser();
        assertNull(mockUser.getProfilePictureUrl());
    }


    /* -------------- *
     * TEST : SETTERS *
     * -------------- */
    //TODO - any more setters to test?
    @Test
    public void testSetUserName(){
        User mock = mockUser();
        mock.setUserName("Cinderella");
        assertEquals("Cinderella",mock.getUserName());
    }
    @Test
    public void testSetUserEmail(){
        User mock = mockUser();
        mock.setUserEmail("cindy@gmail.com");
        assertEquals("cindy@gmail.com",mock.getUserEmail());
    }
    @Test
    public void testSetUserPhoneNumber(){
        User mock = mockUser();
        mock.setUserPhoneNumber("123-456-9987");
        assertEquals("123-456-9987",mock.getUserPhoneNumber());
    }
    @Test
    public void testSetUserAboutMe(){
        User mock = mockUser();
        mock.setUserAboutMe("My name is Cinderella.");
        assertEquals("My name is Cinderella.",mock.getUserAboutMe());
    }
    @Test
    public void testSetProfilePictureURL(){
        User mock = mockUser();
        String newURl = "mydirectory/mygallery/photo";
        mock.setProfilePictureUrl(newURl);
        assertEquals(newURl,mock.getProfilePictureUrl());
    }

}
