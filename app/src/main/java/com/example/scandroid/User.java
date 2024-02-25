package com.example.scandroid;

import android.media.Image;
import android.provider.Settings;

import java.util.ArrayList;
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
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    //TODO - (FRONT END) set character limit for userAboutMe.
    private String userAboutMe;
    public Image userProfileImage;
    private int totalAttended;
    private String adminKey;
    private String userID;
    private ArrayList<UUID> eventsAttending;
    private ArrayList<UUID> eventsOrganized;

    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */

    /**
     * Sole constructor for the <code>User</code> object, specifying user attributes.
     *
     * @param userName The name of the User object you want to instantiate. This is set to a random guest name if none is provided.
     * @param userPhoneNumber The phone number of a user object. May be null.
     * @param userAboutMe The about-me section of a user's profile. May be null.
     * @param userProfileImage The profile image of a user object. This is randomized if none is provided.
     * @param userEmail The email address of a user object. May be null.
     */
    public User(String userName, String userPhoneNumber, String userAboutMe, Image userProfileImage, String userEmail) {
        this.userID = Settings.Secure.ANDROID_ID;
        this.eventsAttending = new ArrayList<UUID>();
        this.eventsOrganized = new ArrayList<UUID>();
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

    public boolean isAdmin(String userAdminKey){
        return Objects.equals(userAdminKey, adminKey);
    }

    //TODO - getters and setters
    //getters
    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public Image getUserProfileImage() {
        return userProfileImage;
    }

    public int getTotalAttended() {
        return totalAttended;
    }

    //setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public void setUserAboutMe(String userAboutMe) {
        this.userAboutMe = userAboutMe;
    }

    public void setUserProfileImage(Image userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public void setTotalAttended(int totalAttended) {
        this.totalAttended = totalAttended;
    }


    //TODO - notifiedBy setup

    //contains a list of eventIDs
    public ArrayList<UUID> notifiedBy;
    //TODO - scanQRCode and createEvent
    //TODO - UserDB
    //TODO - javadocs
}
