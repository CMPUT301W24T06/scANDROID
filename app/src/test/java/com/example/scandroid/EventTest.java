package com.example.scandroid;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import android.location.Location;

import org.junit.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Event test cases for all methods and nested classes
 * @author Jordan Beaubien
 */
public class EventTest {

    /* --------------- *
     * MOCK ATTRIBUTES *
     * --------------- */

    ArrayList<Integer> dateValues = new ArrayList<>(Arrays.asList(2024, 4, 8, 16, 0));
    ArrayList<Double> locationValues = new ArrayList<>(Arrays.asList(53.52726211381912, -113.53023539331814));

    /* ------------ *
     * MOCK CLASSES *
     * ------------ */

    private Event.EventAnnouncement mockAnnouncement() {
        Event mockEvent = mockEvent(dateValues, locationValues);

        long announcementTime = TimeUnit.HOURS.toMillis(20) + TimeUnit.MINUTES.toMillis(24);
        mockEvent.addEventAnnouncement(
                "AnnouncementTitle",
                "AnnouncementAbout",
                new Time(announcementTime));

        return mockEvent.getEventAnnouncements().get(0);
    }

    private Event.CheckIn mockCheckIn(ArrayList<Double> locationValues) {
        long checkInTime = TimeUnit.HOURS.toMillis(12) + TimeUnit.MINUTES.toMillis(34);
        Location checkInLocation = new Location("checkInTest");
        checkInLocation.setLatitude(locationValues.get(0));  // Lat  = 53.52726211381912
        checkInLocation.setLongitude(locationValues.get(1)); // Long = -113.53023539331814

        return new Event.CheckIn(
                "1234",
                new Time(checkInTime),
                locationValues);
    }

    private Event mockEvent(ArrayList<Integer> dateValues, ArrayList<Double> locationValues) {
        Calendar projectDueDate = Calendar.getInstance();
        projectDueDate.set(
                dateValues.get(0),  // year   = 2024
                dateValues.get(1),  // month  = 4
                dateValues.get(2),  // day    = 8
                dateValues.get(3),  // hour   = 16
                dateValues.get(4)); // minute = 0

        Location projectLocation;
        projectLocation = new Location("building_ETLC");
        projectLocation.setLatitude(locationValues.get(0));  // Lat  = 53.52726211381912
        projectLocation.setLongitude(locationValues.get(1)); // Long = -113.53023539331814

        return new Event(
                "EventOrganizerID",
                "EventName",
                "EventDescription",
                projectDueDate,
                locationValues
                );
    }

    private User mockUser() {
        return new User(
            "4321",
            "Person1",
            "123-456-7890",
            "Details",
            "email@domain.com");
    }

    /* --------------------- *
     * TEST : NESTED CLASSES *
     * --------------------- */

    @Test
    public void testCheckInGetUserID() {
        Event.CheckIn mockCheckIn = mockCheckIn(locationValues);
        assertEquals("1234", mockCheckIn.getUserID());
    }

    @Test
    public void testCheckInGetCheckInTime() {
        Event.CheckIn mockCheckIn = mockCheckIn(locationValues);
        assertEquals(45240000, mockCheckIn.getCheckInTime().getTime());
    }

    @Test
    public void testCheckInGetLocation() {
        Event.CheckIn mockCheckIn = mockCheckIn(locationValues);
        ArrayList<Double> mockCheckInLocation = mockCheckIn.getCheckInLocation();
        assertEquals(locationValues.get(0), mockCheckInLocation.get(0));
        assertEquals(locationValues.get(1), mockCheckInLocation.get(1));
    }

    @Test
    public void testAnnouncementGetTitle() {
        Event.EventAnnouncement mockAnnouncement = mockAnnouncement();
        assertEquals("AnnouncementTitle", mockAnnouncement.getAnnouncementTitle());
    }

    @Test
    public void testAnnouncementsGetAbout() {
        Event.EventAnnouncement mockAnnouncement = mockAnnouncement();
        assertEquals("AnnouncementAbout", mockAnnouncement.getAnnouncementAbout());
    }

    @Test
    public void testAnnouncementGetOrganizerID() {
        Event.EventAnnouncement mockAnnouncement = mockAnnouncement();
        assertEquals("EventOrganizerID", mockAnnouncement.getAnnouncementOrganizerID());
    }

    @Test
    public void testAnnouncementGetTime() {
        Event.EventAnnouncement mockAnnouncement = mockAnnouncement();
        assertEquals(73440000, mockAnnouncement.getAnnouncementTime().getTime());
    }

