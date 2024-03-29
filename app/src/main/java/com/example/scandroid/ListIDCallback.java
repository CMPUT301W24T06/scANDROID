package com.example.scandroid;

import java.util.ArrayList;

/**
 * ListIDCallback is an interface used for handling list retrieval callbacks.
 */
public interface ListIDCallback {

    /**
     * Called when a List is retrieved.
     *
     * @param List the retrieved List.
     */
    void onListRetrieved(ArrayList<String> List);
}
