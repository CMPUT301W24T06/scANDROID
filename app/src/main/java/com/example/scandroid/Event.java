package com.example.scandroid;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an Event in the scANDROID app for CMPUT 301 at UofA W24.<br>
 * Events are created by Users (as an Organizer) and attended by Users (as an Attendee). <br>
 * Events are stored via an EventDB object.
 * @author Jordan Beaubien & Moyo Dawodu
 */
public class Event {

    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private ArrayList<CheckIn> EventAttendeeList;
    private ArrayList<EventAnnouncement> EventAnnouncementList;
    private String EventID;
    private Calendar EventDate;
    private String EventDescription;
    private ArrayList<Double> EventLocation;
    private ArrayList<EventMilestone> EventMilestoneList;
    private ArrayList<Integer> MilestoneSeries;
    private String EventName;
    private String EventOrganizerID;


    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor for an <code>Event</code> object, specifying spacial and temporal details
     * as well as the name of the organizer and a poster image.
     *
     * @param eventOrganizerID UserID of User that created Event
     * @param eventName        Name of Event
     * @param eventDescription Contextual information for Event.
     * @param eventDate        Day that Event will take place, includes time of Event
     * @param eventLocation    Geographical coordinates of Event {latitude, longitude}
     */
    public Event(@NonNull String eventOrganizerID, @NonNull String eventName, String eventDescription,
            @NonNull Calendar eventDate, ArrayList<Double> eventLocation) {

        EventID = UUID.randomUUID().toString(); // unique identifier for database key
        this.EventAttendeeList = new ArrayList<>();
        this.EventAnnouncementList = new ArrayList<>();
        this.EventDate = eventDate;
        this.EventDescription = eventDescription;
        this.EventLocation = eventLocation;
        this.EventMilestoneList = new ArrayList<>();
        this.MilestoneSeries = new ArrayList<>(Arrays.asList(1, 1));
        this.EventName = eventName;
        this.EventOrganizerID = eventOrganizerID;
        this.addEventMilestone();   // adds first milestone of threshold of one attendee check-in
   }

    /**
     * Necessary empty constructor for Event.fromSnapshot method. <br>
     * Solution provided by ChatGPT via Simon Thang. <br>
     * {@see <a href="https://chat.openai.com/share/e135f2dc-cd2f-47ca-b48e-55115d41e6bf"> ChatGPT Conversation </a>}
     */
    public Event() {
        this.EventLocation = new ArrayList<>(2); // Ensure initial capacity
        this.EventLocation.add(0.0); // Initial latitude
        this.EventLocation.add(0.0); // Initial longitude
        this.EventMilestoneList = new ArrayList<>();
        this.MilestoneSeries = new ArrayList<>(Arrays.asList(1, 1));
    }
    public void setEventID(String eventID){
        EventID = eventID;
    }

    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Add a new announcement to an Event as an Organizer.
     * @param announcementTitle A name for the announcement
     * @param announcementAbout Context to what the announcement is about
     * @param announcementTime  The time that the announcement is visible to an attendee
     */
    public void addEventAnnouncement(String announcementTitle, String announcementAbout, Time announcementTime) {
        this.EventAnnouncementList.add(new EventAnnouncement(announcementTitle, announcementAbout, announcementTime));
    }

    /**
     * Add a User to an Event as an attendee.
     * @param userID            UserID of attendee that is checking-in to the Event
     * @param checkInTime       The HrMin time that the attendee is checking-in
     * @param checkInLocation   Geographical coordinates of Event {latitude, longitude}
     */
    public void addEventAttendee(String userID, Time checkInTime, ArrayList<Double> checkInLocation) {
        this.EventAttendeeList.add(new CheckIn(userID, checkInTime, checkInLocation));
        if (this.EventAttendeeList.size() == this.MilestoneSeries.get(0)) {
            this.addEventMilestone();       // add next fibonacci milestone when current max is reached
        }
    }

    /**
     * Generate the next fibonacci valued attendee count milestone for the Event.
     * Method is automatically run when enough attendees are checked-in to meet a current threshold.
     */
    private void addEventMilestone() {
        int pastGreatest = this.MilestoneSeries.get(1);                                 // current greatest milestone threshold
        this.EventMilestoneList.add(new EventMilestone(pastGreatest));
        int nextGreatest = this.MilestoneSeries.get(0) + this.MilestoneSeries.get(1);   // next milestone threshold
        this.MilestoneSeries.set(0, pastGreatest);
        this.MilestoneSeries.set(1, nextGreatest);                                      // i.e. [2,3] becomes [3,5]
    }