    @Test
    public void testMilestoneGetThreshold() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        Event.EventMilestone mockMilestone = mockEvent.getEventMilestones().get(0);
        assertEquals(Integer.valueOf(1), mockMilestone.getThreshold());
    }

    @Test // adding attendees automatically generates new milestones
    public void testMileStoneThresholdSeries() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        Location checkInLocation = new Location("MilestoneThresholdIterationTest");
        checkInLocation.setLatitude(locationValues.get(0));
        checkInLocation.setLongitude(locationValues.get(1));
        mockEvent.addEventAttendee("Threshold1", new Time(0), locationValues);
        assertEquals(
                Integer.valueOf(2),     // next milestone threshold is 2
                mockEvent.getEventMilestones().get( // get last element from array of milestones
                        mockEvent.getEventMilestones().size() - 1).getThreshold());
        mockEvent.addEventAttendee("Threshold2", new Time(0), locationValues);
        assertEquals(
                Integer.valueOf(3),     // next milestone threshold is 3
                mockEvent.getEventMilestones().get( // get last element from array of milestones
                        mockEvent.getEventMilestones().size() - 1).getThreshold());
        mockEvent.addEventAttendee("Threshold3", new Time(0), locationValues);
        assertEquals(
                Integer.valueOf(5),     // next milestone threshold is 5
                mockEvent.getEventMilestones().get( // get last element from array of milestones
                        mockEvent.getEventMilestones().size() - 1).getThreshold());
    }

    /* -------------- *
     * TEST : METHODS *
     * -------------- */

    @Test
    public void testAddEventAnnouncement() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        int emptyAnnouncementList = mockEvent.getEventAnnouncements().size();
        mockEvent.addEventAnnouncement("A_Title", "A_About", new Time(45240000));
        assertEquals(emptyAnnouncementList + 1, mockEvent.getEventAnnouncements().size());
    }

    @Test
    public void testAddEventAttendee() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        User mockUser = mockUser();
        int emptyListSize = mockEvent.getEventAttendeeList().size();
        mockEvent.addEventAttendee(mockUser.getUserID(), new Time(45240000), locationValues);
        assertEquals(emptyListSize + 1, mockEvent.getEventAttendeeList().size());
    }

    /* -------------- *
     * TEST : GETTERS *
     * -------------- */

    @Test
    public void testGetEventAnnouncements() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        mockEvent.addEventAnnouncement("A_Title", "A_About", new Time(45240000));
        Event.EventAnnouncement addedAnnouncement = mockEvent.getEventAnnouncements().get(0);
        assertEquals("A_Title", addedAnnouncement.getAnnouncementTitle());
        assertEquals("A_About", addedAnnouncement.getAnnouncementAbout());
        assertEquals(45240000, addedAnnouncement.getAnnouncementTime().getTime());
    }

    @Test
    public void testGetEventAttendeeList() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        User mockUser = mockUser();
        mockEvent.addEventAttendee(mockUser.getUserID(), new Time(45240000), locationValues);
        Event.CheckIn addedUser = mockEvent.getEventAttendeeList().get(0);
        assertEquals("4321", addedUser.getUserID());
        assertEquals(45240000, addedUser.getCheckInTime().getTime());
        assertEquals(locationValues.get(0), addedUser.getCheckInLocation().get(0));
        assertEquals(locationValues.get(1), addedUser.getCheckInLocation().get(1));
    }

    @Test
    public void testGetEventAttendeesTotal() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        User mockUser = mockUser();
        int emptyListSize = mockEvent.getEventAttendeeList().size();
        mockEvent.addEventAttendee(mockUser.getUserID(), new Time(45240000), locationValues);
        assertEquals(Integer.valueOf(emptyListSize + 1), mockEvent.getEventAttendeesTotal());
    }

    @Test
    public void testGetEventDate() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        Calendar eventDate = mockEvent.getEventDate();
        assertEquals(2024, eventDate.get(Calendar.YEAR));
        assertEquals(4, eventDate.get(Calendar.MONTH));
        assertEquals(8, eventDate.get(Calendar.DATE));
        assertEquals(16, eventDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, eventDate.get(Calendar.MINUTE));
    }

    @Test
    public void testGetEventID() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        assertNotNull(mockEvent.getEventID());
    }

    @Test
    public void testGetEventLocation() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        ArrayList<Double> mockEventLocation = mockEvent.getEventLocation();
        assertEquals(locationValues.get(0), mockEventLocation.get(0));
        assertEquals(locationValues.get(1), mockEventLocation.get(1));
    }

    @Test
    public void testGetEventMilestones() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        assertEquals(Integer.valueOf(1), mockEvent.getEventMilestones().get(0).getThreshold());
    }

    @Test
    public void testGetEventName() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        assertEquals("EventName", mockEvent.getEventName());
    }

    @Test
    public void testGetEventOrganizerID() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        assertEquals("EventOrganizerID", mockEvent.getEventOrganizerID());
    }

    /* -------------- *
     * TEST : SETTERS *
     * -------------- */

    @Test
    public void testSetEventDate() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        Calendar initialDate = mockEvent.getEventDate();

        Calendar newDate = Calendar.getInstance();
        newDate.set(2025, 5, 9, 17, 1);

        mockEvent.setEventDate(newDate);
        Calendar updatedDate = mockEvent.getEventDate();

        assertEquals(initialDate.get(Calendar.YEAR) + 1, updatedDate.get(Calendar.YEAR));
        assertEquals(initialDate.get(Calendar.MONTH) + 1, updatedDate.get(Calendar.MONTH));
        assertEquals(initialDate.get(Calendar.DATE) + 1, updatedDate.get(Calendar.DATE));
        assertEquals(initialDate.get(Calendar.HOUR_OF_DAY) + 1, updatedDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(initialDate.get(Calendar.MINUTE) + 1, updatedDate.get(Calendar.MINUTE));
    }

    @Test
    public void testSetEventLocation() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        double randomLatitude = ThreadLocalRandom.current().nextLong(90);
        double randomLongitude = ThreadLocalRandom.current().nextLong(180);
        mockEvent.setEventLocation(new ArrayList<>(Arrays.asList(randomLatitude, randomLongitude)));
        assertEquals(randomLatitude, mockEvent.getEventLocation().get(0));
        assertEquals(randomLongitude, mockEvent.getEventLocation().get(1));
    }

    @Test
    public void testSetEventName() {
        Event mockEvent = mockEvent(dateValues, locationValues);
        String newEventName = "BetterName";
        mockEvent.setEventName(newEventName);
        assertEquals(newEventName, mockEvent.getEventName());
    }

} // end public class EventTest
