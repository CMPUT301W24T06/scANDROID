package com.example.scandroid;

import android.content.Context;
import android.provider.Settings;

public class DeviceIDRetriever {
    String deviceID;

    DeviceIDRetriever(Context context){
        // Use Settings.Secure.ANDROID_ID for a unique device identifier
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    public String getDeviceId() {
        return deviceID;
    }
}
