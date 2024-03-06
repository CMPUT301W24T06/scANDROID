package com.example.scandroid;

import android.graphics.Bitmap;

/**
 * To handle the asynchronous nature of Firebase operations and <br>
 * ensure that the Bitmap is retrieved before returning, you can <br>
 * modify the method to use a callback or a Task to handle the result. <br>
 * {@see <a href="https://chat.openai.com/share/5353b1da-4b10-4b2c-a928-be60d7cbb08c"> GPTConversation</a>}
 * @author ChatGPT (solution found by Simon Thang)
 */
public interface BitmapCallback {
    void onBitmapLoaded(Bitmap bitmap);
    void onBitmapFailed(Exception e);
}
