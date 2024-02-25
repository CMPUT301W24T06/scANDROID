package com.example.scandroid;

import android.location.Location;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class EventTest {

    private Event mockEvent() {

        Calendar projectDueDate = Calendar.getInstance();
        projectDueDate.set(2024, 4, 8, 16, 0);

        Location projectLocation = new Location("building_ETLC");
        projectLocation.setLatitude(53.52726211381912);
        projectLocation.setLongitude(-113.53023539331814);

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
     * TEST : GETTERS *
     * -------------- */
    
    // testGetEventAnnouncements
    // testGetEventAttendeeList
    // testGetEventAttendeeTotal

    @Test
    public void testGetEventDate() {

    }

    // testGetEventLocation
    // testGetEventOrganizerID
    // testGetEventPosterImage

}
