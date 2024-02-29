package com.example.scandroid;

import android.location.Location;
import android.media.Image;
import android.provider.Settings;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.Random;

/**
 * Represents a User in the scANDROID app for CMPUT 301 at UofA W24. <br>
 * This class may represent a user with different permission levels - such as an Admin or an organizer or attendee.<br>
 * Users are stored via a UserDB object.
 * @author Jordan Beaubien & Moyo Dawodu
 */
public class User {
    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    //TODO - (FRONT END) set character limit for userAboutMe.
    private String userAboutMe;
    public Image userProfileImage;
    private final String adminKey = "ThisPersonIsAnAdmin1298";
    private String userID;
    private ArrayList<String> eventsAttending;
    private ArrayList<String> eventsOrganized;
    public ArrayList<String> notifiedBy;

    private HashMap<String, Integer> timesAttended = new HashMap<>() ;
    //TODO - make the user's location optional
    @Nullable private Location userLocation;


    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor for the <code>User</code> object, specifying user attributes.
     *
     * @param userID The user's ID generated upon boot. Accessed via Settings.Secure.ANDROID_ID
     * @param userName The name of the User object you want to instantiate. This is set to a random guest name if none is provided.
     * @param userPhoneNumber The phone number of a user object. May be null.
     * @param userAboutMe The about-me section of a user's profile. May be null.
     * @param userProfileImage The profile image of a user object. This is randomized if none is provided.
     * @param userEmail The email address of a user object. May be null.
     */
    public User(String userID, String userName, String userPhoneNumber, String userAboutMe, Image userProfileImage, String userEmail) {
        this.userID = userID;
        this.eventsAttending = new ArrayList<String>();
        this.eventsOrganized = new ArrayList<String>();
        this.notifiedBy = new ArrayList<String>();
        //set the default value of a user's name to a Guest name if no name is provided.
        if(userName == null){
            Random random_num = new Random();
            int randID = random_num.nextInt(10000);
            this.userName = "Guest" + randID;
        }
        else{
            this.userName = userName;
        }
        this.userAboutMe = userAboutMe;
        this.userProfileImage = userProfileImage;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
    }

    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Determines whether a user is an Admin based on a key they have entered.
     * @param userAdminKey The admin key the user entered.
     * @return boolean value indicating if a user is an admin(true) or if they are not(false)
     */
    public boolean isAdmin(String userAdminKey){
        return Objects.equals(userAdminKey, adminKey);
    }

    /**
     * Adds an event to the user's list of events they are attending.
     * @param event An eventID.
     */
    public void addEventToEventsAttending(String event){
        if(eventsAttending.contains(event)){
            Integer timesAttendedValue = timesAttended.get(event);
            timesAttended.replace(event,timesAttendedValue+1);
        }
        else{
            eventsAttending.add(event);
            timesAttended.put(event,1);
        }
    }

    /**
     * Adds an event to the user's list of events they have organized.
     * @param event An eventID.
     */
    public void addEventToEventsOrganized(String event){
        eventsOrganized.add(event);
    }

    /* ------- *
     * GETTERS *
     * ------- */

    /**
     * @return the user's userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @return the user's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the user's profile picture
     */
    public Image getUserProfileImage() {
        return userProfileImage;
    }

    //TODO - figure out how to calculate total number of times a user has checked in. May have to use some sort of button and track how many times it is  hit.

    /**
     * @return the user's email.
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * @return the user's phone number
     */
    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    /**
     * @return The information in the user's 'About Me' section of their profile.
     */
    public String getUserAboutMe() {
        return userAboutMe;
    }

    /**
     * @return A list of eventIDs that the user may be notified by.
     */
    public ArrayList<String> getNotifiedBy() {
        return notifiedBy;
    }

    /**
     * @return the list of events (eventIDs) a user is attending
     */
    public ArrayList<String> getEventsAttending() {
        return eventsAttending;
    }

    /**
     * @return the list of events a user is organizing.
     */
    public ArrayList<String> getEventsOrganized() {
        return eventsOrganized;
    }

    /**
     * @param event an eventID
     * @return The number of times a User has attended a given event.
     */
    public Integer getTimesAttended(UUID event){
        return timesAttended.get(event);
    }

    /* ------- *
     * SETTERS *
     * ------- */
    
    /**
     * @param userName a name inputted by the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @param userEmail the email inputted by the user.
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * @param userPhoneNumber the phone number inputted by the user.
     */
    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    /**
     * @param userAboutMe the about-me section the user inputs in their profile.
     */
    public void setUserAboutMe(String userAboutMe) {
        this.userAboutMe = userAboutMe;
    }

    /**
     * @param userProfileImage the profile picture the user adds to their profile.
     */
    public void setUserProfileImage(Image userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    /**
     * @param eventsAttending the new list of events a user may be attending.
     */
    public void setEventsAttending(ArrayList<String> eventsAttending) {
        this.eventsAttending = eventsAttending;
    }

    /**
     * @param eventsOrganized the new list of events a user is organizing.
     */
    public void setEventsOrganized(ArrayList<String> eventsOrganized) {
        this.eventsOrganized = eventsOrganized;
    }

    /**
     * @param notifiedBy the new list of events a user may be notified by.
     */
    public void setNotifiedBy(ArrayList<String> notifiedBy) {
        this.notifiedBy = notifiedBy;
    }
    //TODO - scanQRCode
    //TODO - UserDB
}
