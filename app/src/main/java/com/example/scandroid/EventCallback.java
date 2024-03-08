package com.example.scandroid;

/**
 * EventCallback is an interface used for handling event callbacks.
 */
public interface EventCallback {
    /**
     * Called when an event is received.
     *
     * @param event the received event.
     */
    public void onEventReceived(Event event);
}
