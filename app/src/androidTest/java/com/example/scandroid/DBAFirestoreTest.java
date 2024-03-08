package com.example.scandroid;

import static junit.framework.TestCase.assertEquals;

import android.content.Context;
import android.location.Location;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        latch.await(5, TimeUnit.SECONDS);

        // set location to save accessedEvent and get accessedEvent
        final Event[] recievedEvent = new Event[1];
        this.dbA.accessEvent(mockEvent.getEventID(), event -> {
            if (event != null) {
                recievedEvent[0] = event;
            }
        });

        // after delay to ensure event access, assert attributes
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(mockEvent.getEventID(), recievedEvent[0].getEventID());
        assertEquals(mockEvent.getEventName(), recievedEvent[0].getEventName());
        assertEquals(mockEvent.getEventDescription(), recievedEvent[0].getEventDescription());
    }


} // end public class DBAFirestoreTest