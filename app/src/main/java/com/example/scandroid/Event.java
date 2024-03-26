package com.example.scandroid;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
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
    private String EventID;
    private String EventName;
    private String EventOrganizerID;
    private String EventDescription;
    private Long EventCapacity;
    private Boolean hasCapacity;
    private ArrayList<Double> EventLocation;
    private ArrayList<Long> EventMilestoneList;
    private ArrayList<Long> MilestoneSeries;
    private ArrayList<String> AnnouncementTitles;
    private ArrayList<String> AnnouncementAbouts;
    private ArrayList<Long> AnnouncementTimes;
    private HashMap<String, Long> EventDateDetails;
    private ArrayList<String> CheckInIDs;
    private ArrayList<Long> CheckInTimes;
    private ArrayList<String> CheckInLocations;
    private ArrayList<String> SignUpIDs;


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
        this.EventDescription = eventDescription;
        this.EventLocation = eventLocation;
        this.EventMilestoneList = new ArrayList<>();
        this.MilestoneSeries = new ArrayList<>(Arrays.asList((long) 1, (long) 1));
        this.EventName = eventName;
        this.EventOrganizerID = eventOrganizerID;
        this.EventCapacity = 0L;
        this.hasCapacity = Boolean.FALSE;
        this.addEventMilestone();   // adds first milestone of threshold of one attendee check-in

        // Initialize Announcement data structure
        this.AnnouncementAbouts = new ArrayList<>();
        this.AnnouncementTimes = new ArrayList<>();
        this.AnnouncementTitles = new ArrayList<>();

        // Initialize Attendees data structure
        this.CheckInIDs = new ArrayList<>();
        this.CheckInLocations = new ArrayList<>();
        this.CheckInTimes = new ArrayList<>();
        this.SignUpIDs = new ArrayList<>();

        // Initialize Date data structure
        this.EventDateDetails = new HashMap<>();
        this.EventDateDetails.put("YEAR", (long) eventDate.get(Calendar.YEAR));
        this.EventDateDetails.put("MONTH", (long) eventDate.get(Calendar.MONTH));
        this.EventDateDetails.put("DAY_OF_MONTH", (long) eventDate.get(Calendar.DAY_OF_MONTH));
        this.EventDateDetails.put("HOUR_OF_DAY", (long) eventDate.get(Calendar.HOUR_OF_DAY));
        this.EventDateDetails.put("MINUTE", (long) eventDate.get(Calendar.MINUTE));
    }

    /**
     * Necessary empty constructor for Event.fromSnapshot method. <br>
     * Solution provided by ChatGPT via Simon Thang. <br>
     * {@see <a href="https://chat.openai.com/share/e135f2dc-cd2f-47ca-b48e-55115d41e6bf"> ChatGPT Conversation </a>}
     */
    public Event() {
        this.EventLocation = new ArrayList<>(); // Ensure initial capacity
        this.EventMilestoneList = new ArrayList<>();
        this.MilestoneSeries = new ArrayList<>();

        // Initialize Announcement data structure
        this.AnnouncementAbouts = new ArrayList<>();
        this.AnnouncementTimes = new ArrayList<>();
        this.AnnouncementTitles = new ArrayList<>();

        // Initialize Attendees data structure
        this.CheckInIDs = new ArrayList<>();
        this.CheckInLocations = new ArrayList<>();
        this.CheckInTimes = new ArrayList<>();
        this.SignUpIDs = new ArrayList<>();

        // Initialize Date data structure
        this.EventDateDetails = new HashMap<>();
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
        this.AnnouncementTitles.add(announcementTitle);
        this.AnnouncementAbouts.add(announcementAbout);
        this.AnnouncementTimes.add(announcementTime.getTime());
    }

    /**
     * Add a User to an Event as an attendee if Event is not at a capacity.
     * @param userID            UserID of attendee that is checking-in to the Event
     * @param checkInTime       The HrMin time that the attendee is checking-in
     * @param checkInLocation   Geographical coordinates of Event {latitude, longitude}
     */
    public void addEventAttendee(String userID, Time checkInTime, ArrayList<Double> checkInLocation) {
        // without overbooking the event...
        if (!this.hasCapacity | (this.CheckInIDs.size() < this.EventCapacity)) {
            // add attendee information to appropriate arrays
            this.CheckInIDs.add(userID);
            // convert location to string for proper firebase storing
            this.CheckInLocations.add(checkInLocation.get(0).toString() + "@" + checkInLocation.get(1).toString());
            this.CheckInTimes.add(checkInTime.getTime());
            // ensure attendee is not in both userID lists
            this.SignUpIDs.remove(userID);

            // increment milestone if necessary
            if (this.CheckInIDs.size() == this.MilestoneSeries.get(0)) {
                this.addEventMilestone();       // add next fibonacci milestone when current max is reached
            }
        }
    }


    /**
     * Add an attendee to the "promise to attend" list.
     * @param userID    UserID of possible attendee who promises to attend Event
     */
    public void addEventSignUp(String userID) {
        // add a possible attendees "promise" to attend to list
        this.SignUpIDs.add(userID);
    }

    /**
     * Generate the next fibonacci valued attendee count milestone for the Event.
     * Method is automatically run when enough attendees are checked-in to meet a current threshold.
     */
    private void addEventMilestone() {
        int pastGreatest = this.MilestoneSeries.get(1).intValue();                                 // current greatest milestone threshold
        this.EventMilestoneList.add((long) pastGreatest);
        int nextGreatest = this.MilestoneSeries.get(0).intValue() + this.MilestoneSeries.get(1).intValue();   // next milestone threshold
        this.MilestoneSeries.set(0, (long) pastGreatest);
        this.MilestoneSeries.set(1, (long) nextGreatest);                                      // i.e. [2,3] becomes [3,5]
    }

    /**
     * Extracts necessary data from snapshot to create an instance of Event. <br>
     * Solution provided by ChatGPT via Simon Thang. <br>
     * {@see <a href="https://chat.openai.com/share/e135f2dc-cd2f-47ca-b48e-55115d41e6bf"> ChatGPT Conversation </a>}
     * @param snapshot Document read from Firestore database with Event.accessEvent() data.
     * @return Event object with appropriate attributes
     */
    public static Event unpackageEvent(DocumentSnapshot snapshot) {
        // initialize return Event
        Event unpackagedEvent = new Event();

        // Extract data from the DocumentSnapshot
        unpackagedEvent.EventID = snapshot.getString("ID");
        unpackagedEvent.EventDateDetails = (HashMap<String, Long>) snapshot.get("date");
        unpackagedEvent.EventDescription = snapshot.getString("description");
        unpackagedEvent.EventLocation = (ArrayList<Double>) snapshot.get("location");
        unpackagedEvent.EventMilestoneList = (ArrayList<Long>) snapshot.get("milestoneList");
        unpackagedEvent.MilestoneSeries = (ArrayList<Long>) snapshot.get("milestoneSeries");
        unpackagedEvent.EventName = snapshot.getString("name");
        unpackagedEvent.EventOrganizerID = snapshot.getString("organizerID");
        unpackagedEvent.AnnouncementTitles = (ArrayList<String>) snapshot.get("announcementTitles");
        unpackagedEvent.AnnouncementAbouts = (ArrayList<String>) snapshot.get("announcementAbouts");
        unpackagedEvent.AnnouncementTimes = (ArrayList<Long>) snapshot.get("announcementTimes");
        unpackagedEvent.CheckInIDs = (ArrayList<String>) snapshot.get("attendeeIDs");
        unpackagedEvent.CheckInTimes = (ArrayList<Long>) snapshot.get("attendeeTimes");
        unpackagedEvent.CheckInLocations = (ArrayList<String>) snapshot.get("attendeeLocations");
        unpackagedEvent.SignUpIDs = (ArrayList<String>) snapshot.get("signUps");
        unpackagedEvent.EventCapacity = (Long) snapshot.get("capacity");
        unpackagedEvent.hasCapacity = (Boolean) snapshot.get("hasCapacity");

        return unpackagedEvent;

    } // end of fromSnapshot

    /**
     * Simplify Event object to hashmap to be stored in Firestore database.
     * @return packaged Event object
     */
    public Map<String, Object> packageEvent() {
        // initialize map for event fields to be stored
        Map<String, Object> packagedEvent = new HashMap<>();

        // assign event details to map
        packagedEvent.put("ID", this.EventID);
        packagedEvent.put("date", this.EventDateDetails);
        packagedEvent.put("description", this.EventDescription);
        packagedEvent.put("location", this.EventLocation);
        packagedEvent.put("milestoneList", this.EventMilestoneList);
        packagedEvent.put("milestoneSeries", this.MilestoneSeries);
        packagedEvent.put("name", this.EventName);
        packagedEvent.put("organizerID", this.EventOrganizerID);
        packagedEvent.put("announcementTitles", this.AnnouncementTitles);
        packagedEvent.put("announcementAbouts", this.AnnouncementAbouts);
        packagedEvent.put("announcementTimes", this.AnnouncementTimes);
        packagedEvent.put("attendeeIDs", this.CheckInIDs);
        packagedEvent.put("attendeeTimes", this.CheckInTimes);
        packagedEvent.put("attendeeLocations", this.CheckInLocations);
        packagedEvent.put("signUps", this.SignUpIDs);
        packagedEvent.put("capacity", this.EventCapacity);
        packagedEvent.put("hasCapacity", this.hasCapacity);

        // return fully detailed event map
        return packagedEvent;
    }

    /* ------- *
     * GETTERS *
     * ------- */
    /**
     * @return List of all existing announcements of the Event.
     */
    public ArrayList<EventAnnouncement> getEventAnnouncements() {

        // Initialize return array
        ArrayList<EventAnnouncement> EventAnnouncementList = new ArrayList<>();

        // Construct Announcement.class objects from Event data
        for (int i = 0; i < this.AnnouncementTitles.size(); i++) {
            EventAnnouncementList.add(
                    new EventAnnouncement(
                        this.AnnouncementTitles.get(i),
                        this.AnnouncementAbouts.get(i),
                        new Time(this.AnnouncementTimes.get(i))));
        }
        return EventAnnouncementList;
    }

    /**
     * @return List of all checked-in Users attending the Event.
     */
    public ArrayList<CheckIn> getEventAttendeeList() {

        // Initialize return array
        ArrayList<CheckIn> EventAttendeeList = new ArrayList<>();

        // Construct CheckIn.class objects from Event data
        for (int i = 0; i < CheckInIDs.size(); i++) {

            // locations are stored as "@" delimited string for firestore
            // must separate longitude and latitude from concatenated string
            String locationAsString = this.CheckInLocations.get(i);
            String[] locationAsArray = locationAsString.split("@");
            ArrayList<Double> locationAsDoubles = new ArrayList<>();
            locationAsDoubles.add(Double.parseDouble(locationAsArray[0]));
            locationAsDoubles.add(Double.parseDouble(locationAsArray[1]));

            EventAttendeeList.add(
                    new CheckIn(
                            this.CheckInIDs.get(i),
                            new Time(this.CheckInTimes.get(i)),
                            locationAsDoubles));
        }
        return EventAttendeeList;
    }

    /**
     * @return Count of Users currently checked-in to the Event.
     */
    public Integer getEventAttendeesTotal() {
        return this.CheckInIDs.size();
    }

    /**
     * @return Day that Event will take place, includes time of Event.
     */
    public Calendar getEventDate() {
        // Get date details for this Event, ensuring no null returns
        long eventYear = this.EventDateDetails.get("YEAR");
        long eventMonth = this.EventDateDetails.get("MONTH");
        long eventDate = this.EventDateDetails.get("DAY_OF_MONTH");
        long eventHour = this.EventDateDetails.get("HOUR_OF_DAY");
        long eventMinute = this.EventDateDetails.get("MINUTE");

        // build object of required type and return
        Calendar returnCalendar = Calendar.getInstance();
        returnCalendar.set((int) eventYear, (int) eventMonth, (int) eventDate, (int) eventHour, (int) eventMinute);
        return returnCalendar;
    }

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
    public ArrayList<Long> getEventMilestones() {
        return this.EventMilestoneList;
    }

    /**
     * @return Current state of milestone series
     */
    public ArrayList<Long> getEventMilestoneSeries() { return this.MilestoneSeries; }

    /**
     * @return Name that the organizer has given the Event.
     */
    public String getEventName() { return this.EventName; }

    /**
     * @return ANDROID_ID of organizer User to get current name from DB for User.
     */
    public String getEventOrganizerID() { return this.EventOrganizerID; }

    /**
     * @return List of userID's who "promised" to attend the Event.
     */
    public ArrayList<String> getEventSignUps() { return this.SignUpIDs; }

    /**
     * @return The maximum attendance capacity for the Event.
     */
    public Integer getEventCapacity() { return Math.toIntExact(this.EventCapacity); }

    /**
     * @return Flag of whether Event considers a capacity limit or not.
     */
    public Boolean getEventHasCapacity() { return this.hasCapacity; }


    /* ------- *
     * SETTERS *
     * ------- */
    /**
     * @param dateOfEvent Day that Event will take place, includes time of Event.
     */
    public void setEventDate(Calendar dateOfEvent) {
        // Update Event details with values from argument
        this.EventDateDetails.put("YEAR", (long) dateOfEvent.get(Calendar.YEAR));
        this.EventDateDetails.put("MONTH", (long) dateOfEvent.get(Calendar.MONTH));
        this.EventDateDetails.put("DAY_OF_MONTH", (long) dateOfEvent.get(Calendar.DAY_OF_MONTH));
        this.EventDateDetails.put("HOUR_OF_DAY", (long) dateOfEvent.get(Calendar.HOUR_OF_DAY));
        this.EventDateDetails.put("MINUTE", (long) dateOfEvent.get(Calendar.MINUTE));
    }

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

    /**
     * Set an attendee limit capacity for the Event. <br>
     * > 0 : to enable capacity <br>
     * = 0 : to disable capacity
     * @param eventCapacity Maximum amount of attendees of the Event
     */
    public void setEventCapacity(int eventCapacity) {
        // set boolean flag for having capacity and update capacity value
        if (eventCapacity > 0) {
            this.hasCapacity = Boolean.TRUE;
        } else {
            this.hasCapacity = Boolean.FALSE;
        }
        this.EventCapacity = (long) Math.abs(eventCapacity);
    }


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

} // end public class Event















