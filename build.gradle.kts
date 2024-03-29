buildscript {
    repositories {
        google() // Add Google Maven repository
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.1")
        // Add the Google Services plugin for using Google services
        classpath("com.google.gms:google-services:4.4.1") // or the latest version available
    }
}
