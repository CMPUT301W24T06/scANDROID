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
    private String userAboutMe;
    private String userEmail;
    private String userID;
    private String userName;
    private String userPhoneNumber;
    private String adminKey = "ThisPersonIsAnAdmin1298";
    private boolean hasAdminPermissions = false;
    private ArrayList<String> eventsAttending;
    public HashMap<String, Long> timesAttended = new HashMap<>() ;
    private ArrayList<String> eventsOrganized;
    private ArrayList<String> eventsSignedUp;
    private String fcmToken;
    public ArrayList<String> notifiedBy;
    private String profilePictureUrl;



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
        } else { this.userName = userName; }

        this.userAboutMe = userAboutMe;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * Necessary empty constructor for User.unpackageUser method.
     */
    public User() {
        this.userID = "";   // Default to an empty string for userID
        this.userName = ""; // Default to an empty string for userName
        this.eventsAttending = new ArrayList<>();
        this.eventsOrganized = new ArrayList<>();
        this.eventsSignedUp = new ArrayList<>();
        this.notifiedBy = new ArrayList<>();
        this.timesAttended = new HashMap<>();
    }

    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Adds an event to the user's list of events they are attending.
     * @param eventID An eventID.
     */
    public void addEventToEventsAttending(String eventID){
        Log.d("User", "Adding event to eventsAttending: " + eventID);
        if(eventsAttending.contains(eventID)){
            Integer timesAttendedValue = Math.toIntExact(timesAttended.get(eventID));
            if(timesAttendedValue != null){
                this.timesAttended.replace(eventID, (long) (timesAttendedValue+1));
                Log.d("User", "Incrementing attendance count for event " + eventID + ": " + (timesAttendedValue + 1));
            } else {
                this.timesAttended.put(eventID, 1L);
                Log.d("User", "Initializing attendance count for event " + eventID + " to 1");
            }
        } else {
            this.eventsAttending.add(eventID);
            this.timesAttended.put(eventID, 1L);
            Log.d("User", "Adding new event " + eventID + " to eventsAttending with attendance count 1");
        }
        Log.d("User", "timesAttended after adding event " + eventID + ": " + timesAttended.get(eventID));
    }

    /**
     * Adds an event to the user's list of events they have organized.
     * @param eventID An event's ID.
     */
    public void addEventToEventsOrganized(String eventID){
        eventsOrganized.add(eventID);
    }

    /**
     * Adds an event to the user's list of events they are signed up to
     * @param eventID an event's ID
     */
    public void addEventToEventsSignedUp(String eventID){
        this.eventsSignedUp.add(eventID);
    }

    /**
     * Adds an event to a user's list of events they wish to receive notifications from.
     * @param eventID an event's ID.
     */
    public void addEventToNotifiedBy(String eventID){
        notifiedBy.add(eventID);
        //user will get notifications for this event.
    }

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
     * Remove an event from the user's list of events they are attending.
     * @param eventID An event's ID.
     */
    public void removeEventToEventsAttending(String eventID){
        eventsAttending.remove(eventID);
    }

    /**
     * Remove an event from the user's list of events they have organized.
     * @param eventID An event's ID.
     */
    public void removeEventToEventsOrganized(String eventID){
        eventsOrganized.remove(eventID);
    }

    /**
     * Remove an event from the user's list of event's they are signed up to
     * @param eventID an event's ID
     */
    public void removeEventToEventsSignedUp(String eventID){
        eventsSignedUp.remove(eventID);
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
     * Retrieves a Firebase Cloud Messaging token which allows for the user to receive post notifications from organizers
     * @return String value which is the user's token
     */
    public String getFCMToken() {
        return fcmToken;
    }

    /**
     * Determines whether a user is an Admin based on a key they have entered.
     * @return boolean value indicating if a user is an admin(true) or if they are not(false)
     */
    public boolean getHasAdminPermissions(){
        return this.hasAdminPermissions;
    }

    /**
     * @return A list of eventIDs that the user may be notified by.
     */
    public ArrayList<String> getNotifiedBy() {
        return notifiedBy;
    }

    /**
     * @return the URL of the user's profile picture
     */
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    /**
     * @param eventID an eventID
     * @return The number of times a User has attended a given event.
     */
    public Integer getTimesAttended(String eventID){

        Log.d("Your tag","Event ID: " + eventID);
        Integer attendanceCount = Math.toIntExact(timesAttended.get(eventID));
        Log.d("Your tag","Attendance Count: " + attendanceCount);
        if (attendanceCount != null) {
            return attendanceCount;
        } else {
            return 0;
        }
    }

    /**
     * @return The information in the user's 'About Me' section of their profile.
     */
    public String getUserAboutMe() {
        return userAboutMe;
    }

    /**
     * @return the user's email.
     */
    public String getUserEmail() {
        return userEmail;
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

    /**
     * @return the user's phone number
     */
    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }


    /* ------- *
     * SETTERS *
     * ------- */
    /**
     * @param FCMToken string that allows the user to receive post notifications
     */
    public void setFCMToken(String FCMToken) {
        this.fcmToken = FCMToken;
    }

    /**
     * @param adminPermissions boolean for whether user has admin permissions
     */
    public void setHasAdminPermissions(boolean adminPermissions){
        this.hasAdminPermissions = adminPermissions;
    }

    /**
     * @param profilePictureUrl the URL of the user's profile picture
     */
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * @param userAboutMe the about-me section the user inputs in their profile.
     */
    public void setUserAboutMe(String userAboutMe) {
        this.userAboutMe = userAboutMe;
    }

    /**
     * @param userEmail the email inputted by the user.
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

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
     * @param userPhoneNumber the phone number inputted by the user.
     */
    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

}
