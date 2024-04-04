package com.example.scandroid;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
    private String adminKey = "ThisPersonIsAnAdmin1298";
    private String userID;
    private ArrayList<String> eventsAttending;
    private ArrayList<String> eventsOrganized;
    private ArrayList<String> eventsSignedUp;
    public ArrayList<String> notifiedBy;
    private String profilePictureUrl;
    private boolean hasAdminPermissions = false;
    private String fcmToken;
    public HashMap<String, Long> timesAttended = new HashMap<>() ;

    // Add a default constructor
    public User() {
        this.userID = "";  // Default to an empty string for userID
        this.userName = ""; // Default to an empty string for userName
        this.eventsAttending = new ArrayList<>();
        this.eventsOrganized = new ArrayList<>();
        this.eventsSignedUp = new ArrayList<>();
        this.notifiedBy = new ArrayList<>();
        this.timesAttended = new HashMap<>();
    }

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
     * @param userEmail The email address of a user object. May be null.
     *                       * @param profilePictureUrl The URL of the user's profile picture. May be null.
     */
    public User(String userID, String userName, String userPhoneNumber, String userAboutMe, String userEmail) {
        this.userID = userID;
        this.eventsAttending = new ArrayList<>();
        this.eventsOrganized = new ArrayList<>();
        this.eventsSignedUp = new ArrayList<>();
        this.notifiedBy = new ArrayList<>();
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
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
        this.profilePictureUrl = profilePictureUrl;
    }

    /* ------- *
     * METHODS *
     * ------- */

    /**
     * A user enters an admin key in order to get admin permissions
     * @param userAdminKey Key that user has entered
     */
    public void enterAdminKey(String userAdminKey){
        if (Objects.equals(userAdminKey, adminKey)){
            this.hasAdminPermissions = true;
        }
    }

    /**
     * Adds an event to the user's list of events they are attending.
     * @param event An eventID.
     */
    public void addEventToEventsAttending(String event){
        Log.d("User", "Adding event to eventsAttending: " + event);
        if(eventsAttending.contains(event)){
            Integer timesAttendedValue = Math.toIntExact(timesAttended.get(event));
            if(timesAttendedValue != null){
                this.timesAttended.replace(event, (long) (timesAttendedValue+1));
                Log.d("User", "Incrementing attendance count for event " + event + ": " + (timesAttendedValue + 1));
            } else {
                this.timesAttended.put(event, 1L);
                Log.d("User", "Initializing attendance count for event " + event + " to 1");
            }
        }

        else{
            this.eventsAttending.add(event);
            this.timesAttended.put(event, 1L);
            Log.d("User", "Adding new event " + event + " to eventsAttending with attendance count 1");
        }
        Log.d("User", "timesAttended after adding event " + event + ": " + timesAttended.get(event));

    }

    /**
     * Adds an event to the user's list of events they are signed up to
     * @param event an event's ID
     */
    public void addEventToEventsSignedUp(String event){
        this.eventsSignedUp.add(event);
    }

    /**
     * Adds an event to the user's list of events they have organized.
     * @param event An event's ID.
     */
    public void addEventToEventsOrganized(String event){
        eventsOrganized.add(event);
    }

    /**
     * Remove an event from the user's list of event's they are signed up to
     * @param event an event's ID
     */
    public void removeEventToEventsSignedUp(String event){
        eventsSignedUp.remove(event);
    }

    /**
     * Remove an event from the user's list of events they have organized.
     * @param event An event's ID.
     */
    public void removeEventToEventsOrganized(String event){
        eventsOrganized.remove(event);
    }

    /**
     * Remove an event from the user's list of events they are attending.
     * @param event An event's ID.
     */
    public void removeEventToEventsAttending(String event){
        eventsAttending.remove(event);
    }
    /**
     * Adds an event to a user's list of events they wish to receive notifications from.
     * @param event an event's ID.
     */
    public void addEventToNotifiedBy(String event){
        notifiedBy.add(event);
        //user will get notifications for this event.
    }

    /**
     * Simplify User object to hashmap to be stored in Firestore database.
     * @return packaged User object
     */
    public Map<String, Object> packageUser() {
        // initialize map for event fields to be stored
        Map<String, Object> packagedUser = new HashMap<>();

        // assign event details to map
        packagedUser.put("name", this.userName);
        packagedUser.put("email", this.userEmail);
        packagedUser.put("phoneNumber", this.userPhoneNumber);
        packagedUser.put("aboutMe", this.userAboutMe);
        packagedUser.put("adminKey", this.adminKey);
        packagedUser.put("ID", this.userID);
        packagedUser.put("eventsAttending", this.eventsAttending);
        packagedUser.put("eventsOrganizing", this.eventsOrganized);
        packagedUser.put("eventsSignedUp", this.eventsSignedUp);
        packagedUser.put("eventsNotifiedBy", this.notifiedBy);
        packagedUser.put("profilePictureURL", this.profilePictureUrl);
        packagedUser.put("isAdmin", this.hasAdminPermissions);
        packagedUser.put("fcmToken", this.fcmToken);
        packagedUser.put("timesAttended", this.timesAttended);

        // return fully detailed event map
        return packagedUser;
    }

    /**
     * Extracts necessary data from snapshot to create an instance of User. <br>
     * @param snapshot Document read from Firestore database with User.accessUser() data.
     * @return User object with appropriate attributes
     */
    public static User unpackageUser(DocumentSnapshot snapshot) {
        // initialize return Event
        User unpackagedUser = new User();

        // Extract data from the DocumentSnapshot
        unpackagedUser.userName = snapshot.getString("name");
        unpackagedUser.userEmail = snapshot.getString("email");
        unpackagedUser.userPhoneNumber = snapshot.getString("phoneNumber");
        unpackagedUser.userAboutMe = snapshot.getString("aboutMe");
        unpackagedUser.adminKey = snapshot.getString("adminKey");
        unpackagedUser.userID = snapshot.getString("ID");
        unpackagedUser.eventsAttending = (ArrayList<String>) snapshot.get("eventsAttending");
        unpackagedUser.eventsOrganized = (ArrayList<String>) snapshot.get("eventsOrganizing");
        unpackagedUser.eventsSignedUp = (ArrayList<String>) snapshot.get("eventsSignedUp");
        unpackagedUser.notifiedBy = (ArrayList<String>) snapshot.get("eventsNotifiedBy");
        unpackagedUser.profilePictureUrl = snapshot.getString("profilePictureURL");
        unpackagedUser.hasAdminPermissions = (boolean) snapshot.get("isAdmin");
        unpackagedUser.fcmToken = snapshot.getString("fcmToken");
        unpackagedUser.timesAttended = (HashMap<String, Long>) snapshot.get("timesAttended");

        // return re-constructed User object
        return unpackagedUser;
    }

    /* ------- *
     * GETTERS *
     * ------- */

    /**
     * @return Admin key corresponding to User
     */
    public String getAdminKey() {
        return this.adminKey;
    }

    /**
     * @return the URL of the user's profile picture
     */
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

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
     * @return the list of events a user is signed up to
     */
    public ArrayList<String> getEventsSignedUp() {
        return eventsSignedUp;
    }

    /**
     * @param event an eventID
     * @return The number of times a User has attended a given event.
     */
    public Integer getTimesAttended(String event){

        Log.d("Your tag","Event ID: " + event);
        Integer attendanceCount = Math.toIntExact(timesAttended.get(event));
        Log.d("Your tag","Attendance Count: " + attendanceCount);
        if (attendanceCount != null) {
            return attendanceCount;
        } else {
            return 0;
        }
    }

    /**
     * Determines whether a user is an Admin based on a key they have entered.
     * @return boolean value indicating if a user is an admin(true) or if they are not(false)
     */
    public boolean getHasAdminPermissions(){
        return this.hasAdminPermissions;
    }
    /**
     * Retrieves a Firebase Cloud Messaging token which allows for the user to receive post notifications from organizers
     * @return String value which is the user's token
     */
    public String getFCMToken() {
        return fcmToken;
    }

    /* ------- *
     * SETTERS *
     * ------- */

    /**
     * @param userID an ID retrieved by the device's ID.
     */

    public void setUserID(String userID) {
        this.userID = userID;
    }
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
     * @param profilePictureUrl the URL of the user's profile picture
     */
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * @param adminPermissions boolean for whether user has admin permissions
     */
    public void setHasAdminPermissions(boolean adminPermissions){
        this.hasAdminPermissions = adminPermissions;
    }

    /**
     * @param FCMToken string that allows the user to receive post notifications
     */
    public void setFCMToken(String FCMToken) {
        this.fcmToken = FCMToken;
    }
}
