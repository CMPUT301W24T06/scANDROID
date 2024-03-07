package com.example.scandroid;

/**
 * To handle the asynchronous nature of Firebase operations and <br>
 * ensure that the Object is retrieved before returning, you can <br>
 * modify the method to use a callback or a Task to handle the result. <br>
 * {@see <a href="https://chat.openai.com/share/e135f2dc-cd2f-47ca-b48e-55115d41e6bf"> GPTConversation</a>}
 * @author ChatGPT (solution found by Simon Thang)
 */
public interface EventCallback {
    void onEventRetrieved(Event event);
}
