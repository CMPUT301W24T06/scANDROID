package com.example.scandroid;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import android.content.Context;
import android.location.Location;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Testcases for DBAccessor.
 * Tests Event and User methods.
 */
public class DBAFirestoreTest {

    // Source: https://firebase.google.com/docs/emulator-suite/connect_firestore
    // Source: https://chat.openai.com/share/d004e660-2cea-4ac3-8831-1e384d6c71ac

    public FirebaseFirestore firestore;
    public CollectionReference EventRef;
    public CollectionReference UserRef;
    public DBAccessor dbA;
    public ArrayList<Integer> dateValues = new ArrayList<>(Arrays.asList(2024, 4, 8, 16, 0));
    public ArrayList<Double> locationValues = new ArrayList<>(Arrays.asList(53.52726211381912, -113.53023539331814));

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
        projectLocation.setLongitude(1.1234456);
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

    @Before
    public void setUp() {
        // Initialize Firebase in the testing environment
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);

        // tell firestore to use emulator for test run
        // ** ensure firestore emulator:start has been called if testing locally
        this.firestore = FirebaseFirestore.getInstance();
        this.firestore.useEmulator("localhost", 8080);

        // create DBAccessor after emulator has been set
        this.dbA = new DBAccessor();

        // setup collection references for two classes being tested
        this.EventRef = this.firestore.collection("Events");
        this.UserRef = this.firestore.collection("Users");
    }

    @After
    public void tearDown() {
        // Shutdown connections to the Firestore emulator
        firestore.terminate();
    }

    @Test
    public void testWriteEventToFirestore() throws InterruptedException {
        // create Event object to test with
        Event mockEvent = mockEvent(this.dateValues, this.locationValues);

        // initialize wait to ensure database write takes place before testing
        CountDownLatch latch = new CountDownLatch(1);

        // store Event in database and give time to take place
        this.dbA.storeEvent(mockEvent);
        latch.await(1, TimeUnit.SECONDS);

        // set location to save accessedEvent and get accessedEvent
        final Event[] recievedEvent = new Event[1];
        this.dbA.accessEvent(mockEvent.getEventID(), event -> {
            if (event != null) {
                recievedEvent[0] = event;
            }
        });

        // after delay to ensure event access, assert attributes
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(mockEvent.getEventID(), recievedEvent[0].getEventID());
        assertEquals(mockEvent.getEventName(), recievedEvent[0].getEventName());
        assertEquals(mockEvent.getEventDescription(), recievedEvent[0].getEventDescription());

    } // end public void testWriteEventToFirestore

    @Test
    public void testRestoreEventFields() throws InterruptedException {
        // create Event object to test with
        Event mockEvent = mockEvent(this.dateValues, this.locationValues);

        // add a checkIn and an announcement to the Event
        User mockUser = mockUser();
        mockEvent.addEventAnnouncement("A_Title", "A_About", new Time(45240000));
        mockEvent.addEventAttendee(mockUser.getUserID(), new Time(45240000), locationValues);

        // initialize wait to ensure database write takes place before testing
        CountDownLatch latch = new CountDownLatch(1);

        // store Event in database and give time to take place
        this.dbA.storeEvent(mockEvent);
        latch.await(1, TimeUnit.SECONDS);

        // set location to save accessedEvent and get accessedEvent
        final Event[] recievedEvent = new Event[1];
        this.dbA.accessEvent(mockEvent.getEventID(), event -> {
            if (event != null) {
                recievedEvent[0] = event;
            }
        });

        // after delay to ensure event access, assert attributes
        latch.await(1, TimeUnit.SECONDS);

        // remaining simple untested fields
        assertEquals(mockEvent.getEventLocation(), recievedEvent[0].getEventLocation());
        assertEquals(mockEvent.getEventOrganizerID(), recievedEvent[0].getEventOrganizerID());

        // milestone fields
        assertEquals(mockEvent.getEventMilestoneSeries(), recievedEvent[0].getEventMilestoneSeries());
        assertEquals(
                mockEvent.getEventMilestones().get(0),
                recievedEvent[0].getEventMilestones().get(0));

        // date fields
        Calendar originalDate = mockEvent.getEventDate();
        Calendar restoredDate = recievedEvent[0].getEventDate();
        assertEquals(originalDate.get(Calendar.YEAR), restoredDate.get(Calendar.YEAR));
        assertEquals(originalDate.get(Calendar.MONTH), restoredDate.get(Calendar.MONTH));
        assertEquals(originalDate.get(Calendar.DAY_OF_MONTH), restoredDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(originalDate.get(Calendar.HOUR_OF_DAY), restoredDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(originalDate.get(Calendar.MINUTE), restoredDate.get(Calendar.MINUTE));

        // announcement fields
        ArrayList<Event.EventAnnouncement> originalAnnouncements = mockEvent.getEventAnnouncements();
        ArrayList<Event.EventAnnouncement> restoredAnnouncements = recievedEvent[0].getEventAnnouncements();
        assertEquals(originalAnnouncements.size(), restoredAnnouncements.size());
        assertEquals(
                originalAnnouncements.get(0).getAnnouncementTitle(),
                restoredAnnouncements.get(0).getAnnouncementTitle());
        assertEquals(
                originalAnnouncements.get(0).getAnnouncementAbout(),
                restoredAnnouncements.get(0).getAnnouncementAbout());
        assertEquals(
                originalAnnouncements.get(0).getAnnouncementTime(),
                restoredAnnouncements.get(0).getAnnouncementTime());
        assertEquals(
                originalAnnouncements.get(0).getAnnouncementOrganizerID(),
                restoredAnnouncements.get(0).getAnnouncementOrganizerID());

        // attendee fields
        ArrayList<Event.CheckIn> originalCheckIns = mockEvent.getEventAttendeeList();
        ArrayList<Event.CheckIn> restoredCheckIns = recievedEvent[0].getEventAttendeeList();
        assertEquals(originalCheckIns.size(), restoredCheckIns.size());
        assertEquals(
                originalCheckIns.get(0).getUserID(),
                restoredCheckIns.get(0).getUserID());
        assertEquals(
                originalCheckIns.get(0).getCheckInTime(),
                restoredCheckIns.get(0).getCheckInTime());
        assertEquals(
                originalCheckIns.get(0).getCheckInLocation(),
                restoredCheckIns.get(0).getCheckInLocation());

    } // end public void testRestoreEventFields

    @Test
    public void testGetEventIDs() throws InterruptedException {
        // create Event object to test with
        Event mockEvent = mockEvent(this.dateValues, this.locationValues);

        // initialize wait to ensure database write takes place before testing
        CountDownLatch latch = new CountDownLatch(1);

        // store Event in database and give time to take place
        this.dbA.storeEvent(mockEvent);
        latch.await(1, TimeUnit.SECONDS);

        // get the current list of eventID's in the database
        final ArrayList<String> receivedEventIDs = new ArrayList<>();
        this.dbA.getAllEventReferences(eventIDList -> {
            if (eventIDList != null) {
                receivedEventIDs.addAll(eventIDList);
            }
        });

        // verify that the recently added Event is listed
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(receivedEventIDs.contains(mockEvent.getEventID()));

    } // end public void testGetEventIDs


    @Test
    public void testDeleteEvent() throws InterruptedException {
        // create Event object to test with
        Event mockEvent = mockEvent(this.dateValues, this.locationValues);

        // initialize wait to ensure database write takes place before testing
        CountDownLatch latch = new CountDownLatch(1);

        // store Event in database and give time to take place
        this.dbA.storeEvent(mockEvent);
        latch.await(1, TimeUnit.SECONDS);

        // get the current list of eventID's in the database
        final ArrayList<String> receivedAddEventIDs = new ArrayList<>();
        this.dbA.getAllEventReferences(eventIDList -> {
            if (eventIDList != null) {
                receivedAddEventIDs.addAll(eventIDList);
            }
        });

        // verify that the recently added Event is listed
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(receivedAddEventIDs.contains(mockEvent.getEventID()));

        // remove Event from list and verify deletion
        this.dbA.deleteEvent(mockEvent.getEventID());
        latch.await(1, TimeUnit.SECONDS);

        // get the current list of eventID's in the database
        final ArrayList<String> receivedDelEventIDs = new ArrayList<>();
        this.dbA.getAllEventReferences(eventIDList -> {
            if (eventIDList != null) {
                receivedDelEventIDs.addAll(eventIDList);
            }
        });

        // verify that the recently deleted Event is not listed
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(receivedAddEventIDs.size() - 1, receivedDelEventIDs.size());

    } // end public void testDeleteEvent

    @Test
    public void testWriteUserToFirestore() throws InterruptedException {
        // create User object to test with
        User mockUser = mockUser();

        // initialize wait to ensure database write takes place before testing
        CountDownLatch latch = new CountDownLatch(1);

        // store User in database and give time to take place
        this.dbA.storeUser(mockUser);
        latch.await(1, TimeUnit.SECONDS);

        // set location to save accessedUser and get accessedUser
        final User[] recievedUser = new User[1];
        this.dbA.accessUser(mockUser.getUserID(), user -> {
            if (user != null) {
                recievedUser[0] = user;
            }
        });

        // after delay to ensure user access, assert attributes
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(mockUser.getUserID(), recievedUser[0].getUserID());
        assertEquals(mockUser.getUserName(), recievedUser[0].getUserName());
        assertEquals(mockUser.getUserPhoneNumber(), recievedUser[0].getUserPhoneNumber());
        assertEquals(mockUser.getUserAboutMe(), recievedUser[0].getUserAboutMe());
        assertEquals(mockUser.getUserEmail(), recievedUser[0].getUserEmail());

    } // end public void testWriteUserToFirestore

    @Test
    public void testGetUserIDs() throws InterruptedException {
        // create User object to test with
        User mockUser = mockUser();

        // initialize wait to ensure database write takes place before testing
        CountDownLatch latch = new CountDownLatch(1);

        // store User in database and give time to take place
        this.dbA.storeUser(mockUser);
        latch.await(1, TimeUnit.SECONDS);

        // get the current list of userID's in the database
        final ArrayList<String> receivedUserIDs = new ArrayList<>();
        this.dbA.getAllUserReferences(userIDList -> {
            if (userIDList != null) {
                receivedUserIDs.addAll(userIDList);
            }
        });

        // verify that the recently added User is listed
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(receivedUserIDs.contains(mockUser.getUserID()));

    } // end public void testGetEventIDs

    @Test
    public void testDeleteUser() throws InterruptedException {
        // create User object to test with
        User mockUser = mockUser();

        // initialize wait to ensure database write takes place before testing
        CountDownLatch latch = new CountDownLatch(1);

        // store User in database and give time to take place
        this.dbA.storeUser(mockUser);
        latch.await(1, TimeUnit.SECONDS);

        // get the current list of userID's in the database
        final ArrayList<String> receivedAddUserIDs = new ArrayList<>();
        this.dbA.getAllUserReferences(userIDList -> {
            if (userIDList != null) {
                receivedAddUserIDs.addAll(userIDList);
            }
        });

        // verify that the recently added Event is listed
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(receivedAddUserIDs.contains(mockUser.getUserID()));

        // remove Event from list and verify deletion
        this.dbA.deleteUser(mockUser.getUserID());
        latch.await(1, TimeUnit.SECONDS);

        // get the current list of eventID's in the database
        final ArrayList<String> receivedDelUserIDs = new ArrayList<>();
        this.dbA.getAllUserReferences(userIDList -> {
            if (userIDList != null) {
                receivedDelUserIDs.addAll(userIDList);
            }
        });

        // verify that the recently deleted Event is not listed
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(0, receivedDelUserIDs.size());

    } // end public void testDeleteUser

} // end public class DBAFirestoreTest
