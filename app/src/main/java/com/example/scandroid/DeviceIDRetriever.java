package com.example.scandroid;

import android.content.Context;
import android.provider.Settings;

/**
 * DeviceIDRetriever is a class responsible for obtaining a device's unique ID
 * when they first open the app.
 * The ID is used to uniquely identify and save users to the database without the need for a login
 */
public class DeviceIDRetriever {
    String deviceID;

    /**
     * constructs a new DeviceIDRetriever instance and retrieves the unique device ID
     * @param context application context used to access device-related information
     */
    DeviceIDRetriever(Context context){
        // Use Settings.Secure.ANDROID_ID for a unique device identifier
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * retrieves the unique device ID obtained during initialization
     * @return the unique device ID.
     */
    public String getDeviceId() {
        return deviceID;
    }
}