    /**
     * Extracts necessary data from snapshot to create an instance of Event. <br>
     * Solution provided by ChatGPT via Simon Thang. <br>
     * {@see <a href="https://chat.openai.com/share/e135f2dc-cd2f-47ca-b48e-55115d41e6bf"> ChatGPT Conversation </a>}
     * @param snapshot Document read from Firestore database with Event.accessEvent() data.
     * @return Event object with appropriate attributes
     */
    public static Event fromSnapshot(DocumentSnapshot snapshot) {
        // initialize return Event
        Event event = new Event();

        // Extract data from the DocumentSnapshot
        event.setEventID(snapshot.getString("eventID"));
        event.EventOrganizerID = snapshot.getString("eventOrganizerID");
        event.EventName = snapshot.getString("eventName");
        event.EventDescription = snapshot.getString("eventDescription");
        //TODO Properly deserialize these attributes when retrieving them from firebase
        event.EventMilestoneList = new ArrayList<>();
        event.MilestoneSeries = new ArrayList<>(Arrays.asList(1, 1));
        event.EventAttendeeList = new ArrayList<>();

        // ... extract other fields accordingly
        // Convert Firestore Timestamp to Date
        Log.d("Firestore", "Type of eventDate: " + snapshot.get("eventDate").getClass().getName());
        HashMap<String, Object> dateMap = (HashMap<String, Object>) snapshot.get("eventDate");

        if (dateMap != null) {
            Timestamp timeInMillis = (Timestamp) dateMap.get("time");
            if (timeInMillis != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timeInMillis.toDate().getTime());
                event.setEventDate(calendar);
            }
        }
        Log.d("Firestore", "Type of eventLocation: " + snapshot.get("eventLocation").getClass().getName());
        Object eventLocationObject = snapshot.get("eventLocation");

        if (eventLocationObject instanceof GeoPoint) {
            // Case 1: eventLocation is a GeoPoint
            GeoPoint geoPoint = (GeoPoint) eventLocationObject;
            event.EventLocation = new ArrayList<>(2);
            event.EventLocation.add(geoPoint.getLatitude());
            event.EventLocation.add(geoPoint.getLongitude());
        } else if (eventLocationObject instanceof ArrayList) {
            // Case 2: eventLocation is an ArrayList
            event.EventLocation = (ArrayList<Double>) eventLocationObject;
        } else if (eventLocationObject instanceof HashMap) {
            // Case 3: eventLocation is a map
            HashMap<String, Double> eventLocationMap = (HashMap<String, Double>) eventLocationObject;
            event.EventLocation = new ArrayList<>(2);
            event.EventLocation.add(eventLocationMap.get("latitude"));
            event.EventLocation.add(eventLocationMap.get("longitude"));
        }
        //TODO these Event attributes may also have to be deserialized
// Extract EventAttendeeList
        List<Map<String, Object>> attendeeListMap = (List<Map<String, Object>>) snapshot.get("eventAttendeeList");
        if (attendeeListMap != null) {
            for (Map<String, Object> checkInMap : attendeeListMap) {
                String userID = (String) checkInMap.get("userID");
                // Extract other details as needed
                // ...
                // You may need to convert Time and ArrayList<Double> from the checkInMap
                // using similar logic as you did in the original code
                //event.addEventAttendee(userID, checkInTime, checkInLocation);
            }
        }

