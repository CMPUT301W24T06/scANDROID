package com.example.scandroid;

import static junit.framework.TestCase.assertEquals;

import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Event test cases for all methods and nested classes
 * @author Jordan Beaubien
 */
public class EventTest {

    ArrayList<Integer> dateValues = new ArrayList<>(Arrays.asList(2024, 4, 8, 16, 0));
    ArrayList<Double> locationValues = new ArrayList<>(Arrays.asList(53.52726211381912, -113.53023539331814));

    private Event mockEvent(ArrayList<Integer> dateValues, ArrayList<Double> locationValues) {
        Calendar projectDueDate = Calendar.getInstance();
        projectDueDate.set(
                dateValues.get(0),  // year   = 2024
                dateValues.get(1),  // month  = 4
                dateValues.get(2),  // day    = 8
                dateValues.get(3),  // hour   = 16
                dateValues.get(4)); // minute = 0

        Location projectLocation = new Location("building_ETLC");
        projectLocation.setLatitude(locationValues.get(0));  // Lat  = 53.52726211381912
        projectLocation.setLongitude(locationValues.get(1)); // Long = -113.53023539331814

        return new Event(
                "OrganizerID",
                "EventName",
                "EventDescription",
                null,
                projectDueDate,
                projectLocation
                );
    }

    /* -------------- *
     * TEST : METHODS *
     * -------------- */

    // TODO addEventAttendee
    // TODO addEventMilestone

    /* -------------- *
     * TEST : GETTERS *
     * -------------- */

    // TODO testGetEventAnnouncements
    // TODO testGetEventAttendeeList
    // TODO testGetEventAttendeeTotal

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

    // TODO testGetEventLocation
    // TODO testGetEventMilestones
    // TODO testGetEventName
    // TODO testGetEventOrganizerID
    // TODO testGetEventPosterImage

    /* -------------- *
     * TEST : SETTERS *
     * -------------- */

    // TODO testSetEventDate
    // TODO testSetEventLocation
    // TODO testSetEventName
    // TODO testSetEventPosterImage

    /* --------------------- *
     * TEST : NESTED CLASSES *
     * --------------------- */

    // TODO class CheckIin (getUserID, getCheckInTime, getCheckInLocation)
    // TODO class EventAnnouncement (getAnnouncementTitle, getAnnouncementAbout, getAnnouncementOrganizer, getAnnouncementTime)
    // TODO class EventMilestone (getThreshold)

}
