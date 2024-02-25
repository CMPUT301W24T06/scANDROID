package com.example.scandroid;

import android.media.Image;

import java.util.ArrayList;
import java.util.UUID;

public class User {
    //NOTE - Comments can be moved to javadocs later
    //userID is gotten from Settings.Secure.ANDROID_ID and exists for the purpose of identifying unique users
    private String userID;
    //TODO - set default value of userName to "Guest" + "randint"
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    //TODO - character limit for userAboutMe on Front end
    private String userAboutMe;
    public Image userProfileImage;
    private int totalAttended;
    public Boolean isAdmin;
    private ArrayList<UUID> eventsAttending;
    private ArrayList<UUID> eventsOrganized;

    public User(String userID, ArrayList<UUID> eventsAttending, ArrayList<UUID> eventsOrganized) {
        this.userID = userID;
        this.eventsAttending = eventsAttending;
        this.eventsOrganized = eventsOrganized;
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
    //TODO - scanQRCode and createEvent
    //TODO - UserDB
    //TODO - (low priority) javadocs
}
