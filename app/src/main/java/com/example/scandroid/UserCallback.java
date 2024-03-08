package com.example.scandroid;

/**
 * UserCallback is an interface used for handling user retrieval callbacks.
 */
public interface UserCallback {

    /**
     * Called when a user is retrieved.
     *
     * @param user the retrieved user.
     */
    void onUserRetrieved(User user);
}
