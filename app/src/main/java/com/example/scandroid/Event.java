package com.example.scandroid;

import android.location.Location;
import android.media.Image;

import androidx.annotation.NonNull;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private static UUID EventID;
    private String EventOrganizerID;
    private String EventName;
    private String EventDescription;
    private Image EventPosterImage;
    private Date EventDate; // NEEDS G&S FOR TIME AS WELL DATE
    /* TODO ?? private Time EventTime ?? */
    private Location EventLocation;
    private ArrayList<CheckIn> EventAttendeeList;
    private ArrayList<EventAnnouncement> EventAnnouncementList;
    private ArrayList<EventMilestone> EventMilestoneList;
    private ArrayList<Integer> MilestoneSeries;

    /* TODO - Attributes
    * private QRCode EventQR
    * private QRCode EventPromotionQR
    * */

    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor for an <code>Event</code> object, specifying spacial and temporal details
     * as well as the name of the organizer and a poster image.
     *
     * @param eventOrganizerID UserID of User that created Event
     * @param eventName        Name of Event
     * @param eventDescription
     * @param eventPoster      Profile image for Event
     * @param eventDate        Day that Event will take place
     * @param eventLocation    Geographical place of Event
     */
    public Event(@NonNull String eventOrganizerID, @NonNull String eventName, String eventDescription, Image eventPoster, @NonNull Date eventDate, @NonNull Location eventLocation) {
        EventDescription = eventDescription;
        EventID = UUID.randomUUID();
        this.EventOrganizerID = eventOrganizerID;
        this.EventName = eventName;
        this.EventDescription = eventDescription;
        this.EventPosterImage = eventPoster;
        this.EventDate = eventDate;
        this.EventLocation = eventLocation;
        this.EventAttendeeList = new ArrayList<>();
        this.EventAnnouncementList = new ArrayList<>();
        this.MilestoneSeries = new ArrayList<>(Arrays.asList(1, 1));
        this.EventMilestoneList = new ArrayList<>();
        this.addEventMilestone();   // adds first milestone of threshold of one attendee check-in
    }

    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Add a User to an Event as an attendee.
     * @param userID            UserID of attendee that is checking-in to the Event
     * @param checkInTime       The HrMin time that the attendee is checking-in
     * @param checkInLocation   The location that the attendee is checking-in from
     */
    public void addEventAttendee(String userID, Time checkInTime, Location checkInLocation) {
        this.EventAttendeeList.add(new CheckIn(userID, checkInTime, checkInLocation));
        if (this.EventAttendeeList.size() == this.MilestoneSeries.get(1)) {
            this.addEventMilestone();       // add next fibonacci milestone when current max is reached
        }
    }

    /**
     * Generate the next fibonacci valued attendee count milestone for the Event.
     * Method is automatically run when enough attendees are checked-in to meet a current threshold.
     */
    private void addEventMilestone() {
        int pastGreatest = this.MilestoneSeries.get(1);                                 // current greatest milestone threshold
        int nextGreatest = this.MilestoneSeries.get(0) + this.MilestoneSeries.get(1);   // next milestone threshold
        this.EventMilestoneList.add(new EventMilestone(nextGreatest));
        this.MilestoneSeries.set(0, pastGreatest);
        this.MilestoneSeries.set(1, nextGreatest);                                      // i.e. [2,3] becomes [3,5]
    }

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
     * @return Calendar date that the Event takes place on.
     */
    public Date getEventDate() { return this.EventDate; }

    /**
     * @return Location that the Event takes place at.
     */
    public Location getEventLocation() { return this.EventLocation; }

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

    /**
     * @return The poster image provided for the Event.
     */
    public Image getEventPosterImage() { return this.EventPosterImage; }

    /* ------- *
     * SETTERS *
     * ------- */

    /**
     * @param dateOfEvent   The Calendar date that the Event takes place on.
     */
    public void setEventDate(Date dateOfEvent) { this.EventDate = dateOfEvent; }

    /**
     * @param locationOfEvent Location that the Event takes place at.
     */
    public void setEventLocation(Location locationOfEvent) { this.EventLocation = locationOfEvent; }

    /**
     * @param nameOfEvent Name that the organizer has given the Event.
     */
    public void setEventName(String nameOfEvent) { this.EventName = nameOfEvent; }
    
    /**
     * @param posterForEvent The poster image provided for the Event.
     */
    public void setEventPosterImage(Image posterForEvent) { this.EventPosterImage = posterForEvent; }


    /* -------------- *
     * NESTED CLASSES *
     * -------------- */
    /**
     * Represents an Attendee of the Event. <br>
     * Organizers use a list of CheckIn's when viewing Users attending their Event.
     */
    public static class CheckIn {
        String UserID;
        Time CheckInTime;
        Location CheckInLocation;

        /**
         * Sole constructor for <code>CheckIn</code> object, specifying which User checked in,
         * as well as when and where they checked-in.
         * @param userID            UserID of attendee that is checking-in to the Event
         * @param checkInTime       The HrMin time that the attendee is checking-in
         * @param checkInLocation   The location that the attendee is checking-in from
         */
        private CheckIn(String userID, Time checkInTime, Location checkInLocation) {
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
        public long getCheckInTime() {
            return this.CheckInTime.getTime();
        }

        /**
         * @return Where the User checked in from.
         */
        public Location getCheckInLocation() {
            return this.CheckInLocation;
        }
    }

    /**
     * Represents a single Announcement for an Event. <br>
     * Announcements are created by the creator of the Event. <br>
     * Attendees see Announcements for Events they attend if desired.
     */
    public class EventAnnouncement {
        String AnnouncementTitle;
        String AnnouncementAbout;
        String AnnouncementOrganizer;
        Time AnnouncementTime;

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
            this.AnnouncementOrganizer = EventOrganizerID;
            this.AnnouncementTime = time;
        }

    }

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
    }

    /* TODO - Nested Classes
    * private class QRCode
    * private class QRCodeSharer
    * */
}
