        // Extract EventAnnouncements
        List<Map<String, Object>> announcementListMap = (List<Map<String, Object>>) snapshot.get("eventAnnouncements");
        if (announcementListMap != null) {
            for (Map<String, Object> announcementMap : announcementListMap) {
                // Extract details from announcementMap and create EventAnnouncement objects
                // ...
                // You may need to convert timestamp and other fields
                String announcementTitle = (String) announcementMap.get("announcementTitle");
                String announcementAbout = (String) announcementMap.get("announcementAbout");
                //Time announcementTime = ...; // Convert timestamp or other relevant field
                //event.addEventAnnouncement(announcementTitle, announcementAbout, announcementTime);
            }
        }
        return event;

    } // end of fromSnapshot


    /* ------- *
     * GETTERS *
     * ------- */
    /**
     * @return List of all existing announcements of the Event.
     */
    public ArrayList<EventAnnouncement> getEventAnnouncements() {
        return this.EventAnnouncementList;
    }

    /**
     * @return List of all checked-in Users attending the Event.
     */
    public ArrayList<CheckIn> getEventAttendeeList() {
        return this.EventAttendeeList;
    }

    /**
     * @return Count of Users currently checked-in to the Event.
     */
    public Integer getEventAttendeesTotal() {
        return this.EventAttendeeList.size();
    }

    /**
     * @return Day that Event will take place, includes time of Event.
     */
    public Calendar getEventDate() { return this.EventDate; }

    /**
     * @return Contextual information for Event.
     */
    public String getEventDescription() { return this.EventDescription; }

    /**
     * @return Strong pseudo random number generator that belongs to this Event.
     */
    public String getEventID() { return EventID ; }

    /**
     * @return Geographical coordinates of Event {latitude, longitude}
     */
    public ArrayList<Double> getEventLocation() { return this.EventLocation; }

    /**
     * @return List of all accomplished milestones for the Event.
     */
    public ArrayList<EventMilestone> getEventMilestones() {
        return this.EventMilestoneList;
    }

    /**
     * @return Name that the organizer has given the Event.
     */
    public String getEventName() { return this.EventName; }

    /**
     * @return ANDROID_ID of organizer User to get current name from DB for User.
     */
    public String getEventOrganizerID() { return this.EventOrganizerID; }


    /* ------- *
     * SETTERS *
     * ------- */
    /**
     * @param dateOfEvent Day that Event will take place, includes time of Event.
     */
    public void setEventDate(Calendar dateOfEvent) { this.EventDate = dateOfEvent; }

    /**
     * @param eventDescription Contextual information for Event.
     */
    public void setEventDescription(String eventDescription) { this.EventDescription = eventDescription; }

    /**
     * @param locationOfEvent Coordinates that the Event takes place at.
     */
    public void setEventLocation(ArrayList<Double> locationOfEvent) {
        this.EventLocation.set(0, locationOfEvent.get(0));
        this.EventLocation.set(1, locationOfEvent.get(1)); }

    /**
     * @param nameOfEvent Name that the organizer has given the Event.
     */
    public void setEventName(String nameOfEvent) { this.EventName = nameOfEvent; }


    /* -------------- *
     * NESTED CLASSES *
     * -------------- */
    /**
     * Represents an Attendee of the Event. <br>
     * Organizers use a list of CheckIn's when viewing Users attending their Event.
     */
    public static class CheckIn {
        private String UserID;
        private Time CheckInTime;
        private ArrayList<Double> CheckInLocation;

        /**
         * Sole constructor for <code>CheckIn</code> object, specifying which User checked in,
         * as well as when and where they checked-in.
         * @param userID            UserID of attendee that is checking-in to the Event
         * @param checkInTime       The HrMin time that the attendee is checking-in
         * @param checkInLocation   Geographical coordinates of Event {latitude, longitude}
         */
        public CheckIn(String userID, Time checkInTime, ArrayList<Double> checkInLocation) {
            this.UserID = userID;
            this.CheckInTime = checkInTime;
            this.CheckInLocation = checkInLocation;
        }

        /**
         * @return Which User was checked-in
         */
        public String getUserID() {
            return this.UserID;
        }

        /**
         * @return HrMin time that the User checked-in at.
         */
        public Time getCheckInTime() {
            return this.CheckInTime;
        }

        /**
         * @return Geographical coordinates of User checkIn {latitude, longitude}
         */
        public ArrayList<Double> getCheckInLocation() {
            return this.CheckInLocation;
        }

    } // end public class CheckIn

    /**
     * Represents a single Announcement for an Event. <br>
     * Announcements are created by the creator of the Event. <br>
     * Attendees see Announcements for Events they attend if desired.
     */
    public class EventAnnouncement {
        private String AnnouncementAbout;
        private String AnnouncementOrganizerID;
        private Time AnnouncementTime;
        private String AnnouncementTitle;

        /**
         * Sole constructor for an <code>EventAnnouncement</code> object, specifying a
         * name for the announcement, with a description and a time that the announcement
         * should be available to any User that has checked-in to the Event.
         * @param title     A name for the announcement
         * @param about     Context to what the announcement is about
         * @param time      The time that the announcement is visible to an attendee
         */
        private EventAnnouncement(String title, String about, Time time) {
            this.AnnouncementTitle = title;
            this.AnnouncementAbout = about;
            this.AnnouncementOrganizerID = EventOrganizerID;
            this.AnnouncementTime = time;
        }

        /**
         * @return Context to what the announcement is about
         */
        public String getAnnouncementAbout() {
            return AnnouncementAbout;
        }

        /**
         * @return The name of the User that created the Event
         */
        public String getAnnouncementOrganizerID() {
            return AnnouncementOrganizerID;
        }

        /**
         * @return The time that the announcement is visible to an attendee
         */
        public Time getAnnouncementTime() {
            return AnnouncementTime;
        }

        /**
         * @return The name of the announcement
         */
        public String getAnnouncementTitle() {
            return AnnouncementTitle;
        }

    } // end public class EventAnnouncement

    /**
     * Represents a single Milestone for an Event. <br>
     * Milestones are predefined upon creation of an Event. <br>
     * Organizers see Milestones that have been met by the Event. <br>
     * Milestones are automatically created upon meeting highest milestone and follow fibonacci sequence. <br>
     */
    public static class EventMilestone {
        private static Integer Threshold;

        /**
         * Sole constructor for an <code>EventMilestone</code> object, specifying
         * the attendee count that triggers this milestone.
         * @param threshold     How many attendees check-in to trigger the milestone.
         */
        private EventMilestone(Integer threshold) {
            Threshold = threshold;
        }

        /**
         * @return Count of attendee check-in's to trigger the milestone.
         */
        public Integer getThreshold() {
            return Threshold;
        }

    } // end public static class EventMilestone

} // end public class Event
















